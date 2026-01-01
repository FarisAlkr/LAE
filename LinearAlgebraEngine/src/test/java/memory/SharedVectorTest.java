package memory;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SharedVectorTest {

    @Test
    void testGetAndLength() {
        double[] arr = {1.0, 2.0, 3.0};
        SharedVector v = new SharedVector(arr, VectorOrientation.ROW_MAJOR);

        assertEquals(3, v.length());
        assertEquals(1.0, v.get(0));
         assertEquals(3.0, v.get(2));
    }

    @Test
    void testSingleElement() {
        SharedVector v = new SharedVector(new double[]{5.0}, VectorOrientation.ROW_MAJOR);

        assertEquals(1, v.length());
         assertEquals(5.0, v.get(0));
    }

    @Test
    void testAdd() {
        SharedVector v1 = new SharedVector(new double[]{1.0, 2.0}, VectorOrientation.ROW_MAJOR);
        SharedVector v2 = new SharedVector(new double[]{3.0, 4.0}, VectorOrientation.ROW_MAJOR);

        v1.add(v2);

        assertEquals(4.0, v1.get(0));
         assertEquals(6.0, v1.get(1));
    }

    @Test
    void testAddWithZeros() {
        SharedVector v1 = new SharedVector(new double[]{1.0, 2.0}, VectorOrientation.ROW_MAJOR);
        SharedVector v2 = new SharedVector(new double[]{0.0, 0.0}, VectorOrientation.ROW_MAJOR);

        v1.add(v2);

        assertEquals(1.0, v1.get(0));
        assertEquals(2.0, v1.get(1));
    }

    @Test
    void testNegate() {
        SharedVector v = new SharedVector(new double[]{2.0, -3.0, 5.0}, VectorOrientation.ROW_MAJOR);

         v.negate();

        assertEquals(-2.0, v.get(0));
        assertEquals(3.0, v.get(1));
         assertEquals(-5.0, v.get(2));
    }

    @Test
    void testDot() {
        SharedVector v1 = new SharedVector(new double[]{1.0, 2.0, 3.0}, VectorOrientation.ROW_MAJOR);
        SharedVector v2 = new SharedVector(new double[]{4.0, 5.0, 6.0}, VectorOrientation.COLUMN_MAJOR);
        // 1*4 + 2*5 + 3*6 = 32
        assertEquals(32.0, v1.dot(v2));
    }

    @Test
    void testDotWithZero() {
        SharedVector v1 = new SharedVector(new double[]{1.0, 2.0}, VectorOrientation.ROW_MAJOR);
        SharedVector v2 = new SharedVector(new double[]{0.0, 0.0}, VectorOrientation.COLUMN_MAJOR);

        assertEquals(0.0, v1.dot(v2));
    }

    @Test
    void testTranspose() {

        SharedVector v = new SharedVector(new double[]{1.0, 2.0}, VectorOrientation.ROW_MAJOR);

        assertEquals(VectorOrientation.ROW_MAJOR, v.getOrientation());
        v.transpose();
       
        assertEquals(VectorOrientation.COLUMN_MAJOR, v.getOrientation());
    } 
}
