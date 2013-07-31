package org.oztrack.util;

import java.util.List;
import java.util.Map.Entry;
import java.util.WeakHashMap;
import java.util.concurrent.Semaphore;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.oztrack.data.model.Project;

public class ProjectAnimalsMutexExecutor {
    private final Logger logger = Logger.getLogger(getClass());

    private static class ProjectAnimals {
        private Project project;
        private List<Long> animalIds;
        public ProjectAnimals(Project project, List<Long> animalIds) {
            this.project = project;
            this.animalIds = animalIds;
        }
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((animalIds == null) ? 0 : animalIds.hashCode());
            result = prime * result + ((project == null) ? 0 : project.hashCode());
            return result;
        }
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (this.getClass() != obj.getClass()) return false;
            ProjectAnimals other = (ProjectAnimals) obj;
            return
                ObjectUtils.equals(this.project, other.project) &&
                ObjectUtils.equals(this.animalIds, other.animalIds);
        }
        @Override
        public String toString() {
            return getClass().getSimpleName() +
                "(" + project.getId() + ", " +
                "[" + StringUtils.join(animalIds, ", ") + "]" + ")";
        }
    }

    public static abstract class ProjectAnimalsRunnable implements Runnable {
        private ProjectAnimals projectAnimals;
        public ProjectAnimalsRunnable(Project project, List<Long> animalIds) {
            this.projectAnimals = new ProjectAnimals(project, animalIds);
        }
        public ProjectAnimals getProjectAnimals() {
            return projectAnimals;
        };
    }

    // Use WeakHashMap so entries are garbage-collected when key isn't reachable.
    // Ideally, we'd like weak values rather than keys here - the map is really
    // in reverse, meaning we lookup from ProjectAnimals to Semaphore. But aside
    // from that annoyance, this allows the map to be cleaned up without the
    // complications to trying to call HashMap.remove(o) in afterExecute(...):
    // another thread may be on the verge of acquiring the semaphore at the time
    // its removed, causing a race condition.
    private WeakHashMap<Semaphore, ProjectAnimals> weakSemaphoreMap;

    public ProjectAnimalsMutexExecutor() {
        this.weakSemaphoreMap = new WeakHashMap<Semaphore, ProjectAnimals>();
    }

    public void execute(ProjectAnimalsRunnable command) {
        logger.debug("Thread " + command + " obtaining semaphore for " + command.getProjectAnimals());
        // Hard reference to the semaphore.
        Semaphore semaphore = null;
        synchronized (this) {
            // Do reverse lookup of ProjectAnimal in WeakHashMap:
            // necessary because we want the semaphore to be weakly referenced.
            for (Entry<Semaphore, ProjectAnimals> entry : weakSemaphoreMap.entrySet()) {
                if (entry.getValue().equals(command.getProjectAnimals())) {
                    semaphore = entry.getKey();
                    break;
                }
            }
            if (semaphore == null) {
                semaphore = new Semaphore(1, true);
                // Save a weak reference in map that can be garbage collected
                // at some point following clearing of the hard reference.
                weakSemaphoreMap.put(semaphore, command.getProjectAnimals());
            }
        }
        try {
            logger.debug("Thread " + command + " acquiring semaphore for " + command.getProjectAnimals());
            semaphore.acquire();
        }
        catch (InterruptedException e) {
        }
        logger.debug("Thread " + command + " started renumbering position fixes for " + command.getProjectAnimals());
        command.run();
        logger.debug("Thread " + command + " finished renumbering position fixes for " + command.getProjectAnimals());
        semaphore.release();
        logger.debug("Thread " + command + " released semaphore for " + command.getProjectAnimals());
    }
}