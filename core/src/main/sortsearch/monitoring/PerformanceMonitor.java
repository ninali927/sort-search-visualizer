package main.sortsearch.monitoring;

/**
 * Interface for monitoring performance metrics of data structure operations.
 *
 * This interface provides a standardized way to track and measure the performance
 * of algorithms by counting comparisons and structural modifications. It can be
 * implemented by any data structure that wants to provide performance monitoring
 * capabilities.
 *
 * The interface distinguishes between:
 * - Comparisons: Element comparisons made during operations
 * - Modifications: Structural changes like swaps, moves, or reorganizations
 */
public interface PerformanceMonitor {
  /**
   * Reset both comparison and modification counters to zero.
   * This should be called at the beginning of each algorithm execution
   * to get clean measurements.
   */
  void resetCounters();

  /**
   * Get the total number of comparisons performed since the last reset.
   *
   * @return the number of comparison operations performed
   */
  int getComparisonCount();

  /**
   * Get the total number of structural modifications performed since the last reset.
   *
   * @return the number of modification operations performed
   */
  int getModificationCount();
}
