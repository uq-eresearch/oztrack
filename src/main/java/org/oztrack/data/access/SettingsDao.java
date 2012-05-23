package org.oztrack.data.access;

import org.oztrack.data.model.Settings;

import au.edu.uq.itee.maenad.dataaccess.Dao;

public interface SettingsDao extends Dao<Settings> {
    Settings getSettings();
}
