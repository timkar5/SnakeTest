import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.lang.reflect.Method;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@Tag("logic") // Advanced: tag to allow selective execution (e.g., only logic tests)
class ThreadsControllerTest {

    @BeforeEach
    void setUpGrid() {
        // Make Swing run in headless mode if tests are executed on a CI server
        System.setProperty("java.awt.headless", "true");

        // Manually initialize the Window.Grid like the Window constructor would
        Window.Grid = new ArrayList<>();
        for (int i = 0; i < Window.width; i++) {
            ArrayList<DataOfSquare> row = new ArrayList<>();
            for (int j = 0; j < Window.height; j++) {
                row.add(new DataOfSquare(2)); // same value as in Window
            }
            Window.Grid.add(row);
        }
    }

    @Test
    @DisplayName("Constructor initializes snake state and food position correctly")
    void constructorInitializesState() {
        Tuple start = new Tuple(10, 10);
        ThreadsController controller = new ThreadsController(start);

        // Snake size
        assertEquals(3, controller.sizeSnake, "Initial snake size should be 3");

        // Direction
        assertEquals(1, ThreadsController.directionSnake, "Initial direction should be 1 (right)");

        // Positions list should contain exactly one element: the head
        assertEquals(1, controller.positions.size(), "Positions should start with one head element");
        Tuple head = controller.positions.get(0);
        assertEquals(start.getX(), head.getX(), "Head X should match starting X");
        assertEquals(start.getY(), head.getY(), "Head Y should match starting Y");

        // Food position initialized at bottom-right corner (height-1, width-1)
        assertEquals(Window.height - 1, controller.foodPosition.getX(), "Food X should be height-1");
        assertEquals(Window.width - 1, controller.foodPosition.getY(), "Food Y should be width-1");

        // Squares should reuse Window.Grid, not a separate structure
        assertSame(Window.Grid, controller.Squares, "Controller should use Window.Grid as Squares");
    }

    @ParameterizedTest(name = "moveInterne({0}) from (10,10) should land at ({1},{2})")
    @CsvSource({
        // dir, expectedX, expectedY
        "1, 11, 10", // 1: right  (x+1)
        "2, 9,  10", // 2: left   (x-1)
        "3, 10, 9",  // 3: top    (y-1)
        "4, 10, 11"  // 4: bottom (y+1)
    })
    void moveInterneMovesHeadCorrectly(int direction, int expectedX, int expectedY) throws Exception {
        // Arrange
        ThreadsController controller = new ThreadsController(new Tuple(10, 10));

        // Access the private moveInterne(int dir) method via reflection
        Method moveInterne = ThreadsController.class.getDeclaredMethod("moveInterne", int.class);
        moveInterne.setAccessible(true);

        // Act
        moveInterne.invoke(controller, direction);

        // Assert: last element in positions is the new head
        Tuple newHead = controller.positions.get(controller.positions.size() - 1);
        assertEquals(expectedX, newHead.getX(), "Head X should match expected after move");
        assertEquals(expectedY, newHead.getY(), "Head Y should match expected after move");
    }

    @RepeatedTest(5)
    @DisplayName("getValAleaNotInSnake never returns a position already occupied by the snake")
    void randomFoodPositionIsNotOnSnake(RepetitionInfo info) throws Exception {
        // Arrange: create a small horizontal snake
        ThreadsController controller = new ThreadsController(new Tuple(5, 5));
        for (int i = 1; i < controller.sizeSnake; i++) {
            controller.positions.add(new Tuple(5 + i, 5));
        }

        // Access the private getValAleaNotInSnake() method via reflection
        Method getVal = ThreadsController.class.getDeclaredMethod("getValAleaNotInSnake");
        getVal.setAccessible(true);

        // Act
        Tuple food = (Tuple) getVal.invoke(controller);

        // Assert: food position must not overlap the snake
        // Note: the method checks overlap with condition:
        // p.getY() == positions.get(i).getX() && p.getX() == positions.get(i).getY()
        for (Tuple pos : controller.positions) {
            boolean overlap = (food.getY() == pos.getX()) && (food.getX() == pos.getY());
            assertFalse(
                overlap,
                () -> String.format(
                        "Food (%d,%d) should not overlap snake segment (%d,%d)",
                        food.getX(), food.getY(), pos.getX(), pos.getY()
                )
            );
        }
    }
}
