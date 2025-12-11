import static org.junit.jupiter.api.Assertions.*;

import java.awt.Container;
import java.awt.GridLayout;

import org.junit.jupiter.api.*;
// Advanced feature
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@Tag("ui") // advanced: tag this test class as "ui" so it can be included/excluded in runs
class WindowTest {

    private Window window;

    @BeforeEach
    void setUp() {
        // In case tests run in a headless environment (CI server, etc.)
        System.setProperty("java.awt.headless", "true");

        // Creating the window should initialize the grid and layout
        window = new Window();
    }

    @AfterEach
    void tearDown() {
        // Clean up Swing resources
        if (window != null) {
            window.dispose();
        }
    }

    @Test
    @DisplayName("Grid is initialized with the correct dimensions")
    void gridHasCorrectDimensions() {
        assertNotNull(Window.Grid, "Grid should be initialized");

        // Check number of rows
        assertEquals(Window.width, Window.Grid.size(),
                "Grid should have 'width' rows");

        // Check number of columns per row
        for (int i = 0; i < Window.width; i++) {
            assertEquals(Window.height, Window.Grid.get(i).size(),
                    "Each row should have 'height' columns");
        }
    }

    @ParameterizedTest(name = "Row {0} contains only non-null DataOfSquare instances")
    @ValueSource(ints = {0, 5, 10, 19}) // advanced: parameterized test for several rows
    void gridRowsContainNonNullSquares(int rowIndex) {
        assertNotNull(Window.Grid, "Grid should be initialized");
        assertTrue(rowIndex >= 0 && rowIndex < Window.width,
                "Row index must be within grid bounds");

        for (int col = 0; col < Window.height; col++) {
            assertNotNull(
                    Window.Grid.get(rowIndex).get(col),
                    () -> String.format("Square at (%d,%d) should not be null", rowIndex, col)
            );
        }
    }

    @Test
    @DisplayName("Content pane has GridLayout 20x20 and 400 components")
    void contentPaneHasCorrectLayoutAndComponentCount() {
        Container content = window.getContentPane();
        assertNotNull(content, "Content pane should not be null");

        assertTrue(content.getLayout() instanceof GridLayout,
                "Layout should be a GridLayout");

        GridLayout layout = (GridLayout) content.getLayout();

        // The code uses new GridLayout(20, 20, 0, 0)
        assertEquals(20, layout.getRows(), "GridLayout should have 20 rows");
        assertEquals(20, layout.getColumns(), "GridLayout should have 20 columns");

        // There should be width * height components added
        int expectedComponents = Window.width * Window.height;
        assertEquals(expectedComponents, content.getComponentCount(),
                "Content pane should contain one component per grid cell");
    }
}
