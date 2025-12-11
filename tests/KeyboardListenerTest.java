import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.awt.Canvas;
import java.awt.event.KeyEvent;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.provider.Arguments;

class KeyboardListenerTest {

    private KeyboardListener listener;

    @BeforeEach
    void setUp() {
        listener = new KeyboardListener();
        // default direction; you can pick any, we override it in each test
        ThreadsController.directionSnake = 1; 
    }

    // Helper to create a KeyEvent for a given keyCode
    private KeyEvent keyEvent(int keyCode) {
        return new KeyEvent(
                new Canvas(),                 // dummy component source
                KeyEvent.KEY_PRESSED,         // event id
                System.currentTimeMillis(),   // when
                0,                            // modifiers
                keyCode,                      // keyCode
                KeyEvent.CHAR_UNDEFINED       // keyChar
        );
    }

    // -------------------------------------------------------------
    // BASIC TESTS: single direction changes
    // -------------------------------------------------------------

    @Test
    @DisplayName("Pressing RIGHT sets direction to 1 when not coming from LEFT")
    void pressingRightChangesDirectionWhenAllowed() {
        ThreadsController.directionSnake = 3; // currently UP
        listener.keyPressed(keyEvent(KeyEvent.VK_RIGHT));
        assertEquals(1, ThreadsController.directionSnake); // RIGHT
    }

    @Test
    @DisplayName("Pressing LEFT sets direction to 2 when not coming from RIGHT")
    void pressingLeftChangesDirectionWhenAllowed() {
        ThreadsController.directionSnake = 3; // currently UP
        listener.keyPressed(keyEvent(KeyEvent.VK_LEFT));
        assertEquals(2, ThreadsController.directionSnake); // LEFT
    }

    @Test
    @DisplayName("Pressing UP sets direction to 3 when not coming from DOWN")
    void pressingUpChangesDirectionWhenAllowed() {
        ThreadsController.directionSnake = 1; // currently RIGHT
        listener.keyPressed(keyEvent(KeyEvent.VK_UP));
        assertEquals(3, ThreadsController.directionSnake); // UP
    }

    @Test
    @DisplayName("Pressing DOWN sets direction to 4 when not coming from UP")
    void pressingDownChangesDirectionWhenAllowed() {
        ThreadsController.directionSnake = 1; // currently RIGHT
        listener.keyPressed(keyEvent(KeyEvent.VK_DOWN));
        assertEquals(4, ThreadsController.directionSnake); // DOWN
    }

    // -------------------------------------------------------------
    // ADVANCED FEATURE: parameterized tests
    // Illegal 180° reversals should be blocked
    // -------------------------------------------------------------

    static Stream<Arguments> illegalReversals() {
        return Stream.of(
                // currentDirection, keyCode, description
                Arguments.of(1, KeyEvent.VK_LEFT,  "RIGHT -> LEFT is illegal (180°)"),
                Arguments.of(2, KeyEvent.VK_RIGHT, "LEFT -> RIGHT is illegal (180°)"),
                Arguments.of(3, KeyEvent.VK_DOWN,  "UP -> DOWN is illegal (180°)"),
                Arguments.of(4, KeyEvent.VK_UP,    "DOWN -> UP is illegal (180°)")
        );
    }

    @ParameterizedTest(name = "{2}")
    @MethodSource("illegalReversals")
    @DisplayName("Illegal 180° reversals do not change directionSnake")
    void illegalReversalDoesNotChangeDirection(
            int initialDirection,
            int keyCode,
            String description
    ) {
        ThreadsController.directionSnake = initialDirection;

        listener.keyPressed(keyEvent(keyCode));

        // Direction should remain unchanged
        assertEquals(initialDirection, ThreadsController.directionSnake);
    }

    // -------------------------------------------------------------
    // ADVANCED FEATURE: parameterized tests for allowed turns
    // -------------------------------------------------------------

    static Stream<Arguments> allowedDirectionChanges() {
        return Stream.of(
                // initial, key, expected
                Arguments.of(1, KeyEvent.VK_UP,    3), // RIGHT -> UP
                Arguments.of(1, KeyEvent.VK_DOWN,  4), // RIGHT -> DOWN
                Arguments.of(2, KeyEvent.VK_UP,    3), // LEFT -> UP
                Arguments.of(2, KeyEvent.VK_DOWN,  4), // LEFT -> DOWN
                Arguments.of(3, KeyEvent.VK_LEFT,  2), // UP -> LEFT
                Arguments.of(3, KeyEvent.VK_RIGHT, 1), // UP -> RIGHT
                Arguments.of(4, KeyEvent.VK_LEFT,  2), // DOWN -> LEFT
                Arguments.of(4, KeyEvent.VK_RIGHT, 1)  // DOWN -> RIGHT
        );
    }

    @ParameterizedTest(name = "From {0} with key {1} -> expect direction {2}")
    @MethodSource("allowedDirectionChanges")
    @DisplayName("Allowed turns update directionSnake correctly")
    void allowedTurnsChangeDirection(
            int initialDirection,
            int keyCode,
            int expectedDirection
    ) {
        ThreadsController.directionSnake = initialDirection;

        listener.keyPressed(keyEvent(keyCode));

        assertEquals(expectedDirection, ThreadsController.directionSnake);
    }

    // -------------------------------------------------------------
    // DEFAULT CASE: non-arrow keys should not change direction
    // -------------------------------------------------------------

    @Test
    @DisplayName("Non-arrow key does not change directionSnake")
    void nonArrowKeyDoesNotChangeDirection() {
        ThreadsController.directionSnake = 1; // RIGHT
        listener.keyPressed(keyEvent(KeyEvent.VK_A)); // some random key
        assertEquals(1, ThreadsController.directionSnake);
    }
}

