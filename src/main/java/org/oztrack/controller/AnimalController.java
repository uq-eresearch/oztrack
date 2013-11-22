package org.oztrack.controller;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.oztrack.data.access.AnimalDao;
import org.oztrack.data.access.Page;
import org.oztrack.data.access.PositionFixDao;
import org.oztrack.data.model.Animal;
import org.oztrack.data.model.PositionFix;
import org.oztrack.data.model.SearchQuery;
import org.oztrack.validator.AnimalFormValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AnimalController {
    @Autowired
    private AnimalDao animalDao;

    @Autowired
    private PositionFixDao positionFixDao;

    @InitBinder("animal")
    public void initAnimalBinder(WebDataBinder binder) {
        binder.setAllowedFields(
            "projectAnimalId",
            "animalName",
            "animalDescription",
            "colour"
        );
    }

    @ModelAttribute("animal")
    public Animal getAnimal(@PathVariable(value="id") Long animalId) throws Exception {
        return animalDao.getAnimalById(animalId);
    }

    @RequestMapping(value="/projects/{projectId}/animals/{id}", method=RequestMethod.GET)
    @PreAuthorize("hasPermission(#animal.project, 'read')")
    public String getView(Model model, @ModelAttribute("animal") Animal animal) {
        model.addAttribute("project", animal.getProject());
        SearchQuery searchQuery = new SearchQuery();
        searchQuery.setProject(animal.getProject());
        searchQuery.setAnimalIds(Arrays.asList(animal.getId()));
        searchQuery.setSortField("Detection Time");
        Page<PositionFix> positionFixPage = positionFixDao.getPage(searchQuery, 0, 15);
        model.addAttribute("searchQuery", searchQuery);
        model.addAttribute("positionFixPage", positionFixPage);
        return "animal";
    }

    @RequestMapping(value="/projects/{projectId}/animals/{id}/edit", method=RequestMethod.GET)
    @PreAuthorize("hasPermission(#animal.project, 'write')")
    public String getEditView(Model model, @ModelAttribute("animal") Animal animal) {
        model.addAttribute("project", animal.getProject());
        return "animal-form";
    }

    @RequestMapping(value="/projects/{projectId}/animals/{id}", method=RequestMethod.PUT)
    @PreAuthorize("hasPermission(#animal.project, 'write')")
    public String processUpdate(
        RedirectAttributes redirectAttributes,
        Model model,
        @ModelAttribute(value="animal") Animal animal,
        BindingResult bindingResult
    ) throws Exception {
        new AnimalFormValidator().validate(animal, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("project", animal.getProject());
            return "animal-form";
        }
        animalDao.update(animal);
        positionFixDao.renumberPositionFixes(animal.getProject(), Arrays.asList(animal.getId()));
        return "redirect:/projects/" + animal.getProject().getId() + "/animals/" + animal.getId();
    }

    @RequestMapping(value="/projects/{projectId}/animals/{id}", method=RequestMethod.DELETE)
    @PreAuthorize("hasPermission(#animal.project, 'manage')")
    public void processDelete(@ModelAttribute(value="animal") Animal animal, HttpServletResponse response) {
        List<Long> animalIds = Arrays.asList(animal.getId());
        animalDao.delete(animal);
        positionFixDao.renumberPositionFixes(animal.getProject(), animalIds);
        response.setStatus(204);
    }
}
