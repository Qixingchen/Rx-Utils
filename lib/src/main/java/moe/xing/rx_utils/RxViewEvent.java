package moe.xing.rx_utils;

import android.support.annotation.NonNull;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

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
    public static Observable.Transformer<Void, Void> delay() {
        return new Observable.Transformer<Void, Void>() {
            @Override
            public Observable<Void> call(Observable<Void> responseObservable) {
                return responseObservable.throttleLast(1500, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }
}
