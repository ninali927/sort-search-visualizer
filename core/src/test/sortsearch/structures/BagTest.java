package test.sortsearch.structures;

import main.sortsearch.monitoring.PerformanceMonitor;
import main.sortsearch.structures.Bag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * BagTest is an abstract base test class that defines common setup and tests for all Bag implementations.
 * It requires subclasses to implement createBag(), which is used to initialize the bag field
 * before each test.
 *
 * This structure allows you to write generic tests in BagTest that will be run
 * for all bag implementations, ensuring consistency and reducing code duplication. Each subclass can also
 * add its own specific tests if needed. This is a standard and effective approach for testing
 * multiple implementations of a common interface.
 *
 * To run these tests, open the subclass test files (e.g., ArrayBagTest.java,
 * LinkedListBagTest.java) and run the tests from there.
 */
public abstract class BagTest {
  protected static final Integer[] DEFAULT_DATA = {10, 20, 30, 40, 50};
  protected static final Integer[] EMPTY_DATA = {};
  protected static final Integer[] SINGLE_DATA = {42};
  protected static final Integer[] DUPLICATE_DATA = {1, 2, 2, 3, 3, 3};
  protected Bag<Integer> bag;
  protected PerformanceMonitor monitor;

  public abstract Bag<Integer> createBag(Integer[] data);

  @BeforeEach
  public void setup() {
    bag = createBag(DEFAULT_DATA);
    monitor = (PerformanceMonitor) bag;
  }

  // --- Constructor tests ---

  @Test
  public void testConstructorCreatesNonEmptyBag() {
    assertFalse(bag.isEmpty());
  }

  @Test
  public void testConstructorThrowsExceptionForNullData() {
    assertThrows(IllegalArgumentException.class, () -> {
      createBag(null);
    });
  }

  @Test
  public void testConstructorCreatesEmptyBag() {
    Bag<Integer> emptyBag = createBag(EMPTY_DATA);
    assertTrue(emptyBag.isEmpty());
  }

  @Test
  public void testConstructorCreatesSingleElementBag() {
    Bag<Integer> singleBag = createBag(SINGLE_DATA);
    assertFalse(singleBag.isEmpty());
  }

  // --- isEmpty tests ---

  @Test
  public void testIsEmptyReturnsTrueForEmptyBag() {
    Bag<Integer> emptyBag = createBag(EMPTY_DATA);
    assertTrue(emptyBag.isEmpty());
  }

  @Test
  public void testIsEmptyReturnsFalseForNonEmptyBag() {
    assertFalse(bag.isEmpty());
  }

  @Test
  public void testIsEmptyReturnsFalseForSingleElementBag() {
    Bag<Integer> singleBag = createBag(SINGLE_DATA);
    assertFalse(singleBag.isEmpty());
  }

  // --- contains tests for existing elements ---

  @Test
  public void testContainsReturnsTrueForFirstElement() {
    assertTrue(bag.contains(DEFAULT_DATA[0]));
  }

  @Test
  public void testContainsReturnsTrueForLastElement() {
    assertTrue(bag.contains(DEFAULT_DATA[DEFAULT_DATA.length - 1]));
  }

  @Test
  public void testContainsReturnsTrueForMiddleElement() {
    int middleIndex = DEFAULT_DATA.length / 2;
    assertTrue(bag.contains(DEFAULT_DATA[middleIndex]));
  }

  @Test
  public void testContainsReturnsTrueForSecondElement() {
    assertTrue(bag.contains(DEFAULT_DATA[1]));
  }

  @Test
  public void testContainsReturnsTrueForSecondToLastElement() {
    assertTrue(bag.contains(DEFAULT_DATA[DEFAULT_DATA.length - 2]));
  }

  // --- contains tests for non-existing elements ---

  @Test
  public void testContainsReturnsFalseForNonExistentElement() {
    assertFalse(bag.contains(999));
  }

  @Test
  public void testContainsReturnsFalseForZero() {
    assertFalse(bag.contains(0));
  }

  @Test
  public void testContainsReturnsFalseForNegativeNumber() {
    assertFalse(bag.contains(-1));
  }

  @Test
  public void testContainsReturnsFalseForElementNotInRange() {
    assertFalse(bag.contains(100));
  }

  // --- contains exception tests ---

  @Test
  public void testContainsThrowsExceptionForNullElement() {
    assertThrows(IllegalArgumentException.class, () -> {
      bag.contains(null);
    });
  }

  // --- contains tests for edge cases ---

  @Test
  public void testContainsReturnsFalseInEmptyBag() {
    Bag<Integer> emptyBag = createBag(EMPTY_DATA);
    assertFalse(emptyBag.contains(1));
  }

