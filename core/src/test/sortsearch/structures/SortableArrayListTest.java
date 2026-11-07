package test.sortsearch.structures;

import main.sortsearch.structures.SortableArrayList;
import main.sortsearch.structures.SortableList;

public class SortableArrayListTest extends SortableListTest {
  @Override
  public SortableList<Integer> createSortableList(Integer[] data) {
    return new SortableArrayList<>(data);
  }
}
