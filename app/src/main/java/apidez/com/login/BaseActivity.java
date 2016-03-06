package apidez.com.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subjects.PublishSubject;

/**
 * Created by nongdenchet on 3/7/16.
 */
public class BaseActivity extends AppCompatActivity {
    private final int LOGIN_REQUEST = 1;
    private SessionService mSessionService = new SessionService();
    private PublishSubject<Boolean> mLoginEvent = PublishSubject.create();
    private PublishSubject<Boolean> mLoginEventResult = PublishSubject.create();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindLoginEvent();
    }

    private void bindLoginEvent() {
        mLoginEvent.subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean showLogin) {
                if (showLogin) showLoginActivity();
            }
        });
    }

    private void showLoginActivity() {
        showToast("Login required");
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, LOGIN_REQUEST);
    }

    protected <T> Observable.Transformer<T, T> checkLogin() {
        return new Observable.Transformer<T, T>() {
            @Override
            public Observable<T> call(final Observable<T> observable) {
                if (!mSessionService.isLogin()) {
                    mLoginEvent.onNext(true);
                    return waitForLogin(observable);
                }
                return observable;
            }
        };
    }

    private <T> Observable<T> waitForLogin(final Observable<T> observable) {
        return mLoginEventResult.asObservable()
                .take(1)
                .flatMap(new Func1<Boolean, Observable<T>>() {
                    @Override
                    public Observable<T> call(Boolean success) {
                        mSessionService.setLogin(success);
                        return success ? observable : Observable.<T>empty();
                    }
                });
    }

    protected void showToast(String value) {
        Toast.makeText(getApplicationContext(), value, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LOGIN_REQUEST) {
            mLoginEventResult.onNext(resultCode == RESULT_OK);
        }
    }
}
