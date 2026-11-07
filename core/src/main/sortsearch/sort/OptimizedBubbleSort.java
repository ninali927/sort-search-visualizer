package main.sortsearch.sort;

import main.sortsearch.structures.Position;
import main.sortsearch.structures.SortableList;

/**
 * Optimized bubble sort implementation with early termination.
 *
 * This version of bubble sort includes the early termination optimization.
 * If no swaps occur during a complete pass through the array, the algorithm
 * knows that the array is already sorted and can terminate early.
 *
 * This optimization dramatically improves performance on already sorted or
 * nearly sorted data, changing the best-case time complexity from O(n²) to O(n)
 * while maintaining the same O(n²) worst-case performance.
 *
 * @param <T> the type of elements to be sorted.
 *            pre: T must be Comparable.
 */
public class OptimizedBubbleSort<T extends Comparable<T>> implements SortingAlgorithm<T> {

  @Override
  public void sort(SortableList<T> list) {
    if (list == null) {
      throw new IllegalArgumentException("List cannot be null");
    }

    int n = list.size();
    if (n <= 1) {
      return; // Already sorted
    }

    for (int i = 0; i < n - 1; i++) {
      boolean swapped = false;
      for (int j = 0; j < n - i - 1; j++) {
        // Get positions for adjacent elements
        Position pos1 = list.getPosition(j);
        Position pos2 = list.getPosition(j + 1);

        // Compare adjacent elements
        if (list.compare(pos1, pos2) > 0) {
          // Swap if they are in wrong order
          list.swap(pos1, pos2);
          swapped = true;
        }
      }
      if (!swapped) { // no swapping; list already sorted
        break;
      }
    }
  }

  @Override
  public String getName() {
    return "Optimized Bubble Sort";
  }
}
