package apidez.com.login.session;

import rx.Observable;
import rx.functions.Action1;

/**
 * Created by Giang Nguyen at Tiki on 4/11/16.
 */
public class SessionManager {

    private final SessionRequest sessionRequest;
    private Session session;

    public SessionManager(Session session, SessionRequest sessionRequest) {
        this.session = session;
        this.sessionRequest = sessionRequest;
    }

    public Observable<Session> getSession() {
        if (session == null || session.isExpired()) {
            return sessionRequest.requestSessionAsync()
                    .doOnNext(new Action1<Session>() {
                        @Override
                        public void call(Session session) {
                            save(session);
                        }
                    });
        } else {
            return Observable.just(session);
        }
    }

    public void close() {
        save(null);
    }

    private void save(Session session) {
        this.session = session;
    }

}

