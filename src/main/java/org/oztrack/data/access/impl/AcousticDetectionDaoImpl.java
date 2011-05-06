package org.oztrack.data.access.impl;

import au.edu.uq.itee.maenad.dataaccess.jpa.EntityManagerSource;
import au.edu.uq.itee.maenad.dataaccess.jpa.JpaDao;
import org.oztrack.data.access.AcousticDetectionDao;
import org.oztrack.data.model.AcousticDetection;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.io.Serializable;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: uqpnewm5
 * Date: 6/05/11
 * Time: 1:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class AcousticDetectionDaoImpl extends JpaDao<AcousticDetection> implements AcousticDetectionDao, Serializable {

    public AcousticDetectionDaoImpl(EntityManagerSource entityManagerSource) {
        super(entityManagerSource);
    }



}


