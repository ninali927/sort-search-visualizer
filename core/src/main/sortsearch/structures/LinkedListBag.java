package main.sortsearch.structures;

import main.sortsearch.monitoring.PerformanceMonitor;

/**
 * Linked list-based implementation of Bag using move-to-front optimization.
 * 
 * This implementation uses a singly-linked list to store elements and implements
 * the move-to-front optimization: when an element is found, it moves to the
 * front of the list. This provides immediate access to recently found elements
 * on subsequent searches.
 * 
 * The move-to-front method is ideal for linked lists because it only requires
 * O(1) pointer manipulation.
 *
 * @param <T> the type of elements stored in the bag.
 *            pre: T must be Comparable.
 */
public class LinkedListBag<T extends Comparable<T>> implements Bag<T>, PerformanceMonitor {
  protected Node<T> head;
  protected int size;
  protected int comparisonCount;
  protected int modificationCount;

  /**
   * Creates a LinkedListBag with the given data.
   *
   * @param data the array of elements to initialize the bag with
   * @throws IllegalArgumentException if data is null
   */
  public LinkedListBag(T[] data) {
    if (data == null) {
      throw new IllegalArgumentException("Data array cannot be null");
    }

    this.size = data.length;
    this.comparisonCount = 0;
    this.modificationCount = 0;

    // Build the linked list from the array
    if (data.length == 0) {
      this.head = null;
      return;
    }

    // Create nodes and link them
    this.head = new Node<>(data[0]);
    Node<T> current = this.head;

    for (int i = 1; i < data.length; i++) {
      current.next = new Node<>(data[i]);
      current = current.next;
    }
  }

  // Compares the target element with the data in a given node.
  // Should return 0 if the elements are equal.
  protected int compare(Node<T> node, T element) {
    comparisonCount++;
    return node.data.compareTo(element);
  }

  // Moves the given node to the front of the list.
  protected void moveToFront(Node<T> previous, Node<T> current) {
    if (current == head) {
      return;
    }
    modificationCount++;

    previous.next = current.next;
    current.next = head;
    head = current;
  }

  @Override
  public boolean contains(T element) {
    if (element == null) {
      throw new IllegalArgumentException("Element cannot be null");
    }

    Node<T> previous = null;
    Node<T> current = head;

    while (current != null) {
      if (compare(current, element) == 0) {
        moveToFront(previous, current);
        return true;
      }
      previous = current;
      current = current.next;
    }
    return false;
  }

  @Override
  public boolean isEmpty() {
    return head == null;
  }

  // PerformanceMonitor interface implementation
  @Override
  public void resetCounters() {
    comparisonCount = 0;
    modificationCount = 0;
  }

  @Override
  public int getComparisonCount() {
    return comparisonCount;
  }

  @Override
  public int getModificationCount() {
    return modificationCount;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("LinkedListBag[");

    Node<T> current = head;
    boolean first = true;
    while (current != null) {
      if (!first)
        sb.append(", ");
      sb.append(current.data);
      current = current.next;
      first = false;
    }

    sb.append("]");
    return sb.toString();
  }

  /**
   * Private nested class for linked list nodes.
   */
  protected static class Node<T> {
    public T data;
    public Node<T> next;

    Node(T data) {
      this.data = data;
      this.next = null;
    }
  }
}
