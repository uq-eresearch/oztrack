package org.oztrack.data.access;

import java.util.List;

import org.oztrack.data.model.Analysis;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.User;
import org.springframework.stereotype.Service;

@Service
public interface AnalysisDao {
    Analysis getAnalysisById(Long id);
    void save(Analysis analysis);
    Analysis update(Analysis analysis);
    List<Analysis> getSavedAnalyses(Project project);
    List<Analysis> getPreviousAnalyses(Project project, User createUser, String createSession);
}
