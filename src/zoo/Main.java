package zoo;

import zoo.animal.Dog;
import zoo.animal.Eagle;
import zoo.animal.Elephant;
import zoo.animal.Lion;
import zoo.animal.Lizard;
import zoo.animal.Penguin;
import zoo.animal.Salmon;
import zoo.animal.Snake;
import zoo.animal.Tiger;
import zoo.animal.Trout;
import zoo.enclosure.Aquarium;
import zoo.enclosure.CatHouse;
import zoo.enclosure.MammalHouse;
import zoo.enclosure.Terrarium;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    public static void main(String[] args) {
        // --- LOGGING SETUP ---
        // Get the loggers we want to configure
        Logger rootLogger = Logger.getLogger("");
        Logger zooLogger = Logger.getLogger(Zoo.class.getName());

        System.out.println("=== 1. DEFAULT LOGGING LEVEL (INFO and above) ===");
        // By default, Java's ConsoleHandler logs INFO and above. Let's see this in action.
        Zoo zoo = new Zoo();

        // Create some enclosures
        Aquarium fishTank = new Aquarium("Ocean Reef");
        Terrarium reptileHouse = new Terrarium("Desert Dome");
        MammalHouse elephantPlain = new MammalHouse("Savannah");
        CatHouse<Tiger> tigerTerritory = new CatHouse<>("Siberia", Tiger.class);

        // Add enclosures to Zoo
        zoo.addEnclosure(fishTank);
        zoo.addEnclosure(reptileHouse);
        zoo.addEnclosure(elephantPlain);
        zoo.addEnclosure(tigerTerritory);

        // 1. Admit animals
        Trout fritz = new Trout("Fritz");
        Salmon sammy = new Salmon("Sammy");
        Snake sid = new Snake("Sid");
        Lizard leo = new Lizard("Leo");
        Elephant ella = new Elephant("Ella");
        Dog dodo = new Dog("Dodo");
        Tiger tigger = new Tiger("Tigger");

        zoo.admitAnimal("Ocean Reef", fritz);
        zoo.admitAnimal("Ocean Reef", sammy);
        zoo.admitAnimal("Desert Dome", sid);
        zoo.admitAnimal("Desert Dome", leo);
        zoo.admitAnimal("Savannah", ella);
        zoo.admitAnimal("Savannah", dodo);
        zoo.admitAnimal("Siberia", tigger);

        // Try to trigger a WARNING (Enclosure not found)
        System.out.println("\n--- Triggering WARNING: Enclosure not found ---");
        zoo.findEnclosureByName("Non-existent Enclosure");

        // Try to trigger a SEVERE (Duplicate Enclosure name)
        System.out.println("\n--- Triggering SEVERE: Duplicate Enclosure ---");
        zoo.addEnclosure(new Aquarium("Ocean Reef"));

        // Try to trigger a SEVERE (Inconsistent state: Animal already in Zoo)
        System.out.println("\n--- Triggering SEVERE: Duplicate Animal across Zoo ---");
        zoo.admitAnimal("Savannah", fritz); // Fritz the Trout is already in Ocean Reef

        // Try to trigger a SEVERE (Inconsistent state: Incompatible type at runtime)
        System.out.println("\n--- Triggering SEVERE: Incompatible animal type (dynamic check) ---");
        zoo.admitAnimal("Savannah", fritz); // Fritz the Trout cannot go into MammalHouse Savannah

        // COMPILE-TIME PROTECTION DEMO
        // Uncommenting the line below will result in a compilation error, proving compile-time checks work!
        // tigerTerritory.add(new Lion("Simba")); // COMPILER ERROR: Enclosure.add(Tiger) cannot be applied to Lion

        System.out.println("\n=== 2. DYNAMICALLY SWITCHING LOG LEVEL TO FINE ===");
        // Configure Logger and Handlers to allow FINE messages
        rootLogger.setLevel(Level.FINE);
        for (Handler handler : rootLogger.getHandlers()) {
            handler.setLevel(Level.FINE);
        }
        zooLogger.setLevel(Level.FINE);

        System.out.println("[Notice: You should now see FINE level messages below]");

        // Trigger queries to observe FINE logs
        System.out.println("\n--- Executing queries (logging at FINE level) ---");
        zoo.getAllAnimals();
        zoo.getAllMammals();
        zoo.countAnimalsByType();
        zoo.getOvercrowdedEnclosures(2);
        
        System.out.println("\n--- Executing summary() ---");
        String summary = zoo.summary();
        System.out.println("Result of summary(): " + summary);

        // Perform transition actions (Umsetzen / Abgeben)
        System.out.println("\n--- Performing Transition Actions ---");
        
        // 1. Umsetzen (Transfer Sammy from Ocean Reef to Siberia - should fail SEVERE)
        System.out.println("Transferring Sammy the Salmon to Siberia (should fail type check):");
        zoo.transferAnimal(sammy, "Ocean Reef", "Siberia");

        // 2. Umsetzen (Transfer Sammy from Ocean Reef to Desert Dome - should fail type check)
        System.out.println("Transferring Sammy the Salmon to Desert Dome (should fail type check):");
        zoo.transferAnimal(sammy, "Ocean Reef", "Desert Dome");

        // 3. Umsetzen (Transfer Dodo the Dog from Savannah to Savannah - should do nothing or succeed)
        // Let's create another MammalHouse to transfer Dodo
        MammalHouse smallMammalPen = new MammalHouse("Petting Zoo");
        zoo.addEnclosure(smallMammalPen);
        System.out.println("Transferring Dodo from Savannah to Petting Zoo:");
        zoo.transferAnimal(dodo, "Savannah", "Petting Zoo");

        // 4. Abgeben (Release Fritz the Trout)
        System.out.println("Releasing Fritz the Trout from the Zoo:");
        zoo.releaseAnimal(fritz);

        // Show final summary
        System.out.println("\nFinal summary of the Zoo:");
        System.out.println(zoo.summary());
    }
}
