package com.oracle.svm.core.genscavenge;

import org.graalvm.word.UnsignedWord;

public class GCHints {

    public static long countYoungGenerationBytes() {
        HeapImpl heap = HeapImpl.getHeapImpl();
        YoungGeneration youngGeneration = heap.getYoungGeneration();
        return youngGeneration.computeObjectBytes().rawValue();
    }

    public static long countOldGenerationBytes() {
        HeapImpl heap = HeapImpl.getHeapImpl();
        OldGeneration oldGeneration = heap.getOldGeneration();
        return oldGeneration.computeObjectBytes().rawValue();
    }

    public static void printGenerationsInfo(String message) {
        System.out.println();
        System.out.println("==================================================");
        System.out.println(message);
        System.out.println("Young generation size (in bytes): " + countYoungGenerationBytes());
        System.out.println("Old generation size (in bytes): " + countOldGenerationBytes());
        System.out.println("==================================================");
    }

    private static double convertToMb(long bytes) {
        return bytes / 1_000_000.0;
    }

    private static double convertToSeconds(long nanos) {
        return nanos / 1_000_000_000.0;
    }

    public static void printGCSummary() {
        GCAccounting accounting = GCImpl.getAccounting();

        System.out.println();
        System.out.println("==================================================");
        System.out.println("GC summary");

        System.out.printf("  Collected chunk bytes: %.2fM\n", convertToMb(accounting.getTotalCollectedChunkBytes().rawValue()));
        System.out.printf("  Collected object bytes: %.2fM\n", convertToMb(accounting.getTotalCollectedObjectBytes().rawValue()));

        System.out.printf("  Allocated chunk bytes: %.2fM\n", convertToMb(accounting.getTotalAllocatedChunkBytes().rawValue()));
        System.out.printf("  Allocated object bytes: %.2fM\n", convertToMb(accounting.getAllocatedObjectBytes().rawValue()));

        System.out.println("  Incremental GC count: " + accounting.getIncrementalCollectionCount());
        double incrementalGCTime = convertToSeconds(accounting.getIncrementalCollectionTotalNanos());
        System.out.printf("  Incremental GC time: %.3fs\n", incrementalGCTime);

        System.out.println("  Complete GC count: " + accounting.getCompleteCollectionCount());
        double completeGCTime = convertToSeconds(accounting.getCompleteCollectionTotalNanos());
        System.out.printf("  Complete GC time: %.3fs\n", completeGCTime);

        double gcTime = incrementalGCTime + completeGCTime;
        System.out.printf("  GC time: %.3fs\n", gcTime);
        double mutatorNanos = convertToSeconds(GCImpl.getGCImpl().getTimers().mutator.getMeasuredNanos());
        double runTime = gcTime + mutatorNanos;
        System.out.printf("  Run time: %.3fs\n", runTime);

        System.out.printf("  GC load: %d%% \n", (int) ((gcTime / runTime) * 100));

        System.out.println("==================================================");
    }

}
