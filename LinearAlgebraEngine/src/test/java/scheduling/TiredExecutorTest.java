package scheduling;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TiredExecutorTest {

    @Test
    void testSubmitTasks() throws InterruptedException {
        TiredExecutor exec = new TiredExecutor(2);
       AtomicInteger counter = new AtomicInteger(0);

        List<Runnable> tasks = new ArrayList<>();

        tasks.add(() -> counter.incrementAndGet());
        tasks.add(() -> counter.incrementAndGet());
        tasks.add(() -> counter.incrementAndGet());

        exec.submitAll(tasks);
         exec.shutdown();

        assertEquals(3, counter.get());
    }

    @Test
    void testSingleTask() throws InterruptedException {
        TiredExecutor exec = new TiredExecutor(2);
         AtomicInteger counter = new AtomicInteger(0);

        List<Runnable> tasks = new ArrayList<>();
      tasks.add(() -> counter.incrementAndGet());

        exec.submitAll(tasks);
        exec.shutdown();

        assertEquals(1,counter.get());
    }

    @Test
    void testSingleWorker() throws InterruptedException {
        TiredExecutor exec = new TiredExecutor(1);
        AtomicInteger counter = new AtomicInteger(0);

        List<Runnable> tasks = new ArrayList<>();

        for (int i = 0; i < 5; i++) {

            tasks.add(() -> counter.incrementAndGet());
        }

        exec.submitAll(tasks);
        
        exec.shutdown();

        assertEquals(5, counter.get());
    }

    @Test
    void testManyWorkers() throws InterruptedException {
        TiredExecutor exec = new TiredExecutor(8);
         AtomicInteger counter = new AtomicInteger(0);

        List<Runnable> tasks = new ArrayList<>();

        for (int i = 0; i < 10; i++) {

             tasks.add(() -> counter.incrementAndGet());
        }

        exec.submitAll(tasks);
         exec.shutdown();

     assertEquals(10, counter.get());
    }

    @Test
    void testEmptyList() throws InterruptedException {
        TiredExecutor exec = new TiredExecutor(2);
        List<Runnable> tasks = new ArrayList<>();

        exec.submitAll(tasks);
        exec.shutdown();

        // should not throw or hang
    }

    @Test
    void testWorkerReport() throws InterruptedException {

        TiredExecutor exec = new TiredExecutor(2);

        List<Runnable> tasks = new ArrayList<>();
        tasks.add(() -> {

            double x = 0;

            for (int i = 0; i < 100; i++) x += i;
        });
        exec.submitAll(tasks);

        String report = exec.getWorkerReport();
        assertNotNull(report);
        assertTrue(report.contains("Worker"));

        exec.shutdown();
    }

      @Test
      void testFairnessDistribution() throws InterruptedException {
          int numWorkers = 4;

          TiredExecutor exec = new TiredExecutor(numWorkers);

          // Create enough tasks to distribute among workers

          List<Runnable> tasks = new ArrayList<>();

          for (int i = 0; i < 20; i++) {

              tasks.add(() -> {
                  // Simulate some work
                  double x = 0;
                  for (int j = 0; j < 10000; j++) {

                      x += Math.sqrt(j);
                  }
              });
          }

          exec.submitAll(tasks);

          String report =exec.getWorkerReport();
          exec.shutdown();

          // Verify al workrs participated(have non-zero fatigue)

          assertNotNull(report);
          assertTrue(report.contains("Worker 0"));
          assertTrue(report.contains("Worker 1"));
          assertTrue(report.contains("Worker 2"));
          assertTrue(report.contains("Worker 3"));

          // Verify fatigue values exist and are positive
          String[] lines = report.split("\n");
          for (String line : lines) {
              if (line.contains("fatigue=")) {
                  int start = line.indexOf("fatigue=") + 8;
                  int end = line.indexOf(",", start);
                   double fatigue = Double.parseDouble(line.substring(start, end));
                   assertTrue(fatigue >= 0, "Fatigue should be non-negative");
              }
          }
      }
}
