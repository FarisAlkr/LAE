package spl.lae;

import parser.*;
import memory.*;
import scheduling.*;

import java.util.List;

public class LinearAlgebraEngine {

    private SharedMatrix leftMatrix = new SharedMatrix();
    private SharedMatrix rightMatrix = new SharedMatrix();
    private TiredExecutor executor;

    public LinearAlgebraEngine(int numThreads) {
        // TODO: create executor with given thread count
        executor = new TiredExecutor(numThreads);
    }

    public ComputationNode run(ComputationNode computationRoot) {
        // TODO: resolve computation tree step by step until final matrix is produced

        computationRoot.associativeNesting();//handle n ary operations first
       while (computationRoot.getNodeType() != ComputationNodeType.MATRIX) {// while root is not a matris we keep on
          // Find next node to compute
          ComputationNode resolvable = computationRoot.findResolvable();

          if (resolvable == null) {
              break;  // Nothing to resolve
          }

          // Compute this node
          loadAndCompute(resolvable);
      }


          return computationRoot;
    }





    public void loadAndCompute(ComputationNode node) {
        // TODO: load operand matrices
        // TODO: create compute tasks & submit tasks to executor


      ComputationNodeType operation = node.getNodeType();
      List<ComputationNode> children = node.getChildren();

      
      double[][] left = children.get(0).getMatrix();
      leftMatrix.loadRowMajor(left);

      
      if (operation == ComputationNodeType.ADD || operation == ComputationNodeType.MULTIPLY) {
          double[][] right = children.get(1).getMatrix();

          if (operation == ComputationNodeType.MULTIPLY) {
              // For multiplication, store right matrix as columns
              rightMatrix.loadColumnMajor(right);
          } else {
              rightMatrix.loadRowMajor(right);
          }
      }

      // Create tasks based on operation
      List<Runnable> tasks;
      if (operation == ComputationNodeType.ADD) {
          tasks = createAddTasks();
      } else if (operation == ComputationNodeType.MULTIPLY) {
          tasks = createMultiplyTasks();
      } else if (operation == ComputationNodeType.NEGATE) {
          tasks = createNegateTasks();
      } else if (operation == ComputationNodeType.TRANSPOSE) {
          tasks = createTransposeTasks();
      } else {
          tasks = new ArrayList<>();  // Empty list
      }

      // Submit tasks and wait for completion
      executor.submitAll(tasks);

      // Read result and resolve node
      double[][] result = leftMatrix.readRowMajor();
      node.resolve(result);//We are done with this node

    }

    public List<Runnable> createAddTasks() {
        // TODO: return tasks that perform row-wise addition
        List<Runnable> tasks = new ArrayList<>();
    
        int numRows = leftMatrix.length();



        for (int i = 0; i < numRows; i++) {

            final int rowIndex = i;
            tasks.add(() -> {
                SharedVector leftRow = leftMatrix.get(rowIndex);
                SharedVector rightRow = rightMatrix.get(rowIndex);
                leftRow.add(rightRow);
            });
        }
        return tasks;
    }

    public List<Runnable> createMultiplyTasks() {
        // TODO: return tasks that perform row × matrix multiplication
        List<Runnable> tasks = new ArrayList<>();
        int numRows = leftMatrix.length();
        for (int i = 0; i < numRows; i++) {
            final int rowIndex = i;//final for lambdda
            tasks.add(() -> {
                SharedVector leftRow = leftMatrix.get(rowIndex);
                leftRow.vecMatMul(rightMatrix);
            });
        }
        return tasks;
    }

    public List<Runnable> createNegateTasks() {
        // TODO: return tasks that negate rows
     List<Runnable> tasks = new ArrayList<>();

      int numRows = leftMatrix.length();

      for (int i = 0; i < numRows; i++) {
          final int rowIndex = i;

          tasks.add(() -> {
              SharedVector row = leftMatrix.get(rowIndex);
              row.negate();
          });
      }
      return tasks;
    }

    public List<Runnable> createTransposeTasks() {
        // TODO: return tasks that transpose rows

      List<Runnable> tasks = new ArrayList<>();

      //Here i did it with onley one thread with no parallelism 
      //Because transpose changes the matrix dimensions - can't split by row when the rows themselves change
      tasks.add(() -> {
          double[][] original = leftMatrix.readRowMajor();

          int rows = original.length;
          int cols = original[0].length;

          // Create transposed matrix
          double[][] transposed = new double[cols][rows];
          for (int i = 0; i < rows; i++) {
              for (int j = 0; j < cols; j++) {
                  transposed[j][i] = original[i][j];
              }
          }

          leftMatrix.loadRowMajor(transposed);
      });

      return tasks;
    }







    public String getWorkerReport() {
        // TODO: return summary of worker activity
        return executor.getWorkerReport();
    }
}
