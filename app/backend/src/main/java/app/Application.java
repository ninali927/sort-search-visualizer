package app;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;

import io.javalin.Javalin;
import io.javalin.websocket.WsContext;

/**
 * Main application class that sets up the web server and WebSocket connections.
 * Each session gets its own sorting and search controller wrapping the core algorithms.
 */
public class Application {
    private static final int PORT = 7001;
    private static final Gson gson = new Gson();

    // Store WebSocket connections for real-time updates
    private static final Map<String, WsContext> connections = new ConcurrentHashMap<>();

    // Store controllers per session
    
    private static final Map<String, SortingController> sortingControllers = new ConcurrentHashMap<>();
    private static final Map<String, SearchController> searchControllers = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        Javalin app = Javalin.create(config -> {
            // Serve static files from frontend directory
            config.staticFiles.add(staticFiles -> {
                staticFiles.hostedPath = "/";
                staticFiles.directory = "../frontend";
                staticFiles.location = io.javalin.http.staticfiles.Location.EXTERNAL;
            });

            // Enable CORS for development - Javalin 6.x way
            config.bundledPlugins.enableCors(cors -> {
                cors.addRule(corsRule -> corsRule.anyHost());
            });
        }).start(PORT);

        // Serve the main page
        app.get("/", ctx -> ctx.redirect("/index.html"));

        // WebSocket endpoint for real-time algorithm visualization
        app.ws("/algorithm", ws -> {
            ws.onConnect(ctx -> {
                String sessionId = ctx.sessionId();
                connections.put(sessionId, ctx);
                
                System.out.println("Client connected: " + sessionId);

                // Initialize controllers for this session
                sortingControllers.put(sessionId, new SortingController());
                searchControllers.put(sessionId, new SearchController());

                // Send initial state
                AlgorithmMessage welcome = new AlgorithmMessage("connected", "Simple Algorithms Visualizer ready");
                welcome.data = Map.of("serverInfo", "Simple Algorithms Visualizer Backend", "version", "2.0", "features", Map.of("sorting", true, "search", true, "maze", true));
                ctx.send(gson.toJson(welcome));
            });

            ws.onMessage(ctx -> {
                try {
                    AlgorithmMessage message = gson.fromJson(ctx.message(), AlgorithmMessage.class);
                    handleAlgorithmMessage(ctx, message);
                } catch (Exception e) {
                    System.err.println("Error processing message: " + e.getMessage());
                    ctx.send(gson.toJson(new AlgorithmMessage("error", "Invalid message format")));
                }
            });

            ws.onClose(ctx -> {
                String sessionId = ctx.sessionId();
                connections.remove(sessionId);
                
                sortingControllers.remove(sessionId);
                searchControllers.remove(sessionId);
                System.out.println("Client disconnected: " + sessionId);
            });
        });

        // Start periodic ping to all clients every 30 seconds
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            AlgorithmMessage ping = new AlgorithmMessage("ping", "keepalive");
            ping.timestamp = System.currentTimeMillis();
            String json = gson.toJson(ping);
            connections.values().forEach(ctx -> {
                if (ctx.session.isOpen()) {
                    ctx.send(json);
                }
            });
        }, 30, 30, TimeUnit.SECONDS);

        System.out.println("Server started on http://localhost:" + PORT);
        System.out.println("Simple Algorithms Visualizer - Open your browser to view the visualization");
        System.out.println("Features: Sorting Algorithms, Adaptive Search, and Maze Editor");
    }

    /**
     * Handle incoming WebSocket messages from the frontend.
     * Routes messages to appropriate handlers based on message type.
     */
    private static void handleAlgorithmMessage(WsContext ctx, AlgorithmMessage message) {
        String sessionId = ctx.sessionId();

        // Route messages to appropriate controllers
        if (isSortingMessage(message.type)) {
            SortingController sortingController = sortingControllers.get(sessionId);
            if (sortingController == null) {
                ctx.send(gson.toJson(new AlgorithmMessage("error", "No sorting controller found for session")));
                return;
            }
            sortingController.handleSortingMessage(ctx, message);
        } else if (isSearchMessage(message.type)) {
            SearchController searchController = searchControllers.get(sessionId);
            if (searchController == null) {
                ctx.send(gson.toJson(new AlgorithmMessage("error", "No search controller found for session")));
                return;
            }
            searchController.handleSearchMessage(ctx, message);
        
        } else {
            // Handle legacy demo messages for backward compatibility
            switch (message.type) {
                case "start":
                    runDemoAlgorithm(ctx);
                    break;
                case "step":
                    executeAlgorithmStep(ctx);
                    break;
                case "reset":
                    resetAlgorithm(ctx);
                    break;
                case "pong":
                    // Handle ping/pong keepalive - no action needed
                    System.out.println("Received pong from client: " + sessionId);
                    break;
                default:
                    ctx.send(gson.toJson(new AlgorithmMessage("error", "Unknown command: " + message.type)));
            }
        }
    }

    /**
     * Check if a message type is related to sorting operations.
     */
    private static boolean isSortingMessage(String messageType) {
        return messageType.equals("create_array") ||
                messageType.equals("start_sort") ||
                messageType.equals("pause_sort") ||
                messageType.equals("resume_sort") ||
                messageType.equals("step_sort") ||
                messageType.equals("reset_sort") ||
                messageType.equals("skip_to_end") ||
                messageType.equals("set_speed") ||
                messageType.equals("get_state");
    }

    /**
     * Check if a message type is related to search operations.
     */
    private static boolean isSearchMessage(String messageType) {
        return messageType.equals("create_bag") ||
                messageType.equals("search_element") ||
                messageType.equals("run_simulation") ||
                messageType.equals("reset_bag") ||
                messageType.equals("get_search_state") ||
                messageType.equals("get_bag_structure");
    }

    

    /**
     * Example algorithm demonstration.
     * Kept for the legacy "start" demo command.
     */
    private static void runDemoAlgorithm(WsContext ctx) {
        String sessionId = ctx.sessionId();

        // Simulate algorithm steps in background thread
        new Thread(() -> {
            try {
                for (int i = 0; i < 5; i++) {
                    Thread.sleep(1000); // Simulate computation time

                    // Get current connection (in case it changed)
                    WsContext currentCtx = connections.get(sessionId);
                    if (currentCtx == null) {
                        return; // Client disconnected
                    }

                    AlgorithmMessage update = new AlgorithmMessage("step",
                            "Algorithm step " + (i + 1) + " completed");
                    update.data = Map.of("step", i + 1, "progress", (i + 1) * 20);

                    currentCtx.send(gson.toJson(update));
                }

                // Send completion message
                WsContext currentCtx = connections.get(sessionId);
                if (currentCtx != null) {
                    AlgorithmMessage complete = new AlgorithmMessage("complete", "Algorithm finished!");
                    currentCtx.send(gson.toJson(complete));
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    private static void executeAlgorithmStep(WsContext ctx) {
        // Example single step execution
        AlgorithmMessage step = new AlgorithmMessage("step", "Single step executed");
        step.data = Map.of("action", "single_step", "timestamp", System.currentTimeMillis());
        ctx.send(gson.toJson(step));
    }

    private static void resetAlgorithm(WsContext ctx) {
        AlgorithmMessage reset = new AlgorithmMessage("reset", "Algorithm state reset");
        ctx.send(gson.toJson(reset));
    }
}