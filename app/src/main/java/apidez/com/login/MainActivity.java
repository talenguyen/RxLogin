package apidez.com.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity {

    private SessionManager<String> sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager<>(null, new SessionParser<String>() {
            @Override
            public String parse(Intent data) {
                return data.getStringExtra("email");
            }
        });
        setContentView(R.layout.activity_main);
        setUpView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        sessionManager.onActivityResult(requestCode, resultCode, data);
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
        sessionManager.getSessionStream(this, LoginActivity.class)
                .flatMap(new Func1<String, Observable<String>>() {
                    @Override
                    public Observable<String> call(String s) {
                        return Observable.just("Hello " + s);
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
