package org.oztrack.data.access.impl;

import au.edu.uq.itee.maenad.dataaccess.jpa.EntityManagerSource;
import au.edu.uq.itee.maenad.dataaccess.jpa.JpaDao;
import org.oztrack.data.access.ReceiverDeploymentDao;
import org.oztrack.data.access.SightingDao;
import org.oztrack.data.model.ReceiverDeployment;
import org.oztrack.data.model.Sighting;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: uqpnewm5
 * Date: 8/06/11
 * Time: 9:45 AM
 */
public class SightingDaoImpl extends JpaDao<Sighting> implements SightingDao, Serializable {

    public SightingDaoImpl(EntityManagerSource entityManagerSource) {
        super(entityManagerSource);
    }

}
