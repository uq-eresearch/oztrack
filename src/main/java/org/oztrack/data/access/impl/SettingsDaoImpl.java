package org.oztrack.data.access.impl;

import java.io.Serializable;

import org.oztrack.data.access.SettingsDao;
import org.oztrack.data.model.Settings;

import au.edu.uq.itee.maenad.dataaccess.jpa.EntityManagerSource;
import au.edu.uq.itee.maenad.dataaccess.jpa.JpaDao;

public class SettingsDaoImpl extends JpaDao<Settings> implements SettingsDao, Serializable {
    public SettingsDaoImpl(EntityManagerSource entityManagerSource) {
        super(entityManagerSource);
    }
    
    @Override
    public Settings getSettings() {
        return (Settings) entityManagerSource.getEntityManager()
            .createQuery("from org.oztrack.data.model.Settings")
            .getSingleResult();
    }
}
