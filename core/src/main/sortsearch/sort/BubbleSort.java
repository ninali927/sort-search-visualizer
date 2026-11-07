package main.sortsearch.sort;

import main.sortsearch.structures.Position;
import main.sortsearch.structures.SortableList;

/**
 * Basic bubble sort implementation.
 *
 * Bubble sort works by repeatedly stepping through the list, comparing adjacent
 * elements and swapping them if they are in the wrong order. The pass through
 * the list is repeated until the list is sorted. The algorithm gets its name
 * from the way smaller elements "bubble" to the top of the list.
 *
 * This is the basic version that always performs all passes regardless of
 * whether the array becomes sorted early.
 *
 * @param <T> the type of elements to be sorted.
 *            pre: T must be Comparable.
 */
public class BubbleSort<T extends Comparable<T>> implements SortingAlgorithm<T> {

  @Override
  public void sort(SortableList<T> list) {
    if (list == null) {
      throw new IllegalArgumentException("List cannot be null");
    }

    int n = list.size();
    if (n <= 1) {
      return; // Already sorted
    }

    // Perform n-1 passes
    for (int i = 0; i < n - 1; i++) {
      // In each pass, bubble the largest unsorted element to its correct position
      for (int j = 0; j < n - i - 1; j++) {
        // Get positions for adjacent elements
        Position pos1 = list.getPosition(j);
        Position pos2 = list.getPosition(j + 1);

        // Compare adjacent elements
        if (list.compare(pos1, pos2) > 0) {
          // Swap if they are in wrong order
          list.swap(pos1, pos2);
        }
      }
    }
  }

  @Override
  public String getName() {
    return "Bubble Sort";
  }
}
