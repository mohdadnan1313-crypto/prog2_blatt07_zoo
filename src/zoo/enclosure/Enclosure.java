package zoo.enclosure;

import zoo.animal.Animal;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Enclosure<T extends Animal> {
    private final String name;
    private final Class<T> animalType;
    private final Set<T> inhabitants;

    public Enclosure(String name, Class<T> animalType) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Enclosure name cannot be null or blank");
        }
        if (animalType == null) {
            throw new IllegalArgumentException("Animal type class cannot be null");
        }
        this.name = name;
        this.animalType = animalType;
        this.inhabitants = new LinkedHashSet<>();
    }

    public String getName() {
        return name;
    }

    public Class<T> getAnimalType() {
        return animalType;
    }

    public boolean add(T animal) {
        if (animal == null) {
            throw new IllegalArgumentException("Animal cannot be null");
        }
        return inhabitants.add(animal);
    }

    public boolean remove(T animal) {
        return inhabitants.remove(animal);
    }

    public List<T> getInhabitants() {
        return List.copyOf(inhabitants);
    }

    public boolean isCompatible(Animal animal) {
        return animalType.isInstance(animal);
    }

    @SuppressWarnings("unchecked")
    public boolean addDynamic(Animal animal) {
        if (!isCompatible(animal)) {
            throw new ClassCastException("Animal " + animal.name() + " (" + animal.getClass().getSimpleName() + 
                ") is not compatible with enclosure type " + animalType.getSimpleName());
        }
        return add((T) animal);
    }

    @Override
    public String toString() {
        return String.format("%s{name='%s', type=%s, inhabitantsCount=%d}", 
            getClass().getSimpleName(), name, animalType.getSimpleName(), inhabitants.size());
    }
}
