package apidez.com.login;

import android.app.Activity;
import android.content.Intent;
import android.os.OperationCanceledException;
import android.support.annotation.NonNull;

import apidez.com.login.session.Session;
import apidez.com.login.session.SessionRequest;
import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by Giang Nguyen at Tiki on 4/11/16.
 */
public class LoginSessionRequest implements SessionRequest {

    public static final int LOGIN_REQUEST_CODE = 2403;
    private Activity activity;
    private PublishSubject<Session> sessionStream;

    public LoginSessionRequest(Activity activity) {
        this.activity = activity;
    }

    @NonNull
    @Override
    public Observable<Session> requestSessionAsync() {
        return startLoginForResult();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (sessionStream == null) {
            return;
        }

        if (requestCode == LOGIN_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            final String email = data.getStringExtra("email");
            final EmailSession session = new EmailSession(email);
            sessionStream.onNext(session);
        } else {
            sessionStream.onError(new OperationCanceledException());
        }
    }

    private Observable<Session> startLoginForResult() {
        activity.startActivityForResult(new Intent(activity, LoginActivity.class), LOGIN_REQUEST_CODE);
        sessionStream = PublishSubject.create();
        return sessionStream;
    }
}
