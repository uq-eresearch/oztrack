package org.oztrack.controller;

import javax.servlet.http.HttpSession;

import org.oztrack.app.OzTrackApplication;
import org.oztrack.data.access.AnimalDao;
import org.oztrack.data.model.Animal;
import org.oztrack.validator.AnimalFormValidator;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AnimalController {
    @ModelAttribute("animal")
    public Animal getAnimal(@RequestParam(value="animal_id") Long animalId) throws Exception {
        if (animalId == null) {
            return new Animal();
        }
        else {
            AnimalDao animalDao = OzTrackApplication.getApplicationContext().getDaoManager().getAnimalDao();
            return animalDao.getAnimalById(animalId);
        }
    }

    @RequestMapping(value="/animalform", method=RequestMethod.GET)
    public String getFormView(HttpSession session) {
        return "animalform";
    }
    
    @RequestMapping(value="/animalform", method=RequestMethod.POST)
    public String processSubmit(
        HttpSession session,
        RedirectAttributes redirectAttributes,
        @ModelAttribute(value="animal") Animal animal,
        BindingResult bindingResult
    ) throws Exception {
        new AnimalFormValidator().validate(animal, bindingResult);
        if (bindingResult.hasErrors()) {
            return "animalform";
        }
        AnimalDao animalDao = OzTrackApplication.getApplicationContext().getDaoManager().getAnimalDao();
        animalDao.update(animal);
        redirectAttributes.addAttribute("projectId", animal.getProject().getId());
        return "redirect:projectanimals?id={projectId}";
    }
}