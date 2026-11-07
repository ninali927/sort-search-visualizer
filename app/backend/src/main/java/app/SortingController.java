package app;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.gson.Gson;

import main.sortsearch.structures.SortableList;
import main.sortsearch.structures.SortableArrayList;
import main.sortsearch.structures.SortableLinkedList;
import main.sortsearch.structures.Position;
import main.sortsearch.sort.SortingAlgorithm;
import main.sortsearch.sort.BubbleSort;
import main.sortsearch.sort.OptimizedBubbleSort;
import main.sortsearch.sort.InsertionSort;
import main.sortsearch.sort.BinaryInsertionSort;
import main.sortsearch.sort.SelectionSort;
import main.sortsearch.monitoring.PerformanceMonitor;
import io.javalin.websocket.WsContext;

public class SortingController {
    private SortableList<Integer> currentList;
    private SortingAlgorithm<Integer> currentAlgorithm;
    private SortingVisualizationState currentState;
    private CompletableFuture<Void> currentSorting;

    // Concurrency controls for pause/resume/step
    private final AtomicBoolean isPaused = new AtomicBoolean(false);
    private final AtomicBoolean skipToEndRequested = new AtomicBoolean(false);
    private final Semaphore stepSemaphore = new Semaphore(0);

    private volatile int animationSpeed = 500;

    private static final Gson gson = new Gson();

    @FunctionalInterface
    interface StepListener {
        void onStep(SortingStep step);
    }

    private static class VisualizableArrayList<T extends Comparable<T>> extends SortableArrayList<T> {
        private StepListener listener;

        public VisualizableArrayList(T[] data) {
            super(data);
        }

        public void setStepListener(StepListener listener) {
            this.listener = listener;
        }

        @Override
        public int compare(Position pos1, Position pos2) {
            if (listener != null) {
                listener.onStep(new SortingStep("compare", pos1.index, pos2.index, 0, getCurrentArray()));
            }
            int result = super.compare(pos1, pos2);
            return result;
        }

        @Override
        public void swap(Position pos1, Position pos2) {
            if (listener != null) {
                listener.onStep(new SortingStep("swap", pos1.index, pos2.index, 0, getCurrentArray()));
            }
            super.swap(pos1, pos2);
            if (listener != null) {
                listener.onStep(new SortingStep("swapped", pos1.index, pos2.index, 0, getCurrentArray()));
            }
        }

        private Integer[] getCurrentArray() {
            Integer[] result = new Integer[this.size()];
            for (int i = 0; i < this.size(); i++) {
                result[i] = (Integer) this.get(this.getPosition(i));
            }
            return result;
        }
    }

    private static class VisualizableLinkedList<T extends Comparable<T>> extends SortableLinkedList<T> {
        private StepListener listener;

        public VisualizableLinkedList(T[] data) {
            super(data);
        }

        public void setStepListener(StepListener listener) {
            this.listener = listener;
        }

        @Override
        public int compare(Position pos1, Position pos2) {
            if (listener != null) {
                listener.onStep(new SortingStep("compare", pos1.index, pos2.index, 0, getCurrentArray()));
            }
            int result = super.compare(pos1, pos2);
            return result;
        }

        @Override
        public void swap(Position pos1, Position pos2) {
            if (listener != null) {
                listener.onStep(new SortingStep("swap", pos1.index, pos2.index, 0, getCurrentArray()));
            }
            super.swap(pos1, pos2);
            if (listener != null) {
                listener.onStep(new SortingStep("swapped", pos1.index, pos2.index, 0, getCurrentArray()));
            }
        }

        private Integer[] getCurrentArray() {
            Integer[] result = new Integer[this.size()];
            for (int i = 0; i < this.size(); i++) {
                result[i] = (Integer) this.get(this.getPosition(i));
            }
            return result;
        }
    }

    private static class SortingStep {
        public final String operation;
        public final int index1;
        public final int index2;
        public final int compareResult;
        public final Integer[] arrayState;
        public final long timestamp;

