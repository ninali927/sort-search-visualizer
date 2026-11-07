package main.sortsearch.sort;

import main.sortsearch.structures.Position;
import main.sortsearch.structures.SortableList;

/**
 * Binary insertion sort implementation.
 *
 * This is an optimization of the standard insertion sort that uses binary search
 * to find the insertion position for each element. While this reduces the number
 * of comparisons from O(n) to O(log n) per insertion, the overall time complexity
 * remains O(n²) because elements still need to be shifted to make room for insertion.
 *
 * This optimization is most beneficial when comparison operations are expensive
 * relative to element movement operations.
 *
 * @param <T> the type of elements to be sorted.
 *            pre: T must be Comparable.
 */
public class BinaryInsertionSort<T extends Comparable<T>> implements SortingAlgorithm<T> {

  @Override
  public void sort(SortableList<T> list) {
    if (list == null) {
      throw new IllegalArgumentException("List cannot be null");
    }

    int n = list.size();
    if (n <= 1) {
      return; // Already sorted
    }

    for (int i = 1; i < n; i++) {
      int insertPosition = InsertPosition(list, 0, i - 1, i);
      int j = i;
      while (j > insertPosition) {
        Position prevPos = list.getPosition(j - 1);
        Position currentPos = list.getPosition(j);
        list.swap(prevPos, currentPos);
        j--;
      }
    }
  }

  /**
   * Find the index for the element at current
   *
   * @param list the sorted list
   * @param value the element we need to insert
   * @param left the lower bound
   * @param right the upper bound
   * @return the index where the element should be inserted
   */
  private int InsertPosition(SortableList<T> list, int left, int right, int targetIndex) {
    Position targetPosition = list.getPosition(targetIndex);

    while (left <= right) {
      int mid = left + (right - left) / 2;
      Position midPosition = list.getPosition(mid);

      if (list.compare(targetPosition, midPosition) < 0) {
        right = mid - 1;
      } else {
        left = mid + 1;
      }
    }
    return left;
  }

  @Override
  public String getName() {
    return "Binary Insertion Sort";
  }
}
