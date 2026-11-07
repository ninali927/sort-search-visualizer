package test.sortsearch.sort;

import main.sortsearch.sort.OptimizedBubbleSort;
import main.sortsearch.sort.SortingAlgorithm;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OptimizedBubbleSortTest extends SortingAlgorithmTest {
  @Override
  public SortingAlgorithm<Integer> createSortingAlgorithm() {
    return new OptimizedBubbleSort<>();
  }

  @Test
  public void testAlgorithmName() {
    assertEquals("Optimized Bubble Sort", algorithm.getName());
  }
}
