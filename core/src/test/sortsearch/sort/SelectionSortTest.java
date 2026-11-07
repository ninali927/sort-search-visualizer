package test.sortsearch.sort;

import main.sortsearch.sort.SelectionSort;
import main.sortsearch.sort.SortingAlgorithm;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SelectionSortTest extends SortingAlgorithmTest {
  @Override
  public SortingAlgorithm<Integer> createSortingAlgorithm() {
    return new SelectionSort<>();
  }

  @Test
  public void testAlgorithmName() {
    assertEquals("Selection Sort", algorithm.getName());
  }
}
