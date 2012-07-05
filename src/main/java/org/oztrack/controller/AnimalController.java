package org.oztrack.controller;

import javax.servlet.http.HttpServletResponse;

import org.oztrack.data.access.AnimalDao;
import org.oztrack.data.model.Animal;
import org.oztrack.validator.AnimalFormValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AnimalController {
    @Autowired
    private AnimalDao animalDao;
    
    @ModelAttribute("animal")
    public Animal getAnimal(@PathVariable(value="id") Long animalId) throws Exception {
        return animalDao.getAnimalById(animalId);
    }
    
    @RequestMapping(value="/animals/{id}", method=RequestMethod.GET)
    @PreAuthorize("#animal.project.global or hasPermission(#animal.project, 'read')")
    public String getView(Model model, @ModelAttribute("animal") Animal animal) {
        model.addAttribute("project", animal.getProject());
        return "animal";
    }

    @RequestMapping(value="/animals/{id}/edit", method=RequestMethod.GET)
    @PreAuthorize("hasPermission(#animal.project, 'write')")
    public String getEditView(Model model, @ModelAttribute("animal") Animal animal) {
        model.addAttribute("project", animal.getProject());
        return "animal-form";
    }
    
    @RequestMapping(value="/animals/{id}", method=RequestMethod.PUT)
    @PreAuthorize("hasPermission(#animal.project, 'write')")
    public String processUpdate(
        RedirectAttributes redirectAttributes,
        @ModelAttribute(value="animal") Animal animal,
        BindingResult bindingResult
    ) throws Exception {
        new AnimalFormValidator().validate(animal, bindingResult);
        if (bindingResult.hasErrors()) {
            return "animal-form";
        }
        animalDao.update(animal);
        redirectAttributes.addAttribute("projectId", animal.getProject().getId());
        return "redirect:/projects/{projectId}/animals";
    }

    @RequestMapping(value="/animals/{id}", method=RequestMethod.DELETE)
    @PreAuthorize("hasPermission(#animal.project, 'write')")
    public void processDelete(@ModelAttribute(value="animal") Animal animal, HttpServletResponse response) {
        animalDao.delete(animal);
        response.setStatus(204);
    }
}
