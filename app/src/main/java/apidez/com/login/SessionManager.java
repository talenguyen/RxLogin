package apidez.com.login;

import android.app.Activity;
import android.content.Intent;
import android.os.OperationCanceledException;

import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by Giang Nguyen at Tiki on 4/11/16.
 */
public class SessionManager<Session> {

    public static final int LOGIN_REQUEST_CODE = 1010;
    private final SessionParser<Session> sessionParser;
    private Session session;
    private PublishSubject<Session> sessionStream;

    public SessionManager(Session session, SessionParser<Session> sessionParser) {
        this.session = session;
        this.sessionParser = sessionParser;
    }

    public Observable<Session> getSessionStream(Activity activity, Class<? extends Activity> loginActivity) {
        if (session == null) {
            if (sessionStream == null) {
                sessionStream = PublishSubject.create();
            }
            activity.startActivityForResult(new Intent(activity, loginActivity), LOGIN_REQUEST_CODE);
            return sessionStream;
        } else {
            return Observable.just(session);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LOGIN_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            this.session = sessionParser.parse(data);
            if (sessionStream == null) {
                return;
            }
            sessionStream.onNext(this.session);
            sessionStream.onCompleted();
        } else {
            sessionStream.onError(new OperationCanceledException());
        }
        sessionStream = null;
    }

    public void close() {
        session = null;
    }
}

