package ${package}.gitb;

import com.gitb.ms.MessagingClientService;
import com.gitb.ms.NotifyForMessageRequest;
import com.gitb.tr.TAR;
import com.gitb.tr.TestResultType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Component used to store sessions and their state.
 *
 * This class is key in maintaining an overall context across a request and one or more
 * responses. It allows mapping of received data to a given test session running in the
 * test bed.
 *
 * This implementation stores session information in memory. An alternative solution
 * that would be fault-tolerant could store test session data in a DB.
 */
@Component
public class SessionManager {

    /** Logger. */
    private static final Logger LOG = LoggerFactory.getLogger(SessionManager.class);

    /** The map of in-memory active sessions. */
    private Map<String, Map<String, Object>> sessions = new ConcurrentHashMap<>();

    /**
     * Create a new session.
     *
     * @param callbackURL The callback URL to set for this session.
     * @return The session ID that was generated.
     */
    public String createSession(String callbackURL) {
        if (callbackURL == null) {
            throw new IllegalArgumentException("A callback URL must be provided");
        }
        String sessionId = UUID.randomUUID().toString();
        Map<String, Object> sessionInfo = new HashMap<>();
        sessionInfo.put(SessionData.CALLBACK_URL, callbackURL);
        sessions.put(sessionId, sessionInfo);
        return sessionId;
    }

    /**
     * Remove the provided session from the list of tracked sessions.
     *
     * @param sessionId The session ID to remove.
     */
    public void destroySession(String sessionId) {
        sessions.remove(sessionId);
    }

    /**
     * Get a given item of information linked to a specific session.
     *
     * @param sessionId The session ID we want to lookup.
     * @param infoKey The key of the value that we want to retrieve.
     * @return The retrieved value.
     */
    public Object getSessionInfo(String sessionId, String infoKey) {
        Object value = null;
        if (sessions.containsKey(sessionId)) {
            value = sessions.get(sessionId).get(infoKey);
        }
        return value;
    }

    /**
     * Set the given information item for a session.
     *
     * @param sessionId The session ID to set the information for.
     * @param infoKey The information key.
     * @param infoValue The information value.
     */
    public void setSessionInfo(String sessionId, String infoKey, Object infoValue) {
        sessions.get(sessionId).put(infoKey, infoValue);
    }

    /**
     * Get all the active sessions.
     *
     * @return An unmodifiable map of the sessions.
     */
    public Map<String, Map<String, Object>> getAllSessions() {
        return Collections.unmodifiableMap(sessions);
    }

    /**
     * Notify the test bed for a given session.
     *
     * @param sessionId The session ID to notify the test bed for.
     * @param report The report to notify the test bed with.
     */
    public void notifyTestBed(String sessionId, TAR report){
        MessagingClientService client;
        String callback = (String)getSessionInfo(sessionId, SessionData.CALLBACK_URL);
        if (callback == null) {
            LOG.warn("Could not find callback URL for session [{}]", sessionId);
        } else {
            try {
                client = new MessagingClientService(new URI(callback).toURL());
            } catch (Exception e) {
                throw new IllegalStateException("Unable to call callback URL ["+callback+"] for session ["+sessionId+"]", e);
            }
            try {
                NotifyForMessageRequest request = new NotifyForMessageRequest();
                request.setSessionId(sessionId);
                request.setReport(report);
                client.getMessagingClientPort().notifyForMessage(request);
                LOG.info("Notified test bed for session [{}]", sessionId);
            } catch (Exception e) {
                NotifyForMessageRequest request = new NotifyForMessageRequest();
                request.setSessionId(sessionId);
                request.setReport(Utils.createReport(TestResultType.FAILURE));
                client.getMessagingClientPort().notifyForMessage(request);
                LOG.warn("Error while notifying test bed for session [{}]", sessionId, e);
                throw new IllegalStateException(e);
            }
        }
    }

    /**
     * Constants used to identify data maintained as part of a session's state.
     */
    static class SessionData {

        /** The URL on which the test bed is to be called back. */
        public static final String CALLBACK_URL = "callbackURL";

    }

}
