package test.sortsearch.sort;

import main.sortsearch.sort.BubbleSort;
import main.sortsearch.sort.SortingAlgorithm;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BubbleSortTest extends SortingAlgorithmTest {
  @Override
  public SortingAlgorithm<Integer> createSortingAlgorithm() {
    return new BubbleSort<>();
  }

  @Test
  public void testAlgorithmName() {
    assertEquals("Bubble Sort", algorithm.getName());
  }
}
