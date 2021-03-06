package org.oztrack.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.oztrack.data.access.UserDao;
import org.oztrack.data.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.transaction.annotation.Transactional;

public class OzTrackAuthenticationProvider implements AuthenticationProvider {
    @Autowired
    private UserDao userDao;

    @Override
    @Transactional
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        User user = userDao.getByUsername((String) authentication.getPrincipal());
        String password = (String) authentication.getCredentials();
        if (
            (user == null) ||
            StringUtils.isBlank(password) ||
            StringUtils.isBlank(user.getPassword()) ||
            !BCrypt.checkpw(password, user.getPassword())
        ) {
            throw new BadCredentialsException("Invalid username or password.");
        }
        user.getLoginDates().add(new Date());
        userDao.save(user);
        return buildAuthentication(user);
    }

    public static Authentication buildAuthentication(User user) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        if ((user.getAdmin() != null) && user.getAdmin()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }
        return new UsernamePasswordAuthenticationToken(
            user.getUsername(),
            null,
            authorities
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.isAssignableFrom(UsernamePasswordAuthenticationToken.class);
    }
}