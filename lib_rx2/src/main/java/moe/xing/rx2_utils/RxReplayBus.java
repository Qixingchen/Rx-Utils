package moe.xing.rx2_utils;


import io.reactivex.Flowable;
import io.reactivex.processors.FlowableProcessor;
import io.reactivex.processors.ReplayProcessor;

/**
 * Created by Qi Xingchen on 16-11-23.
 */

public class RxReplayBus {

    private static RxReplayBus instance;
    private final FlowableProcessor<Object> _bus = ReplayProcessor.create().toSerialized();

    public static RxReplayBus getInstance() {
        if (instance == null) {
            synchronized (RxReplayBus.class) {
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

    public Flowable<Object> toObservable() {
        return _bus;
    }

}
