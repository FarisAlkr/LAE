package memory;

public class SharedMatrix {

    private volatile SharedVector[] vectors = {}; // underlying vectors

    public SharedMatrix() {
        // TODO: initialize empty matrix

        //it's already initialized
    }

    public SharedMatrix(double[][] matrix) {
        // TODO: construct matrix as row-major SharedVectors
        int numRows=matrix.length;
        vectors=new SharedVector[numRows];
        for(int i=0;i<numRows;i++){
            vectors[i]=new SharedVector(matrix[i],VectorOrientation.ROW_MAJOR);
        }
    }

    public void loadRowMajor(double[][] matrix) {
        // TODO: replace internal data with new row-major matrix
        int numRows=matrix.length;
        vectors=new SharedVector[numRows];
        for(int i=0;i<numRows;i++){
            vectors[i]=new SharedVector(matrix[i],VectorOrientation.ROW_MAJOR);
        }
    }

    public void loadColumnMajor(double[][] matrix) {
        // TODO: replace internal data with new column-major matrix
        int numRows=matrix.length;
        int numCols=matrix[0].length;
        vectors=new SharedVector[numCols];
        for(int j=0;j<numCols;j++){
            double[] col=new double[numRows];
            for(int i=0;i<numRows;i++){
                col[i]=matrix[i][j];
            }
            vectors[j]=new SharedVector(col,VectorOrientation.COLUMN_MAJOR);
        }
    }
    public double[][] readRowMajor() {
        // TODO: return matrix contents as a row-major double[][]
        if(vectors.length==0){
            return new double[0][0];
        }   
        if (vectors[0].getOrientation() == VectorOrientation.ROW_MAJOR) {
            // Stored as rows
          double[][] result = new double[vectors.length][];
          for (int i = 0; i < vectors.length; i++) {
              result[i] = new double[vectors[i].length()];
              for (int j = 0; j < vectors[i].length(); j++) {
                  result[i][j] = vectors[i].get(j);
              }
          }
          return result;
      } else {
          // Stored columns  need  transpose
          int numCols = vectors.length;

          int numRows = vectors[0].length();

          double[][] result = new double[numRows][numCols];
          for (int j = 0; j < numCols; j++) {
              for (int i = 0; i < numRows; i++) {
                  result[i][j] = vectors[j].get(i);
              }
          }
          return result;
      }
    }

    public SharedVector get(int index) {
        // TODO: return vector at index
        return vectors[index];
    }

    public int length() {
        // TODO: return number of stored vectors
        return vectors.length;
    }

    public VectorOrientation getOrientation() {
        // TODO: return orientation
        if(vectors.length==0){
            return null; 
        }
        return vectors[0].getOrientation();
    }

    private void acquireAllVectorReadLocks(SharedVector[] vecs) {
        // TODO: acquire read lock for each vector
        for(int i=0;i<vecs.length;i++){
            vecs[i].readLock();
        }



    }

    private void releaseAllVectorReadLocks(SharedVector[] vecs) {
        // TODO: release read locks
        for(int i=0;i<vecs.length;i++){
            vecs[i].readUnlock();
        }
    }

    private void acquireAllVectorWriteLocks(SharedVector[] vecs) {
        // TODO: acquire write lock for each vector
        for(int i=0;i<vecs.length;i++){
            vecs[i].writeLock();
        }
    }

    private void releaseAllVectorWriteLocks(SharedVector[] vecs) {
        // TODO: release write locks
        for(int i=0;i<vecs.length;i++){
            vecs[i].writeUnlock();
        }
    }
}
