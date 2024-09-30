import com.apple.foundationdb.Database;
import com.apple.foundationdb.FDB;
import com.apple.foundationdb.Transaction;
import com.apple.foundationdb.tuple.Tuple;

/**
 * @version 1.0
 * @description read snapshot
 * @date 2024/9/29 23:48
 */

public class ReadSnapshot {
    public static void main(String[] args) {
        BasicFDBOps.clearAll();
        FDB fdb = FDB.selectAPIVersion(620);
        Database db = fdb.open();

        // Store arbitrary 4 key-value pairs
        try (Transaction tr = db.createTransaction()) {
            tr.set(Tuple.from("K1").pack(), Tuple.from("Value1").pack());
            tr.set(Tuple.from("K2").pack(), Tuple.from("Value2").pack());
            tr.set(Tuple.from("K3").pack(), Tuple.from("Value3").pack());
            tr.set(Tuple.from("K4").pack(), Tuple.from("Value4").pack());
            tr.commit().join(); // Commit
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Start transaction T1 to read K1, K2, K3
        Thread readThread = new Thread(() -> {
            try (Transaction tr1 = db.createTransaction()) {
                // Read K1, K2, K3
                byte[] k1Value = tr1.get(Tuple.from("K1").pack()).join();
                byte[] k2Value = tr1.get(Tuple.from("K2").pack()).join();
                byte[] k3Value = tr1.get(Tuple.from("K3").pack()).join();

                System.out.println("T1 Read K1: " + Tuple.fromBytes(k1Value).get(0));
                System.out.println("T1 Read K2: " + Tuple.fromBytes(k2Value).get(0));
                System.out.println("T1 Read K3: " + Tuple.fromBytes(k3Value).get(0));

                tr1.commit().join();
                System.out.println("T1 committed.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Start another thread for transaction T2 that updates K2 and K4
        Thread updateThread = new Thread(() -> {
            try (Transaction tr2 = db.createTransaction()) {
                tr2.set(Tuple.from("K2").pack(), Tuple.from("NewValue2").pack());
                tr2.set(Tuple.from("K4").pack(), Tuple.from("NewValue4").pack());

                // Commit T2
                tr2.commit().join();
                System.out.println("T2 committed.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        readThread.start();
        updateThread.start();

        try {
            readThread.join();
            updateThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
