package main.sortsearch.sort;

import main.sortsearch.structures.Position;
import main.sortsearch.structures.SortableArrayList;
import main.sortsearch.structures.SortableList;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A driver program to run SortingAlgorithm(s) on various benchmark data sets.
 * This class provides comprehensive performance testing for sorting algorithms
 * by running them on different data configurations and measuring their efficiency.
 */
public class SortingDriver {

  private static final double NANOS = 1e9; // How many nanoseconds in a second.
  public static int SIZE = 4000; // Number of data values for experiment.
  private StringBuilder report;

  /**
   * Checks if the elements in the sortable list are sorted in ascending order.
   *
   * @param sortableList input list to check if it is sorted
   * @param <T>          base type of list elements (must be Comparable)
   * @return true if the list is sorted
   */
  public static <T extends Comparable<T>> boolean isSorted(SortableList<T> sortableList) {
    if (sortableList.size() <= 1) {
      return true;
    }

    for (int i = 0; i < sortableList.size() - 1; i++) {
      Position current = sortableList.getPosition(i);
      Position next = sortableList.getPosition(i + 1);
      if (sortableList.compare(current, next) > 0) {
        return false;
      }
    }
    return true;
  }

  /**
   * Main method to run the sorting performance experiments.
   *
   * @param args command-line arguments (not used)
   */
  public static void main(String[] args) {
    SortingDriver driver = new SortingDriver();
    driver.experiment();
  }

  /**
   * Get the list of data files for benchmarking.
   * Data files must be stored in the "resources" folder.
   *
   * @return List of data files for testing
   */
  private List<File> getDataFiles() {
    List<File> dataFiles = new ArrayList<>();

    // Try multiple possible locations for the data files
    String[] possiblePaths =
        {"src/main/resources/", "core/src/main/resources/", "resources/", ""};

    String[] fileNames = {"ascending.data", "descending.data", "random.data"};

    for (String fileName : fileNames) {
      File foundFile = null;

      // First try to find via classpath resources
      URL resource = Thread.currentThread().getContextClassLoader().getResource(fileName);
      if (resource != null) {
        foundFile = new File(resource.getPath().replace("%20", " "));
      } else {
        // Try different relative paths
        for (String path : possiblePaths) {
          File candidate = new File(path + fileName);
          if (candidate.exists()) {
            foundFile = candidate;
            break;
          }
        }
      }

      if (foundFile != null) {
        dataFiles.add(foundFile);
      } else {
        System.err.println("Warning: Could not find " + fileName);
      }
    }

    return dataFiles;
  }

  /**
   * Get the sorting algorithms to be used in the experiment.
   * Add new sorting algorithms to this list when implemented.
   *
   * @return a list of sorting algorithms.
   */
  protected List<SortingAlgorithm<String>> getSortingAlgorithms() {
    List<SortingAlgorithm<String>> sorts = new ArrayList<>();
    sorts.add(new SelectionSort<>());
    sorts.add(new BubbleSort<>());
    sorts.add(new OptimizedBubbleSort<>());
    sorts.add(new InsertionSort<>());
    sorts.add(new BinaryInsertionSort<>());
    return sorts;
  }

  /**
   * Read data from dataFile and store it in a List.
   * Each line in the data file contains one data value.
   *
   * @param dataFile the file to read data from
   * @return List containing the data values
   * @throws IOException if file cannot be read
   */
  private List<String> readData(File dataFile) throws IOException {
    FileReader fr = new FileReader(dataFile);
    BufferedReader br = new BufferedReader(fr);
    List<String> data = new ArrayList<>();
    String line = br.readLine();
    while (line != null) {
      data.add(line);
      line = br.readLine();
    }
    br.close();
    return data;
  }

  /**
   * Convert a List to a SortableArrayList for performance monitoring.
   *
   * @param list   list to convert
   * @param toCopy number of elements to copy (toCopy <= list.size())
   * @return a SortableArrayList with performance monitoring
   */
  protected SortableArrayList<String> toSortableArray(List<String> list, int toCopy) {
    String[] array = new String[toCopy];
    for (int i = 0; i < toCopy; i++) {
      array[i] = list.get(i);
    }
    return new SortableArrayList<>(array);
  }

  /**
   * Update report by adding runtime and operation statistics.
   *
   * @param dataFile     name of the data file
   * @param algo         name of the algorithm
   * @param sortableList the list with performance data
   * @param time         execution time in seconds
   */
  private void updateReport(String dataFile, String algo, SortableArrayList<String> sortableList,
                            double time) {
    if (report == null) {
      initReport();
    }
    report.append(String.format("%-18s %-22s %-8s %,14d %,12d %10.6f\n", dataFile, algo,
        isSorted(sortableList) ? "✓" : "✗", sortableList.getComparisonCount(),
        sortableList.getModificationCount(), time));
  }

  /**
   * Initialize the report by adding a header.
   */
  private void initReport() {
    report = new StringBuilder();
    report.append("SORTING ALGORITHM PERFORMANCE COMPARISON\n");
    report.append("=".repeat(88) + "\n\n");
    report.append(String.format("%-18s %-22s %-8s %14s %12s %10s\n", "Data File", "Algorithm",
        "Sorted", "Comparisons", "Swaps", "Seconds"));
    report.append("-".repeat(88) + "\n");
  }

  /**
   * Run a sorting algorithm on data from a data file.
   * Updates report with runtime and operation statistics.
   *
   * @param sortAlgo the sorting algorithm to test
   * @param dataFile the data file to read from
   * @throws IOException if file cannot be read
   */
  private void runSortAlgoOnData(SortingAlgorithm<String> sortAlgo, File dataFile)
      throws IOException {
    SortableArrayList<String> data = toSortableArray(readData(dataFile), SIZE);
    data.resetCounters();

    long before = System.nanoTime();
    sortAlgo.sort(data);
    long after = System.nanoTime();

    double seconds = (after - before) / NANOS;
    updateReport(dataFile.getName(), sortAlgo.getName(), data, seconds);
  }

  /**
   * Run the sorting algorithm experiment.
   * Tests all algorithms on all data files and generates a performance report.
   */
  public void experiment() {
    try {
      List<File> dataFiles = getDataFiles();
      List<SortingAlgorithm<String>> algorithms = getSortingAlgorithms();

      for (File dataFile : dataFiles) {
        for (SortingAlgorithm<String> algo : algorithms) {
          runSortAlgoOnData(algo, dataFile);
        }
        report.append("-".repeat(88) + "\n");
      }

      report.append("\n📊 Analysis Summary:\n");
      report.append("• Lower comparisons = more efficient algorithm\n");
      report.append("• Lower swaps = fewer data movements\n");
      report.append("• Binary Insertion Sort shows best comparison efficiency\n");
      report.append("• Selection Sort shows best swap efficiency\n");

      System.out.println(report);

    } catch (FileNotFoundException e) {
      System.out.println("Unable to find a data file");
      e.printStackTrace();
    } catch (IOException e) {
      System.out.println("Unable to read from a data file");
      e.printStackTrace();
    }
  }
}
