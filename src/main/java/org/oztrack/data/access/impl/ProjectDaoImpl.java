package org.oztrack.data.access.impl;

import java.io.File;
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
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProjectDaoImpl implements ProjectDao {
    @PersistenceContext
    private EntityManager em;
    
    @Override
    @Transactional(readOnly=true)
    public List<Project> getAll() {
        @SuppressWarnings("unchecked")
        List<Project> resultList = em.createQuery("from Project").getResultList();
        return resultList;
    }

    @Override
    @Transactional(readOnly=true)
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
    public void create(Project project, User currentUser) throws Exception {
        // create/update details
        project.setCreateDate(new java.util.Date());
        project.setCreateUser(currentUser);
        project.setDataSpaceAgent(currentUser);
        
        // set the current user to be an admin for this project
        ProjectUser adminProjectUser = new ProjectUser();
        adminProjectUser.setProject(project);
        adminProjectUser.setUser(currentUser);
        adminProjectUser.setRole(Role.ADMIN);
        
        // add this user to the project's list of users
        List <ProjectUser> projectProjectUsers = project.getProjectUsers();
        projectProjectUsers.add(adminProjectUser);
        project.setProjectUsers(projectProjectUsers);
        
        // save it all - project first
        save(project);
        
        project.setDataDirectoryPath("project-" + project.getId().toString());
        saveProjectImageFile(project);
        update(project);
    }
    
    @Override
    @Transactional
    public void saveProjectImageFile(Project project) throws Exception {
        MultipartFile file = project.getImageFile();
        if ((file == null) || project.getImageFile().getSize() == 0) {
            return;
        }
        project.setImageFilePath(file.getOriginalFilename());
        File saveFile = new File(project.getAbsoluteImageFilePath());
        saveFile.mkdirs();
        file.transferTo(saveFile);
    }
    
    @Override
    @Transactional
    public List<Project> getPublishedProjects() {
        @SuppressWarnings("unchecked")
        List<Project> resultList = em.createQuery("from Project where isglobal = true").getResultList();
        return resultList;
    }
}
