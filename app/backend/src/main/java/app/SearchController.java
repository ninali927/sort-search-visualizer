package app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

import com.google.gson.Gson;

import main.sortsearch.structures.Bag;
import main.sortsearch.structures.ArrayBag;
import main.sortsearch.structures.LinkedListBag;
import main.sortsearch.monitoring.PerformanceMonitor;
import io.javalin.websocket.WsContext;
import java.lang.reflect.Field;

/**
 * Controller for handling adaptive search algorithm visualization WebSocket messages.
 *
 * This class manages Bag implementations and provides visualization
 * capabilities for different search optimization strategies.
 */
public class SearchController {
    private Bag<Integer> currentBag;
    private String currentBagType;
    private SearchVisualizationState currentState;
    private CompletableFuture<Void> currentSimulation;
    private static final Gson gson = new Gson();

    // Helper to get the current state of an ArrayBag using reflection
    @SuppressWarnings("unchecked")
    private static <T extends Comparable<T>> T[] getArrayBagState(ArrayBag<T> bag) {
        try {
            Field elementsField = ArrayBag.class.getDeclaredField("elements");
            elementsField.setAccessible(true);
            Object elementsObject = elementsField.get(bag);
            return (T[]) ((Object[]) elementsObject).clone();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            // This is a fallback and should not happen if the class structure is correct
            return (T[]) new Object[0];
        }
    }

