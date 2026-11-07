package test.sortsearch.monitoring;

import main.sortsearch.monitoring.PerformanceMonitor;
import main.sortsearch.structures.ArrayBag;
import main.sortsearch.structures.LinkedListBag;
import main.sortsearch.structures.SortableArrayList;
import main.sortsearch.structures.SortableLinkedList;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Focused tests for PerformanceMonitor functionality across all implementations.
 * These tests verify that performance counters are accurate, consistent, and properly
 * track the intended operations.
 */
public class PerformanceMonitorTest {

  // --- SortableArrayList performance monitoring tests ---

  @Test
  public void testSortableArrayListTracksComparisons() {
    Integer[] data = {3, 1, 4};
    SortableArrayList<Integer> list = new SortableArrayList<>(data);
    PerformanceMonitor monitor = list;

    monitor.resetCounters();
    list.compare(list.getPosition(0), list.getPosition(1));

    assertEquals(1, monitor.getComparisonCount());
  }

  @Test
  public void testSortableArrayListTracksMultipleComparisons() {
    Integer[] data = {3, 1, 4, 1, 5};
    SortableArrayList<Integer> list = new SortableArrayList<>(data);
    PerformanceMonitor monitor = list;

    monitor.resetCounters();
    list.compare(list.getPosition(0), list.getPosition(1));
    list.compare(list.getPosition(2), list.getPosition(3));
    list.compare(list.getPosition(1), list.getPosition(4));

    assertEquals(3, monitor.getComparisonCount());
  }

  @Test
  public void testSortableArrayListTracksSwaps() {
    Integer[] data = {3, 1, 4};
    SortableArrayList<Integer> list = new SortableArrayList<>(data);
    PerformanceMonitor monitor = list;

    monitor.resetCounters();
    list.swap(list.getPosition(0), list.getPosition(1));

    assertEquals(1, monitor.getComparisonCount());
  }

  @Test
  public void testSortableArrayListTracksMultipleSwaps() {
    Integer[] data = {3, 1, 4, 1, 5};
    SortableArrayList<Integer> list = new SortableArrayList<>(data);
    PerformanceMonitor monitor = list;

    monitor.resetCounters();
    list.swap(list.getPosition(0), list.getPosition(1));
    list.swap(list.getPosition(2), list.getPosition(3));
    list.swap(list.getPosition(1), list.getPosition(4));

    assertEquals(3, monitor.getComparisonCount());
  }

  @Test
  public void testSortableArrayListResetCounters() {
    Integer[] data = {3, 1, 4};
    SortableArrayList<Integer> list = new SortableArrayList<>(data);
    PerformanceMonitor monitor = list;

    list.compare(list.getPosition(0), list.getPosition(1));
    list.swap(list.getPosition(0), list.getPosition(1));
    assertEquals(1, monitor.getComparisonCount());
    assertEquals(1, monitor.getModificationCount());

    monitor.resetCounters();
    assertEquals(0, monitor.getComparisonCount());
    assertEquals(0, monitor.getModificationCount());
  }

  // --- SortableLinkedList performance monitoring tests ---
  @Test
  public void testSortableLinkedListTracksComparisons() {
    Integer[] data = {3, 1, 4};
    SortableLinkedList<Integer> list = new SortableLinkedList<>(data);
    PerformanceMonitor monitor = list;

    monitor.resetCounters();
    list.compare(list.getPosition(0), list.getPosition(1));

    assertEquals(1, monitor.getComparisonCount());
  }

  @Test
  public void testSortableLinkedListTracksMultipleComparisons() {
    Integer[] data = {3, 1, 4, 1, 5};
    SortableLinkedList<Integer> list = new SortableLinkedList<>(data);
    PerformanceMonitor monitor = list;

    monitor.resetCounters();
    list.compare(list.getPosition(0), list.getPosition(1));
    list.compare(list.getPosition(2), list.getPosition(3));
    list.compare(list.getPosition(1), list.getPosition(4));

    assertEquals(3, monitor.getComparisonCount());
  }

  @Test
  public void testSortableLinkedListTracksSwaps() {
    Integer[] data = {3, 1, 4};
    SortableLinkedList<Integer> list = new SortableLinkedList<>(data);
    PerformanceMonitor monitor = list;

    monitor.resetCounters();
    list.swap(list.getPosition(0), list.getPosition(1));

    assertEquals(1, monitor.getComparisonCount());
  }



  @Test
  public void testSortableLinkedListTracksMultipleSwaps() {
    Integer[] data = {3, 1, 4, 1, 5};
    SortableLinkedList<Integer> list = new SortableLinkedList<>(data);
    PerformanceMonitor monitor = list;

    monitor.resetCounters();
    list.swap(list.getPosition(0), list.getPosition(1));
    list.swap(list.getPosition(2), list.getPosition(3));
    list.swap(list.getPosition(1), list.getPosition(4));

    assertEquals(3, monitor.getComparisonCount());
  }

