package org.oztrack.controller;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.oztrack.app.OzTrackConfiguration;
import org.oztrack.data.access.UserDao;
import org.oztrack.data.model.DataFile;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.ProjectUser;
import org.oztrack.data.model.User;
import org.oztrack.data.model.types.ProjectAccess;
import org.oztrack.data.model.types.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

@Service
public class OzTrackPermissionEvaluator implements PermissionEvaluator {
    @Autowired
    private OzTrackConfiguration configuration;

    @Autowired
    private UserDao userDao;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        User currentUser = getAuthenticatedUser(authentication);
        if ((currentUser != null) && (currentUser.getAdmin() != null) && currentUser.getAdmin()) {
            return true;
        }
        if (targetDomainObject == null) {
            return false;
        }
        if (targetDomainObject instanceof Project) {
            return hasProjectPermission(currentUser, (Project) targetDomainObject, permission);
        }
        if (targetDomainObject instanceof DataFile) {
            return hasDataFilePermission(currentUser, (DataFile) targetDomainObject, permission);
        }
        return false;
    }

    public boolean haveAuthenticatedUser(Authentication authentication) {
        return
            (authentication != null) &&
            authentication.isAuthenticated() &&
            authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER"));
    }

    public User getAuthenticatedUser(Authentication authentication) {
        return haveAuthenticatedUser(authentication) ? userDao.getByUsername((String) authentication.getPrincipal()) : null;
    }

    private boolean hasProjectPermission(User currentUser, Project project, Object permission) {
        if (permission.equals("read") && (project.getAccess() == ProjectAccess.OPEN)) {
            return true;
        }
        if (currentUser == null) {
            return false;
        }
        List<ProjectUser> projectUsers = project.getProjectUsers();
        for (ProjectUser projectUser : projectUsers) {
            if (!projectUser.getUser().equals(currentUser)) {
                continue;
            }
            if (permission.equals("read")) {
                if (
                    (projectUser.getRole() == Role.READER) ||
                    (projectUser.getRole() == Role.WRITER) ||
                    (projectUser.getRole() == Role.MANAGER)
                ) {
                    return true;
                }
            }
            else if (permission.equals("write")) {
                if (
                    (projectUser.getRole() == Role.WRITER) ||
                    (projectUser.getRole() == Role.MANAGER)
                ) {
                    return true;
                }
            }
            else if (permission.equals("manage")) {
                if (projectUser.getRole() == Role.MANAGER) {
                    return true;
                }
            }
            else if (permission.equals("delete")) {
                if (projectUser.getRole() == Role.MANAGER) {
                    if (project.getDataFiles().isEmpty()) {
                        return true;
                    }
                    Integer deleteRestrictedAfterDays = configuration.getProjectDeleteRestrictedAfterDays();
                    Date deleteRestrictedDate = DateUtils.addDays(project.getCreateDate(), deleteRestrictedAfterDays);
                    Date currentDate = new Date();
                    if (currentDate.before(deleteRestrictedDate)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean hasDataFilePermission(User currentUser, DataFile dataFile, Object permission) {
        // Only users with at least write access to a project can "see" data files
        if (hasProjectPermission(currentUser, dataFile.getProject(), "write")) {
            if (permission.equals("read")) {
                return true;
            }
            if (permission.equals("delete")) {
                Integer deleteRestrictedAfterDays = configuration.getDataFileDeleteRestrictedAfterDays();
                Date deleteRestrictedDate = DateUtils.addDays(dataFile.getCreateDate(), deleteRestrictedAfterDays);
                Date currentDate = new Date();
                if (currentDate.before(deleteRestrictedDate)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return false;
    }
}