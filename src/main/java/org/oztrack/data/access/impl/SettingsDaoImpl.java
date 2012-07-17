package org.oztrack.data.access.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.oztrack.data.access.SettingsDao;
import org.oztrack.data.model.Settings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SettingsDaoImpl implements SettingsDao {
    @PersistenceContext
    private EntityManager em;    
    
    @Override
    @Transactional(readOnly=true)
    public Settings getSettings() {
        return (Settings) em
            .createQuery("from org.oztrack.data.model.Settings")
            .getSingleResult();
    }
    
    @Override
    @Transactional
    public Settings update(Settings settings) {
        return em.merge(settings);
    }
}
