package memory;

import java.util.concurrent.locks.ReadWriteLock;

public class SharedVector {

    private double[] vector;
    private VectorOrientation orientation;
    private ReadWriteLock lock = new java.util.concurrent.locks.ReentrantReadWriteLock();

    public SharedVector(double[] vector, VectorOrientation orientation) {
        // TODO: store vector data and its orientation
        
        this.vector = new double[vector.length];
        for(int i=0;i<vector.length;i++){
            this.vector[i]=vector[i];
        }

        this.orientation = orientation;
    }

    public double get(int index) {
        // TODO: return element at index (read-locked)
        this.readLock();
        try{
            return this.vector[index];
        }finally{
            this.readUnlock();
        }
    
    }

    public int length() {
        // TODO: return vector length

        return vector.length;// there is no need for locking since the length is final
        // and reading an int is atomic in Java
    }

    public VectorOrientation getOrientation() {
        // TODO: return vector orientation
        return this.orientation;
    }



    public void writeLock() {
        // TODO: acquire write lock

        lock.writeLock().lock();
    }

    public void writeUnlock() {
        // TODO: release write lock
        lock.writeLock().unlock();
    }

    public void readLock() {
        // TODO: acquire read lock
        lock.readLock().lock();
    }

    public void readUnlock() {
        // TODO: release read lock
        lock.readLock().unlock();
    }





    public void transpose() {
        // TODO: transpose vector
        this.writeLock();
        try{
            if(this.orientation==VectorOrientation.ROW_MAJOR){
                this.orientation=VectorOrientation.COLUMN_MAJOR;
            }else{
                this.orientation=VectorOrientation.ROW_MAJOR;
            }
        }
        finally{
            this.writeUnlock();
        }

    }

    public void add(SharedVector other) {
        // TODO: add two vectors
        this.writeLock();
        other.readLock();
        try{
            for(int i=0;i<vector.length;i++){
                vector[i]=vector[i]+other.vector[i];
            }
        }finally{
            other.readUnlock();
            this.writeUnlock();
        }

    }

    public void negate() {
        // TODO: negate vector
        this.writeLock();
        try{
            for(int i=0;i<vector.length;i++){
                vector[i]=-vector[i];
            }
        }finally{
            this.writeUnlock();
        }
    }

    public double dot(SharedVector other) {
        // TODO: compute dot product (row · column)
        this.readLock();
        other.readLock();
        double result=0;
        try{
            for(int i=0;i<vector.length;i++){
                result+=this.vector[i]*other.vector[i];
            }
            return result;
        }finally{
            other.readUnlock();
            this.readUnlock();
        }
    }

    public void vecMatMul(SharedMatrix matrix) {
        // TODO: compute row-vector × matrix
      this.writeLock();
      try {
          int numCols = matrix.length();
          double[] result = new double[numCols];

          //Get all columns and lock them ALL first
          SharedVector[] columns = new SharedVector[numCols];
          for (int j = 0; j < numCols; j++) {

              columns[j] = matrix.get(j);
              columns[j].readLock();

          }

          try {
              // Now compute.. all columns  locked
              for (int j = 0; j < numCols; j++) {

                  double sum = 0;
                  for (int i = 0; i < vector.length; i++) {
                      sum += vector[i] * columns[j].vector[i];

                  }
                  result[j] = sum;
              }
              this.vector = result;
              this.orientation = VectorOrientation.ROW_MAJOR;
              
          } finally {
              //  Unlock all columns
              for (int j = 0; j < numCols; j++) {
                  columns[j].readUnlock();
              }
          }
      } finally {
          this.writeUnlock();
      }
  }
}

