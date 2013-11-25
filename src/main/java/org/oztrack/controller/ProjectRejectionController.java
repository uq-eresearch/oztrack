package org.oztrack.controller;

import java.util.Date;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.oztrack.data.access.PersonDao;
import org.oztrack.data.access.ProjectDao;
import org.oztrack.data.model.Person;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.ProjectContribution;
import org.oztrack.data.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ProjectRejectionController {
    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private PersonDao personDao;

    @Autowired
    private OzTrackPermissionEvaluator permissionEvaluator;

    @ModelAttribute
    public void populateModel(
        Model model,
        @PathVariable(value="id") Long projectId,
        @RequestParam(value="person") String personUuid
    ) {
        final Project project = projectDao.getProjectById(projectId);
        final Person person = personDao.getByUuid(UUID.fromString(personUuid));
        if ((project == null) || (person == null)) {
            throw new RuntimeException("Invalid project or person ID.");
        }

        final ProjectContribution contribution = (ProjectContribution) CollectionUtils.find(project.getProjectContributions(), new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                return ((ProjectContribution) object).getContributor().equals(person);
            }
        });
        if (contribution == null) {
            throw new RuntimeException("Not a contributor to this project.");
        }

        model.addAttribute("project", project);
        model.addAttribute("person", person);
        model.addAttribute("contribution", contribution);
    }

    @RequestMapping(value="/projects/{id}/reject", method=RequestMethod.GET)
    public String getConfirmationView() {
        return "project-reject";
    }

    @RequestMapping(value="/projects/{id}/reject", method=RequestMethod.POST)
    public String handleRequest(Authentication authentication, Model model) {
        Date currentDate = new Date();
        User currentUser = permissionEvaluator.getAuthenticatedUser(authentication);

        Project project = (Project) model.asMap().get("project");
        Person person = (Person) model.asMap().get("person");
        ProjectContribution contribution = (ProjectContribution) model.asMap().get("contribution");

        // Remove person's contribution to this project.
        project.getProjectContributions().remove(contribution);
        person.getProjectContributions().remove(contribution);
        for (int i = 0; i < project.getProjectContributions().size(); i++) {
            project.getProjectContributions().get(i).setOrdinal(i);
        }
        project.setUpdateDate(currentDate);
        project.setUpdateDateForOaiPmh(currentDate);
        project.setUpdateUser(currentUser);
        projectDao.update(project);

        // Remove person entity if no longer associated with a user or project.
        if ((person.getUser() == null) && person.getProjectContributions().isEmpty()) {
            personDao.delete(person);
        }

        return "redirect:/projects/" + project.getId();
    }
}