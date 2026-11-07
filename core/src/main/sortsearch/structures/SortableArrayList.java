package main.sortsearch.structures;

import main.sortsearch.monitoring.PerformanceMonitor;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Array-based implementation of SortableList and PerformanceMonitor.
 *
 * This implementation uses ArrayPosition objects as the actual elements in a
 * array list structure.
 *
 * @param <T> the type of elements stored in the list.
 *            pre: T must be Comparable.
 */
public class SortableArrayList<T extends Comparable<T>>
    implements SortableList<T>, PerformanceMonitor {
  protected final ArrayPosition<T>[] positions;
  protected int comparisonCount;
  protected int swapCount;

  /**
   * Creates a SortableArrayList with the given data.
   *
   * @param data the array of elements to initialize the list with
   * @throws IllegalArgumentException if data is null
   */
  @SuppressWarnings("unchecked")
  public SortableArrayList(T[] data) {
    if (data == null) {
      throw new IllegalArgumentException("Data array cannot be null");
    }

    // Create array of ArrayPosition objects
    this.positions = (ArrayPosition<T>[]) new ArrayPosition[data.length];

    // Initialize each position with its index and data
    for (int i = 0; i < data.length; i++) {
      this.positions[i] = new ArrayPosition<>(i, data[i]);
    }

    this.comparisonCount = 0;
    this.swapCount = 0;
  }

  @Override
  public int compare(Position pos1, Position pos2) {
    ArrayPosition<T> aPos1 = validatePosition(pos1);
    ArrayPosition<T> aPos2 = validatePosition(pos2);
    comparisonCount++;
    return aPos1.data.compareTo(aPos2.data);
  }

  @Override
  public void swap(Position pos1, Position pos2) {
    ArrayPosition<T> aPos1 = validatePosition(pos1);
    ArrayPosition<T> aPos2 = validatePosition(pos2);
    swapCount++;
    T temp = aPos1.data;
    aPos1.data = aPos2.data;
    aPos2.data = temp;
  }

  @Override
  public int size() {
    return positions.length;
  }

  @Override
  public Position getPosition(int index) {
    validateIndex(index);
    return positions[index];
  }

  @Override
  public T get(Position pos) {
    ArrayPosition<T> aPos = validatePosition(pos);
    return aPos.data;
  }

  @Override
  public Iterator<Position> iterator() {
    return new SortableArrayListIterator();
  }

  /**
   * Validates that the given index is within bounds.
   *
   * @param index the index to validate
   * @throws IndexOutOfBoundsException if index is negative or >= size()
   */
  private void validateIndex(int index) {
    if (index < 0 || index >= positions.length) {
      throw new IndexOutOfBoundsException(
          String.format("Index %d out of bounds for length %d", index, positions.length));
    }
  }

  /**
   * Validates that the given position is valid and belongs to this list.
   *
   * @param pos the position to validate
   * @return the validated ArrayPosition
   * @throws IllegalArgumentException if position is null or invalid
   */
  @SuppressWarnings("unchecked")
  private ArrayPosition<T> validatePosition(Position pos) {
    if (pos == null) {
      throw new IllegalArgumentException("Position cannot be null");
    }
    if (!(pos instanceof ArrayPosition)) {
      throw new IllegalArgumentException(
          "Position must be an ArrayPosition for this implementation");
    }
    if (pos.index < 0 || pos.index >= positions.length) {
      throw new IllegalArgumentException("Position index out of bounds: " + pos.index);
    }
    if (positions[pos.index] != pos) {
      throw new IllegalArgumentException("Position does not belong to this list");
    }
    return (ArrayPosition<T>) pos;
  }

  // PerformanceMonitor interface implementation
  @Override
  public void resetCounters() {
    comparisonCount = 0;
    swapCount = 0;
  }

  @Override
  public int getComparisonCount() {
    return comparisonCount;
  }

  @Override
  public int getModificationCount() {
    return swapCount;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("[");
    for (int i = 0; i < positions.length; i++) {
      if (i > 0)
        sb.append(", ");
      sb.append(positions[i].data);
    }
    sb.append("]");
    return sb.toString();
  }

  // Private nested class for array-based positions.
  // This encapsulates the position implementation details within the SortableArrayList.
  protected static class ArrayPosition<T> extends Position {
    public T data;

    ArrayPosition(int index, T data) {
      super(index);
      this.data = data;
    }

    @Override
    public String toString() {
      return "ArrayPosition[" + index + ":" + data + "]";
    }
  }

  /**
   * Iterator implementation for TrackedArrayList.
   * Returns Position objects instead of data elements.
   */
  private class SortableArrayListIterator implements Iterator<Position> {
    private int currentIndex = 0;

    @Override
    public boolean hasNext() {
      return currentIndex < positions.length;
    }

    @Override
    public Position next() {
      if (!hasNext()) {
        throw new NoSuchElementException("No more elements in the list");
      }
      return positions[currentIndex++];
    }
  }
}
