package test.sortsearch.sort;

import main.sortsearch.sort.BinaryInsertionSort;
import main.sortsearch.sort.SortingAlgorithm;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BinaryInsertionSortTest extends SortingAlgorithmTest {
  @Override
  public SortingAlgorithm<Integer> createSortingAlgorithm() {
    return new BinaryInsertionSort<>();
  }

  @Test
  public void testAlgorithmName() {
    assertEquals("Binary Insertion Sort", algorithm.getName());
  }
}
