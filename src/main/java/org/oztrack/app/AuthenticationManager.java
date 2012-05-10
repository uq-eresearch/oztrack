package org.oztrack.app;

import javax.servlet.http.HttpSession;

import org.oztrack.data.model.User;

public class AuthenticationManager {
    public AuthenticationManager() {
    }

    public void setUserSession(HttpSession session, User user) {
        session.setAttribute("currentUser", user);
    }

    public User getUserFromSession(HttpSession session) {
        return (User) session.getAttribute("currentUser");
    }
}