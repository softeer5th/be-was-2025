package http;

import java.util.concurrent.ConcurrentHashMap;

public class HttpSessionStorage {

	public static final String SESSION_ID = "SID";
	private static ConcurrentHashMap<String, HttpSession> sessions = new ConcurrentHashMap<>();

	public static void createSession(HttpSession session) {
		sessions.put(session.getId(), session);
	}

	public static HttpSession getSession(String id) {
		return sessions.get(id);
	}
}
