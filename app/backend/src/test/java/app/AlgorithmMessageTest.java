package app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the AlgorithmMessage WebSocket message format.
 */
public class AlgorithmMessageTest {

    private AlgorithmMessage message;

    @BeforeEach
    void setUp() {
        message = new AlgorithmMessage();
    }

    @Test
    void testMessageCreation() {
        AlgorithmMessage msg = new AlgorithmMessage("test", "Test message");

        assertEquals("test", msg.type);
        assertEquals("Test message", msg.message);
        assertNotNull(msg.timestamp);
        assertTrue(msg.timestamp > 0);
    }

    @Test
    void testMessageWithData() {
        Map<String, Object> data = Map.of("step", 1, "value", "test");
        AlgorithmMessage msg = new AlgorithmMessage("step", "Step executed", data);

        assertEquals("step", msg.type);
        assertEquals("Step executed", msg.message);
        assertEquals(data, msg.data);
    }

    @Test
    void testTimestampGeneration() {
        long before = System.currentTimeMillis();
        AlgorithmMessage msg = new AlgorithmMessage("test", "message");
        long after = System.currentTimeMillis();

        assertTrue(msg.timestamp >= before && msg.timestamp <= after);
    }

    @Test
    void testToString() {
        AlgorithmMessage msg = new AlgorithmMessage("test", "message");
        String result = msg.toString();

        assertTrue(result.contains("test"));
        assertTrue(result.contains("message"));
        assertTrue(result.contains("AlgorithmMessage"));
    }
}