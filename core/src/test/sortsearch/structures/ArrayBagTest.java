package test.sortsearch.structures;

import main.sortsearch.structures.ArrayBag;
import main.sortsearch.structures.Bag;

public class ArrayBagTest extends BagTest {
  @Override
  public Bag<Integer> createBag(Integer[] data) {
    return new ArrayBag<>(data);
  }
}
