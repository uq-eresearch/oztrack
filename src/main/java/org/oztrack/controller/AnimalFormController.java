package org.oztrack.controller;

import org.oztrack.app.OzTrackApplication;
import org.oztrack.data.access.AnimalDao;
import org.oztrack.data.model.Animal;
import org.oztrack.data.model.DataFile;
import org.oztrack.data.model.Project;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by IntelliJ IDEA.
 * User: uqpnewm5
 * Date: 23/05/11
 * Time: 2:09 PM
 */
public class AnimalFormController extends SimpleFormController {


@Override
protected Object formBackingObject(HttpServletRequest request) throws Exception {

    String animalId = request.getParameter("animal_id");
    Animal animal = new Animal();

        if (animalId != null) {
            AnimalDao animalDao = OzTrackApplication.getApplicationContext().getDaoManager().getAnimalDao();
            animal = animalDao.getAnimalById(Long.parseLong(animalId));
        }

    return animal;
}


    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception {

        Animal animal = (Animal) command;

        AnimalDao animalDao = OzTrackApplication.getApplicationContext().getDaoManager().getAnimalDao();
        animalDao.update(animal);

        ModelAndView modelAndView = new ModelAndView(getSuccessView());
        return modelAndView;
    }


}
