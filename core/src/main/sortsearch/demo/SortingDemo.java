package main.sortsearch.demo;

import main.sortsearch.monitoring.PerformanceMonitor;
import main.sortsearch.sort.*;
import main.sortsearch.structures.SortableArrayList;
import main.sortsearch.structures.SortableList;

/**
 * Demonstration class showing how to use the sorting algorithms with SortableList.
 *
 * This class provides examples of how to use the sorting algorithms
 * and compare their performance characteristics on different types of data.
 */
public class SortingDemo {

  @SuppressWarnings("unchecked")
  public static void main(String[] args) {
    // Test data sets
    Integer[] randomData = {64, 34, 25, 12, 22, 11, 90, 3, 77};
    Integer[] sortedData = {11, 12, 22, 25, 34, 64, 77, 90};
    Integer[] reverseData = {90, 77, 64, 34, 25, 22, 12, 11};

    // Create all sorting algorithms
    SortingAlgorithm<Integer>[] algorithms = new SortingAlgorithm[] {new BubbleSort<Integer>(),
        new OptimizedBubbleSort<Integer>(), new InsertionSort<Integer>(),
        new BinaryInsertionSort<Integer>(), new SelectionSort<Integer>()};

    System.out.println("=== Sorting Algorithm Performance Comparison ===\n");

    // Test each algorithm on different data types
    testAlgorithmsOnData("Random Data", randomData, algorithms);
    testAlgorithmsOnData("Already Sorted Data", sortedData, algorithms);
    testAlgorithmsOnData("Reverse Sorted Data", reverseData, algorithms);
  }

  private static void testAlgorithmsOnData(String dataType, Integer[] data,
      SortingAlgorithm<Integer>[] algorithms) {
    System.out.println("--- " + dataType + " ---");
    System.out.printf("%-25s | %s | %s | %s%n", "Algorithm", "Comparisons", "Swaps", "Total Ops");
    System.out.println("-".repeat(65));

    for (SortingAlgorithm<Integer> algorithm : algorithms) {
      // Create a fresh copy of data for each algorithm
      SortableList<Integer> list = new SortableArrayList<>(data.clone());

      // Sort the data
      algorithm.sort(list);

      // Display results
      PerformanceMonitor performanceMonitor = (PerformanceMonitor) list;
      int comparisons = performanceMonitor.getComparisonCount();
      int swaps = performanceMonitor.getModificationCount();
      int totalOps = comparisons + swaps;

      System.out.printf("%-25s | %11d | %5d | %9d%n", algorithm.getName(), comparisons, swaps,
          totalOps);

      // Verify the result is actually sorted
      if (!isSorted(list)) {
        System.out.println("ERROR: " + algorithm.getName() + " failed to sort correctly!");
      }
    }
    System.out.println();
  }

  /**
   * Verify that a SortableList is sorted in ascending order.
   */
  private static boolean isSorted(SortableList<Integer> list) {
    for (int i = 0; i < list.size() - 1; i++) {
      if (list.get(list.getPosition(i)) > list.get(list.getPosition(i + 1))) {
        return false;
      }
    }
    return true;
  }
}
