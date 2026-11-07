package main.sortsearch.demo;

import main.sortsearch.structures.ArrayBag;
import main.sortsearch.structures.LinkedListBag;

/**
 * Demonstration class showing how to use the Bag implementations for adaptive search.
 *
 * This class provides examples of how to use the different Bag implementations
 * and compare their performance characteristics on different access patterns. It demonstrates
 * two adaptive search optimizations: transpose sequential search
 * for arrays and move-to-front for linked lists.
 */
public class SearchDemo {

  @SuppressWarnings("unchecked")
  public static void main(String[] args) {
    // Test data sets
    Integer[] uniformData = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    Integer[] skewedData = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10}; // Will be accessed with skewed pattern
    Integer[] temporalData = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10}; // Will show temporal locality

    System.out.println("=== Adaptive Search Algorithm Performance Comparison ===\n");

    // Test uniform access pattern
    testUniformAccessPattern("Uniform Access Pattern", uniformData);

    // Test skewed access pattern (80% of searches for elements 8, 9, 10)
    testSkewedAccessPattern("Skewed Access Pattern", skewedData);

    // Test temporal locality (repeated searches for same elements)
    testTemporalLocalityPattern("Temporal Locality Pattern", temporalData);
  }

  /**
   * Test uniform access pattern where each element is equally likely to be searched.
   */
  private static void testUniformAccessPattern(String patternName, Integer[] data) {
    System.out.println("--- " + patternName + " ---");
    System.out.println("Searching for each element once in random order");
    System.out.printf("%-20s | %s | %s | %s%n", "Implementation", "Comparisons", "Modifications",
        "Total Ops");
    System.out.println("-".repeat(65));

    // Test ArrayBag
    ArrayBag<Integer> arrayBag = new ArrayBag<>(data.clone());
    arrayBag.resetCounters();

    // Search for each element once in random order
    Integer[] searchOrder = {3, 7, 1, 9, 5, 2, 8, 4, 6, 10};
    for (Integer target : searchOrder) {
      arrayBag.contains(target);
    }

    int arrayComparisons = arrayBag.getComparisonCount();
    int arrayModifications = arrayBag.getModificationCount();
    int arrayTotal = arrayComparisons + arrayModifications;

    System.out.printf("%-20s | %11d | %12d | %9d%n", "ArrayBag (Transpose)", arrayComparisons,
        arrayModifications, arrayTotal);

    // Test LinkedListBag
    LinkedListBag<Integer> listBag = new LinkedListBag<>(data.clone());
    listBag.resetCounters();

    // Search for each element once in random order
    for (Integer target : searchOrder) {
      listBag.contains(target);
    }

    int listComparisons = listBag.getComparisonCount();
    int listModifications = listBag.getModificationCount();
    int listTotal = listComparisons + listModifications;

    System.out.printf("%-20s | %11d | %12d | %9d%n", "LinkedListBag (MTF)", listComparisons,
        listModifications, listTotal);

    System.out.println();
  }

  /**
   * Test skewed access pattern where some elements are searched much more frequently.
   */
  private static void testSkewedAccessPattern(String patternName, Integer[] data) {
    System.out.println("--- " + patternName + " ---");
    System.out.println("80% of searches for elements 8, 9, 10 (initially at end)");
    System.out.printf("%-20s | %s | %s | %s%n", "Implementation", "Comparisons", "Modifications",
        "Total Ops");
    System.out.println("-".repeat(65));

    // Test ArrayBag
    ArrayBag<Integer> arrayBag = new ArrayBag<>(data.clone());
    arrayBag.resetCounters();

    // Skewed search pattern: 8 searches for 8,9,10 and 2 searches for others
    Integer[] skewedSearches = {8, 9, 10, 8, 9, 10, 8, 9, 10, 8, 9, 10, 1, 5};
    for (Integer target : skewedSearches) {
      arrayBag.contains(target);
    }

    int arrayComparisons = arrayBag.getComparisonCount();
    int arrayModifications = arrayBag.getModificationCount();
    int arrayTotal = arrayComparisons + arrayModifications;

    System.out.printf("%-20s | %11d | %12d | %9d%n", "ArrayBag (Transpose)", arrayComparisons,
        arrayModifications, arrayTotal);

    // Test LinkedListBag
    LinkedListBag<Integer> listBag = new LinkedListBag<>(data.clone());
    listBag.resetCounters();

    // Same skewed search pattern
    for (Integer target : skewedSearches) {
      listBag.contains(target);
    }

    int listComparisons = listBag.getComparisonCount();
    int listModifications = listBag.getModificationCount();
    int listTotal = listComparisons + listModifications;

    System.out.printf("%-20s | %11d | %12d | %9d%n", "LinkedListBag (MTF)", listComparisons,
        listModifications, listTotal);

    System.out.println();
  }

  /**
   * Test temporal locality pattern where recently accessed elements are accessed again.
   */
  private static void testTemporalLocalityPattern(String patternName, Integer[] data) {
    System.out.println("--- " + patternName + " ---");
    System.out.println("Repeated searches showing temporal locality benefits");
    System.out.printf("%-20s | %s | %s | %s%n", "Implementation", "Comparisons", "Modifications",
        "Total Ops");
    System.out.println("-".repeat(65));

    // Test ArrayBag
    ArrayBag<Integer> arrayBag = new ArrayBag<>(data.clone());
    arrayBag.resetCounters();

    // Temporal locality pattern: repeated searches for same elements
    Integer[] temporalSearches = {7, 7, 7, 3, 3, 9, 9, 9, 9, 1, 1};
    for (Integer target : temporalSearches) {
      arrayBag.contains(target);
    }

    int arrayComparisons = arrayBag.getComparisonCount();
    int arrayModifications = arrayBag.getModificationCount();
    int arrayTotal = arrayComparisons + arrayModifications;

    System.out.printf("%-20s | %11d | %12d | %9d%n", "ArrayBag (Transpose)", arrayComparisons,
        arrayModifications, arrayTotal);

    // Test LinkedListBag
    LinkedListBag<Integer> listBag = new LinkedListBag<>(data.clone());
    listBag.resetCounters();

    // Same temporal locality pattern
    for (Integer target : temporalSearches) {
      listBag.contains(target);
    }

    int listComparisons = listBag.getComparisonCount();
    int listModifications = listBag.getModificationCount();
    int listTotal = listComparisons + listModifications;

    System.out.printf("%-20s | %11d | %12d | %9d%n", "LinkedListBag (MTF)", listComparisons,
        listModifications, listTotal);

    System.out.println();

    // Show convergence demonstration
    demonstrateConvergence();
  }

  /**
   * Demonstrate how move-to-front converges faster than transpose.
   */
  private static void demonstrateConvergence() {
    System.out.println("--- Convergence Demonstration ---");
    System.out.println("Searching for element 10 (initially at end) repeatedly");
    System.out.printf("%-20s | %s | %s%n", "Implementation", "Search #", "Comparisons");
    System.out.println("-".repeat(45));

    Integer[] data = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

    // Test ArrayBag convergence
    ArrayBag<Integer> arrayBag = new ArrayBag<>(data.clone());
    System.out.println("ArrayBag (Transpose):");
    for (int i = 1; i <= 6; i++) {
      arrayBag.resetCounters();
      arrayBag.contains(10);
      int comparisons = arrayBag.getComparisonCount();
      System.out.printf("%-20s | %8d | %11d%n", "", i, comparisons);
    }

    System.out.println();

    // Test LinkedListBag convergence
    LinkedListBag<Integer> listBag = new LinkedListBag<>(data.clone());
    System.out.println("LinkedListBag (Move-to-Front):");
    for (int i = 1; i <= 6; i++) {
      listBag.resetCounters();
      listBag.contains(10);
      int comparisons = listBag.getComparisonCount();
      System.out.printf("%-20s | %8d | %11d%n", "", i, comparisons);
    }

    System.out.println();
    System.out.println("Note: Move-to-Front reaches O(1) access after just 1 search!");
    System.out.println("      Transpose gradually improves but takes longer to converge.");
  }
}
