package org.oztrack.data.access.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.oztrack.data.access.ProjectDao;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.ProjectUser;
import org.oztrack.data.model.User;
import org.oztrack.data.model.types.Role;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjectDaoImpl implements ProjectDao {
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Project> getAll() {
        @SuppressWarnings("unchecked")
        List<Project> resultList = em.createQuery("from Project").getResultList();
        return resultList;
    }

    @Override
    public Project getProjectById(Long id) {
        Query query = em.createQuery("SELECT o FROM Project o WHERE o.id = :id");
        query.setParameter("id", id);
        try {
            return (Project) query.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    @Override
    @Transactional
    public void save(Project object) {
        object.setUpdateDate(new java.util.Date());
        em.persist(object);
    }

    @Override
    @Transactional
    public Project update(Project object) {
        object.setUpdateDate(new java.util.Date());
        return em.merge(object);
    }

    @Override
    @Transactional
    public void delete(Project project) {
        em.remove(project);
    }

    @Override
    @Transactional
    public void create(Project project, User currentUser) throws Exception {
        // create/update details
        project.setCreateDate(new java.util.Date());
        project.setCreateUser(currentUser);
        project.setDataSpaceAgent(currentUser);

        // set the current user to be an admin for this project
        ProjectUser adminProjectUser = new ProjectUser();
        adminProjectUser.setProject(project);
        adminProjectUser.setUser(currentUser);
        adminProjectUser.setRole(Role.MANAGER);

        // add this user to the project's list of users
        List <ProjectUser> projectProjectUsers = project.getProjectUsers();
        projectProjectUsers.add(adminProjectUser);
        project.setProjectUsers(projectProjectUsers);

        // save it all - project first
        save(project);

        project.setDataDirectoryPath("project-" + project.getId().toString());
        update(project);
    }

    @Override
    public List<Project> getProjectsByPublished(boolean published) {
        @SuppressWarnings("unchecked")
        List<Project> resultList = em
            .createQuery("from Project where isglobal = :published order by createDate")
            .setParameter("published", published)
            .getResultList();
        return resultList;
    }

    @Override
    public List<ProjectUser> getProjectUsersWithRole(Project project, Role role) {
        @SuppressWarnings("unchecked")
        List<ProjectUser> resultList = em
            .createQuery(
                "from org.oztrack.data.model.ProjectUser\n" +
                "where project = :project and role = :role\n" +
                "order by user.lastName, user.firstName")
            .setParameter("project", project)
            .setParameter("role", role)
            .getResultList();
        return resultList;
    }
}
