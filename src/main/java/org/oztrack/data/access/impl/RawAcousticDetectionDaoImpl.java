package org.oztrack.data.access.impl;

import au.edu.uq.itee.maenad.dataaccess.jpa.EntityManagerSource;
import au.edu.uq.itee.maenad.dataaccess.jpa.JpaDao;
import org.oztrack.data.access.RawAcousticDetectionDao;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.RawAcousticDetection;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: uqpnewm5
 * Date: 13/04/11
 * Time: 1:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class RawAcousticDetectionDaoImpl extends JpaDao<RawAcousticDetection> implements RawAcousticDetectionDao, Serializable {

	public RawAcousticDetectionDaoImpl(EntityManagerSource entityManagerSource) {
        super(entityManagerSource);
    }


    public int getNumberDetections() {
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT count(*) FROM RawAcousticDetection");
        try {
            return Integer.parseInt(query.getSingleResult().toString());
        } catch (NoResultException ex) {
            return Integer.parseInt(null);
        }
    }

//    java.util.Date getMinDetectionDate();
//    java.util.Date getMaxDetectionDate();
//    int [] getAllAnimalIds();
//    int [] getAllReceiverIds();

}