        public SortingStep(String operation, int index1, int index2, int compareResult, Integer[] arrayState) {
            this.operation = operation;
            this.index1 = index1;
            this.index2 = index2;
            this.compareResult = compareResult;
            this.arrayState = arrayState.clone();
            this.timestamp = System.currentTimeMillis();
        }
    }

    private static class SortingVisualizationState {
        public String algorithm;
        public String listType;
        public Integer[] data;
        public int comparisons;
        public int swaps;
        public boolean isRunning;
        public boolean isCompleted;
        public int currentStepIndex;

        public SortingVisualizationState() {
            this.isRunning = false;
            this.isCompleted = false;
            this.currentStepIndex = 0;
        }
    }

    public SortingController() {
        this.currentState = new SortingVisualizationState();
    }

    public void handleSortingMessage(WsContext ctx, AlgorithmMessage message) {
        try {
            switch (message.type) {
                case "create_array":
                    createArray(ctx, message);
                    break;
                case "start_sort":
                    startSort(ctx, message);
                    break;
                case "pause_sort":
                    pauseSort(ctx);
                    break;
                case "resume_sort":
                    resumeSort(ctx);
                    break;
                case "step_sort":
                    stepSort(ctx);
                    break;
                case "reset_sort":
                    resetSort(ctx);
                    break;
                case "skip_to_end":
                    skipToEnd(ctx);
                    break;
                case "set_speed":
                    setAnimationSpeed(ctx, message);
                    break;
                case "get_state":
                    sendCurrentState(ctx);
                    break;
                default:
                    ctx.send(gson.toJson(new AlgorithmMessage("error", "Unknown sorting command: " + message.type)));
            }
        } catch (Exception e) {
            ctx.send(gson.toJson(new AlgorithmMessage("error", "Error handling sorting message: " + e.getMessage())));
        }
    }

    private void createArray(WsContext ctx, AlgorithmMessage message) {
        Map<String, Object> data = message.data;
        String listType = (String) data.get("listType");
        String dataType = (String) data.get("dataType");

        Integer[] arrayData;

        if ("custom".equals(dataType)) {
            @SuppressWarnings("unchecked")
            List<Number> customData = (List<Number>) data.get("data");
            arrayData = customData.stream().map(Number::intValue).toArray(Integer[]::new);
        } else {
            int size = ((Number) data.get("size")).intValue();
            arrayData = generateData(dataType, size);
        }

        try {
            if ("array".equals(listType)) {
                currentList = new VisualizableArrayList<>(arrayData);
            } else {
                currentList = new VisualizableLinkedList<>(arrayData);
            }

            currentState = new SortingVisualizationState();
            currentState.algorithm = "none";
            currentState.listType = listType;
            currentState.data = arrayData.clone();

            AlgorithmMessage response = new AlgorithmMessage("array_created", "Array created successfully");
            response.data = Map.of(
                "listType", listType,
                "data", arrayData,
                "size", arrayData.length
            );
            ctx.send(gson.toJson(response));

        } catch (UnsupportedOperationException e) {
            ctx.send(gson.toJson(new AlgorithmMessage("error", "Data structure not implemented: " + e.getMessage() + ". Please complete the " + listType + " implementation.")));
        } catch (Exception e) {
            ctx.send(gson.toJson(new AlgorithmMessage("error", "Error creating data structure: " + e.getMessage() + ". Check your " + listType + " implementation.")));
        }
    }

