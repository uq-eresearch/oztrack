package org.oztrack.app;

import javax.servlet.http.HttpSession;

import org.oztrack.data.model.User;

public class AuthenticationManager {

    //private HttpSession session;

    // map key: session_id, user

    public AuthenticationManager() {
    }

    public void setUserSession(HttpSession session, User user) {
        session.setAttribute("currentUser", user);
    }

    public User getUserFromSession(HttpSession session) {
        return (User) session.getAttribute("currentUser");
    }


    /*
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


     public String getCurrentUsername() {
         User user = (User) session.getAttribute("user");
         return user.getUsername();
     }
     */
}