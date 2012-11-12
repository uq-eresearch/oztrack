package org.oztrack.data.access;

import org.oztrack.data.model.Analysis;
import org.springframework.stereotype.Service;

@Service
public interface AnalysisDao {
    Analysis getAnalysisById(Long id);
    void save(Analysis analysis);
}
