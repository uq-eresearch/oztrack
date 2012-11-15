package org.oztrack.data.access;

import java.util.List;

import org.oztrack.data.model.Analysis;
import org.oztrack.data.model.User;
import org.springframework.stereotype.Service;

@Service
public interface AnalysisDao {
    Analysis getAnalysisById(Long id);
    void save(Analysis analysis);
    List<Analysis> getPreviousAnalyses(User createUser, String createSession);
}
