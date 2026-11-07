package test.sortsearch.sort;

import main.sortsearch.monitoring.PerformanceMonitor;
import main.sortsearch.sort.SortingAlgorithm;
import main.sortsearch.structures.SortableArrayList;
import main.sortsearch.structures.SortableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SortingAlgorithmTest is an abstract base test class that defines common setup and tests for all sorting algorithms.
 * It requires subclasses to implement createSortingAlgorithm(), which is used to initialize the algorithm field
 * before each test.
 *
 * This structure allows you to write generic tests in SortingAlgorithmTest that will be run
 * for all sorting algorithm implementations, ensuring consistency and reducing code duplication. Each subclass can also
 * add its own specific tests if needed. This is a standard and effective approach for testing
 * multiple implementations of a common interface.
 *
 * To run these tests, open the subclass test files (e.g., BubbleSortTest.java,
 * InsertionSortTest.java, etc.) and run the tests from there.
 */
public abstract class SortingAlgorithmTest {
  protected static final Integer[] EMPTY_DATA = {};
  protected static final Integer[] SINGLE_DATA = {42};
  protected static final Integer[] TWO_SORTED = {1, 2};
  protected static final Integer[] TWO_REVERSE = {2, 1};
  protected static final Integer[] THREE_RANDOM = {3, 1, 2};
  protected static final Integer[] THREE_SORTED = {1, 2, 3};
  protected static final Integer[] THREE_REVERSE = {3, 2, 1};
  protected static final Integer[] FIVE_RANDOM = {3, 1, 4, 1, 5};
  protected static final Integer[] FIVE_SORTED = {1, 1, 3, 4, 5};
  protected static final Integer[] FIVE_REVERSE = {5, 4, 3, 1, 1};
  protected static final Integer[] WITH_DUPLICATES = {5, 2, 8, 2, 9, 1, 5, 5};
  protected static final Integer[] ALL_SAME = {7, 7, 7, 7, 7};
  protected SortingAlgorithm<Integer> algorithm;

  public abstract SortingAlgorithm<Integer> createSortingAlgorithm();

  @BeforeEach
  public void setup() {
    algorithm = createSortingAlgorithm();
  }

  // --- Basic correctness tests ---

  @Test
  public void testSortEmptyList() {
    SortableList<Integer> list = new SortableArrayList<>(EMPTY_DATA.clone());
    assertDoesNotThrow(() -> algorithm.sort(list));
  }

  @Test
  public void testEmptyListRemainsEmpty() {
    SortableList<Integer> list = new SortableArrayList<>(EMPTY_DATA.clone());
    algorithm.sort(list);
    assertEquals(0, list.size());
  }

  @Test
  public void testSortSingleElement() {
    SortableList<Integer> list = new SortableArrayList<>(SINGLE_DATA.clone());
    assertDoesNotThrow(() -> algorithm.sort(list));
  }

  @Test
  public void testSingleElementRemainsUnchanged() {
    SortableList<Integer> list = new SortableArrayList<>(SINGLE_DATA.clone());
    algorithm.sort(list);
    assertEquals(1, list.size());
    assertEquals(Integer.valueOf(42), list.get(list.getPosition(0)));
  }

  @Test
  public void testSortTwoElementsAlreadySorted() {
    SortableList<Integer> list = new SortableArrayList<>(TWO_SORTED.clone());
    assertDoesNotThrow(() -> algorithm.sort(list));
  }

  @Test
  public void testTwoSortedElementsRemainInOrder() {
    SortableList<Integer> list = new SortableArrayList<>(TWO_SORTED.clone());
    algorithm.sort(list);
    assertEquals(Integer.valueOf(1), list.get(list.getPosition(0)));
    assertEquals(Integer.valueOf(2), list.get(list.getPosition(1)));
  }

  @Test
  public void testSortTwoElementsReverse() {
    SortableList<Integer> list = new SortableArrayList<>(TWO_REVERSE.clone());
    assertDoesNotThrow(() -> algorithm.sort(list));
  }

