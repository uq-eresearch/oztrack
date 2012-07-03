package org.oztrack.controller;

import java.util.ArrayList;
import java.util.List;

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

public class OzTrackAuthenticationProvider implements AuthenticationProvider {
    @Autowired
    private UserDao userDao;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        User user = userDao.getByUsername((String) authentication.getPrincipal());
        if ((user == null) || !BCrypt.checkpw((String) authentication.getCredentials(), user.getPassword())) {
            throw new BadCredentialsException("Invalid username or password.");
        }
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