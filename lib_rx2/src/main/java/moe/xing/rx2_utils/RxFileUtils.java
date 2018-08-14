package moe.xing.rx2_utils;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.SingleObserver;
import io.reactivex.SingleOperator;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import moe.xing.baseutils.Init;
import moe.xing.baseutils.utils.FileUtils;


/**
 * Created by Qi Xingchen on 16-10-24.
 * <p>
 * 文件帮助类 RX 包装
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class RxFileUtils extends FileUtils {


    /**
     * 将文件拷贝至外置缓存区的 {@link io.reactivex.SingleOperator}
     *
     * @return Observable.Operator
     */
    @WorkerThread
    @NonNull
    public static SingleOperator<File, File> copyFileToExCache() {
        return new SingleOperator<File, File>() {
            @Override
            public SingleObserver<? super File> apply(final SingleObserver<? super File> observer) throws Exception {
                return new SingleObserver<File>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        observer.onSubscribe(d);
                    }

                    @Override
                    public void onSuccess(File file) {
                        if (!FileUtils.isExternalStorageWritable()) {
                            observer.onError(new Throwable(Init.getApplication()
                                    .getString(moe.xing.baseutils.R.string.external_disk_not_exits)));
                        }
                        try {
                            File dst = getCacheFile(file.getName());
                            CopyFile(file, dst);
                            observer.onSuccess(dst);
                        } catch (IOException e) {
                            e.printStackTrace();
                            observer.onError(e);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        observer.onError(e);
                    }
                };
            }
        };
    }

    /**
     * 将 Asset 文件拷贝到缓存
     *
     * @param fileName 文件名
     * @return Observable<File>
     */
    @WorkerThread
    @NonNull
    public static Observable<File> copyAsset(@NonNull final String fileName) {
        return Observable.just(fileName).map(new Function<String, File>() {
            @Override
            public File apply(String s) throws Exception {
                return copyAssetFile(fileName);
            }
        });
    }

    /**
     * 将指定的字符串存入缓存
     *
     * @param s    要被储存的字符串
     * @param file 被储存的文件(原来是 preview.html)
     * @return Observable<File>
     */
    @WorkerThread
    @NonNull
    public static Observable<File> SaveString(@NonNull final String s, @NonNull final File file) {
        return Observable.create(new ObservableOnSubscribe<File>() {
            @Override
            public void subscribe(ObservableEmitter<File> emitter) throws Exception {
                try {
                    FileOutputStream fos = new FileOutputStream(file);
                    writeToFile(s, fos);
                    fos.close();
                    emitter.onNext(file);
                } catch (IOException e) {
                    e.printStackTrace();
                    emitter.onError(e);
                }
            }
        });
    }

    /**
     * 替换文件中的字符
     *
     * @param file    要求替换字符的文件
     * @param replace 替换的 map
     * @return Observable<File>
     */
    @NonNull
    @WorkerThread
    public static Observable<File> replaceStringsInfile(@NonNull final File file, @NonNull final Map<String, String> replace) {

        return Observable.create(new ObservableOnSubscribe<File>() {
            @Override
            public void subscribe(ObservableEmitter<File> emitter) throws Exception {
                try {
                    FileInputStream fis = new FileInputStream(file);
                    String content = convertStreamToString(fis);

                    for (Map.Entry<String, String> entry : replace.entrySet()) {
                        content = content.replaceAll(entry.getKey(), entry.getValue());
                    }
                    File out = getCacheFile("preview-" + file.getName());
                    FileOutputStream fos = new FileOutputStream(out);
                    writeToFile(content, fos);
                    fis.close();
                    fos.close();
                    emitter.onNext(out);
                } catch (IOException e) {
                    emitter.onError(e);
                }
            }
        });
    }

    /**
     * 从 Uri 获取文件
     *
     * @param context 有权限的 context
     * @param uri     文件的 Uri
     * @return Observable<File>
     */
    @NonNull
    public static Observable<File> getFileUrlWithAuthority(@NonNull final Context context, @NonNull final Uri uri) {

        return Observable.create(new ObservableOnSubscribe<File>() {
            @Override
            public void subscribe(ObservableEmitter<File> emitter) throws Exception {
                InputStream is = null;
                OutputStream os = null;
                File file = null;
                if (uri.getAuthority() != null) {
                    try {
                        is = context.getContentResolver().openInputStream(uri);

                        file = FileUtils.getCacheFile(FileUtils.getFileNameFromUri(uri));

                        os = new FileOutputStream(file);
                        byte[] buf = new byte[1024 * 8];
                        int len;
                        if (is != null) {
                            while ((len = is.read(buf)) != -1) {
                                os.write(buf, 0, len);
                            }
                        }
                        os.flush();

                    } catch (IOException | SecurityException e) {
                        e.printStackTrace();
                        emitter.onError(e);
                    } finally {
                        try {
                            if (is != null) {
                                is.close();
                            }
                            if (os != null) {
                                os.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (file != null) {
                    emitter.onNext(file);
                } else {
                    emitter.onError(new Throwable("获取文件时出错"));
                }
            }
        });

    }

}
