package moe.xing.rx2_utils;

import io.reactivex.Flowable;
import io.reactivex.processors.FlowableProcessor;
import io.reactivex.processors.PublishProcessor;

/**
 * Created by Qi Xingchen on 2018-8-14.
 */
public class RxBus {
    private static RxBus instance;
    private final FlowableProcessor<Object> _bus = PublishProcessor.create().toSerialized();

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

    public void send(Object o) {
        _bus.onNext(o);
    }

    public Flowable<Object> toObserverable() {
        return _bus;
    }

}
