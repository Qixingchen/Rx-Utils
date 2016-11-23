package moe.xing.rx_utils;

import rx.Observable;
import rx.subjects.ReplaySubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * Created by Qi Xingchen on 16-11-23.
 */

public class RxReplayBus {

    private static RxReplayBus instance;
    private final Subject<Object, Object> _bus = new SerializedSubject<>(ReplaySubject.create());

    public static RxReplayBus getInstance() {
        if (instance == null) {
            synchronized (RxBus.class) {
                if (instance == null) {
                    instance = new RxReplayBus();
                }
            }
        }
        return instance;
    }

    public void send(Object o) {
        _bus.onNext(o);
    }

    public Observable<Object> toObservable() {
        return _bus;
    }

}
