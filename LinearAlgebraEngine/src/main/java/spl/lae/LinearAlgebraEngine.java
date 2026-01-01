package spl.lae;

import parser.*;
import memory.*;
import scheduling.*;

import java.util.List;
 import java.util.ArrayList;

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
       
      try {
          while (computationRoot.getNodeType() != ComputationNodeType.MATRIX) {
             
              ComputationNode resolvable = computationRoot.findResolvable();
              if (resolvable == null) {
                  break;
              }
              loadAndCompute(resolvable);
          }
      } finally {
          // Ensure executor is properly shut down
          try {
              executor.shutdown();
          } catch (InterruptedException e) {
              Thread.currentThread().interrupt();
          }
      }

      return computationRoot;
  }





    public void loadAndCompute(ComputationNode node) {
        // TODO: load operand matrices
        // TODO: create compute tasks & submit tasks to executor


      ComputationNodeType operation = node.getNodeType();
      List<ComputationNode> children = node.getChildren();

      

     
       int count = children.size();
       if (operation == ComputationNodeType.NEGATE || operation == ComputationNodeType.TRANSPOSE) {
            if (count != 1) {
           throw new IllegalArgumentException("Illegal operation: unary operator requires exactly one operand");
               }
         }

        if (operation == ComputationNodeType.ADD || operation == ComputationNodeType.MULTIPLY) {
               if (count < 2) {
                      throw new IllegalArgumentException("Illegal operation: binary operator requires at least two operands");
                 }
           }
      
      double[][] left = children.get(0).getMatrix();
      leftMatrix.loadRowMajor(left);

      
      if (operation == ComputationNodeType.ADD || operation == ComputationNodeType.MULTIPLY) {
          double[][] right = children.get(1).getMatrix();


           if (operation == ComputationNodeType.ADD) {
              if ((left.length != right.length) || (left[0].length != right[0].length)) {

                  throw new IllegalArgumentException("Illegal operation: dimensions mismatch");
              }
          } else if (operation == ComputationNodeType.MULTIPLY) {

              if (left[0].length != right.length) {
                  throw new IllegalArgumentException("Illegal operation: dimensions mismatch");
              }
          }

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

      double[][] original = leftMatrix.readRowMajor();
      int numRows = original.length;
      int numCols = original[0].length;

      // Store original as columns in rightMatix
      rightMatrix.loadColumnMajor(original);

      // vreate zeroed result with transposed dimensions
      double[][] zeros = new double[numCols][numRows];
      leftMatrix.loadRowMajor(zeros);

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

      // leftMatrix now has transpsed dimensions [numCols][numRows]
      // rightMatrix has original data [numRows][numCols]
      // each task fills one row of leftMatrix

      int resultRows = leftMatrix.length();

       for (int i = 0; i < resultRows; i++) {
          final int rowIndex = i;
          tasks.add(() -> {
              SharedVector resultRow = leftMatrix.get(rowIndex);
              SharedVector sourceCol = rightMatrix.get(rowIndex);
              resultRow.add(sourceCol);
          });
      }

      return tasks;
  }



    public String getWorkerReport() {
        // TODO: return summary of worker activity
        return executor.getWorkerReport();
    }
}
