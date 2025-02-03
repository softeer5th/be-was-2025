package http;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class HttpSessionStorage {

	// TODO: 싱글톤 고민해보기
	public static final String SESSION_ID = "SID";
	private static ConcurrentHashMap<String, HttpSession> sessions = new ConcurrentHashMap<>();

	public HttpSessionStorage() {
	}

	public static void saveSession(HttpSession session) {
		sessions.put(session.getId(), session);
	}

	public static HttpSession getSession(String id) {
		return sessions.getOrDefault(id, new HttpSession(id, new HashMap<>()));
	}

	public static void removeSession(String id) {
		sessions.remove(id);
	}
}
