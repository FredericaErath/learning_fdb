import com.apple.foundationdb.Database;
import com.apple.foundationdb.FDB;
import com.apple.foundationdb.Transaction;
import com.apple.foundationdb.tuple.Tuple;

import java.sql.Time;

import static java.lang.Thread.sleep;

/**
 * @version 1.0
 * @description understand transaction conflict
 * @date 2024/9/30 0:01
 */

public class TransactionConflict {
    public static void main(String[] args) throws InterruptedException {
        BasicFDBOps.clearAll();
        FDB fdb = FDB.selectAPIVersion(620);
        Database db = fdb.open();

        // Store arbitrary 2 key-value pairs
        try (Transaction tr = db.createTransaction()) {
            tr.set(Tuple.from("K1").pack(), Tuple.from("Value1").pack());
            tr.set(Tuple.from("K2").pack(), Tuple.from("Value2").pack());
            tr.commit().join(); // Commit the transaction
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Start transaction T1 to read K1 and update K2
        Thread transactionT1 = new Thread(() -> {
            try (Transaction t1 = db.createTransaction()) {
                // Read K1
                byte[] k1Value = t1.get(Tuple.from("K1").pack()).join();
                System.out.println("T1 Read K1: " + Tuple.fromBytes(k1Value).get(0));

                // Update K2
                t1.set(Tuple.from("K2").pack(), Tuple.from("UpdatedValue2").pack());

                // Commit T1
                t1.commit().join();
                System.out.println("T1 committed.");
            } catch (Exception e) {
                System.out.println("T1 aborted: " + e.getMessage());
            }
        });

        // Start transaction T2 to read K2 and update K1
        Thread transactionT2 = new Thread(() -> {
            try (Transaction t2 = db.createTransaction()) {
                // Read K2
                byte[] k2Value = t2.get(Tuple.from("K2").pack()).join();
                System.out.println("T2 Read K2: " + Tuple.fromBytes(k2Value).get(0));

                // Update K1
                t2.set(Tuple.from("K1").pack(), Tuple.from("UpdatedValue1").pack());

                // Commit T2
                t2.commit().join();
                System.out.println("T2 committed.");
            } catch (Exception e) {
                System.out.println("T2 aborted: " + e.getMessage());
            }
        });

        // Start both transactions
        transactionT1.start();
        transactionT2.start();



        try {
            // Wait for both transactions to finish
            transactionT2.join();
            transactionT1.join();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