  @Test
  public void testContainsReturnsTrueInSingleElementBag() {
    Bag<Integer> singleBag = createBag(SINGLE_DATA);
    assertTrue(singleBag.contains(SINGLE_DATA[0]));
  }

  @Test
  public void testContainsReturnsFalseForWrongElementInSingleElementBag() {
    Bag<Integer> singleBag = createBag(SINGLE_DATA);
    assertFalse(singleBag.contains(999));
  }

  @Test
  public void testContainsReturnsTrueForDuplicateElement() {
    Bag<Integer> dupBag = createBag(DUPLICATE_DATA);
    assertTrue(dupBag.contains(2)); // appears multiple times
  }

  @Test
  public void testContainsReturnsTrueForTriplicateElement() {
    Bag<Integer> dupBag = createBag(DUPLICATE_DATA);
    assertTrue(dupBag.contains(3)); // appears three times
  }

  @Test
  public void testContainsReturnsFalseForNonExistentInDuplicateBag() {
    Bag<Integer> dupBag = createBag(DUPLICATE_DATA);
    assertFalse(dupBag.contains(4)); // not in duplicate data
  }

  // --- Performance monitoring tests ---

  @Test
  public void testPerformanceMonitorTracksComparisons() {
    monitor.resetCounters();
    bag.contains(DEFAULT_DATA[2]); // middle element
    assertTrue(monitor.getComparisonCount() > 0);
  }

  @Test
  public void testPerformanceMonitorResetsComparisonCount() {
    bag.contains(DEFAULT_DATA[0]);
    assertTrue(monitor.getComparisonCount() > 0);
    monitor.resetCounters();
    assertEquals(0, monitor.getComparisonCount());
  }

  @Test
  public void testPerformanceMonitorResetsModificationCount() {
    bag.contains(DEFAULT_DATA[0]);
    monitor.resetCounters();
    assertEquals(0, monitor.getModificationCount());
  }

  @Test
  public void testPerformanceMonitorTracksSearchForFirstElement() {
    monitor.resetCounters();
    bag.contains(DEFAULT_DATA[0]);
    assertTrue(monitor.getComparisonCount() >= 1);
  }

  @Test
  public void testPerformanceMonitorTracksSearchForLastElement() {
    monitor.resetCounters();
    bag.contains(DEFAULT_DATA[DEFAULT_DATA.length - 1]);
    assertTrue(monitor.getComparisonCount() >= 1);
  }

  @Test
  public void testPerformanceMonitorTracksFailedSearch() {
    monitor.resetCounters();
    bag.contains(999);
    assertTrue(monitor.getComparisonCount() > 0);
  }

  // --- Basic optimization behavior tests ---

  @Test
  public void testFirstSearchEstablishesBaseline() {
    Integer target = DEFAULT_DATA[DEFAULT_DATA.length - 1]; // worst case element
    monitor.resetCounters();
    assertTrue(bag.contains(target));
    int baselineComparisons = monitor.getComparisonCount();
    assertTrue(baselineComparisons > 0);
  }

  @Test
  public void testSecondSearchDoesNotPerformWorse() {
    Integer target = DEFAULT_DATA[DEFAULT_DATA.length - 1]; // worst case element

    // First search
    monitor.resetCounters();
    bag.contains(target);
    int firstComparisons = monitor.getComparisonCount();

    // Second search should not be worse
    monitor.resetCounters();
    bag.contains(target);
    int secondComparisons = monitor.getComparisonCount();

    assertTrue(secondComparisons <= firstComparisons);
  }

  @Test
  public void testOptimizationDoesNotAffectMembership() {
    Integer target = DEFAULT_DATA[2];

    // Search multiple times
    for (int i = 0; i < 3; i++) {
      assertTrue(bag.contains(target));
    }

    // Element should still be found
    assertTrue(bag.contains(target));
  }

  @Test
  public void testOptimizationDoesNotAffectOtherElements() {
    Integer target = DEFAULT_DATA[DEFAULT_DATA.length - 1];

    // Repeatedly search for one element
    for (int i = 0; i < 3; i++) {
      bag.contains(target);
    }

    // All other elements should still be findable
    assertTrue(bag.contains(DEFAULT_DATA[0]));
  }

  @Test
  public void testOptimizationDoesNotCreateFalsePositives() {
    Integer target = DEFAULT_DATA[DEFAULT_DATA.length - 1];

    // Optimize structure with existing element
    for (int i = 0; i < 3; i++) {
      bag.contains(target);
    }

    // Non-existent element should still return false
    assertFalse(bag.contains(999));
  }
}