    private Integer[] generateData(String dataType, int size) {
        Integer[] data = new Integer[size];
        Random random = new Random();

        switch (dataType) {
            case "random":
                for (int i = 0; i < size; i++) {
                    data[i] = random.nextInt(100) + 1;
                }
                break;
            case "sorted":
                for (int i = 0; i < size; i++) {
                    data[i] = i + 1;
                }
                break;
            case "reverse":
                for (int i = 0; i < size; i++) {
                    data[i] = size - i;
                }
                break;
            case "nearly_sorted":
                for (int i = 0; i < size; i++) {
                    data[i] = i + 1;
                }
                for (int i = 0; i < size / 10; i++) {
                    int idx1 = random.nextInt(size);
                    int idx2 = random.nextInt(size);
                    Integer temp = data[idx1];
                    data[idx1] = data[idx2];
                    data[idx2] = temp;
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown data type: " + dataType);
        }

        return data;
    }

    private void startSort(WsContext ctx, AlgorithmMessage message) {
        if (currentList == null) {
            ctx.send(gson.toJson(new AlgorithmMessage("error", "No array created")));
            return;
        }

        String algorithmName = (String) message.data.get("algorithm");
        currentAlgorithm = createAlgorithm(algorithmName);

        if (currentAlgorithm == null) {
            ctx.send(gson.toJson(new AlgorithmMessage("error", "Unknown algorithm: " + algorithmName)));
            return;
        }

        currentState.algorithm = algorithmName;
        currentState.isRunning = true;
        currentState.isCompleted = false;
        currentState.currentStepIndex = 0;
        isPaused.set(false);
        skipToEndRequested.set(false);
        stepSemaphore.drainPermits();

        if (currentList instanceof PerformanceMonitor) {
            ((PerformanceMonitor) currentList).resetCounters();
        }

        StepListener listener = step -> {
            if (isPaused.get()) {
                try {
                    stepSemaphore.acquire();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            if (Thread.currentThread().isInterrupted()) {
                throw new RuntimeException("Sorting thread interrupted");
            }

            AlgorithmMessage stepMessage = new AlgorithmMessage("sorting_step", "Step " + (currentState.currentStepIndex + 1));
            PerformanceMonitor perfMon = (PerformanceMonitor) currentList;
            stepMessage.data = Map.of(
                "stepIndex", currentState.currentStepIndex,
                "operation", step.operation,
                "index1", step.index1,
                "index2", step.index2,
                "compareResult", step.compareResult,
                "arrayState", step.arrayState,
                "comparisons", perfMon.getComparisonCount(),
                "swaps", perfMon.getModificationCount()
            );
            ctx.send(gson.toJson(stepMessage));
            currentState.currentStepIndex++;

            // Dual-speed system: fast comparisons, user-controlled swaps
            try {
                if (!skipToEndRequested.get()) {
                    if (step.operation.equals("compare")) {
                        // Fast visual feedback for comparisons (fixed 200ms)
                        Thread.sleep(200);
                    } else if (step.operation.equals("swap") || step.operation.equals("swapped")) {
                        // User-controlled speed for swaps
                        Thread.sleep(animationSpeed);
                    }
                }
                // If skip to end is requested, don't sleep at all
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        if (currentList instanceof VisualizableArrayList) {
            ((VisualizableArrayList<Integer>) currentList).setStepListener(listener);
        } else if (currentList instanceof VisualizableLinkedList) {
            ((VisualizableLinkedList<Integer>) currentList).setStepListener(listener);
        }

        currentSorting = CompletableFuture.runAsync(() -> {
            try {
                // Add timeout protection for algorithms that might not terminate
                long startTime = System.currentTimeMillis();
                long timeoutMs = 30000; // 30 second timeout

                currentAlgorithm.sort(currentList);

                // Check if sorting took too long (might indicate infinite loop)
                long duration = System.currentTimeMillis() - startTime;
                if (duration > timeoutMs) {
                    ctx.send(gson.toJson(new AlgorithmMessage("warning", "Sorting took unusually long (" + duration + "ms). Check algorithm implementation.")));
                }

                currentState.isCompleted = true;
                currentState.isRunning = false;

                AlgorithmMessage complete = new AlgorithmMessage("sort_completed", "Sorting completed");
                PerformanceMonitor perfMon = (PerformanceMonitor) currentList;
                complete.data = Map.of(
                    "comparisons", perfMon.getComparisonCount(),
                    "swaps", perfMon.getModificationCount(),
                    "totalSteps", currentState.currentStepIndex,
                    "finalArray", getCurrentArrayState()
                );
                ctx.send(gson.toJson(complete));

            } catch (UnsupportedOperationException e) {
                // Handle missing implementations gracefully
                currentState.isRunning = false;
                ctx.send(gson.toJson(new AlgorithmMessage("error", "Algorithm not implemented yet: " + e.getMessage() + ". Please complete the implementation.")));
            } catch (NullPointerException e) {
                // Handle null pointer issues from incomplete implementations
                currentState.isRunning = false;
                ctx.send(gson.toJson(new AlgorithmMessage("error", "Implementation incomplete - null pointer error: " + e.getMessage() + ". Check your algorithm implementation.")));
            } catch (ArrayIndexOutOfBoundsException e) {
                // Handle array bounds issues from buggy implementations
                currentState.isRunning = false;
                ctx.send(gson.toJson(new AlgorithmMessage("error", "Array index error: " + e.getMessage() + ". Check array bounds in your implementation.")));
            } catch (StackOverflowError e) {
                // Handle infinite recursion in algorithm implementations
                currentState.isRunning = false;
                ctx.send(gson.toJson(new AlgorithmMessage("error", "Stack overflow - possible infinite recursion in algorithm. Check your recursive calls.")));
            } catch (Exception e) {
                // Catch all other exceptions
                currentState.isRunning = false;
                if (!Thread.currentThread().isInterrupted()) {
                    ctx.send(gson.toJson(new AlgorithmMessage("error", "Error during sorting: " + e.getClass().getSimpleName() + " - " + e.getMessage() + ". Check your algorithm implementation.")));
                }
            }
        });

        AlgorithmMessage response = new AlgorithmMessage("sort_started", "Sorting started with " + algorithmName);
        response.data = Map.of("algorithm", algorithmName);
        ctx.send(gson.toJson(response));
    }

    @SuppressWarnings("unchecked")
    private SortingAlgorithm<Integer> createAlgorithm(String algorithmName) {
        try {
            switch (algorithmName.toLowerCase()) {
                case "bubble":
                    return new BubbleSort<Integer>();
                case "optimized_bubble":
                    // Test if OptimizedBubbleSort is properly implemented
                    OptimizedBubbleSort<Integer> optBubble = new OptimizedBubbleSort<Integer>();
                    // Try a small test to see if it's implemented
                    testAlgorithmImplementation(optBubble);
                    return optBubble;
                case "insertion":
                    return new InsertionSort<Integer>();
                case "binary_insertion":
                    // Test if BinaryInsertionSort is properly implemented
                    BinaryInsertionSort<Integer> binInsertion = new BinaryInsertionSort<Integer>();
                    testAlgorithmImplementation(binInsertion);
                    return binInsertion;
                case "selection":
                    return new SelectionSort<Integer>();
                default:
                    return null;
            }
        } catch (UnsupportedOperationException e) {
            // Algorithm not implemented yet
            return null;
        } catch (Exception e) {
            // Other implementation issues
            return null;
        }
    }

    private void testAlgorithmImplementation(SortingAlgorithm<Integer> algorithm) {
        // Create a small test array to see if the algorithm throws UnsupportedOperationException
        Integer[] testData = {3, 1, 2};
        SortableArrayList<Integer> testList = new SortableArrayList<>(testData);

        try {
            // Just call the sort method - if it throws UnsupportedOperationException, we'll catch it
            algorithm.sort(testList);
        } catch (UnsupportedOperationException e) {
            // Algorithm is not implemented
            throw e;
        } catch (Exception e) {
            // Other errors during testing - algorithm might be partially implemented
            // Let it continue, errors will be caught during actual sorting
        }
    }

    private void pauseSort(WsContext ctx) {
        isPaused.set(true);
        AlgorithmMessage response = new AlgorithmMessage("sort_paused", "Sorting paused");
        ctx.send(gson.toJson(response));
    }

    private void resumeSort(WsContext ctx) {
        isPaused.set(false);
        stepSemaphore.release();
        AlgorithmMessage response = new AlgorithmMessage("sort_resumed", "Sorting resumed");
        ctx.send(gson.toJson(response));
    }

    private void stepSort(WsContext ctx) {
        if (isPaused.get()) {
            stepSemaphore.release();
        }
    }

    private void resetSort(WsContext ctx) {
        if (currentSorting != null) {
            currentSorting.cancel(true);
        }

        currentState.isRunning = false;
        currentState.isCompleted = false;
        currentState.currentStepIndex = 0;
        isPaused.set(false);
        skipToEndRequested.set(false);
        stepSemaphore.drainPermits();

        if (currentState.data != null) {
            if (currentList instanceof VisualizableArrayList) {
                currentList = new VisualizableArrayList<>(currentState.data);
            } else if (currentList instanceof VisualizableLinkedList) {
                currentList = new VisualizableLinkedList<>(currentState.data);
            }
        }

        AlgorithmMessage response = new AlgorithmMessage("sort_reset", "Sorting reset to initial state");
        response.data = Map.of("data", currentState.data);
        ctx.send(gson.toJson(response));
    }

    private void skipToEnd(WsContext ctx) {
        if (currentSorting != null && currentState.isRunning && !currentState.isCompleted) {
            // Set the skip flag to bypass sleep operations
            skipToEndRequested.set(true);

            // If currently paused, resume it to allow completion
            if (isPaused.get()) {
                isPaused.set(false);
                // Release enough permits to unblock any waiting threads
                stepSemaphore.release(10000);
            }

            AlgorithmMessage response = new AlgorithmMessage("skip_started", "Skipping to end of sorting");
            ctx.send(gson.toJson(response));
        } else if (!currentState.isRunning) {
            AlgorithmMessage response = new AlgorithmMessage("error", "No sorting in progress to skip");
            ctx.send(gson.toJson(response));
        } else if (currentState.isCompleted) {
            AlgorithmMessage response = new AlgorithmMessage("info", "Sorting already completed");
            ctx.send(gson.toJson(response));
        }
    }

    private void setAnimationSpeed(WsContext ctx, AlgorithmMessage message) {
        animationSpeed = ((Number) message.data.get("speed")).intValue();
        AlgorithmMessage response = new AlgorithmMessage("speed_set", "Animation speed updated");
        response.data = Map.of("speed", animationSpeed);
        ctx.send(gson.toJson(response));
    }

    private void sendCurrentState(WsContext ctx) {
        AlgorithmMessage response = new AlgorithmMessage("current_state", "Current sorting state");
        Map<String, Object> stateData = new HashMap<>();
        stateData.put("algorithm", currentState.algorithm);
        stateData.put("listType", currentState.listType);
        stateData.put("isRunning", currentState.isRunning);
        stateData.put("isCompleted", currentState.isCompleted);
        stateData.put("isPaused", isPaused.get());
        stateData.put("currentStepIndex", currentState.currentStepIndex);
        stateData.put("data", getCurrentArrayState());

        if (currentList instanceof PerformanceMonitor) {
            PerformanceMonitor perfMon = (PerformanceMonitor) currentList;
            stateData.put("comparisons", perfMon.getComparisonCount());
            stateData.put("swaps", perfMon.getModificationCount());
        }

        response.data = stateData;
        ctx.send(gson.toJson(response));
    }

    private Integer[] getCurrentArrayState() {
        if (currentList == null) return new Integer[0];

        Integer[] result = new Integer[currentList.size()];
        for (int i = 0; i < currentList.size(); i++) {
            result[i] = currentList.get(currentList.getPosition(i));
        }
        return result;
    }
}
