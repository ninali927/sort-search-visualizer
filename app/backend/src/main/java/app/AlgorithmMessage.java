package app;

import java.util.Map;

/**
 * Standard message format for WebSocket communication between frontend and
 * backend.
 * This provides a consistent structure for all algorithm visualization updates.
 */
public class AlgorithmMessage {
    /**
     * Message type (e.g., "start", "step", "complete", "error", "data_update")
     */
    public String type;

    /**
     * Human-readable message content
     */
    public String message;

    /**
     * Additional data payload (algorithm-specific)
     * Examples:
     * - Array state for sorting visualization
     * - Graph nodes/edges for pathfinding
     * - Performance metrics
     */
    public Map<String, Object> data;

    /**
     * Timestamp for message ordering
     */
    public long timestamp;

    public AlgorithmMessage() {
        this.timestamp = System.currentTimeMillis();
    }

    public AlgorithmMessage(String type, String message) {
        this();
        this.type = type;
        this.message = message;
    }

    public AlgorithmMessage(String type, String message, Map<String, Object> data) {
        this(type, message);
        this.data = data;
    }

    @Override
    public String toString() {
        return String.format("AlgorithmMessage{type='%s', message='%s', data=%s}",
                type, message, data);
    }
}