  @Test
  public void testTwoReverseElementsBecomeSorted() {
    SortableList<Integer> list = new SortableArrayList<>(TWO_REVERSE.clone());
    algorithm.sort(list);
    assertEquals(Integer.valueOf(1), list.get(list.getPosition(0)));
    assertEquals(Integer.valueOf(2), list.get(list.getPosition(1)));
  }

  @Test
  public void testSortThreeRandomElements() {
    SortableList<Integer> list = new SortableArrayList<>(THREE_RANDOM.clone());
    assertDoesNotThrow(() -> algorithm.sort(list));
  }

  @Test
  public void testThreeRandomElementsBecomeSorted() {
    SortableList<Integer> list = new SortableArrayList<>(THREE_RANDOM.clone());
    algorithm.sort(list);
    assertEquals(Integer.valueOf(1), list.get(list.getPosition(0)));
    assertEquals(Integer.valueOf(2), list.get(list.getPosition(1)));
    assertEquals(Integer.valueOf(3), list.get(list.getPosition(2)));
  }

  @Test
  public void testSortThreeAlreadySorted() {
    SortableList<Integer> list = new SortableArrayList<>(THREE_SORTED.clone());
    algorithm.sort(list);
    assertEquals(Integer.valueOf(1), list.get(list.getPosition(0)));
    assertEquals(Integer.valueOf(2), list.get(list.getPosition(1)));
    assertEquals(Integer.valueOf(3), list.get(list.getPosition(2)));
  }

  @Test
  public void testSortThreeReverse() {
    SortableList<Integer> list = new SortableArrayList<>(THREE_REVERSE.clone());
    algorithm.sort(list);
    assertEquals(Integer.valueOf(1), list.get(list.getPosition(0)));
    assertEquals(Integer.valueOf(2), list.get(list.getPosition(1)));
    assertEquals(Integer.valueOf(3), list.get(list.getPosition(2)));
  }

  @Test
  public void testSortFiveRandomElements() {
    SortableList<Integer> list = new SortableArrayList<>(FIVE_RANDOM.clone());
    algorithm.sort(list);
    assertEquals(Integer.valueOf(1), list.get(list.getPosition(0)));
    assertEquals(Integer.valueOf(1), list.get(list.getPosition(1)));
    assertEquals(Integer.valueOf(3), list.get(list.getPosition(2)));
    assertEquals(Integer.valueOf(4), list.get(list.getPosition(3)));
    assertEquals(Integer.valueOf(5), list.get(list.getPosition(4)));
  }

  @Test
  public void testSortFiveAlreadySorted() {
    SortableList<Integer> list = new SortableArrayList<>(FIVE_SORTED.clone());
    algorithm.sort(list);
    assertEquals(Integer.valueOf(1), list.get(list.getPosition(0)));
    assertEquals(Integer.valueOf(1), list.get(list.getPosition(1)));
    assertEquals(Integer.valueOf(3), list.get(list.getPosition(2)));
    assertEquals(Integer.valueOf(4), list.get(list.getPosition(3)));
    assertEquals(Integer.valueOf(5), list.get(list.getPosition(4)));
  }

  @Test
  public void testSortFiveReverse() {
    SortableList<Integer> list = new SortableArrayList<>(FIVE_REVERSE.clone());
    algorithm.sort(list);
    assertEquals(Integer.valueOf(1), list.get(list.getPosition(0)));
    assertEquals(Integer.valueOf(1), list.get(list.getPosition(1)));
    assertEquals(Integer.valueOf(3), list.get(list.getPosition(2)));
    assertEquals(Integer.valueOf(4), list.get(list.getPosition(3)));
    assertEquals(Integer.valueOf(5), list.get(list.getPosition(4)));
  }

  @Test
  public void testSortWithDuplicatesFirstElement() {
    SortableList<Integer> list = new SortableArrayList<>(WITH_DUPLICATES.clone());
    algorithm.sort(list);
    assertEquals(Integer.valueOf(1), list.get(list.getPosition(0)));
  }

