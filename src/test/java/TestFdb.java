/**
 * @version 1.0
 * @description fdb functions test
 * @date 2024/9/28 16:19
 */
import com.apple.foundationdb.Database;
import com.apple.foundationdb.FDB;
import com.apple.foundationdb.Range;
import com.apple.foundationdb.Transaction;
import com.apple.foundationdb.tuple.Tuple;

public class TestFdb {

    public static void main(String[] args) {
        FDB fdb = FDB.selectAPIVersion(620);

        Database db = fdb.open();

        // Start a transaction to clear all key-value pairs
        try (Transaction tr = db.createTransaction()) {
            // Define the range for the entire keyspace (from the smallest to the largest possible key)
            Range range = new Range(new byte[]{}, new byte[]{(byte) 0xFF});

            // Clear the range (this deletes all key-value pairs in this range)
            tr.clear(range);

            // Commit the transaction
            tr.commit().join();

            System.out.println("All key-value pairs have been cleared.");
        }

        // Close the database connection
        db.close();

//        FDB fdb = FDB.selectAPIVersion(620);
//        Database db = fdb.open();
//
//        // 读取所有键
//        try (Transaction tr = db.createTransaction()) {
//            // 扫描范围
//            tr.getRange(Tuple.from("key_00000").pack(), Tuple.from("key_00999").pack())
//                    .forEach(kv -> {
//                        System.out.println("Key: " + Tuple.fromBytes(kv.getKey()).get(0));
//                    });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

}
