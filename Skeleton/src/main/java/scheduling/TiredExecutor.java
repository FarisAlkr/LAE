package scheduling;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TiredExecutor {

    private final TiredThread[] workers;
    private final PriorityBlockingQueue<TiredThread> idleMinHeap = new PriorityBlockingQueue<>();
    private final AtomicInteger inFlight = new AtomicInteger(0);

    public TiredExecutor(int numThreads) {
        // TODO
        workers = new TiredThread[numThreads];
        for (int i = 0; i < numThreads; i++) {
            double fatigueFactor = 0.5 + Math.random();
            workers[i] = new TiredThread(i, fatigueFactor);
            workers[i].start();
            idleMinHeap.add(workers[i]);
        }
    }

    public void submit(Runnable task) {
        // TODO

        try {
            TiredThread worker = idleMinHeap.take();
            inFlight.incrementAndGet();

            worker.newTask(() -> {
                try {

                    task.run();

                } finally {
                    idleMinHeap.add(worker);
                   int remaining = inFlight.decrementAndGet();
   
               // Only notify if ALL tasks are done
                 if (remaining == 0) {
                    synchronized (TiredExecutor.this) {
                        TiredExecutor.this.notify();
                    }
                }
            }
            });
        } catch (InterruptedException e) {
            
            Thread.currentThread().interrupt();
        }
    }

    public void submitAll(Iterable<Runnable> tasks) {
        // TODO: submit tasks one by one and wait until all finish
         
          for(Runnable task:tasks){

            submit(task);
        }
      
        synchronized (this) {
          while (inFlight.get() > 0) {
              try {
                  wait();  // Sleep until notified
              } catch (InterruptedException e) {

                  Thread.currentThread().interrupt();

                  break;
              }
          }
      }

       
    }

    public void shutdown() throws InterruptedException {
        // TODO
    
        for (TiredThread worker : workers) {

            worker.shutdown();
        }
        for (TiredThread worker : workers) {

            worker.join();
        }

       
    }

    public synchronized String getWorkerReport() {
        // TODO: return readable statistics for each worker
     String report = "";

      for (TiredThread worker : workers) {
          report = report + "Worker " + worker.getWorkerId()
                 + ": fatigue=" + worker.getFatigue()
                 + ", timeUsed=" + worker.getTimeUsed()
                 + ", timeIdle=" + worker.getTimeIdle()
                 + "\n";
      }

      return report;
  }
}
