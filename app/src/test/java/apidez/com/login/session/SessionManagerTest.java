package apidez.com.login.session;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import rx.Observable;
import rx.observers.TestSubscriber;
import rx.subjects.PublishSubject;

/**
 * Created by Giang Nguyen at Tiki on 4/11/16.
 */
public class SessionManagerTest {

    @Mock
    Session session;

    @Mock
    SessionRequest sessionRequest;

    private SessionManager sessionManager;
    private TestSubscriber<Object> testSubscriber;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        testSubscriber = new TestSubscriber<>();
    }

    @Test
    public void testGetSession_sessionNull_shouldRequestSession() throws Exception {
        Mockito.when(sessionRequest.requestSessionAsync()).thenReturn(PublishSubject.<Session>create());
        sessionManager = new SessionManager(null, sessionRequest);
        sessionManager.getSession().subscribe(testSubscriber);
        Mockito.verify(sessionRequest).requestSessionAsync();
    }

    @Test
    public void testGetSession_sessionNull_shouldReceiveDataBySessionRequest() throws Exception {
        final Session session = new Session() {
            @Override
            public boolean isExpired() {
                return true;
            }
        };
        sessionManager = new SessionManager(null, sessionRequest);
        Mockito.when(sessionRequest.requestSessionAsync()).thenReturn(Observable.just(session));
        sessionManager.getSession().subscribe(testSubscriber);
        testSubscriber.assertValue(session);
    }

    @Test
    public void testGetSession_sessionNull_shouldReceiveErrorBySessionRequest() throws Exception {
        sessionManager = new SessionManager(null, sessionRequest);
        final RuntimeException exectedError = new RuntimeException();
        final Observable<Session> errorStream = Observable.error(exectedError);
        Mockito.when(sessionRequest.requestSessionAsync()).thenReturn(errorStream);
        sessionManager.getSession().subscribe(testSubscriber);
        testSubscriber.assertError(exectedError);
    }

    @Test
    public void testGetSession_sessionNotNull_shouldReceiveSession() throws Exception {
        final Session session = Mockito.mock(Session.class);
        sessionManager = new SessionManager(session, sessionRequest);

        sessionManager.getSession().subscribe(testSubscriber);
        testSubscriber.assertValue(session);
    }

    @Test
    public void testClose_shouldWaitForSessionRequest() throws Exception {
        final Session session = Mockito.mock(Session.class);
        sessionManager = new SessionManager(session, sessionRequest);
        sessionManager.close();

        final Session newSession = Mockito.mock(Session.class);
        Mockito.when(sessionRequest.requestSessionAsync()).thenReturn(Observable.just(newSession));
        sessionManager.getSession().subscribe(testSubscriber);
        testSubscriber.assertValue(newSession);
    }
}