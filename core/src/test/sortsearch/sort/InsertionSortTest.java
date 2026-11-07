package test.sortsearch.sort;

import main.sortsearch.sort.InsertionSort;
import main.sortsearch.sort.SortingAlgorithm;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InsertionSortTest extends SortingAlgorithmTest {
  @Override
  public SortingAlgorithm<Integer> createSortingAlgorithm() {
    return new InsertionSort<>();
  }

  @Test
  public void testAlgorithmName() {
    assertEquals("Insertion Sort", algorithm.getName());
  }
}
