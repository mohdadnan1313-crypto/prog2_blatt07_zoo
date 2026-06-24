package zoo.enclosure;

import zoo.animal.Fish;

public class Aquarium extends Enclosure<Fish> {
    public Aquarium(String name) {
        super(name, Fish.class);
    }
}
