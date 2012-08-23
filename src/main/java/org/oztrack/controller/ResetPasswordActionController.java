package org.oztrack.controller;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.data.access.UserDao;
import org.oztrack.data.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ResetPasswordActionController {
    protected final Log logger = LogFactory.getLog(getClass());

    @Autowired
    private UserDao userDao;

    @RequestMapping(value="/reset-password/{token}", method=RequestMethod.GET)
    public String getActionView(
        Model model,
        @PathVariable(value="token") String token
    ) {
        User user = userDao.getByPasswordResetToken(token);
        if (user == null) {
            model.addAttribute("errorMessage", "Invalid or previously used token supplied.");
            return "reset-password-action";
        }
        if (user.getPasswordResetExpiresAt().before(new Date())) {
            model.addAttribute("errorMessage", "Password reset link has expired.");
            return "reset-password-action";
        }
        model.addAttribute("user", user);
        return "reset-password-action";
    }

    @RequestMapping(value="/reset-password/{token}", method=RequestMethod.POST)
    public String processPost(
        Model model,
        @PathVariable(value="token") String token,
        @RequestParam(value="password", required=false) String password
    ) {
        User user = userDao.getByPasswordResetToken(token);
        if (user == null) {
            model.addAttribute("errorMessage", "Invalid or previously used token supplied.");
            return "reset-password-action";
        }
        if (user.getPasswordResetExpiresAt().before(new Date())) {
            model.addAttribute("errorMessage", "Password reset link has expired.");
            return "reset-password-action";
        }
        if (StringUtils.isBlank(password)) {
            model.addAttribute("errorMessage", "No password was entered.");
            return "reset-password-action";
        }
        user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
        user.setPasswordResetToken(null);
        user.setPasswordResetExpiresAt(null);
        userDao.update(user);
        SecurityContextHolder.getContext().setAuthentication(OzTrackAuthenticationProvider.buildAuthentication(user));
        return "redirect:/";
    }
}
