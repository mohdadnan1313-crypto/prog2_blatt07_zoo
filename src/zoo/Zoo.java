package zoo;

import zoo.animal.Animal;
import zoo.animal.Bird;
import zoo.animal.Fish;
import zoo.animal.Mammal;
import zoo.animal.Reptile;
import zoo.enclosure.Enclosure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Zoo {
    private static final Logger LOGGER = Logger.getLogger(Zoo.class.getName());
    private final List<Enclosure<? extends Animal>> enclosures = new ArrayList<>();

    public boolean addEnclosure(Enclosure<? extends Animal> enclosure) {
        LOGGER.log(Level.INFO, "addEnclosure called: enclosure={0}", enclosure);
        if (enclosure == null) {
            LOGGER.log(Level.SEVERE, "Inconsistency: Attempted to add a null enclosure");
            return false;
        }

        // Check for duplicate enclosure name
        boolean nameExists = enclosures.stream()
                .anyMatch(e -> e.getName().equalsIgnoreCase(enclosure.getName()));
        if (nameExists) {
            LOGGER.log(Level.SEVERE, "Inconsistency: Enclosure with name ''{0}'' already exists", enclosure.getName());
            return false;
        }

        boolean added = enclosures.add(enclosure);
        if (added) {
            LOGGER.log(Level.FINE, "Enclosure added successfully. Current enclosures count: {0}", enclosures.size());
        }
        return added;
    }

    public List<Enclosure<? extends Animal>> getEnclosures() {
        LOGGER.log(Level.INFO, "getEnclosures called");
        List<Enclosure<? extends Animal>> result = Collections.unmodifiableList(enclosures);
        LOGGER.log(Level.FINE, "getEnclosures returned {0} enclosures", result.size());
        return result;
    }

    public Optional<Enclosure<? extends Animal>> findEnclosureByName(String name) {
        LOGGER.log(Level.INFO, "findEnclosureByName called: name={0}", name);
        if (name == null || name.isBlank()) {
            LOGGER.log(Level.WARNING, "findEnclosureByName: Search name is null or blank");
            return Optional.empty();
        }

        Optional<Enclosure<? extends Animal>> result = enclosures.stream()
                .filter(e -> e.getName().equalsIgnoreCase(name))
                .findFirst();

        if (result.isEmpty()) {
            LOGGER.log(Level.WARNING, "Enclosure with name ''{0}'' not found", name);
        } else {
            LOGGER.log(Level.FINE, "Enclosure with name ''{0}'' found: {1}", new Object[]{name, result.get()});
        }
        return result;
    }

    public List<Animal> getAllAnimals() {
        LOGGER.log(Level.INFO, "getAllAnimals called");
        List<Animal> result = enclosures.stream()
                .flatMap(e -> e.getInhabitants().stream())
                .map(a -> (Animal) a)
                .toList();
        LOGGER.log(Level.FINE, "getAllAnimals returned {0} animals", result.size());
        return result;
    }

    public List<Mammal> getAllMammals() {
        LOGGER.log(Level.INFO, "getAllMammals called");
        List<Mammal> result = enclosures.stream()
                .flatMap(e -> e.getInhabitants().stream())
                .filter(Mammal.class::isInstance)
                .map(Mammal.class::cast)
                .toList();
        LOGGER.log(Level.FINE, "getAllMammals returned {0} mammals", result.size());
        return result;
    }

    public List<Animal> getAnimalsByPredicate(Predicate<Animal> predicate) {
        LOGGER.log(Level.INFO, "getAnimalsByPredicate called");
        if (predicate == null) {
            LOGGER.log(Level.SEVERE, "Inconsistency: Predicate is null");
            return List.of();
        }

        List<Animal> result = enclosures.stream()
                .flatMap(e -> e.getInhabitants().stream())
                .filter(predicate)
                .map(a -> (Animal) a)
                .toList();
        LOGGER.log(Level.FINE, "getAnimalsByPredicate returned {0} animals", result.size());
        return result;
    }

    public Map<Class<? extends Animal>, Long> countAnimalsByType() {
        LOGGER.log(Level.INFO, "countAnimalsByType called");
        Map<Class<? extends Animal>, Long> result = enclosures.stream()
                .flatMap(e -> e.getInhabitants().stream())
                .collect(Collectors.groupingBy(Animal::getClass, Collectors.counting()));
        LOGGER.log(Level.FINE, "countAnimalsByType returned map with {0} types", result.size());
        return result;
    }

    public List<Enclosure<? extends Animal>> getOvercrowdedEnclosures(int maxInhabitants) {
        LOGGER.log(Level.INFO, "getOvercrowdedEnclosures called: maxInhabitants={0}", maxInhabitants);
        List<Enclosure<? extends Animal>> result = enclosures.stream()
                .filter(e -> e.getInhabitants().size() > maxInhabitants)
                .toList();
        LOGGER.log(Level.FINE, "getOvercrowdedEnclosures returned {0} overcrowded enclosures", result.size());
        return result;
    }

    public String summary() {
        LOGGER.log(Level.INFO, "summary called");
        long totalAnimals = enclosures.stream().mapToInt(e -> e.getInhabitants().size()).sum();
        long mammalsCount = enclosures.stream().flatMap(e -> e.getInhabitants().stream()).filter(a -> a instanceof Mammal).count();
        long birdsCount = enclosures.stream().flatMap(e -> e.getInhabitants().stream()).filter(a -> a instanceof Bird).count();
        long fishCount = enclosures.stream().flatMap(e -> e.getInhabitants().stream()).filter(a -> a instanceof Fish).count();
        long reptilesCount = enclosures.stream().flatMap(e -> e.getInhabitants().stream()).filter(a -> a instanceof Reptile).count();

        String summaryText = String.format("Zoo mit %d Gehegen und %d Tieren: %d Mammals, %d Birds, %d Fish, %d Reptiles",
                enclosures.size(), totalAnimals, mammalsCount, birdsCount, fishCount, reptilesCount);

        LOGGER.log(Level.FINE, "summary generated: {0}", summaryText);
        return summaryText;
    }

    public boolean admitAnimal(String enclosureName, Animal animal) {
        LOGGER.log(Level.INFO, "admitAnimal called: enclosureName={0}, animal={1}", new Object[]{enclosureName, animal});
        if (animal == null) {
            LOGGER.log(Level.SEVERE, "Inconsistency: Attempted to admit a null animal");
            return false;
        }

        Optional<Enclosure<? extends Animal>> optEnc = findEnclosureByName(enclosureName);
        if (optEnc.isEmpty()) {
            LOGGER.log(Level.WARNING, "admitAnimal: Target enclosure ''{0}'' not found for animal {1}", new Object[]{enclosureName, animal});
            return false;
        }
        Enclosure<? extends Animal> enc = optEnc.get();

        // Inconsistency check: Is the animal already in any enclosure in the zoo?
        boolean alreadyExists = enclosures.stream()
                .anyMatch(e -> e.getInhabitants().contains(animal));
        if (alreadyExists) {
            LOGGER.log(Level.SEVERE, "Inconsistency: Animal {0} is already in another enclosure in this Zoo", animal);
            return false;
        }

        try {
            boolean added = enc.addDynamic(animal);
            if (added) {
                LOGGER.log(Level.FINE, "admitAnimal: Successfully admitted animal {0} to enclosure ''{1}''. Total animals: {2}",
                        new Object[]{animal, enclosureName, getAllAnimals().size()});
                return true;
            } else {
                LOGGER.log(Level.WARNING, "admitAnimal: Animal {0} is already present in enclosure ''{1}''", new Object[]{animal, enclosureName});
                return false;
            }
        } catch (ClassCastException e) {
            LOGGER.log(Level.SEVERE, "Inconsistency: Cannot admit animal {0} to enclosure ''{1}'' due to type mismatch: {2}",
                    new Object[]{animal, enclosureName, e.getMessage()});
            return false;
        }
    }

    public boolean releaseAnimal(Animal animal) {
        LOGGER.log(Level.INFO, "releaseAnimal called: animal={0}", animal);
        if (animal == null) {
            LOGGER.log(Level.SEVERE, "Inconsistency: Attempted to release a null animal");
            return false;
        }

        Optional<Enclosure<? extends Animal>> sourceOpt = enclosures.stream()
                .filter(e -> e.getInhabitants().contains(animal))
                .findFirst();

        if (sourceOpt.isEmpty()) {
            LOGGER.log(Level.WARNING, "releaseAnimal: Animal {0} not found in any enclosure in the Zoo", animal);
            return false;
        }

        Enclosure<? extends Animal> enc = sourceOpt.get();
        boolean removed = removeAnimalFromEnclosure(enc, animal);
        if (removed) {
            LOGGER.log(Level.FINE, "releaseAnimal: Successfully released animal {0} from enclosure ''{1}''. Total animals: {2}",
                    new Object[]{animal, enc.getName(), getAllAnimals().size()});
        }
        return removed;
    }

    public boolean transferAnimal(Animal animal, String sourceEnclosureName, String destEnclosureName) {
        LOGGER.log(Level.INFO, "transferAnimal called: animal={0}, source={1}, dest={2}",
                new Object[]{animal, sourceEnclosureName, destEnclosureName});
        if (animal == null) {
            LOGGER.log(Level.SEVERE, "Inconsistency: Attempted to transfer a null animal");
            return false;
        }

        Optional<Enclosure<? extends Animal>> srcOpt = findEnclosureByName(sourceEnclosureName);
        Optional<Enclosure<? extends Animal>> destOpt = findEnclosureByName(destEnclosureName);

        if (srcOpt.isEmpty()) {
            LOGGER.log(Level.WARNING, "transferAnimal: Source enclosure ''{0}'' not found", sourceEnclosureName);
            return false;
        }
        if (destOpt.isEmpty()) {
            LOGGER.log(Level.WARNING, "transferAnimal: Destination enclosure ''{0}'' not found", destEnclosureName);
            return false;
        }

        Enclosure<? extends Animal> src = srcOpt.get();
        Enclosure<? extends Animal> dest = destOpt.get();

        if (!src.getInhabitants().contains(animal)) {
            LOGGER.log(Level.WARNING, "transferAnimal: Animal {0} is not present in source enclosure ''{1}''",
                    new Object[]{animal, sourceEnclosureName});
            return false;
        }

        if (!dest.isCompatible(animal)) {
            LOGGER.log(Level.SEVERE, "Inconsistency: Animal {0} is not compatible with destination enclosure ''{1}'' (Expected: {2})",
                    new Object[]{animal, destEnclosureName, dest.getAnimalType().getSimpleName()});
            return false;
        }

        boolean removed = removeAnimalFromEnclosure(src, animal);
        if (!removed) {
            LOGGER.log(Level.SEVERE, "Inconsistency: Failed to remove animal {0} from source enclosure ''{1}'' during transfer",
                    new Object[]{animal, sourceEnclosureName});
            return false;
        }

        boolean added = dest.addDynamic(animal);
        if (added) {
            LOGGER.log(Level.FINE, "transferAnimal: Successfully transferred {0} from ''{1}'' to ''{2}''. Total animals: {3}",
                    new Object[]{animal, sourceEnclosureName, destEnclosureName, getAllAnimals().size()});
            return true;
        } else {
            src.addDynamic(animal); // Rollback
            LOGGER.log(Level.SEVERE, "Inconsistency: Animal {0} already in destination enclosure ''{1}''. Rollback performed.",
                    new Object[]{animal, destEnclosureName});
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    private <A extends Animal> boolean removeAnimalFromEnclosure(Enclosure<A> enc, Animal animal) {
        return enc.remove((A) animal);
    }
}
