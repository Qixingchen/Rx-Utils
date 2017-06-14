package moe.xing.rx_utils;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * Created by Qi Xingchen on 16-11-23.
 * <p>
 * rx bus
 */

public class RxBus {

    private static RxBus instance;
    private final Subject<Object, Object> _bus = new SerializedSubject<>(PublishSubject.create());

    public static RxBus getInstance() {
        if (instance == null) {
            synchronized (RxBus.class) {
                if (instance == null) {
                    instance = new RxBus();
                }
            }
        }
        return instance;
    }

    public static RxBus getNewInstance() {
        return new RxBus();
    }

    public void send(Object o) {
        _bus.onNext(o);
    }

    public Observable<Object> toObserverable() {
        return _bus;
    }
}