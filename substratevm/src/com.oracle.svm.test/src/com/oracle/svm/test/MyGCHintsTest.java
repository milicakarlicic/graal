package com.oracle.svm.test;

import com.oracle.svm.core.genscavenge.GCHints;
import com.oracle.svm.core.genscavenge.GCImpl;
import com.oracle.svm.core.genscavenge.SerialAndEpsilonGCOptions;
import com.oracle.svm.core.genscavenge.SerialGCOptions;
import jdk.graal.compiler.api.directives.GraalDirectives;
import org.junit.Test;

public class MyGCHintsTest {
    record Data(int i) { }

    @Test
    public void testGCHeapPercent() {
        long startTime = System.nanoTime();

        System.out.println(SerialGCOptions.PrintGCSummary.getValue());

        GCHints.printGenerationsInfo("TEST START");

        for (int i = 0; i < 5; i++) {
            int n = 100_000_000;
            Data[] array = new Data[n];

            GCHints.printGenerationsInfo("BEFORE INIT");
            for (int j = 0; j < n; j++) {
                array[j] = new Data(j);
            }
            GCHints.printGenerationsInfo("AFTER INIT");
            System.gc();
            GCHints.printGenerationsInfo("AFTER GC");

            GraalDirectives.blackhole(array);
            GraalDirectives.blackhole(array[0]);
            GraalDirectives.blackhole(array[n - 1]);
        }

        GCHints.printGCSummary(startTime);

//        Assert.assertEquals("MaximumHeapSizePercent = 80", 80, (int) SerialAndEpsilonGCOptions.MaximumHeapSizePercent.getValue());
//        Assert.assertEquals("MaximumYoungGenerationSizePercent = 10", 10, (int) SerialAndEpsilonGCOptions.MaximumYoungGenerationSizePercent.getValue());
//
//        SerialAndEpsilonGCOptions.MaximumHeapSizePercent.update(50);
//        SerialAndEpsilonGCOptions.MaximumYoungGenerationSizePercent.update(80);
//
//        Object[] array2 = new Object[n];
//
//        for (int i = 0; i < n; i++) {
//            array2[i] = i;
//        }
//
//        System.gc();
//
//        //youngGenSize = HeapImpl.getHeapImpl().getYoungGeneration().computeObjectBytes();
//        //System.out.println(youngGenSize);
//
//        Assert.assertEquals("MaximumHeapSizePercent = 50", 50, (int) SerialAndEpsilonGCOptions.MaximumHeapSizePercent.getValue());
//        Assert.assertEquals("MaximumYoungGenerationSizePercent = 20", 20, (int) SerialAndEpsilonGCOptions.MaximumYoungGenerationSizePercent.getValue());
    }

}
