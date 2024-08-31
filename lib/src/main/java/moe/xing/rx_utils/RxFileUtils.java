package moe.xing.rx_utils;

import android.Manifest;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import com.tbruyelle.rxpermissions.RxPermissions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import moe.xing.baseutils.Init;
import moe.xing.baseutils.utils.FileUtils;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;

/**
 * Created by Qi Xingchen on 16-10-24.
 * <p>
 * 文件帮助类 RX 包装
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class RxFileUtils extends FileUtils {


    /**
     * 将文件拷贝至外置缓存区的 {@link rx.Observable.Operator}
     *
     * @return Observable.Operator
     */
    @WorkerThread
    @NonNull
    public static Observable.Operator<File, File> copyFileToExCache() {
        return new Observable.Operator<File, File>() {
            @Override
            public Subscriber<? super File> call(final Subscriber<? super File> subscriber) {
                return new Subscriber<File>() {
                    @Override
                    public void onCompleted() {
                        subscriber.onCompleted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        subscriber.onError(e);
                    }

                    @Override
                    public void onNext(File file) {
                        if (!FileUtils.isExternalStorageWritable()) {
                            subscriber.onError(new Throwable(Init.getApplication()
                                    .getString(moe.xing.baseutils.R.string.external_disk_not_exits)));
                        }
                        try {
                            File dst = getCacheFile(file.getName());
                            CopyFile(file, dst);
                            subscriber.onNext(dst);
                        } catch (IOException e) {
                            e.printStackTrace();
                            subscriber.onError(e);
                        }
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
        return Observable.create(new Observable.OnSubscribe<File>() {
            @Override
            public void call(Subscriber<? super File> subscriber) {
                try {
                    subscriber.onNext(copyAssetFile(fileName));
                } catch (IOException e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    /**
     * 将指定的字符串存入缓存
     *
     * @param s        要被储存的字符串
     * @param filename 被储存的文件(原来是 preview.html)
     * @return Observable<File>
     */
    @WorkerThread
    @NonNull
    public static Observable<File> SaveString(@NonNull final String s, @NonNull final String filename) {
        return Observable.create(new Observable.OnSubscribe<File>() {
            @Override
            public void call(Subscriber<? super File> subscriber) {
                File out;
                try {
                    out = getCacheFile(filename);
                    FileOutputStream fos = new FileOutputStream(out);
                    writeToFile(s, fos);
                    fos.close();
                    subscriber.onNext(out);
                } catch (IOException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
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
        return Observable.create(new Observable.OnSubscribe<File>() {
            @Override
            public void call(Subscriber<? super File> subscriber) {
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
                    subscriber.onNext(out);
                } catch (IOException e) {
                    subscriber.onError(e);
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

        return Observable.create(new Observable.OnSubscribe<File>() {
            @Override
            public void call(final Subscriber<? super File> subscriber) {
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

                    } catch (final IOException e) {
                        e.printStackTrace();
                        subscriber.onError(e);
                    } catch (SecurityException e) {
                        new RxPermissions(context).request(Manifest.permission.READ_EXTERNAL_STORAGE)
                                .subscribe(new Action1<Boolean>() {
                                    @Override
                                    public void call(Boolean aBoolean) {
                                        subscriber.onError(new Throwable("请再选择一次"));
                                    }
                                });
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
                    subscriber.onNext(file);
                } else {
                    subscriber.onError(new Throwable("获取文件时出错"));
                }
            }
        });
    }

}