    // Helper to get the current state of a LinkedListBag using reflection
    @SuppressWarnings("unchecked")
    private static Integer[] getLinkedListBagState(LinkedListBag<Integer> bag) {
        List<Integer> result = new ArrayList<>();
        try {
            Field headField = LinkedListBag.class.getDeclaredField("head");
            headField.setAccessible(true);
            Object headNode = headField.get(bag);

            if (headNode != null) {
                Class<?> nodeClass = headNode.getClass();
                Field dataField = nodeClass.getDeclaredField("data");
                Field nextField = nodeClass.getDeclaredField("next");
                dataField.setAccessible(true);
                nextField.setAccessible(true);

                Object currentNode = headNode;
                while (currentNode != null) {
                    result.add((Integer) dataField.get(currentNode));
                    currentNode = nextField.get(currentNode);
                }
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            // This is a fallback and should not happen if the class structure is correct
        }
        return result.toArray(new Integer[0]);
    }


    /**
     * Enhanced ArrayBag that hooks into primitive operations for visualization.
     */
    private static class VisualizableArrayBag<T extends Comparable<T>> extends ArrayBag<T> {
        private List<SearchStep> steps = new ArrayList<>();

        public VisualizableArrayBag(T[] data) {
            super(data);
        }

        @Override
        protected int compare(T element, int index) {
            steps.add(new SearchStep("compare", element, index, getArrayBagState(this), comparisonCount + 1, modificationCount));
            return super.compare(element, index);
        }

        @Override
        protected void transpose(int index) {
            // Capture state before the transpose
            steps.add(new SearchStep("transpose_start", elements[index], index, getArrayBagState(this), comparisonCount, modificationCount + 1));
            super.transpose(index);
            // Capture state after the transpose
            steps.add(new SearchStep("transpose_complete", elements[index - 1], index - 1, getArrayBagState(this), comparisonCount, modificationCount));
        }

        public List<SearchStep> getSteps() { return new ArrayList<>(steps); }
        public void clearSteps() { steps.clear(); }
    }

    /**
     * Enhanced LinkedListBag that hooks into primitive operations for visualization.
     */
    private static class VisualizableLinkedListBag<T extends Comparable<T>> extends LinkedListBag<T> {
        private List<SearchStep> steps = new ArrayList<>();

        public VisualizableLinkedListBag(T[] data) {
            super(data);
        }

        // Find the index of a node, required for visualization. This is inefficient but necessary.
        private int findNodeIndex(Node<T> targetNode) {
            int index = 0;
            Node<T> current = head;
            while (current != null) {
                if (current == targetNode) {
                    return index;
                }
                current = current.next;
                index++;
            }
            return -1;
        }

        @Override
        protected int compare(Node<T> node, T element) {
            int index = findNodeIndex(node);
            steps.add(new SearchStep("compare", element, index, (Object[]) getLinkedListBagState((LinkedListBag<Integer>)this), comparisonCount + 1, modificationCount));
            return super.compare(node, element);
        }

        @Override
        protected void moveToFront(Node<T> previous, Node<T> current) {
            int index = findNodeIndex(current);
            steps.add(new SearchStep("move_to_front_start", current.data, index, (Object[]) getLinkedListBagState((LinkedListBag<Integer>)this), comparisonCount, modificationCount + 1));
            super.moveToFront(previous, current);
            steps.add(new SearchStep("move_to_front_complete", head.data, 0, (Object[]) getLinkedListBagState((LinkedListBag<Integer>)this), comparisonCount, modificationCount));
        }
        
        public List<SearchStep> getSteps() { return new ArrayList<>(steps); }
        public void clearSteps() { steps.clear(); }
    }

    /**
     * Represents a single step in the search visualization
     */
    private static class SearchStep {
        public final String operation;
        public final Object searchElement;
        public final int index;
        public final Object[] structureState;
        public final int comparisons;
        public final int modifications;
        public final long timestamp;

        public SearchStep(String operation, Object searchElement, int index, Object[] structureState,
                         int comparisons, int modifications) {
            this.operation = operation;
            this.searchElement = searchElement;
            this.index = index;
            this.structureState = structureState != null ? structureState.clone() : new Object[0];
            this.comparisons = comparisons;
            this.modifications = modifications;
            this.timestamp = System.currentTimeMillis();
        }
    }

    /**
     * Current state of the search visualization
     */
    private static class SearchVisualizationState {
        public String bagType;
        public Integer[] data;
        public boolean isRunning;

        public SearchVisualizationState() {
            this.isRunning = false;
        }
    }

    public SearchController() {
        this.currentState = new SearchVisualizationState();
    }

    /**
     * Handle incoming search-related WebSocket messages.
     */
    public void handleSearchMessage(WsContext ctx, AlgorithmMessage message) {
        try {
            switch (message.type) {
                case "create_bag":
                    createBag(ctx, message);
                    break;
                case "search_element":
                    searchElement(ctx, message);
                    break;
                case "run_simulation":
                    runSimulation(ctx, message);
                    break;
                case "reset_bag":
                    resetBag(ctx);
                    break;
                case "get_search_state":
                    sendCurrentState(ctx);
                    break;
                case "get_bag_structure":
                    sendBagStructure(ctx);
                    break;
                default:
                    ctx.send(gson.toJson(new AlgorithmMessage("error", "Unknown search command: " + message.type)));
            }
        } catch (Exception e) {
            ctx.send(gson.toJson(new AlgorithmMessage("error", "Error handling search message: " + e.getMessage())));
        }
    }

    private void createBag(WsContext ctx, AlgorithmMessage message) {
        Map<String, Object> data = message.data;
        String bagType = (String) data.get("bagType");
        String dataType = (String) data.get("dataType");

        Integer[] bagData;

        if ("custom".equals(dataType)) {
            @SuppressWarnings("unchecked")
            List<Number> customData = (List<Number>) data.get("data");
            bagData = customData.stream().map(Number::intValue).toArray(Integer[]::new);
        } else {
            int size = ((Number) data.get("size")).intValue();
            bagData = generateData(dataType, size);
        }

        // Create the appropriate bag type with error handling
        try {
            if ("array".equals(bagType)) {
                currentBag = new VisualizableArrayBag<>(bagData);
            } else {
                currentBag = new VisualizableLinkedListBag<>(bagData);
            }

            currentBagType = bagType;
            currentState = new SearchVisualizationState();
            currentState.bagType = bagType;
            currentState.data = bagData.clone();

            AlgorithmMessage response = new AlgorithmMessage("bag_created", "Bag created successfully");
            response.data = Map.of(
                "bagType", bagType,
                "data", bagData,
                "size", bagData.length
            );
            ctx.send(gson.toJson(response));

        } catch (UnsupportedOperationException e) {
            ctx.send(gson.toJson(new AlgorithmMessage("error", "Bag implementation not complete: " + e.getMessage() + ". Please implement the missing methods.")));
        } catch (Exception e) {
            ctx.send(gson.toJson(new AlgorithmMessage("error", "Error creating bag: " + e.getMessage() + ". Check your bag implementation.")));
        }
    }

    private Integer[] generateData(String dataType, int size) {
        Integer[] data = new Integer[size];
        Random random = new Random();

        switch (dataType) {
            case "sequential":
                for (int i = 0; i < size; i++) {
                    data[i] = i + 1;
                }
                break;
            case "random":
                for (int i = 0; i < size; i++) {
                    data[i] = random.nextInt(100) + 1;
                }
                break;
            case "skewed":
                // Create data that will demonstrate optimization effects
                for (int i = 0; i < size; i++) {
                    data[i] = (i < size / 3) ? random.nextInt(10) + 1 : random.nextInt(90) + 11;
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown data type: " + dataType);
        }

        return data;
    }

    private void searchElement(WsContext ctx, AlgorithmMessage message) {
        if (currentBag == null) {
            ctx.send(gson.toJson(new AlgorithmMessage("error", "No bag created")));
            return;
        }

        Integer element = ((Number) message.data.get("element")).intValue();

        // Clear steps and counters before search
        if (currentBag instanceof VisualizableArrayBag) {
            ((VisualizableArrayBag<Integer>) currentBag).clearSteps();
        } else if (currentBag instanceof VisualizableLinkedListBag) {
            ((VisualizableLinkedListBag<Integer>) currentBag).clearSteps();
        }
        ((PerformanceMonitor) currentBag).resetCounters();

        // Perform the search
        boolean found = currentBag.contains(element);

        // Get the steps from the visualizable bag
        List<SearchStep> steps;
        if (currentBag instanceof VisualizableArrayBag) {
            steps = ((VisualizableArrayBag<Integer>) currentBag).getSteps();
        } else if (currentBag instanceof VisualizableLinkedListBag) {
            steps = ((VisualizableLinkedListBag<Integer>) currentBag).getSteps();
        } else {
            steps = new ArrayList<>();
        }

        // Send response with search results and steps
        AlgorithmMessage response = new AlgorithmMessage("search_completed", found ? "Element found" : "Element not found");
        PerformanceMonitor perfMon = (PerformanceMonitor) currentBag;
        response.data = Map.of(
            "element", element,
            "found", found,
            "steps", steps,
            "comparisons", perfMon.getComparisonCount(),
            "modifications", perfMon.getModificationCount(),
            "finalStructure", getCurrentBagStructure()
        );
        ctx.send(gson.toJson(response));
    }

    private void runSimulation(WsContext ctx, AlgorithmMessage message) {
        if (currentBag == null) {
            ctx.send(gson.toJson(new AlgorithmMessage("error", "No bag created")));
            return;
        }

        Map<String, Object> data = message.data;
        String pattern = (String) data.get("pattern");
        int iterations = ((Number) data.get("iterations")).intValue();

        currentSimulation = CompletableFuture.runAsync(() -> {
            try {
                runSimulationPattern(ctx, pattern, iterations);
            } catch (Exception e) {
                ctx.send(gson.toJson(new AlgorithmMessage("error", "Error during simulation: " + e.getMessage())));
            }
        });

        AlgorithmMessage response = new AlgorithmMessage("simulation_started", "Running " + pattern + " pattern simulation");
        response.data = Map.of("pattern", pattern, "iterations", iterations);
        ctx.send(gson.toJson(response));
    }

    private void runSimulationPattern(WsContext ctx, String pattern, int iterations) {
        Random random = new Random();
        List<SimulationResult> results = new ArrayList<>();

        for (int i = 0; i < iterations; i++) {
            Integer searchElement = generateSearchElement(pattern, random);

            // Reset counters
            if (currentBag instanceof PerformanceMonitor) {
                ((PerformanceMonitor) currentBag).resetCounters();
            }

            // Perform search
            boolean found = currentBag.contains(searchElement);

            // Record results
            PerformanceMonitor perfMon = (PerformanceMonitor) currentBag;
            results.add(new SimulationResult(
                i + 1, searchElement, found,
                perfMon.getComparisonCount(),
                perfMon.getModificationCount()
            ));

            // Send periodic updates
            if ((i + 1) % 10 == 0 || i == iterations - 1) {
                AlgorithmMessage update = new AlgorithmMessage("simulation_progress",
                    "Completed " + (i + 1) + "/" + iterations + " searches");
                update.data = Map.of(
                    "iteration", i + 1,
                    "total", iterations,
                    "results", results,
                    "currentStructure", getCurrentBagStructure()
                );
                ctx.send(gson.toJson(update));
            }

            // Small delay for visualization
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        // Send final results
        AlgorithmMessage complete = new AlgorithmMessage("simulation_completed", "Simulation completed");
        complete.data = Map.of(
            "pattern", pattern,
            "totalIterations", iterations,
            "results", results,
            "finalStructure", getCurrentBagStructure(),
            "summary", calculateSimulationSummary(results)
        );
        ctx.send(gson.toJson(complete));
    }

    private Integer generateSearchElement(String pattern, Random random) {
        if (currentState.data == null || currentState.data.length == 0) {
            return 1;
        }

        switch (pattern) {
            case "uniform":
                return currentState.data[random.nextInt(currentState.data.length)];
            case "skewed":
                // 80% of searches on first 20% of elements
                if (random.nextDouble() < 0.8) {
                    int frontElements = Math.max(1, currentState.data.length / 5);
                    return currentState.data[random.nextInt(frontElements)];
                } else {
                    return currentState.data[random.nextInt(currentState.data.length)];
                }
            case "temporal":
                // Repeated searches for same elements
                int[] popularElements = {currentState.data[0], currentState.data[Math.min(2, currentState.data.length - 1)]};
                return popularElements[random.nextInt(popularElements.length)];
            default:
                return currentState.data[random.nextInt(currentState.data.length)];
        }
    }

    private Map<String, Object> calculateSimulationSummary(List<SimulationResult> results) {
        int totalComparisons = results.stream().mapToInt(r -> r.comparisons).sum();
        int totalModifications = results.stream().mapToInt(r -> r.modifications).sum();
        double avgComparisons = (double) totalComparisons / results.size();
        double avgModifications = (double) totalModifications / results.size();

        return Map.of(
            "totalComparisons", totalComparisons,
            "totalModifications", totalModifications,
            "averageComparisons", avgComparisons,
            "averageModifications", avgModifications,
            "totalSearches", results.size()
        );
    }

    private static class SimulationResult {
        public final int iteration;
        public final Integer searchElement;
        public final boolean found;
        public final int comparisons;
        public final int modifications;

        public SimulationResult(int iteration, Integer searchElement, boolean found, int comparisons, int modifications) {
            this.iteration = iteration;
            this.searchElement = searchElement;
            this.found = found;
            this.comparisons = comparisons;
            this.modifications = modifications;
        }
    }

    private void resetBag(WsContext ctx) {
        if (currentSimulation != null) {
            currentSimulation.cancel(true);
        }

        // Reset the bag to original state
        if (currentState.data != null) {
            if ("array".equals(currentBagType)) {
                currentBag = new VisualizableArrayBag<>(currentState.data);
            } else {
                currentBag = new VisualizableLinkedListBag<>(currentState.data);
            }
        }

        AlgorithmMessage response = new AlgorithmMessage("bag_reset", "Bag reset to initial state");
        response.data = Map.of(
            "data", currentState.data,
            "structure", getCurrentBagStructure()
        );
        ctx.send(gson.toJson(response));
    }

    private void sendCurrentState(WsContext ctx) {
        AlgorithmMessage response = new AlgorithmMessage("current_search_state", "Current search state");
        Map<String, Object> stateData = new HashMap<>();
        stateData.put("bagType", currentState.bagType);
        stateData.put("data", currentState.data);
        stateData.put("structure", getCurrentBagStructure());

        if (currentBag instanceof PerformanceMonitor) {
            PerformanceMonitor perfMon = (PerformanceMonitor) currentBag;
            stateData.put("comparisons", perfMon.getComparisonCount());
            stateData.put("modifications", perfMon.getModificationCount());
        }

        response.data = stateData;
        ctx.send(gson.toJson(response));
    }

    private void sendBagStructure(WsContext ctx) {
        AlgorithmMessage response = new AlgorithmMessage("bag_structure", "Current bag structure");
        response.data = Map.of("structure", getCurrentBagStructure());
        ctx.send(gson.toJson(response));
    }

    private Object getCurrentBagStructure() {
        if (currentBag == null) return new Object[0];

        if (currentBag instanceof VisualizableArrayBag) {
            return getArrayBagState((VisualizableArrayBag<Integer>) currentBag);
        } else if (currentBag instanceof VisualizableLinkedListBag) {
            return getLinkedListBagState((VisualizableLinkedListBag<Integer>) currentBag);
        }

        return new Object[0];
    }
}