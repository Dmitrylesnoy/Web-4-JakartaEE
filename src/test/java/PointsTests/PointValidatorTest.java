package PointsTests;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.lab.web.data.PointData;
import com.lab.web.utils.PointValidator;

public class PointValidatorTest {

    // validateX tests
    @ParameterizedTest
    @ValueSource(floats = { -3.0f, -2.5f, -1f, 0f, 1f, 4.5f, 5.0f })
    void validateXrangePositive(float x) {
        assertTrue(PointValidator.validateX(x));
    }

    @ParameterizedTest
    @ValueSource(floats = { -3.000001f, -4f, -10f, 5.000001f, 6f, 10f })
    void validateXrangeNegative(float x) {
        assertFalse(PointValidator.validateX(x));
    }

    // validateY tests
    @ParameterizedTest
    @ValueSource(floats = { -5.0f, -4f, -1f, 0f, 1f, 4f, 5.0f })
    void validateYrangePositive(float y) {
        assertTrue(PointValidator.validateY(y));
    }

    @ParameterizedTest
    @ValueSource(floats = { -5.000001f, -6f, -10f, 5.000001f, 6f, 10f })
    void validateYrangeNegative(float y) {
        assertFalse(PointValidator.validateY(y));
    }

    // validateR tests
    @ParameterizedTest
    @ValueSource(floats = { 0f, 1f, 2.5f, 4f, 5.0f })
    void validateRrangePositive(float r) {
        assertTrue(PointValidator.validateR(r));
    }

    @ParameterizedTest
    @ValueSource(floats = { -1f, -5f, 5.000001f, 6f })
    void validateRrangeNegative(float r) {
        assertFalse(PointValidator.validateR(r));
    }

    // checkArea tests - quadrant 4: rectangle (x>=0, y<=0)
    @ParameterizedTest
    @CsvSource({ "2,-2,5,true", "6,-2,5,false", "2,-6,5,false" })
    void checkArea_RectangleQuadrant(float x, float y, float r, boolean expected) {
        assertEquals(expected, PointValidator.checkArea(x, y, r));
    }

    // checkArea tests - quadrant 2: quarter circle (x<=0, y>=0)
    @ParameterizedTest
    @CsvSource({ "-3,4,5,true", "-4,4,5,false", "-6,0,5,false" })
    void checkArea_CircleQuadrant(float x, float y, float r, boolean expected) {
        assertEquals(expected, PointValidator.checkArea(x, y, r));
    }

    // checkArea tests - quadrant 3: triangle (x<=0, y<=0)
    @ParameterizedTest
    @CsvSource({ "-2,-1,5,true", "-5,-5,5,false", "-2,-3,5,false" })
    void checkArea_TriangleQuadrant(float x, float y, float r, boolean expected) {
        assertEquals(expected, PointValidator.checkArea(x, y, r));
    }

    @Test
    void checkArea_OriginShouldAlwaysReturnTrue() {
        assertTrue(PointValidator.checkArea(0, 0, 5));
    }

    // fillPoint tests
    @ParameterizedTest
    @CsvSource({ "0,0,5", "2,-2,5", "-3,4,5", "-2,-1,5" })
    void fillPoint_WithValidCoordinates_ShouldCreatePoint(String x, String y, String r) {
        PointData point = PointValidator.fillPoint(x, y, r, true);
        assertNotNull(point);
        assertEquals(PointValidator.checkArea(Float.parseFloat(x), Float.parseFloat(y), Float.parseFloat(r)),
                point.isHit());
    }

    @ParameterizedTest
    @CsvSource({ "6,0,5", "0,6,5", "0,0,6" })
    void fillPointInvalidCoords(String x, String y, String r) {
        assertThrows(IllegalArgumentException.class, () -> PointValidator.fillPoint(x, y, r, true));
    }

    @Test
    void fillPointAlwaysCreatePoint() {
        assertNotNull(PointValidator.fillPoint("100", "100", "100", false));
    }

    @Test
    void fillPointSetCurrentDate() {
        LocalDateTime before = LocalDateTime.now();
        PointData point = PointValidator.fillPoint("0", "0", "5", true);
        assertTrue(point.getDate().isAfter(before) || point.getDate().equals(before));
    }

    @Test
    void fillPointInvalidFormat() {
        assertThrows(IllegalArgumentException.class, () -> PointValidator.fillPoint("abc", "0", "5", true));
    }
}