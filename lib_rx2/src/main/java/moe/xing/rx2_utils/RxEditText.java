package moe.xing.rx2_utils;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.MainThreadDisposable;


/**
 * Created by Qi Xingchen on 2018-8-14.
 */
public class RxEditText {
    @CheckResult
    @NonNull
    public static Observable<Editable> checkedChanges(@NonNull EditText view) {
        return new EditTextIMEDoneObservable(view);
    }

    static final class EditTextIMEDoneObservable extends Observable<Editable> {
        private final EditText view;

        EditTextIMEDoneObservable(EditText view) {
            this.view = view;
        }

        @Override
        protected void subscribeActual(Observer<? super Editable> observer) {
            EditTextIMEDoneListener editTextIMEDoneListener = new EditTextIMEDoneListener(view, observer);
            view.setOnEditorActionListener(editTextIMEDoneListener);
            observer.onSubscribe(editTextIMEDoneListener);
        }
    }

    static final class EditTextIMEDoneListener extends MainThreadDisposable implements TextView.OnEditorActionListener {
        private final EditText view;
        private final Observer<? super Editable> observer;

        EditTextIMEDoneListener(EditText view, Observer<? super Editable> observer) {
            this.view = view;
            this.observer = observer;
        }

        @Override
        protected void onDispose() {
            view.setOnEditorActionListener(null);
        }

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (!isDisposed()) {
                if (actionId == KeyEvent.KEYCODE_ENTER
                        || actionId == EditorInfo.IME_NULL
                        || actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || actionId == EditorInfo.IME_ACTION_SEND
                ) {
                    observer.onNext(view.getText());
                    return true;
                }
            }
            return false;
        }
    }
}
