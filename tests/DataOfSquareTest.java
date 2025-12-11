import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.awt.Color;
import java.lang.reflect.Field;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class DataOfSquareTest {

    // --- Helper to access the private color list via reflection ---
    @SuppressWarnings("unchecked")
    private ArrayList<Color> getColorList(DataOfSquare data) throws Exception {
        Field colorListField = DataOfSquare.class.getDeclaredField("C");
        colorListField.setAccessible(true);
        return (ArrayList<Color>) colorListField.get(data);
    }

    // ---------- BASIC FEATURES: creation + valid indices ----------

    @ParameterizedTest(name = "Constructor accepts color index {0}")
    @ValueSource(ints = {0, 1, 2})
    @DisplayName("Constructor does not throw for valid color indices (0,1,2)")
    void constructorAcceptsValidColorIndices(int index) {
        assertDoesNotThrow(() -> new DataOfSquare(index));
    }

    @Test
    @DisplayName("Constructor throws IndexOutOfBoundsException for invalid color index")
    void constructorThrowsForInvalidColorIndex() {
        assertThrows(IndexOutOfBoundsException.class, () -> new DataOfSquare(3));
    }

    // ---------- BASIC / WHITE-BOX: internal color mapping ----------

    @Test
    @DisplayName("Color list is initialized with the expected mapping")
    void colorMappingIsInitializedAsExpected() throws Exception {
        // any valid index will do; we just want access to the instance
        DataOfSquare data = new DataOfSquare(0);

        ArrayList<Color> colors = getColorList(data);

        assertEquals(3, colors.size(), "Expected exactly three color entries");
        assertEquals(Color.darkGray, colors.get(0), "Index 0 should be darkGray (empty)");
        assertEquals(Color.BLUE,     colors.get(1), "Index 1 should be BLUE (food)");
        assertEquals(Color.white,    colors.get(2), "Index 2 should be white (snake)");
    }

    // ---------- BASIC FEATURES: lightMeUp with valid/invalid indices ----------

    @ParameterizedTest(name = "lightMeUp accepts color index {0}")
    @ValueSource(ints = {0, 1, 2})
    @DisplayName("lightMeUp does not throw for valid color indices (0,1,2)")
    void lightMeUpAcceptsValidColorIndices(int index) {
        DataOfSquare data = new DataOfSquare(0); // valid initial color
        assertDoesNotThrow(() -> data.lightMeUp(index));
    }

    @Test
    @DisplayName("lightMeUp throws IndexOutOfBoundsException for invalid color index")
    void lightMeUpThrowsForInvalidColorIndex() {
        DataOfSquare data = new DataOfSquare(0);
        assertThrows(IndexOutOfBoundsException.class, () -> data.lightMeUp(3));
    }
}
