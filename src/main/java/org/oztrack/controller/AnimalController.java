package org.oztrack.controller;

import org.oztrack.data.access.AnimalDao;
import org.oztrack.data.model.Animal;
import org.oztrack.validator.AnimalFormValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AnimalController {
    @Autowired
    private AnimalDao animalDao;
    
    @ModelAttribute("animal")
    public Animal getAnimal(@RequestParam(value="animal_id") Long animalId) throws Exception {
        return animalDao.getAnimalById(animalId);
    }

    @RequestMapping(value="/animalform", method=RequestMethod.GET)
    @PreAuthorize("hasPermission(#animal.project, 'write')")
    public String getFormView(Model model, @ModelAttribute("animal") Animal animal) {
        model.addAttribute("project", animal.getProject());
        return "animalform";
    }
    
    @RequestMapping(value="/animalform", method=RequestMethod.POST)
    @PreAuthorize("hasPermission(#animal.project, 'write')")
    public String processSubmit(
        RedirectAttributes redirectAttributes,
        @ModelAttribute(value="animal") Animal animal,
        BindingResult bindingResult
    ) throws Exception {
        new AnimalFormValidator().validate(animal, bindingResult);
        if (bindingResult.hasErrors()) {
            return "animalform";
        }
        animalDao.update(animal);
        redirectAttributes.addAttribute("projectId", animal.getProject().getId());
        return "redirect:projectanimals?id={projectId}";
    }
}