package zoo.animal;

public sealed interface Mammal extends Animal permits Cat, Elephant, Dog {
}
