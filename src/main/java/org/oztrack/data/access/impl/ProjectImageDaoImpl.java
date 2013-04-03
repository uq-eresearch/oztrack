package org.oztrack.data.access.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.io.FileUtils;
import org.oztrack.data.access.ProjectImageDao;
import org.oztrack.data.model.ProjectImage;
import org.oztrack.data.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjectImageDaoImpl implements ProjectImageDao {
    private EntityManager em;

    @PersistenceContext
    public void setEntityManger(EntityManager em) {
        this.em = em;
    }

    @Override
    public ProjectImage getProjectImageById(Long id) {
        @SuppressWarnings("unchecked")
        List<ProjectImage> resultList = em
            .createQuery("from org.oztrack.data.model.ProjectImage where id = :id")
            .setParameter("id", id)
            .getResultList();
        return resultList.isEmpty() ? null : resultList.get(0);
    }

    @Override
    @Transactional
    public void create(ProjectImage projectImage, User currentUser) throws Exception {
        projectImage.setCreateDate(new java.util.Date());
        projectImage.setCreateUser(currentUser);
        save(projectImage);

        projectImage.setDataDirectoryPath("image-" + projectImage.getId().toString());
        projectImage.setFilePath("file");
        projectImage.setThumbnailPath("thumbnail");
        update(projectImage);
    }

    @Override
    @Transactional
    public void save(ProjectImage projectImage) {
        em.persist(projectImage);
    }

    @Override
    @Transactional
    public ProjectImage update(ProjectImage projectImage) {
        return em.merge(projectImage);
    }

    @Override
    @Transactional
    public void delete(ProjectImage projectImage) throws IOException {
        FileUtils.deleteDirectory(new File(projectImage.getAbsoluteDataDirectoryPath()));
        em.remove(projectImage);
    }
}