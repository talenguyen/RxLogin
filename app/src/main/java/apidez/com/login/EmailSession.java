package apidez.com.login;

import apidez.com.login.session.Session;

/**
 * Created by Giang Nguyen at Tiki on 4/11/16.
 */
public class EmailSession implements Session {
    private String email;

    public EmailSession(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public boolean isExpired() {
        return email == null;
    }
}
