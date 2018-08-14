package moe.xing.rx_utils_sample;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.jakewharton.rxbinding2.view.RxView;

import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import moe.xing.baseutils.Init;
import moe.xing.baseutils.utils.FileUtils;
import moe.xing.rx2_utils.RxFileUtils;
import moe.xing.rx2_utils.RxViewEvent;

/**
 * Created by Qi Xingchen on 17-6-14.
 */

public class MainActivity extends AppCompatActivity {

    CompositeDisposable compositeDisposable = new CompositeDisposable();


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Init.init(getApplication(), true, "1.0", "rx_util");

        Disposable disposable = RxView.clicks(findViewById(R.id.copy_file_button)).compose(RxViewEvent.delay())
                .subscribe(o -> {
                    Disposable dis = RxFileUtils.SaveString("2333", FileUtils.getCacheFile("2333.txt"))
                            .flatMap(file -> {
                                HashMap<String, String> replace = new HashMap<>();
                                replace.put("333", "222");
                                return RxFileUtils.replaceStringsInfile(file, replace);

                            })
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(file -> Toast.makeText(MainActivity.this, file.getAbsolutePath() + " saved", Toast.LENGTH_LONG).show());

                    compositeDisposable.add(dis);
                });
        compositeDisposable.add(disposable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }
}
