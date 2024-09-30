import com.apple.foundationdb.*;
import com.apple.foundationdb.tuple.Tuple;
import sun.net.idn.Punycode;

import java.util.List;

/**
 * @version 1.0
 * @description basicFDBOps
 * @date 2024/9/28 16:34
 */

public class BasicFDBOps {
    private static final FDB fdb = FDB.selectAPIVersion(620);  // Select API version
    private static final Database db = fdb.open();             // Open the database

    // Set a key-value pair
    public void set(String key, String value) {
        try (Transaction tr = db.createTransaction()) {
            tr.set(Tuple.from(key).pack(), Tuple.from(value).pack());
            tr.commit().join();
            System.out.println("Set key: " + key + " with value: " + value);
        }
    }

    // Get a value for a key
    public String get(String key) {
        try (Transaction tr = db.createTransaction()) {
            byte[] result = tr.get(Tuple.from(key).pack()).join();
            if (result != null) {
                String value = Tuple.fromBytes(result).getString(0);
                System.out.println("Retrieved value for key " + key + ": " + value);
                return value;
            } else {
                System.out.println("Key " + key + " not found.");
                return null;
            }
        }
    }

    // Get a range of keys
    public void getRange(String startKey, String endKey) {
        try (Transaction tr = db.createTransaction()) {
            List<KeyValue> results = tr.getRange(Tuple.from(startKey).pack(), Tuple.from(endKey).pack()).asList().join();
            for (KeyValue kv : results) {
                String key = Tuple.fromBytes(kv.getKey()).getString(0);
                String value = Tuple.fromBytes(kv.getValue()).getString(0);
                System.out.println("Key: " + key + ", Value: " + value);
            }
        }
    }

    // clear all keys
    public static void clearAll(){
        try (Transaction tr = db.createTransaction()) {
            Range range = new Range(new byte[]{}, new byte[]{(byte) 0xFF});
            tr.clear(range);
            tr.commit().join();
            System.out.println("All key-value pairs have been cleared.");
        }

        db.close();
    }
}
