package test.sortsearch.structures;

import main.sortsearch.structures.Bag;
import main.sortsearch.structures.LinkedListBag;

public class LinkedListBagTest extends BagTest {
  @Override
  public Bag<Integer> createBag(Integer[] data) {
    return new LinkedListBag<>(data);
  }
}
