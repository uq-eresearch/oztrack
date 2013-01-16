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
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.resolver.DataSourceClassPathResolver;
import org.oztrack.data.access.impl.ProjectDaoImpl;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.User;
import org.oztrack.util.EmailUtils;
import org.springframework.stereotype.Service;

@Service
public class EmbargoNotifier implements Runnable {
    protected final Log logger = LogFactory.getLog(getClass());

    private SimpleDateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;

    public EmbargoNotifier() {
    }

    @Override
    public void run() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        ProjectDaoImpl projectDao = new ProjectDaoImpl();
        projectDao.setEntityManger(entityManager);
        notify(entityManager, projectDao, Calendar.MONTH, 2);
    }

    private void notify(EntityManager entityManager, ProjectDaoImpl projectDao, int field, int amount) {
        Calendar expiryCalendar = new GregorianCalendar();
        expiryCalendar.add(field, amount);
        Date expiryDate = DateUtils.truncate(expiryCalendar.getTime(), Calendar.DATE);
        List<Project> projects = projectDao.getProjectsWithExpiredEmbargo(expiryDate);

        for (Project project : projects) {
            logger.info(
                "Sending notification for project " + project.getId() + " " +
                "(embargo expires " + isoDateFormat.format(project.getEmbargoDate()) + ")."
            );
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();
            try {
                User user = project.getCreateUser();
                HtmlEmail email = EmailUtils.createHtmlEmail(
                    user,
                    "OzTrack project embargo ends " + isoDateFormat.format(project.getEmbargoDate()));

                DataSourceClassPathResolver imageResolver = new DataSourceClassPathResolver("/images");
                String oztrackLogoImgSrc = "cid:" + email.embed(imageResolver.resolve("oztrack-logo.png"), "oztrack-logo.png");

                StringBuilder htmlMsg = new StringBuilder();
                htmlMsg.append("<html>\n");
                htmlMsg.append("<body>\n");
                htmlMsg.append("<p><img src=\"" + oztrackLogoImgSrc + "\" /></p>\n");
                htmlMsg.append("<p>Dear " + user.getFirstName() + ",</p>\n");
                htmlMsg.append("<p>");
                htmlMsg.append("    This is an automated message from OzTrack, notifying you that your project,\n");
                htmlMsg.append("    <b>" + project.getTitle() + "</b>, will end its embargo period on ");
                htmlMsg.append("    " + isoDateFormat.format(project.getEmbargoDate()) + ".\n");
                htmlMsg.append("</p>\n");
                htmlMsg.append("<p>");
                htmlMsg.append("    Following this date, data in this project will be made publicly available via OzTrack. ");
                htmlMsg.append("</p>");
                htmlMsg.append("</body>\n");
                htmlMsg.append("</html>");
                email.setHtmlMsg(htmlMsg.toString());

                email.send();

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
