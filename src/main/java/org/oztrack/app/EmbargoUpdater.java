package org.oztrack.app;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceUnit;

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
        logger.info("Running embargo updater.");

        EntityManager entityManager = entityManagerFactory.createEntityManager();
        ProjectDaoImpl projectDao = new ProjectDaoImpl();
        projectDao.setEntityManger(entityManager);

        List<Project> projects = projectDao.getProjectsWithExpiredEmbargo(new Date());
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
}
