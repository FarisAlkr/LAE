package memory;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SharedMatrixTest {

    @Test
    void testLoadRowMajor() {
        SharedMatrix m = new SharedMatrix();
        double[][] data = {{1.0, 2.0}, {3.0, 4.0}};
        m.loadRowMajor(data);

        assertEquals(2, m.length());
        assertEquals(VectorOrientation.ROW_MAJOR, m.getOrientation());
        assertEquals(1.0, m.get(0).get(0));
        assertEquals(4.0, m.get(1).get(1));
    }

    @Test
    void testLoadColumnMajor() {
        SharedMatrix m = new SharedMatrix();
         double[][] data = {{1.0, 2.0}, {3.0, 4.0}};
         m.loadColumnMajor(data);

        assertEquals(2, m.length());
        assertEquals(VectorOrientation.COLUMN_MAJOR, m.getOrientation());
    }

    @Test
    void testReadRowMajor() {
        SharedMatrix m = new SharedMatrix();
        double[][] data = {{1.0, 2.0}, {3.0, 4.0}};
        m.loadRowMajor(data);

        double[][] result = m.readRowMajor();

        assertEquals(1.0, result[0][0]);
        assertEquals(4.0, result[1][1]);
    }

    @Test
    void testSingleElement() {
        double[][] data = {{7.0}};
        SharedMatrix m = new SharedMatrix(data);

        assertEquals(1, m.length());
        assertEquals(7.0, m.get(0).get(0));

        double[][] result = m.readRowMajor();
        assertEquals(7.0, result[0][0]);
    }

    @Test
    void testSingleRow() {
        SharedMatrix m = new SharedMatrix();
        double[][] data = {{1.0, 2.0, 3.0}};

        m.loadRowMajor(data);

        assertEquals(1, m.length());
        assertEquals(3, m.get(0).length());
    }

    @Test
    void testSingleColumn() {
        SharedMatrix m = new SharedMatrix();
         double[][] data = {{1.0}, {2.0}, {3.0}};

        m.loadRowMajor(data);

        assertEquals(3, m.length());
        assertEquals(1, m.get(0).length());
    }

    @Test
    void testEmptyMatrix() {
        SharedMatrix m = new SharedMatrix();
        assertEquals(0, m.length());
    }
}
