package org.oztrack.util;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;

/**
 * Normal embargo is a maximum of 3 years, but can be extended up to 5 years.
 *
 * Extensions are only allowed when a project is within 2 months of its embargo date:
 * this prevents an overly long embargo period being selected when creating a project,
 * but, if necessary, allows it to be extended as the embargo deadline approaches.
 */
public class EmbargoUtils {
    public static final int maxEmbargoYearsNorm = 3;
    public static final int maxEmbargoYearsExtn = 5;
    public static final int embargoNotificationMonths = 2;

    public static class EmbargoInfo {
        private Date maxEmbargoDateNorm;
        private Date maxEmbargoDateExtn;
        private Date embargoNotificationDate;
        private int maxEmbargoYears;
        private Date maxEmbargoDate;

        private EmbargoInfo() {
        }

        public Date getMaxEmbargoDateNorm() {
            return maxEmbargoDateNorm;
        }

        public Date getMaxEmbargoDateExtn() {
            return maxEmbargoDateExtn;
        }

        public Date getEmbargoNotificationDate() {
            return embargoNotificationDate;
        }

        public int getMaxEmbargoYears() {
            return maxEmbargoYears;
        }

        public Date getMaxEmbargoDate() {
            return maxEmbargoDate;
        }
    }

    public static EmbargoInfo getEmbargoInfo(Date createDate) {
        final Date truncatedCurrentDate = DateUtils.truncate(new Date(), Calendar.DATE);
        final Date truncatedCreateDate = DateUtils.truncate(createDate, Calendar.DATE);
        EmbargoInfo embargoInfo = new EmbargoInfo();
        embargoInfo.maxEmbargoDateNorm = DateUtils.addYears(truncatedCreateDate, maxEmbargoYearsNorm);
        embargoInfo.maxEmbargoDateExtn = DateUtils.addYears(truncatedCreateDate, maxEmbargoYearsExtn);
        embargoInfo.embargoNotificationDate = DateUtils.addMonths(embargoInfo.maxEmbargoDateNorm, -1 * embargoNotificationMonths);
        boolean canExtend = !truncatedCurrentDate.before(embargoInfo.embargoNotificationDate);
        embargoInfo.maxEmbargoYears = canExtend ? maxEmbargoYearsExtn : maxEmbargoYearsNorm;
        embargoInfo.maxEmbargoDate = canExtend ? embargoInfo.maxEmbargoDateExtn : embargoInfo.maxEmbargoDateNorm;
        return embargoInfo;
    }
}
