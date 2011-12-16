package org.oztrack.data.access.impl;

import au.edu.uq.itee.maenad.dataaccess.jpa.EntityManagerSource;
import au.edu.uq.itee.maenad.dataaccess.jpa.JpaDao;
import org.oztrack.data.access.ProjectDao;
import org.oztrack.data.model.PositionFix;
import org.oztrack.data.model.Project;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.swing.text.Position;
import java.io.Serializable;
import java.util.List;

/**
 * Author: alabri
 * Date: 9/03/11
 * Time: 11:08 AM
 */
public class ProjectDaoImpl extends JpaDao<Project> implements ProjectDao, Serializable {
    
	public ProjectDaoImpl(EntityManagerSource entityManagerSource) {
        super(entityManagerSource);
    }

    @Override
    public Project getProjectById(Long id) {
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM Project o WHERE o.id = :id");
        query.setParameter("id", id);
        try {
            return (Project) query.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }
    
/*
    public List<PositionFix> getAllPositionFixes(Long projectId) {

        Query query = entityManagerSource.getEntityManager().createQuery("SELECT o from PositionFix o, datafile d where o.datafile_id=d.id and d.project_id = :projectId");
        query.setParameter("projectId", projectId);
        try {
            return (List <PositionFix>) query.getResultList();
        } catch (NoResultException ex) {
            return null;
        }
    }
 */

    @Override
    public void save(Project object) {
        object.setUpdateDate(new java.util.Date());
        super.save(object);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public Project update(Project object) {
        object.setUpdateDate(new java.util.Date());
        return super.update(object);    //To change body of overridden methods use File | Settings | File Templates.
    }

    /*    public List<Project> getProjectListByUserId(Long id) {
            Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM Project o " +
                                                                             "WHERE o.id in (select pu.project_id from ProjectUser pu " +
                                                                             "where pu.user_id = :id)");
            query.setParameter("id", id);
            try {
                return (List<Project>) query.getResultList();
            } catch (NoResultException ex) {
                return null;
            }
        }
    */
}
