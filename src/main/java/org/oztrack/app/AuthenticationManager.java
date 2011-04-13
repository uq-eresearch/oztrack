package org.oztrack.app;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.oztrack.data.model.User;

public class AuthenticationManager {
	
	private HttpSession session;

	public AuthenticationManager() {
	}

	public void setSession(HttpSession session) {
		this.session = session;
	}
	
	public HttpSession getSession() {
		return session;
	}
	
	public User getCurrentUser() {
		return (User) session.getAttribute(Constants.CURRENT_USER);
	}

	public void setCurrentUser(User user) {
		session.setAttribute(Constants.CURRENT_USER, user);
	}
	
	/*
	public String getCurrentUsername() {
		User user = (User) session.getAttribute("user");
		return user.getUsername();
	}
	*/
}