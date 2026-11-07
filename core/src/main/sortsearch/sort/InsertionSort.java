package main.sortsearch.sort;

import main.sortsearch.structures.Position;
import main.sortsearch.structures.SortableList;

/**
 * Standard insertion sort implementation.
 *
 * Insertion sort builds the final sorted array one element at a time. It works
 * by taking each element from the unsorted portion and inserting it into its
 * correct position within the already sorted portion of the array.
 *
 * The algorithm is naturally adaptive, meaning it performs significantly better
 * on data that is already partially sorted. It's also stable, maintaining the
 * relative order of equal elements.
 *
 * @param <T> the type of elements to be sorted.
 *            pre: T must be Comparable.
 */
public class InsertionSort<T extends Comparable<T>> implements SortingAlgorithm<T> {

  @Override
  public void sort(SortableList<T> list) {
    if (list == null) {
      throw new IllegalArgumentException("List cannot be null");
    }

    int n = list.size();
    if (n <= 1) {
      return; // Already sorted
    }

    // Start from the second element (index 1)
    // The first element is considered already "sorted"
    for (int i = 1; i < n; i++) {
      // Find the correct position for element at index i
      // by comparing it with elements in the sorted portion (0 to i-1)
      int j = i;

      // Move element at position i to its correct position
      // by swapping it leftward until it's in the right place
      while (j > 0) {
        Position prevPos = list.getPosition(j - 1);
        Position currentPos = list.getPosition(j);

        if (list.compare(prevPos, currentPos) > 0) {
          // Swap with the previous element
          list.swap(prevPos, currentPos);
          j--;
        } else {
          break; // Found correct position
        }
      }
    }
  }

  @Override
  public String getName() {
    return "Insertion Sort";
  }
}
