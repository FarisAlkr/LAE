package spl.lae;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import parser.ComputationNode;
import parser.ComputationNodeType;
import java.util.ArrayList;
import java.util.List;

public class LinearAlgebraEngineTest {

    @Test
    void testAddition() {
        LinearAlgebraEngine engine = new LinearAlgebraEngine(2);

        double[][] a = {{1.0, 2.0}, {3.0, 4.0}};
        double[][] b = {{5.0, 6.0}, {7.0, 8.0}};

        List<ComputationNode> children = new ArrayList<>();
        children.add(new ComputationNode(a));
        children.add(new ComputationNode(b));

        ComputationNode root = new ComputationNode(ComputationNodeType.ADD, children);
        ComputationNode result = engine.run(root);

        double[][] res = result.getMatrix();
        assertEquals(6.0, res[0][0]);
        assertEquals(12.0, res[1][1]);
    }

    @Test
    void testMultiplication() {
        LinearAlgebraEngine engine = new LinearAlgebraEngine(2);

        double[][] a = {{1.0, 2.0}, {3.0, 4.0}};
        double[][] b = {{2.0, 0.0}, {1.0, 2.0}};

        List<ComputationNode> children = new ArrayList<>();
        children.add(new ComputationNode(a));
        children.add(new ComputationNode(b));

        ComputationNode root = new ComputationNode(ComputationNodeType.MULTIPLY, children);
        ComputationNode result = engine.run(root);

        double[][] res = result.getMatrix();
        // [1,2]*[[2,0],[1,2]] = [4, 4]
        assertEquals(4.0, res[0][0]);
        assertEquals(4.0, res[0][1]);
    }

    @Test
    void testNegate() {
        LinearAlgebraEngine engine = new LinearAlgebraEngine(2);

        double[][] a = {{1.0, -2.0}, {3.0, 4.0}};

        List<ComputationNode> children = new ArrayList<>();
        children.add(new ComputationNode(a));

        ComputationNode root = new ComputationNode(ComputationNodeType.NEGATE, children);
        ComputationNode result = engine.run(root);

        double[][] res = result.getMatrix();

        assertEquals(-1.0, res[0][0]);
         assertEquals(2.0, res[0][1]);
        assertEquals(-3.0, res[1][0]);
    }

    @Test
    void testTranspose() {
        LinearAlgebraEngine engine = new LinearAlgebraEngine(2);

        double[][] a ={{1.0, 2.0, 3.0},{4.0, 5.0, 6.0}};

        List<ComputationNode> children = new ArrayList<>();
        children.add(new ComputationNode(a));

        ComputationNode root = new ComputationNode(ComputationNodeType.TRANSPOSE, children);
        ComputationNode result = engine.run(root);

        double[][] res = result.getMatrix();
        assertEquals(3, res.length);
        assertEquals(2, res[0].length);
    }

    @Test
    void testSingleElementMatrix() {
        LinearAlgebraEngine engine = new LinearAlgebraEngine(2);

        double[][] a = {{3.0}};
         double[][] b = {{5.0}};

        List<ComputationNode> children = new ArrayList<>();
        children.add(new ComputationNode(a));
        children.add(new ComputationNode(b));

        ComputationNode root = new ComputationNode(ComputationNodeType.ADD, children);
        ComputationNode result = engine.run(root);

        assertEquals(8.0, result.getMatrix()[0][0]);
    }

    @Test
    void testJustMatrix() {
        LinearAlgebraEngine engine = new LinearAlgebraEngine(2);

        double[][] a = {{1.0, 2.0}};
        ComputationNode root = new ComputationNode(a);

        ComputationNode result = engine.run(root);
        assertEquals(1.0, result.getMatrix()[0][0]);
    }

    @Test
    void testAddDimensionError() {
        LinearAlgebraEngine engine = new LinearAlgebraEngine(2);

        double[][] a = {{1.0, 2.0}};
        double[][] b ={{1.0}, {2.0}};

        List<ComputationNode> children = new ArrayList<>();
        children.add(new ComputationNode(a));
        children.add(new ComputationNode(b));

        ComputationNode root = new ComputationNode(ComputationNodeType.ADD, children);
        assertThrows(IllegalArgumentException.class, () -> engine.run(root));
    }

    @Test
    void testMultiplyDimensionError() {
        LinearAlgebraEngine engine = new LinearAlgebraEngine(2);

        double[][] a = {{1.0, 2.0, 3.0}};
        double[][] b = {{1.0, 2.0}};

        List<ComputationNode> children = new ArrayList<>();
        children.add(new ComputationNode(a));
        children.add(new ComputationNode(b));

        ComputationNode root = new ComputationNode(ComputationNodeType.MULTIPLY, children);
        assertThrows(IllegalArgumentException.class, () -> engine.run(root));
    }

    @Test
    void testNegateTooManyOperands() {
        LinearAlgebraEngine engine = new LinearAlgebraEngine(2);

        double[][] a = {{1.0}};
        double[][] b={{2.0}};

        List<ComputationNode> children = new ArrayList<>();
        children.add(new ComputationNode(a));
        children.add(new ComputationNode(b));

        ComputationNode root = new ComputationNode(ComputationNodeType.NEGATE, children);
        assertThrows(IllegalArgumentException.class, () -> engine.run(root));
    }
    @Test
      void testNestedOperation() {
          LinearAlgebraEngine engine = new LinearAlgebraEngine(2);

          // (A + B) * C
          double[][] a = {{1.0, 2.0}, {3.0, 4.0}};
          double[][] b = {{1.0, 1.0}, {1.0, 1.0}};
          double[][] c = {{1.0, 0.0},{0.0, 1.0}};

          // Create A + B node
          List<ComputationNode> addChildren = new ArrayList<>();

          addChildren.add(new ComputationNode(a));
          addChildren.add(new ComputationNode(b));

          ComputationNode addNode = new ComputationNode(ComputationNodeType.ADD, addChildren);

          // Create (A+B) * C node
          List<ComputationNode> mulChildren = new ArrayList<>();
          mulChildren.add(addNode);
          mulChildren.add(new ComputationNode(c));

          ComputationNode root = new ComputationNode(ComputationNodeType.MULTIPLY, mulChildren);

          ComputationNode result = engine.run(root);
          double[][] res = result.getMatrix();

          // A + B = {{2,3},{4,5}}, * identity = {{2,3},{4,5}}

          assertEquals(2.0, res[0][0]);
           assertEquals(3.0, res[0][1]);
          assertEquals(4.0, res[1][0]);
          assertEquals(5.0, res[1][1]);
      }

      @Test
      void testNaryAddition() {
          LinearAlgebraEngine engine = new LinearAlgebraEngine(2);

          // A + B + C (3 operands)
          double[][] a = {{1.0, 1.0}, {1.0, 1.0}};
          double[][] b = {{2.0, 2.0}, {2.0, 2.0}};
           double[][] c= {{3.0, 3.0},{3.0, 3.0}};

          List<ComputationNode> children = new ArrayList<>();

          children.add(new ComputationNode(a));
          children.add(new ComputationNode(b));
          children.add(new ComputationNode(c));

          ComputationNode root = new ComputationNode(ComputationNodeType.ADD, children);

          ComputationNode result = engine.run(root);
          double[][] res = result.getMatrix();

          // 1+2+3 = 6 for all elements
          assertEquals(6.0, res[0][0]);
          assertEquals(6.0, res[0][1]);
          assertEquals(6.0, res[1][0]);
          assertEquals(6.0, res[1][1]);
      }
}
