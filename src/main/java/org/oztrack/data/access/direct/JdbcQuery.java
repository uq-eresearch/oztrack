package org.oztrack.data.access.direct;

import java.util.List;

import org.oztrack.data.model.AcousticDetection;
import org.oztrack.data.model.PositionFix;
import org.oztrack.data.model.SearchQuery;

/**
 * Created by IntelliJ IDEA.
 * User: uqpnewm5
 * Date: 19/07/11
 * Time: 9:25 AM
 */
public interface JdbcQuery {

    public List<PositionFix> queryProjectPositionFixes(SearchQuery searchQuery);
    public List<AcousticDetection> queryAcousticDetections(String sql);
    public List<AcousticDetection> queryAcousticDetections2(SearchQuery searchQuery);

}
