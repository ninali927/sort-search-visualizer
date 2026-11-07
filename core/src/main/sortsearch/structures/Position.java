package main.sortsearch.structures;

/**
 * Abstract base class representing a position in a list.
 *
 * A Position encapsulates the logical index of an element within a list,
 * providing a uniform interface for various list implementations.
 * Concrete subclasses handle the actual data storage and any implementation-specific
 * details.
 */
public abstract class Position {
  /**
   * The logical index of this position within the list.
   * This index represents where this position appears in the conceptual
   * sequence, regardless of the underlying storage mechanism.
   */
  public final int index;

  /**
   * Creates a new Position with the specified logical index.
   *
   * @param index the logical index of this position (0-based)
   */
  protected Position(int index) {
    this.index = index;
  }

  /**
   * Returns a string representation of this position.
   * Subclasses should override this to provide more detailed information.
   *
   * @return string representation including the index
   */
  @Override
  public String toString() {
    return "Position[" + index + "]";
  }
}
