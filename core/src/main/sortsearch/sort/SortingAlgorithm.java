package main.sortsearch.sort;

import main.sortsearch.structures.SortableList;

/**
 * Interface for sorting algorithms that work with SortableList.
 *
 * @param <T> the type of elements to be sorted.
 *            pre: T must be Comparable.
 */
public interface SortingAlgorithm<T extends Comparable<T>> {

  /**
   * Sort the elements in the given SortableList in ascending order.
   *
   * This method should modify the list in-place using only the compare()
   * and swap() operations provided by the SortableList interface.
   *
   * @param list the SortableList to be sorted
   * @throws IllegalArgumentException if list is null
   */
  void sort(SortableList<T> list);

  /**
   * Get the name of this sorting algorithm.
   *
   * This method should return a human-readable name for the algorithm,
   * which can be used for identification in benchmarks, reports, and
   * user interfaces.
   *
   * @return the name of the sorting algorithm
   */
  String getName();
}
