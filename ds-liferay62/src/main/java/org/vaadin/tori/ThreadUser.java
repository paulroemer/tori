package org.vaadin.tori;

/**
 * Created by wolfgang on 19/07/16.
 */
public class ThreadUser {
	private static ThreadLocal<Long> USER = new ThreadLocal<>();
	public static void set(Long user) {
		USER.set(user);
	}
	public static void remove() {
		USER.remove();
	}

	public static String get() {
 		Long userId = USER.get();
		if (userId != null) {
			return userId.toString();
		} else {
			return "";
		}
	}
	public static Long getId() {
		return USER.get();
	}
}
