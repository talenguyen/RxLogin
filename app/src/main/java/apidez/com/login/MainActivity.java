package apidez.com.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import apidez.com.login.session.Session;
import apidez.com.login.session.SessionManager;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity {

    private SessionManager sessionManager;
    private LoginSessionRequest loginSessionRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginSessionRequest = new LoginSessionRequest(this);
        sessionManager = new SessionManager(null, loginSessionRequest);
        setContentView(R.layout.activity_main);
        setUpView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loginSessionRequest.onActivityResult(requestCode, resultCode, data);
    }

    private void setUpView() {
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subscribeTask();
            }
        });
    }

    private void subscribeTask() {
        sessionManager.getSession()
                .flatMap(new Func1<Session, Observable<String>>() {
                    @Override
                    public Observable<String> call(Session session) {
                        final String msg = "Hello " + ((EmailSession) session).getEmail();
                        return Observable.just(msg);
                    }
                })
                .takeUntil(destroyEvent())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String response) {
                        showToast(response);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        showToast(throwable.getMessage());
                    }
                });
    }
}
