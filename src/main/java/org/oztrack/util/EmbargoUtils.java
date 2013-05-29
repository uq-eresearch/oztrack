package org.oztrack.util;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;

public class EmbargoUtils {
    public static final int maxEmbargoYears = 3;
    public static final int embargoNotificationMonths = 2;

    public static class EmbargoInfo {
        private int maxEmbargoYears;
        private Date maxEmbargoDate;
        private Date embargoNotificationDate;
        private Date maxIncrementalEmbargoDate;

        private EmbargoInfo() {
        }

        public int getMaxEmbargoYears() {
            return maxEmbargoYears;
        }

        public Date getMaxEmbargoDate() {
            return maxEmbargoDate;
        }

        public Date getMaxIncrementalEmbargoDate() {
            return maxIncrementalEmbargoDate;
        }

        public Date getEmbargoNotificationDate() {
            return embargoNotificationDate;
        }
    }

    public static EmbargoInfo getEmbargoInfo(Date createDate, Date prevEmbargoDate) {
        final Date truncatedCurrentDate = DateUtils.truncate(new Date(), Calendar.DATE);
        final Date truncatedCreateDate = DateUtils.truncate(createDate, Calendar.DATE);

        EmbargoInfo embargoInfo = new EmbargoInfo();
        embargoInfo.maxEmbargoYears = maxEmbargoYears;
        embargoInfo.maxEmbargoDate = DateUtils.addYears(truncatedCreateDate, maxEmbargoYears);

        // Send notification N months before embargo period ends.
        if (prevEmbargoDate != null) {
            embargoInfo.embargoNotificationDate = DateUtils.addMonths(prevEmbargoDate, -1 * embargoNotificationMonths);
        }
        else {
            embargoInfo.embargoNotificationDate = null;
        }

        // At any time, a user can extend a project's embargo period by 1 year from the current date.
        // As a special case, if the user has received a notification about an embargo period ending,
        // then they can extend the embargo period by 1 year from the current embargo end date. This
        // allows extension of the embargo period by slightly more than 1 year (an extra N months,
        // depending on the EmbargoUtils.embargoNotificationMonths setting).
        if ((embargoInfo.embargoNotificationDate != null) && !truncatedCurrentDate.before(embargoInfo.embargoNotificationDate)) {
            embargoInfo.maxIncrementalEmbargoDate = DateUtils.addYears(prevEmbargoDate, 1);
        }
        else {
            embargoInfo.maxIncrementalEmbargoDate = DateUtils.addYears(truncatedCurrentDate, 1);
        }

        // Limit incremental embargo date to at least be embargo date.
        if ((prevEmbargoDate != null) && embargoInfo.maxIncrementalEmbargoDate.before(prevEmbargoDate)) {
            embargoInfo.maxIncrementalEmbargoDate = prevEmbargoDate;
        }

        // Limit incremental embargo date to system-wide maximum.
        if (embargoInfo.maxIncrementalEmbargoDate.after(embargoInfo.maxEmbargoDate)) {
            embargoInfo.maxIncrementalEmbargoDate = embargoInfo.maxEmbargoDate;
        }

        return embargoInfo;
    }
}
