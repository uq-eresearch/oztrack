package org.oztrack.app;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceUnit;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.data.access.impl.ProjectDaoImpl;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.types.ProjectAccess;
import org.oztrack.util.EmailBuilder;
import org.oztrack.util.EmailBuilderFactory;
import org.oztrack.util.EmbargoUtils;
import org.oztrack.util.EmbargoUtils.EmbargoInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmbargoUpdater implements Runnable {
    protected final Log logger = LogFactory.getLog(getClass());

    private final SimpleDateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    private OzTrackConfiguration configuration;

    @Autowired
    private EmailBuilderFactory emailBuilderFactory;

    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;

    public EmbargoUpdater() {
    }

    @Override
    public void run() {
        logger.debug("Running embargo updater.");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        ProjectDaoImpl projectDao = new ProjectDaoImpl();
        projectDao.setEntityManger(entityManager);
        Date currentDate = new Date();
        endEmbargo(entityManager, projectDao, currentDate);
        sendNotifications(entityManager, projectDao, currentDate);
    }

    private void endEmbargo(EntityManager entityManager, ProjectDaoImpl projectDao, Date currentDate) {
        List<Project> projects = projectDao.getProjectsWithExpiredEmbargo(currentDate);
        for (Project project : projects) {
            logger.info(
                "Making project " + project.getId() + " open access " +
                "(embargo expired " + isoDateFormat.format(project.getEmbargoDate()) + ")."
            );
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();
            try {
                project.setAccess(ProjectAccess.OPEN);
                projectDao.update(project);
                transaction.commit();

                EmailBuilder emailBuilder = emailBuilderFactory.getObject();
                emailBuilder.to(project.getCreateUser());
                emailBuilder.subject("OzTrack project embargo ended");
                String projectLink = configuration.getBaseUrl() + "/projects/" + project.getId();
                StringBuilder htmlMsgContent = new StringBuilder();
                htmlMsgContent.append("<p>\n");
                htmlMsgContent.append("    Please note that your OzTrack project,\n");
                htmlMsgContent.append("    <i>" + project.getTitle() + "</i>,\n");
                htmlMsgContent.append("    has <b>ended its embargo period</b>.\n");
                htmlMsgContent.append("</p>\n");
                htmlMsgContent.append("<p>\n");
                htmlMsgContent.append("    Data in this project are now publicly available in OzTrack.\n");
                htmlMsgContent.append("</p>");
                htmlMsgContent.append("<p>\n");
                htmlMsgContent.append("    To view your project, click here:\n");
                htmlMsgContent.append("    <a href=\"" + projectLink + "\">" + projectLink + "</a>\n");
                htmlMsgContent.append("</p>\n");
                emailBuilder.htmlMsgContent(htmlMsgContent.toString());
                emailBuilder.build().send();
            }
            catch (Exception e) {
                logger.error("Exception in embargo updater", e);
                try {transaction.rollback();} catch (Exception e2) {}
            }
        }
    }

    private void sendNotifications(EntityManager entityManager, ProjectDaoImpl projectDao, Date currentDate) {
        Calendar expiryCalendar = new GregorianCalendar();
        expiryCalendar.setTime(currentDate);
        expiryCalendar.add(Calendar.MONTH, EmbargoUtils.embargoNotificationMonths);
        Date expiryDate = DateUtils.truncate(expiryCalendar.getTime(), Calendar.DATE);
        List<Project> projects = projectDao.getProjectsWithExpiredEmbargo(expiryDate);

        for (Project project : projects) {
            // Don't send notifications for projects already at end of embargo period.
            // These should be picked up by the endEmargo updater, which sends its own notification.
            if (!currentDate.before(project.getEmbargoDate())) {
                continue;
            }
            // If notification has been sent for an earlier or equal expiry date, e.g. a one-week
            // notification has been sent and we're preparing to send two-month notifications here,
            // then skip this project. It doesn't make sense to send both if we are already in the
            // shorter period before expiry due to the scheduler not being run for a while - or to
            // send duplicate notifications for the same date.
            if ((project.getEmbargoNotificationDate() != null) && !expiryDate.before(project.getEmbargoNotificationDate())) {
                continue;
            }
            logger.info(
                "Sending notification for project " + project.getId() + " " +
                "(embargo expires " + isoDateFormat.format(project.getEmbargoDate()) + ")."
            );
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();
            try {
                EmailBuilder emailBuilder = emailBuilderFactory.getObject();
                emailBuilder.to(project.getCreateUser());
                emailBuilder.subject("OzTrack project embargo ending");
                String projectLink = configuration.getBaseUrl() + "/projects/" + project.getId();
                String projectEditLink = projectLink + "/edit";

                StringBuilder htmlMsgContent = new StringBuilder();
                htmlMsgContent.append("<p>\n");
                htmlMsgContent.append("    Please note that your OzTrack project,\n");
                htmlMsgContent.append("    <i>" + project.getTitle() + "</i>,\n");
                htmlMsgContent.append("    will <b>end its embargo period</b> on " + isoDateFormat.format(project.getEmbargoDate()) + ".\n");
                htmlMsgContent.append("</p>\n");
                htmlMsgContent.append("<p>\n");
                htmlMsgContent.append("    Starting from this date, data in the project will be made publicly available in OzTrack.\n");
                htmlMsgContent.append("</p>");
                EmbargoInfo embargoInfo = EmbargoUtils.getEmbargoInfo(project.getCreateDate(), project.getEmbargoDate());
                if (project.getEmbargoDate().before(embargoInfo.getMaxEmbargoDate())) {
                    htmlMsgContent.append("<p style=\"color: #666;\">\n");
                    htmlMsgContent.append("    <b>Extending the embargo period</b>\n");
                    htmlMsgContent.append("</p>\n");
                    if (project.getCreateDate().before(configuration.getNonIncrementalEmbargoDisableDate())) {
                        htmlMsgContent.append("<p>\n");
                        htmlMsgContent.append("    If necessary, you can extend the embargo period up to \n");
                        htmlMsgContent.append("    " + isoDateFormat.format(embargoInfo.getMaxEmbargoDate()) + ".\n");
                        htmlMsgContent.append("</p>\n");
                    }
                    else if (embargoInfo.getMaxIncrementalEmbargoDate().before(embargoInfo.getMaxEmbargoDate())) {
                        htmlMsgContent.append("<p>\n");
                        htmlMsgContent.append("    If necessary, you can extend the embargo period by another year \n");
                        htmlMsgContent.append("    to " + isoDateFormat.format(embargoInfo.getMaxIncrementalEmbargoDate()) + ".\n");
                        htmlMsgContent.append("</p>\n");
                        htmlMsgContent.append("<p>\n");
                        htmlMsgContent.append("    Project embargoes can be renewed annually up to a maximum of 3 years.\n");
                        htmlMsgContent.append("</p>\n");
                    }
                    else {
                        htmlMsgContent.append("<p>\n");
                        htmlMsgContent.append("    If necessary, you can extend the embargo period up to \n");
                        htmlMsgContent.append("    " + isoDateFormat.format(embargoInfo.getMaxEmbargoDate()) + ".\n");
                        htmlMsgContent.append("</p>\n");
                        htmlMsgContent.append("<p>\n");
                        htmlMsgContent.append("    This is the final renewal permitted by OzTrack, taking the embargo period up to 3 years.\n");
                        htmlMsgContent.append("</p>\n");
                    }
                }
                else {
                    htmlMsgContent.append("<p>\n");
                    htmlMsgContent.append("    This embargo period has been the maximum permitted by OzTrack (3 years),");
                    htmlMsgContent.append("    so cannot be renewed.\n");
                    htmlMsgContent.append("</p>\n");
                }
                htmlMsgContent.append("<p>\n");
                htmlMsgContent.append("    To view your project, click here:\n");
                htmlMsgContent.append("    <a href=\"" + projectLink + "\">" + projectLink + "</a>\n");
                htmlMsgContent.append("</p>\n");
                htmlMsgContent.append("<p>\n");
                htmlMsgContent.append("    To update your project, click here:\n");
                htmlMsgContent.append("    <a href=\"" + projectEditLink + "\">" + projectEditLink + "</a>\n");
                htmlMsgContent.append("</p>\n");
                emailBuilder.htmlMsgContent(htmlMsgContent.toString());
                emailBuilder.build().send();

                project.setEmbargoNotificationDate(expiryDate);
                projectDao.update(project);
                transaction.commit();
            }
            catch (Exception e) {
                logger.error("Exception in embargo notifier", e);
                try {transaction.rollback();} catch (Exception e2) {}
            }
        }
    }
}