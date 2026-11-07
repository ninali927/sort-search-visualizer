package test.sortsearch.structures;

import main.sortsearch.structures.SortableLinkedList;
import main.sortsearch.structures.SortableList;

public class SortableLinkedListTest extends SortableListTest {
  @Override
  public SortableList<Integer> createSortableList(Integer[] data) {
    return new SortableLinkedList<>(data);
  }
}
