package org.oztrack.data.access.impl;

import au.edu.uq.itee.maenad.dataaccess.jpa.EntityManagerSource;
import au.edu.uq.itee.maenad.dataaccess.jpa.JpaDao;
import org.oztrack.data.access.ProjectDao;
import org.oztrack.data.model.Project;

import javax.persistence.NoResultException;
import javax.persistence.Query;
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
