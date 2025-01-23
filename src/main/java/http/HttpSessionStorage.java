package http;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The type Http session storage.
 */
public class HttpSessionStorage {

	/**
	 * The constant SESSION_ID.
	 */
	// TODO: 싱글톤 고민해보기
	public static final String SESSION_ID = "SID";
	private static ConcurrentHashMap<String, HttpSession> sessions = new ConcurrentHashMap<>();

	/**
	 * Instantiates a new Http session storage.
	 */
	public HttpSessionStorage() {
	}

	/**
	 * Save session.
	 *
	 * @param session the session
	 */
	public static void saveSession(HttpSession session) {
		sessions.put(session.getId(), session);
	}

	/**
	 * Gets session.
	 *
	 * @param id the id
	 * @return the session
	 */
	public static HttpSession getSession(String id) {
		return sessions.get(id);
	}

	/**
	 * Remove session.
	 *
	 * @param id the id
	 */
	public static void removeSession(String id) {
		sessions.remove(id);
	}
}
