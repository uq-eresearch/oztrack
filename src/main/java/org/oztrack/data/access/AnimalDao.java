package org.oztrack.data.access;

import java.util.List;

import org.oztrack.data.model.Animal;

import au.edu.uq.itee.maenad.dataaccess.Dao;

/**
 * Created by IntelliJ IDEA.
 * User: uqpnewm5
 * Date: 4/05/11
 * Time: 12:04 PM
 * To change this template use File | Settings | File Templates.
 */
public interface AnimalDao extends Dao<Animal> {

    List<Animal> getAnimalsByProjectId(Long projectId);
    Animal getAnimal(String animalId, Long projectId);
    Animal getAnimalById(Long id);
}
