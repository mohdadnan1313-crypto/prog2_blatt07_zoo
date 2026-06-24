package zoo.enclosure;

import zoo.animal.Reptile;

public class Terrarium extends Enclosure<Reptile> {
    public Terrarium(String name) {
        super(name, Reptile.class);
    }
}
