import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@Tag("unit")  // advanced feature: tagging tests for grouping & selective execution
class TupleTest {

    private Tuple tuple;

    @BeforeEach
    void setUp() {
        tuple = new Tuple(5, 7);  // default starting values
    }

    @Test
    @DisplayName("Constructor correctly initializes x and y")
    void constructorInitializesCoordinates() {
        assertEquals(5, tuple.getX());
        assertEquals(7, tuple.getY());
    }

    @Test
    @DisplayName("ChangeData updates both x and y")
    void changeDataUpdatesCoordinates() {
        tuple.ChangeData(10, 20);
        assertEquals(10, tuple.getX());
        assertEquals(20, tuple.getY());
    }

    @ParameterizedTest(name = "Tuple({0}, {1}) should return x={0} and y={1}")
    @CsvSource({
        "0, 0",
        "5, 10",
        "-3, 7",
        "100, -50"
    })
    void parameterizedConstructorTest(int x, int y) {
        Tuple t = new Tuple(x, y);
        assertEquals(x, t.getX());
        assertEquals(y, t.getY());
    }

    @Test
    @DisplayName("xf and yf default to 0 when uninitialized")
    void xfAndYfDefaultToZero() {
        assertEquals(0, tuple.getXf(), "xf should default to 0");
        assertEquals(0, tuple.getYf(), "yf should default to 0");
    }

    @Test
    @DisplayName("Direct field mutation works as expected (public fields)")
    void publicFieldMutation() {
        tuple.x = 42;
        tuple.y = 99;

        assertEquals(42, tuple.getX());
        assertEquals(99, tuple.getY());
    }
}