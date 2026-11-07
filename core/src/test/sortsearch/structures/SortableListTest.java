package test.sortsearch.structures;

import main.sortsearch.structures.Position;
import main.sortsearch.structures.SortableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SortableListTest is an abstract base test class that defines common setup and tests for all SortableList implementations.
 * It requires subclasses to implement createSortableList(), which is used to initialize the list field
 * before each test.
 *
 * This structure allows you to write generic tests in SortableListTest that will be run
 * for all SortableList implementations, ensuring consistency and reducing code duplication. Each subclass can also
 * add its own specific tests if needed. This is a standard and effective approach for testing
 * multiple implementations of a common interface.
 *
 * To run these tests, open the subclass test files (e.g., SortableArrayListTest.java,
 * SortableLinkedListTest.java) and run the tests from there.
 */
public abstract class SortableListTest {
  protected static final Integer[] DEFAULT_DATA = {30, 10, 40, 20};
  protected static final Integer[] EMPTY_DATA = {};
  protected static final Integer[] SINGLE_DATA = {50};
  protected SortableList<Integer> list;

  public abstract SortableList<Integer> createSortableList(Integer[] data);

  @BeforeEach
  public void setup() {
    list = createSortableList(DEFAULT_DATA);
  }

  @Test
  public void testConstructorCreatesList() {
    assertEquals(DEFAULT_DATA.length, list.size());
  }

  @Test
  public void testConstructorThrowsExceptionForNullData() {
    assertThrows(IllegalArgumentException.class, () -> {
      createSortableList(null);
    });
  }

  @Test
  public void testConstructorCreatesEmptyList() {
    SortableList<Integer> emptyList = createSortableList(EMPTY_DATA);
    assertEquals(0, emptyList.size());
  }

  @Test
  public void testConstructorCreatesSingleElementList() {
    SortableList<Integer> singleList = createSortableList(SINGLE_DATA);
    assertEquals(1, singleList.size());
  }

  @Test
  public void testSizeReturnsCorrectSize() {
    assertEquals(4, list.size());
  }

  @Test
  public void testGetPositionReturnsValidPosition() {
    Position pos = list.getPosition(0);
    assertNotNull(pos);
  }

  @Test
  public void testGetPositionReturnsPositionWithCorrectIndex() {
    Position pos = list.getPosition(2);
    assertEquals(2, pos.index);
  }

  @Test
  public void testGetPositionThrowsExceptionForNegativeIndex() {
    assertThrows(IndexOutOfBoundsException.class, () -> {
      list.getPosition(-1);
    });
  }

  @Test
  public void testGetPositionThrowsExceptionForIndexTooLarge() {
    assertThrows(IndexOutOfBoundsException.class, () -> {
      list.getPosition(list.size());
    });
  }

  @Test
  public void testGetReturnsCorrectElement() {
    Position pos = list.getPosition(0);
    Integer element = list.get(pos);
    assertEquals(DEFAULT_DATA[0], element);
  }

  @Test
  public void testGetThrowsExceptionForNullPosition() {
    assertThrows(IllegalArgumentException.class, () -> {
      list.get(null);
    });
  }

  @Test
  public void testComparePositionsReturnsNegativeWhenFirstSmaller() {
    Position pos1 = list.getPosition(1); // 10
    Position pos2 = list.getPosition(0); // 30
    int result = list.compare(pos1, pos2);
    assertTrue(result < 0);
  }

  @Test
  public void testComparePositionsReturnsPositiveWhenFirstLarger() {
    Position pos1 = list.getPosition(0); // 30
    Position pos2 = list.getPosition(1); // 10
    int result = list.compare(pos1, pos2);
    assertTrue(result > 0);
  }

  @Test
  public void testComparePositionsReturnsZeroWhenEqual() {
    Position pos1 = list.getPosition(0);
    Position pos2 = list.getPosition(0);
    int result = list.compare(pos1, pos2);
    assertEquals(0, result);
  }

  @Test
  public void testComparePositionsThrowsExceptionForNullFirstPosition() {
    Position pos2 = list.getPosition(1);
    assertThrows(IllegalArgumentException.class, () -> {
      list.compare(null, pos2);
    });
  }

  @Test
  public void testComparePositionsThrowsExceptionForNullSecondPosition() {
    Position pos1 = list.getPosition(0);
    assertThrows(IllegalArgumentException.class, () -> {
      list.compare(pos1, null);
    });
  }

  @Test
  public void testSwapExchangesElementsAtPositions() {
    Position pos1 = list.getPosition(0); // 30
    Position pos2 = list.getPosition(1); // 10

    list.swap(pos1, pos2);

    assertEquals(DEFAULT_DATA[1], list.get(pos1)); // pos1 now has 10
    assertEquals(DEFAULT_DATA[0], list.get(pos2)); // pos2 now has 30
  }

  @Test
  public void testSwapWithSamePositionHasNoEffect() {
    Position pos = list.getPosition(0);
    Integer originalValue = list.get(pos);

    list.swap(pos, pos);

    assertEquals(originalValue, list.get(pos));
  }

  @Test
  public void testSwapThrowsExceptionForNullFirstPosition() {
    Position pos2 = list.getPosition(1);
    assertThrows(IllegalArgumentException.class, () -> {
      list.swap(null, pos2);
    });
  }

  @Test
  public void testSwapThrowsExceptionForNullSecondPosition() {
    Position pos1 = list.getPosition(0);
    assertThrows(IllegalArgumentException.class, () -> {
      list.swap(pos1, null);
    });
  }

  @Test
  public void testIteratorReturnsAllPositions() {
    int count = 0;
    for (Position pos : list) {
      assertNotNull(pos);
      count++;
    }
    assertEquals(list.size(), count);
  }

  @Test
  public void testIteratorReturnsPositionsInOrder() {
    int expectedIndex = 0;
    for (Position pos : list) {
      assertEquals(expectedIndex, pos.index);
      expectedIndex++;
    }
  }

  @Test
  public void testIteratorWorksForEmptyList() {
    SortableList<Integer> emptyList = createSortableList(EMPTY_DATA);
    int count = 0;
    for (Position pos : emptyList) {
      count++;
    }
    assertEquals(0, count);
  }

  @Test
  public void testPositionIndexIsImmutable() {
    Position pos = list.getPosition(2);
    int originalIndex = pos.index;

    // Swap with another position
    list.swap(pos, list.getPosition(0));

    assertEquals(originalIndex, pos.index);
  }
}
