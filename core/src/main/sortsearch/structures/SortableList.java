package main.sortsearch.structures;

/**
 * Interface for lists that can be sorted using Position-based algorithms.
 *
 * This interface provides a uniform way for sorting algorithms to work with
 * different list implementations (array-based, linked-list-based, etc.).
 *
 * Notice the use of the Position abstraction, which represents a logical
 * position in the list. Algorithms work with Position objects rather than
 * direct indices.
 *
 * @param <T> the type of elements stored in the list.
 *            pre: T must be Comparable.
 */
public interface SortableList<T extends Comparable<T>> extends Iterable<Position> {

  /**
   * Compare the elements stored at two positions.
   *
   * @param pos1 the first position
   * @param pos2 the second position
   * @return negative integer if element at pos1 < element at pos2,
   * zero if element at pos1 equals element at pos2,
   * positive integer if element at pos1 > element at pos2
   * @throws IllegalArgumentException if either position is null or invalid
   */
  int compare(Position pos1, Position pos2);

  /**
   * Swap the elements stored at two positions.
   * This method efficiently exchanges the data stored at the two positions.
   *
   * @param pos1 the first position
   * @param pos2 the second position
   * @throws IllegalArgumentException if either position is null or invalid
   */
  void swap(Position pos1, Position pos2);

  /**
   * Get the number of elements in the list.
   *
   * @return the size of the list
   */
  int size();

  /**
   * Get the position at the specified index.
   * This method provides access to positions for algorithmic operations.
   *
   * @param index the index of the position to retrieve
   * @return the Position object at the specified index
   * @throws IndexOutOfBoundsException if index is invalid
   */
  Position getPosition(int index);

  /**
   * Get the element (data stored) at the specified position.
   * This method is provided for read-only access to the list elements.
   * It should be used only for displaying results or debugging, not for
   * algorithmic comparisons.
   *
   * @param pos the position of the element to retrieve
   * @return the element at the specified position
   * @throws IndexOutOfBoundsException if position is null or invalid
   */
  T get(Position pos);

}
