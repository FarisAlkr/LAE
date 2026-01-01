package spl.lae;
import java.io.IOException;

import parser.*;

public class Main {
    public static void main(String[] args) throws IOException {
      // TODO: main

          int numThreads = Integer.parseInt(args[0]);
          String inputFile = args[1];
          String outputFile = args[2];

          LinearAlgebraEngine engine = null;

          try {
              InputParser parser = new InputParser();
              ComputationNode root = parser.parse(inputFile);

              // If root is already a matrix, write directly
              if (root.getNodeType() == ComputationNodeType.MATRIX) {
                  OutputWriter.write(root.getMatrix(), outputFile);
                  return;
              }

              engine = new LinearAlgebraEngine(numThreads);
              ComputationNode result = engine.run(root);

              // Write result
              OutputWriter.write(result.getMatrix(), outputFile);

          } catch (Exception e) {
              // Write error to output file
              OutputWriter.write(e.getMessage(), outputFile);
          } 
            
          }
          



    
}