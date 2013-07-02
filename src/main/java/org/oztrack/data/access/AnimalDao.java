package org.oztrack.data.access;

import java.util.List;

import org.oztrack.data.model.Animal;
import org.springframework.stereotype.Service;

@Service
public interface AnimalDao {
    int getNumAnimals();
    List<Animal> getAnimalsByProjectId(Long projectId);
    Animal getAnimalById(Long id);
    List<Animal> getAnimalsById(List<Long> ids);
    void save(Animal object);
    Animal update(Animal object);
    void delete(Animal animal);
    List<String> getSpeciesList();
}
