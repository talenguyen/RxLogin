package apidez.com.login;

import android.content.Intent;

/**
 * Created by Giang Nguyen at Tiki on 4/11/16.
 */
public interface SessionParser<Session> {

    Session parse(Intent data);
}
