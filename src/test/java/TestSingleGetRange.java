/**
 * @version 1.0
 * @description single get range
 * @date 2024/9/28 17:14
 */

public class TestSingleGetRange {
    public static void main(String[] args) {
        SingleGetRange singleGetRange = new SingleGetRange();
        singleGetRange.getRangeWithMode("WANT_ALL");
        singleGetRange.getRangeWithMode("EXACT");
        singleGetRange.getRangeWithMode("ITERATOR");
        singleGetRange.getRangeWithMode("SMALL");
        singleGetRange.getRangeWithMode("MEDIUM");
        singleGetRange.getRangeWithMode("LARGE");
        singleGetRange.getRangeWithMode("SERIAL");
        singleGetRange.storeKeyValuePairs();
    }
}
