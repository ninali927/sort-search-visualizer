package main.sortsearch.sort;

import main.sortsearch.structures.Position;
import main.sortsearch.structures.SortableList;

/**
 * Selection sort implementation.
 *
 * Selection sort works by finding the minimum element from the unsorted portion
 * of the array and swapping it with the element at the beginning of the unsorted
 * portion. This process is repeated until the entire array is sorted.
 *
 * The algorithm is not adaptive (performs the same number of comparisons regardless
 * of input order) but has the advantage of minimizing the number of swaps performed,
 * making exactly n-1 swaps for an array of size n.
 *
 * @param <T> the type of elements to be sorted.
 *            pre: T must be Comparable.
 */
public class SelectionSort<T extends Comparable<T>> implements SortingAlgorithm<T> {

  @Override
  public void sort(SortableList<T> list) {
    if (list == null) {
      throw new IllegalArgumentException("List cannot be null");
    }

    int n = list.size();
    if (n <= 1) {
      return; // Already sorted
    }

    // Move the boundary of unsorted subarray one element at a time
    for (int i = 0; i < n - 1; i++) {
      // Find the minimum element in the remaining unsorted array
      int minIndex = i;

      for (int j = i + 1; j < n; j++) {
        // Get positions for comparison
        Position currentPos = list.getPosition(j);
        Position minPos = list.getPosition(minIndex);

        // Compare current element with the current minimum
        if (list.compare(currentPos, minPos) < 0) {
          minIndex = j; // Update minimum index
        }
      }

      // Swap the found minimum element with the first element of unsorted portion
      // Only swap if the minimum element is not already in the correct position
      if (minIndex != i) {
        Position iPos = list.getPosition(i);
        Position minPos = list.getPosition(minIndex);
        list.swap(iPos, minPos);
      }
    }
  }

  @Override
  public String getName() {
    return "Selection Sort";
  }
}
