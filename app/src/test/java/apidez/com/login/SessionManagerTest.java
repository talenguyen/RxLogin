package apidez.com.login;

import android.app.Activity;
import android.content.Intent;
import android.os.OperationCanceledException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import rx.observers.TestSubscriber;

/**
 * Created by Giang Nguyen at Tiki on 4/11/16.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class SessionManagerTest {

    @Mock SessionParser<String> sessionParser;
    private Activity activity;
    private TestSubscriber<Object> testSubscriber;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        activity = Robolectric.buildActivity(Activity.class).get();
        testSubscriber = new TestSubscriber<>();
    }

    @Test
    public void testGetSessionStream_initNull_shouldNotReceiveData() throws Exception {
        final SessionManager<String> sessionManager = new SessionManager<>(null, sessionParser);
        sessionManager.getSessionStream(activity, Activity.class)
                .subscribe(testSubscriber);
        testSubscriber.assertNoValues();
        testSubscriber.assertNoErrors();
        testSubscriber.assertNotCompleted();
    }

    @Test
    public void testGetSessionStream_initNotNull_shouldReceiveOnSubscribe() throws Exception {
        final String expectedSession = "Expected session";
        final SessionManager<String> sessionManager = new SessionManager<>(expectedSession, sessionParser);
        sessionManager.getSessionStream(activity, Activity.class)
                .subscribe(testSubscriber);
        testSubscriber.assertValue(expectedSession);
    }

    @Test
    public void testOnActivityResult_cancelled_shouldReceiveError() throws Exception {
        final SessionManager<String> sessionManager = new SessionManager<>(null, sessionParser);
        sessionManager.getSessionStream(activity, Activity.class)
                .subscribe(testSubscriber);
        sessionManager.onActivityResult(SessionManager.LOGIN_REQUEST_CODE, Activity.RESULT_CANCELED, null);
        testSubscriber.assertError(OperationCanceledException.class);
    }

    @Test
    public void testOnActivityResult_success_shouldReceiveData() throws Exception {
        final SessionManager<String> sessionManager = new SessionManager<>(null, sessionParser);
        sessionManager.getSessionStream(activity, Activity.class)
                .subscribe(testSubscriber);
        final Intent intent = new Intent();
        final String expectedSession = "expected session";
        intent.putExtra("session", expectedSession);
        Mockito.when(sessionParser.parse(intent)).thenReturn(expectedSession);
        sessionManager.onActivityResult(SessionManager.LOGIN_REQUEST_CODE, Activity.RESULT_OK, intent);
        testSubscriber.assertValue(expectedSession);
    }

    @Test
    public void testOnActivityResult_afterSuccess_shouldAlwaysReceiveData() throws Exception {
        final SessionManager<String> sessionManager = new SessionManager<>(null, sessionParser);
        sessionManager.getSessionStream(activity, Activity.class)
                .subscribe(testSubscriber);
        final Intent intent = new Intent();
        final String expectedSession = "expected session";
        intent.putExtra("session", expectedSession);
        Mockito.when(sessionParser.parse(intent)).thenReturn(expectedSession);
        sessionManager.onActivityResult(SessionManager.LOGIN_REQUEST_CODE, Activity.RESULT_OK, intent);
        testSubscriber.assertValue(expectedSession);

        final TestSubscriber<String> anotherTestSubscriber = new TestSubscriber<>();
        sessionManager.getSessionStream(activity, Activity.class)
                .subscribe(anotherTestSubscriber);
        anotherTestSubscriber.assertValue(expectedSession);
    }

    @Test
    public void testClose_shouldNotReceiveData() throws Exception {
        final SessionManager<String> sessionManager = new SessionManager<>("session", sessionParser);
        sessionManager.close();
        sessionManager.getSessionStream(activity, Activity.class)
                .subscribe(testSubscriber);
        testSubscriber.assertNoValues();
        testSubscriber.assertNoErrors();
        testSubscriber.assertNotCompleted();
    }
}