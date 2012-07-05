package org.oztrack.data.access;

import java.util.List;

import org.oztrack.data.model.Animal;
import org.springframework.stereotype.Service;

@Service
public interface AnimalDao {
    List<Animal> getAnimalsByProjectId(Long projectId);
    Animal getAnimal(String animalId, Long projectId);
    Animal getAnimalById(Long id);
    void save(Animal object);
    Animal update(Animal object);
    void delete(Animal animal);
}
