package org.oztrack.data.access;

import org.oztrack.data.model.Settings;
import org.springframework.stereotype.Service;

@Service
public interface SettingsDao {
    Settings getSettings();
    Settings update(Settings settings);
}