  @Test
  public void testSortWithDuplicatesLastElement() {
    SortableList<Integer> list = new SortableArrayList<>(WITH_DUPLICATES.clone());
    algorithm.sort(list);
    assertEquals(Integer.valueOf(9), list.get(list.getPosition(list.size() - 1)));
  }

  @Test
  public void testSortWithAllSameElements() {
    SortableList<Integer> list = new SortableArrayList<>(ALL_SAME.clone());
    algorithm.sort(list);
    for (int i = 0; i < list.size(); i++) {
      assertEquals(Integer.valueOf(7), list.get(list.getPosition(i)));
    }
  }

  // --- Exception handling tests ---

  @Test
  public void testSortThrowsExceptionForNullList() {
    assertThrows(IllegalArgumentException.class, () -> algorithm.sort(null));
  }

  // --- Algorithm name tests ---

  @Test
  public void testAlgorithmNameIsNotNull() {
    assertNotNull(algorithm.getName());
  }

  @Test
  public void testAlgorithmNameIsNotEmpty() {
    assertFalse(algorithm.getName().trim().isEmpty());
  }

  // --- Basic performance tracking tests ---

  @Test
  public void testPerformanceCountersTrackComparisons() {
    SortableList<Integer> list = new SortableArrayList<>(FIVE_RANDOM.clone());
    PerformanceMonitor monitor = (PerformanceMonitor) list;
    monitor.resetCounters();
    algorithm.sort(list);
    assertTrue(monitor.getComparisonCount() > 0);
  }

  @Test
  public void testPerformanceCountersCanBeReset() {
    SortableList<Integer> list = new SortableArrayList<>(FIVE_RANDOM.clone());
    PerformanceMonitor monitor = (PerformanceMonitor) list;
    algorithm.sort(list);
    monitor.resetCounters();
    assertEquals(0, monitor.getComparisonCount());
    assertEquals(0, monitor.getModificationCount());
  }

  // --- List size preservation tests ---

  @Test
  public void testSortPreservesListSize() {
    SortableList<Integer> list = new SortableArrayList<>(FIVE_RANDOM.clone());
    int originalSize = list.size();
    algorithm.sort(list);
    assertEquals(originalSize, list.size());
  }

  @Test
  public void testSortPreservesEmptyListSize() {
    SortableList<Integer> list = new SortableArrayList<>(EMPTY_DATA.clone());
    algorithm.sort(list);
    assertEquals(0, list.size());
  }

  @Test
  public void testSortPreservesSingleElementSize() {
    SortableList<Integer> list = new SortableArrayList<>(SINGLE_DATA.clone());
    algorithm.sort(list);
    assertEquals(1, list.size());
  }

  // --- Element preservation tests ---

  @Test
  public void testSortPreservesAllElements() {
    SortableList<Integer> list = new SortableArrayList<>(WITH_DUPLICATES.clone());
    algorithm.sort(list);

    // Check that we have the right count of each element
    int count1 = 0, count2 = 0, count5 = 0, count8 = 0, count9 = 0;
    for (int i = 0; i < list.size(); i++) {
      Integer value = list.get(list.getPosition(i));
      if (value.equals(1))
        count1++;
      else if (value.equals(2))
        count2++;
      else if (value.equals(5))
        count5++;
      else if (value.equals(8))
        count8++;
      else if (value.equals(9))
        count9++;
    }

    assertEquals(1, count1);
    assertEquals(2, count2);
    assertEquals(3, count5);
    assertEquals(1, count8);
    assertEquals(1, count9);
  }

  // --- Helper methods ---

  protected boolean isSorted(SortableList<Integer> list) {
    for (int i = 0; i < list.size() - 1; i++) {
      if (list.compare(list.getPosition(i), list.getPosition(i + 1)) > 0) {
        return false;
      }
    }
    return true;
  }
}
