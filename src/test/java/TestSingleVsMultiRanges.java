import java.util.concurrent.ExecutionException;

/**
 * @version 1.0
 * @description single vs multi ranges
 * @date 2024/9/28 17:41
 */

public class TestSingleVsMultiRanges {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        SingleVsMultiRanges singleVsMultiRanges = new SingleVsMultiRanges();
        singleVsMultiRanges.singleGetRange("WANT_ALL");
        singleVsMultiRanges.multipleGetRangesParallel("WANT_ALL");
        singleVsMultiRanges.singleGetRange("EXACT");
        singleVsMultiRanges.multipleGetRangesParallel("EXACT");
        singleVsMultiRanges.singleGetRange("ITERATOR");
        singleVsMultiRanges.multipleGetRangesParallel("ITERATOR");
        singleVsMultiRanges.singleGetRange("SMALL");
        singleVsMultiRanges.multipleGetRangesParallel("SMALL");
        singleVsMultiRanges.singleGetRange("MEDIUM");
        singleVsMultiRanges.multipleGetRangesParallel("MEDIUM");
        singleVsMultiRanges.singleGetRange("LARGE");
        singleVsMultiRanges.multipleGetRangesParallel("LARGE");
        singleVsMultiRanges.singleGetRange("SERIAL");
        singleVsMultiRanges.multipleGetRangesParallel("SERIAL");
    }
}
