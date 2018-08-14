package moe.xing.rx2_utils;

import android.support.annotation.NonNull;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by Qi Xingchen on 16-12-8.
 * <p>
 * RX view 事件
 */

public class RxViewEvent {

    /**
     * UI 时间延迟处理 500 ms
     */
    @NonNull
    public static ObservableTransformer<Object, Object> delay() {
        return new ObservableTransformer<Object, Object>() {
            /**
             * Applies a function to the upstream Observable and returns an ObservableSource with
             * optionally different element type.
             *
             * @param upstream the upstream Observable instance
             * @return the transformed ObservableSource instance
             */
            @Override
            public ObservableSource<Object> apply(Observable<Object> upstream) {
                return upstream.throttleLast(500, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }
}
