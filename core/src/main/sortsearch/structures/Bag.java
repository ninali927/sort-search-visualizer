package main.sortsearch.structures;

/**
 * A Bag represents a collection of elements that can be efficiently searched
 * for membership.
 *
 * @param <T> the type of elements stored in the bag.
 *            pre: T must be Comparable.
 */
public interface Bag<T extends Comparable<T>> {

  /**
   * Check if the bag contains the specified element.
   *
   * This method may modify the internal structure of the bag to optimize
   * future searches (e.g., move-to-front or transpose optimizations).
   * The specific optimization strategy depends on the implementation.
   *
   * @param element the element to search for
   * @return true if the element is found, false otherwise
   * @throws IllegalArgumentException if element is null
   */
  boolean contains(T element);

  /**
   * Check if the bag is empty.
   *
   * @return true if the bag contains no elements, false otherwise
   */
  boolean isEmpty();
}
