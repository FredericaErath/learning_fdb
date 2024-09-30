import com.apple.foundationdb.*;
import com.apple.foundationdb.tuple.Tuple;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @version 1.0
 * @description singleGetRange
 * @date 2024/9/28 16:42
 */

public class SingleGetRange {
    private static final FDB fdb = FDB.selectAPIVersion(620);
    private static final Database db = fdb.open();

    // Method to store 10k key-value pairs
    public void storeKeyValuePairs() {
        try (Transaction tr = db.createTransaction()) {
            for (int i = 0; i < 10000; i++) {

                // fdb sort key according to lexi order
                String key = String.format("key_%05d", i);
                String value = "value_" + i;

                tr.set(Tuple.from(key).pack(), Tuple.from(value).pack());
            }

            tr.commit().join();

            System.out.println("Inserted 10000 key-value pairs in Unicode order.");
        }
    }

    public void getRangeWithMode(String mode) {
        try (Transaction tr = db.createTransaction()) {
            long startTime = System.nanoTime(); // Start time

            // Get range from \x00 to \xff
            Range range = new Range(Tuple.from("").pack(), Tuple.from("\uffff").pack());
            List<KeyValue> results = SingleVsMultiRanges.getResByMode(mode, range, tr);

            long endTime = System.nanoTime();

            long timeElapsed = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
            System.out.println("Mode: " + mode + " | Retrieved " + results.size() + " key-value pairs in " + timeElapsed + " ms");
        }
    }
}
