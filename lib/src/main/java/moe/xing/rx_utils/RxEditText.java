package moe.xing.rx_utils;

import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;

import rx.Observable;
import rx.Subscriber;
import rx.android.MainThreadSubscription;

import static com.jakewharton.rxbinding.internal.Preconditions.checkNotNull;
import static com.jakewharton.rxbinding.internal.Preconditions.checkUiThread;

/**
 * Created by Qi Xingchen on 17-4-25.
 * <p>
 * 输入文本的 IMEDone 事件 rx订阅
 */

public class RxEditText {

    /**
     * 对传入的 EditText 订阅 action done 订阅
     *
     * @param editText 需要订阅的 EditText
     * @return Observable<Void> action done 订阅
     */
    @NonNull
    public static Observable<Void> IMEDone(@NonNull EditText editText) {
        checkNotNull(editText, "editText == null");
        return Observable.create(new IMEDoneOnSubscribe(editText));
    }

    private final static class IMEDoneOnSubscribe implements Observable.OnSubscribe<Void> {
        final EditText mEditText;

        IMEDoneOnSubscribe(EditText view) {
            this.mEditText = view;
        }

        @Override
        public void call(final Subscriber<? super Void> subscriber) {
            checkUiThread();

            TextView.OnEditorActionListener listener = new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == KeyEvent.KEYCODE_ENTER || actionId == KeyEvent.KEYCODE_ENDCALL) {
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onNext(null);
                        }
                    }
                    return false;
                }
            };
            mEditText.setOnEditorActionListener(listener);

            subscriber.add(new MainThreadSubscription() {
                @Override
                protected void onUnsubscribe() {
                    mEditText.setOnEditorActionListener(null);
                }
            });
        }
    }

}
