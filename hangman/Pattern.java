package hangman;

import java.util.ArrayList;
import java.util.Objects;

public class Pattern {
    public ArrayList<Integer> elementIndexes;
    public int numElements = 0;

    public Pattern(ArrayList<Integer> elementIndexes, int numElements) {
        this.elementIndexes = elementIndexes;
        this.numElements = numElements;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pattern pattern = (Pattern) o;
        return numElements == pattern.numElements &&
                Objects.equals(elementIndexes, pattern.elementIndexes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(elementIndexes, numElements);
    }
}