  @Test
  public void testSortableLinkedListResetCounters() {
    Integer[] data = {3, 1, 4};
    SortableLinkedList<Integer> list = new SortableLinkedList<>(data);
    PerformanceMonitor monitor = list;

    list.compare(list.getPosition(0), list.getPosition(1));
    list.swap(list.getPosition(0), list.getPosition(1));
    assertEquals(1, monitor.getComparisonCount());
    assertEquals(1, monitor.getModificationCount());

    monitor.resetCounters();
    assertEquals(0, monitor.getComparisonCount());
    assertEquals(0, monitor.getModificationCount());
  }


  // --- ArrayBag performance monitoring tests ---
  @Test
  public void testArrayBagTracksComparisons() {
    Integer[] data = {3, 1, 4, 1, 5};
    ArrayBag<Integer> bag = new ArrayBag<>(data);
    PerformanceMonitor monitor = bag;

    monitor.resetCounters();
    bag.contains(1);

    assertEquals(2, monitor.getComparisonCount());
  }

  @Test
  public void testArrayBagComparisonNotFound() {
    Integer[] data = {3, 1, 4, 1, 5};
    ArrayBag<Integer> bag = new ArrayBag<>(data);
    PerformanceMonitor monitor = bag;

    monitor.resetCounters();
    boolean found = bag.contains(2);

    assertFalse(found);
    assertEquals(5, monitor.getComparisonCount());
  }

  @Test
  public void testArrayBagTranspose() {
    Integer[] data = {3, 1, 4, 1, 5};
    ArrayBag<Integer> bag = new ArrayBag<>(data);
    PerformanceMonitor monitor = bag;

    monitor.resetCounters();
    bag.contains(4);

    assertEquals(3, monitor.getComparisonCount());
    assertEquals(1, monitor.getModificationCount());
  }

  @Test
  public void testArrayBagTransposeNotNeeded() {
    Integer[] data = {3, 1, 4, 1, 5};
    ArrayBag<Integer> bag = new ArrayBag<>(data);
    PerformanceMonitor monitor = bag;

    monitor.resetCounters();
    bag.contains(3);

    assertEquals(1, monitor.getComparisonCount());
    assertEquals(0, monitor.getModificationCount());
  }

  @Test
  public void testArrayBagResetCounters() {
    Integer[] data = {3, 1, 4};
    ArrayBag<Integer> bag = new ArrayBag<>(data);
    PerformanceMonitor monitor = bag;

    bag.contains(3);
    assertTrue(monitor.getComparisonCount() > 0);
    assertTrue(monitor.getModificationCount() > 0);

    monitor.resetCounters();
    assertEquals(0, monitor.getComparisonCount());
    assertEquals(0, monitor.getModificationCount());
  }


  // --- LinkedListBag performance monitoring tests ---
  @Test
  public void testLinkedListBagTracksComparisons() {
    Integer[] data = {3, 1, 4, 1, 5};
    LinkedListBag<Integer> bag = new LinkedListBag<>(data);
    PerformanceMonitor monitor = bag;

    monitor.resetCounters();
    bag.contains(1);

    assertEquals(2, monitor.getComparisonCount());
  }

  @Test
  public void testLinkedListBagComparisonNotFound() {
    Integer[] data = {3, 1, 4, 1, 5};
    LinkedListBag<Integer> bag = new LinkedListBag<>(data);
    PerformanceMonitor monitor = bag;

    monitor.resetCounters();
    boolean found = bag.contains(2);

    assertFalse(found);
    assertEquals(5, monitor.getComparisonCount());
  }

  @Test
  public void testLinkedListBagMoveToFront() {
    Integer[] data = {3, 1, 4, 1, 5};
    LinkedListBag<Integer> bag = new LinkedListBag<>(data);
    PerformanceMonitor monitor = bag;

    monitor.resetCounters();
    bag.contains(4);

    assertEquals(3, monitor.getComparisonCount());
    assertEquals(1, monitor.getModificationCount());
  }


  @Test
  public void testLinkedListBagMoveToFrontNotNeeded() {
    Integer[] data = {3, 1, 4, 1, 5};
    LinkedListBag<Integer> bag = new LinkedListBag<>(data);
    PerformanceMonitor monitor = bag;

    monitor.resetCounters();
    bag.contains(3);

    assertEquals(1, monitor.getComparisonCount());
    assertEquals(0, monitor.getModificationCount());
  }

  @Test
  public void testLinkedListBagResetCounters() {
    Integer[] data = {3, 1, 4, 1, 5};
    LinkedListBag<Integer> bag = new LinkedListBag<>(data);
    PerformanceMonitor monitor = bag;

    bag.contains(3);
    assertTrue(monitor.getComparisonCount() > 0);
    assertTrue(monitor.getModificationCount() > 0);

    monitor.resetCounters();
    assertEquals(0, monitor.getComparisonCount());
    assertEquals(0, monitor.getModificationCount());
  }

}
