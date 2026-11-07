# Sort & Search Visualizer

A Java project that implements classic quadratic sorting algorithms and two self-organizing
search strategies, instruments them to count comparisons and swaps, and visualizes them step by
step in the browser over a WebSocket.

## What it demonstrates

**Sorting.** Five algorithms run against a common `SortableList` interface. That interface only
exposes `compare` and `swap`, so every algorithm is measured the same way:

| Algorithm             | Notes                                                       |
| --------------------- | ----------------------------------------------------------- |
| Bubble Sort           | Always performs all n−1 passes                              |
| Optimized Bubble Sort | Stops early after a pass with no swaps                      |
| Insertion Sort        | Adaptive: fast on nearly sorted input                       |
| Binary Insertion Sort | Finds the insertion point with binary search (O(log n) comparisons) |
| Selection Sort        | Same comparisons on any input, at most n−1 swaps            |

Each algorithm runs on both an array-backed and a linked-list-backed `SortableList`.

**Adaptive search.** Two implementations of `Bag.contains` reorder the collection so that
elements searched often get faster to find:

- `ArrayBag` uses **transpose**: a found element swaps one step toward the front. That is O(1)
  for an array.
- `LinkedListBag` uses **move-to-front**: a found node is relinked to the head. That is O(1)
  for a linked list.

### Observations

- With skewed and repeated access patterns, move-to-front clearly beats transpose. In
  `SearchDemo`, skewed access costs 80 vs. 112 total operations and temporal locality costs
  35 vs. 66.
- `SortingDriver` reads each data file as **strings**. Strings compare lexicographically, so
  `"100" < "2"`, and the numerically ascending file is not actually sorted. As a result, the
  early-exit algorithms still do millions of comparisons on `ascending.data` (for example,
  about 3.5 million for Optimized Bubble Sort).

## Project layout

```
.
├── start-visualization.sh      # Launches the web visualizer on port 7001
├── core/                       # Algorithms and data structures (plain Java)
│   └── src/
│       ├── main/sortsearch/
│       │   ├── sort/           # Sorting algorithms + SortingDriver benchmark
│       │   ├── structures/     # SortableList, Bag and their implementations
│       │   ├── monitoring/     # PerformanceMonitor (comparison/swap counters)
│       │   └── demo/           # Console demos: SortingDemo, SearchDemo
│       ├── main/resources/     # Benchmark data (ascending/descending/random)
│       └── test/sortsearch/    # JUnit 5 tests
└── app/
    ├── backend/                # Javalin server + Gradle build (compiles core/ too)
    └── frontend/               # Static HTML/CSS/JS served by the backend
```

The Gradle build in `app/backend` adds `core/src/main/sortsearch` and `core/src/test/sortsearch`
to its source sets. The backend's controllers wrap the core classes so that every compare, swap,
transpose, and move-to-front is streamed to the browser.

## Running it

Requires **JDK 25**. The Gradle wrapper downloads Gradle 9.1.0 the first time it runs.

```bash
# Web visualizer (then open http://localhost:7001)
bash start-visualization.sh

# Run all tests
cd app/backend && ./gradlew test
```

Run the console demos and benchmark from the project root after compiling:

```bash
(cd app/backend && ./gradlew compileJava)
java -cp app/backend/build/classes/java/main main.sortsearch.demo.SortingDemo
java -cp app/backend/build/classes/java/main main.sortsearch.demo.SearchDemo
java -cp app/backend/build/classes/java/main main.sortsearch.sort.SortingDriver
```

`start-visualization.sh` runs the server in the background. To stop it, run
`pkill -f app.Application`.

## Tech stack

- **Java 25** and **Gradle 9.1** (wrapper)
- **Javalin 6** (HTTP + WebSocket), **Gson**, SLF4J
- **JUnit 5**, Mockito, JaCoCo (coverage report written to `app/backend/build/reports/jacoco`
  after a passing test run)
- Frontend: vanilla JavaScript with Alpine.js, htmx (WebSocket extension), and Chart.js, all
  loaded from CDNs
