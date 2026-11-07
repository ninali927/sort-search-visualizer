package main.sortsearch.structures;

import main.sortsearch.monitoring.PerformanceMonitor;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Linked list-based implementation of SortableList and PerformanceMonitor.
 *
 * This implementation uses LinkPosition objects as the actual nodes in a
 * singly-linked list structure with a sentinel node.
 *
 * @param <T> the type of elements stored in the list.
 *            pre: T must be Comparable.
 */
public class SortableLinkedList<T extends Comparable<T>>
    implements SortableList<T>, PerformanceMonitor {
  protected final LinkPosition<T> sentinel;
  protected final int size;
  protected int comparisonCount;
  protected int swapCount;

  /**
   * Creates a SortableLinkedList with the given data.
   *
   * @param data the array of elements to initialize the list with
   * @throws IllegalArgumentException if data is null
   */
  public SortableLinkedList(T[] data) {
    if (data == null) {
      throw new IllegalArgumentException("Data array cannot be null");
    }

    this.sentinel = new LinkPosition<>(-1, null);
    this.size = data.length;

    LinkPosition<T> current = sentinel;
    for (int i = 0; i < data.length; i++) {
      LinkPosition<T> newNode = new LinkPosition<>(i, data[i]);
      current.next = newNode;
      current = newNode;
    }

    this.comparisonCount = 0;
    this.swapCount = 0;
  }

  @Override
  public int compare(Position pos1, Position pos2) {
    LinkPosition<T> lPos1 = validatePosition(pos1);
    LinkPosition<T> lPos2 = validatePosition(pos2);
    comparisonCount++;
    return lPos1.data.compareTo(lPos2.data);
  }

  @Override
  public void swap(Position pos1, Position pos2) {
    LinkPosition<T> lPos1 = validatePosition(pos1);
    LinkPosition<T> lPos2 = validatePosition(pos2);
    swapCount++;
    T temp = lPos1.data;
    lPos1.data = lPos2.data;
    lPos2.data = temp;
  }

  @Override
  public int size() {
    return size;
  }

  @Override
  public Position getPosition(int index) {
    validateIndex(index);
    LinkPosition<T> current = sentinel.next;
    for (int i = 0; i < index; i++) {
      current = current.next;
    }
    return current;
  }

  @Override
  public T get(Position pos) {
    LinkPosition<T> lPos = validatePosition(pos);
    return lPos.data;
  }

  @Override
  public Iterator<Position> iterator() {
    return new SortableLinkedListIterator();
  }

  /**
   * Validates that the given index is within bounds.
   *
   * @param index the index to validate
   * @throws IndexOutOfBoundsException if index is negative or >= size()
   */
  private void validateIndex(int index) {
    if (index < 0 || index >= size) {
      throw new IndexOutOfBoundsException(
          String.format("Index %d out of bounds for length %d", index, size));
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
  private LinkPosition<T> validatePosition(Position pos) {
    if (pos == null) {
      throw new IllegalArgumentException("Position cannot be null");
    }
    if (!(pos instanceof LinkPosition)) {
      throw new IllegalArgumentException(
          "Position must be an ArrayPosition for this implementation");
    }
    if (pos.index < 0 || pos.index >= size) {
      throw new IllegalArgumentException("Position index out of bounds: " + pos.index);
    }

    LinkPosition<T> current = sentinel.next;
    while (current != null) {
      if (current == pos) {
        return (LinkPosition<T>) pos;
      }
      current = current.next;
    }
    throw new IllegalArgumentException("Position does not belong to this list");
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
    LinkPosition<T> current = sentinel.next;
    for (int i = 0; i < size; i++) {
      if (i > 0)
        sb.append(", ");
      sb.append(current.data);
      current = current.next;
    }
    sb.append("]");
    return sb.toString();
  }

  // Protected nested class for linked list-based positions.
  // This encapsulates the position implementation details within the SortableLinkedList.
  protected static class LinkPosition<T> extends Position {
    public T data;
    public LinkPosition<T> next;

    public LinkPosition(int index, T data) {
      super(index);
      this.data = data;
      this.next = null;
    }

    @Override
    public String toString() {
      return "LinkPosition[" + index + ":" + data + "]";
    }
  }

  private class SortableLinkedListIterator implements Iterator<Position> {
    private LinkPosition<T> current = sentinel.next;

    @Override
    public boolean hasNext() {
      return current != null;
    }

    @Override
    public Position next() {
      if (!hasNext()) {
        throw new NoSuchElementException("No more elements in the list");
      }
      Position result = current;
      current = current.next;
      return result;
    }
  }
}
