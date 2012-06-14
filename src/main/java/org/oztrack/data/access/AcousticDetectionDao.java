package org.oztrack.data.access;

import org.oztrack.data.model.AcousticDetection;
import org.springframework.stereotype.Service;

@Service
public interface AcousticDetectionDao {
    Page<AcousticDetection> getPage(int offset, int limit);
    int getTotalCount();
}
