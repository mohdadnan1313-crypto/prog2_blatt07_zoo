package zoo.animal;

public sealed interface Animal permits Bird, Fish, Reptile, Mammal {
    String name();
}
