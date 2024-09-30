import com.apple.foundationdb.*;
import com.apple.foundationdb.tuple.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @version 1.0
 * @description single vs multiple efficient comparison
 * @date 2024/9/28 17:33
 */

public class SingleVsMultiRanges {
    private static final FDB fdb = FDB.selectAPIVersion(620);
    private static final Database db = fdb.open();

    static List<KeyValue> getResByMode(String mode, Range range, Transaction tr) {
        List<KeyValue> results;
        switch (mode) {
            case "WANT_ALL":
                // i int: rowLimit, b boolean: reverse,  <LIMIT> defaults to 25 if omitted.
                results = tr.getRange(range, 10000, false, StreamingMode.WANT_ALL).asList().join();
                break;
            case "EXACT":
                results = tr.getRange(range, 10000, false, StreamingMode.EXACT).asList().join();
                break;
            case "ITERATOR":
                results = tr.getRange(range, 10000, false, StreamingMode.ITERATOR).asList().join();
                break;
            case "SMALL":
                results = tr.getRange(range, 10000, false, StreamingMode.SMALL).asList().join();
                break;
            case "MEDIUM":
                results = tr.getRange(range, 10000, false, StreamingMode.MEDIUM).asList().join();
                break;
            case "LARGE":
                results = tr.getRange(range, 10000, false, StreamingMode.LARGE).asList().join();
                break;
            case "SERIAL":
                results = tr.getRange(range, 10000, false, StreamingMode.SERIAL).asList().join();
                break;
            default:
                throw new IllegalArgumentException("Unknown mode: " + mode);
        }
        return results;
    }

    public void singleGetRange(String mode) {
        try (Transaction tr = db.createTransaction()) {
            long startTime = System.nanoTime(); // Start time

            // Get range from \x00 to \xff
            Range range = new Range(Tuple.from("").pack(), Tuple.from("\uffff").pack());
            List<KeyValue> results = getResByMode(mode, range, tr);

            long endTime = System.nanoTime(); // End time

            // Calculate time taken
            long timeElapsed = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
            System.out.println("mode: " + mode  + ", Single getRange | Retrieved " + results.size() + " key-value pairs in " + timeElapsed + " ms");
        }
    }

    // Execute multiple getRange requests in parallel for 10 ranges
    public void multipleGetRangesParallel(String mode) throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(10);

        List<Callable<List<KeyValue>>> tasks = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            final int rangeStart = i * 1000;  // Defining range boundaries for each 1k keys
            tasks.add(() -> {
                try (Transaction tr = db.createTransaction()) {
                    // Define the range for this partition
                    Range range = new Range(
                            Tuple.from(String.format("key_%05d", rangeStart)).pack(),
                            Tuple.from(String.format("key_%05d", rangeStart+1000)).pack());
                    // Execute the getRange
                    List<KeyValue> results = getResByMode(mode, range, tr);
                    return results;
                }
            });
        }

        long startTime = System.nanoTime(); // Start time

        // Submit all tasks to be executed in parallel
        List<Future<List<KeyValue>>> futures = executor.invokeAll(tasks);

        // Collect the results
        int totalSize = 0;
        for (Future<List<KeyValue>> future : futures) {
            totalSize += future.get().size();
        }

        long endTime = System.nanoTime(); // End time
        long timeElapsed = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);

        // Shut down executor
        executor.shutdown();

        System.out.println("mode: " + mode  + ", Parallel getRanges | Retrieved " + totalSize + " key-value pairs in " + timeElapsed + " ms");
    }

}
