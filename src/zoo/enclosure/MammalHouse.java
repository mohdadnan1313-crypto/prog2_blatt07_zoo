package zoo.enclosure;

import zoo.animal.Mammal;

public class MammalHouse extends Enclosure<Mammal> {
    public MammalHouse(String name) {
        super(name, Mammal.class);
    }
}
