package apidez.com.login.session;

import android.support.annotation.NonNull;

import rx.Observable;

/**
 * Created by Giang Nguyen at Tiki on 4/11/16.
 */
public interface SessionRequest {

    @NonNull  Observable<Session> requestSessionAsync();
}
