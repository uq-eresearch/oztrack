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
import org.springframework.stereotype.Service;

@Service
public class EmbargoUpdater implements Runnable {
    protected final Log logger = LogFactory.getLog(getClass());

    private SimpleDateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd");

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
        sendNotifications(entityManager, projectDao, currentDate, Calendar.MONTH, 2);
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

                EmailBuilder emailBuilder = new EmailBuilder();
                emailBuilder.to(project.getCreateUser());
                emailBuilder.subject("OzTrack project embargo ended");
                StringBuilder htmlMsgContent = new StringBuilder();
                htmlMsgContent.append("<p>");
                htmlMsgContent.append("    Please note that your OzTrack project,\n");
                htmlMsgContent.append("    <b>" + project.getTitle() + "</b>, has ended its embargo period.\n");
                htmlMsgContent.append("</p>\n");
                htmlMsgContent.append("<p>");
                htmlMsgContent.append("    Data in this project are now publicly available via OzTrack.");
                htmlMsgContent.append("</p>");
                emailBuilder.htmlMsgContent(htmlMsgContent.toString());
                emailBuilder.build().send();
            }
            catch (Exception e) {
                logger.error("Exception in embargo updater", e);
                try {transaction.rollback();} catch (Exception e2) {}
            }
        }
    }

    // Note: successive calls to this method should be performed with earlier expiry dates first.
    // See code below that skips notification for a project if one already sent for an equal or shorter period.
    private void sendNotifications(EntityManager entityManager, ProjectDaoImpl projectDao, Date currentDate, int field, int amount) {
        Calendar expiryCalendar = new GregorianCalendar();
        expiryCalendar.setTime(currentDate);
        expiryCalendar.add(field, amount);
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
                EmailBuilder emailBuilder = new EmailBuilder();
                emailBuilder.to(project.getCreateUser());
                emailBuilder.subject("OzTrack project embargo ending");
                StringBuilder htmlMsgContent = new StringBuilder();
                htmlMsgContent.append("<p>");
                htmlMsgContent.append("    Please note that your OzTrack project,\n");
                htmlMsgContent.append("    <b>" + project.getTitle() + "</b>, will end its embargo period on ");
                htmlMsgContent.append("    " + isoDateFormat.format(project.getEmbargoDate()) + ".\n");
                htmlMsgContent.append("</p>\n");
                htmlMsgContent.append("<p>");
                htmlMsgContent.append("    Starting from this date, data in this project will be made publicly available via OzTrack.");
                htmlMsgContent.append("</p>");
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