package org.oztrack.controller;

import java.io.Serializable;
import java.util.List;

import org.oztrack.data.access.UserDao;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.ProjectUser;
import org.oztrack.data.model.User;
import org.oztrack.data.model.types.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class OzTrackPermissionEvaluator implements PermissionEvaluator {
    @Autowired
    private UserDao userDao;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (
            (authentication == null) ||
            !authentication.isAuthenticated() ||
            !authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER"))
        ) {
            return false;
        }
        User currentUser = userDao.getByUsername((String) authentication.getPrincipal());
        if ((currentUser.getAdmin() != null) && currentUser.getAdmin()) {
            return true;
        }
        if (targetDomainObject instanceof Project) {
            return hasProjectPermission(currentUser, (Project) targetDomainObject, permission);
        }
        return false;
    }

    private boolean hasProjectPermission(User currentUser, Project project, Object permission) {
        List<ProjectUser> projectUsers = project.getProjectUsers();
        for (ProjectUser projectUser : projectUsers) {
            if (!projectUser.getUser().equals(currentUser)) {
                continue;
            }
            if (permission.equals("read")) {
                return
                    (projectUser.getRole() == Role.READER) ||
                    (projectUser.getRole() == Role.WRITER) ||
                    (projectUser.getRole() == Role.MANAGER);
            }
            else if (permission.equals("write")) {
                return
                    (projectUser.getRole() == Role.WRITER) ||
                    (projectUser.getRole() == Role.MANAGER);
            }
            else if (permission.equals("manage")) {
                return (projectUser.getRole() == Role.MANAGER);
            }
        }
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return false;
    }
}