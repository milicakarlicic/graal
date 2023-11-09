package com.oracle.svm.test;

import com.oracle.svm.core.genscavenge.GCHints;
import com.oracle.svm.core.genscavenge.GCImpl;
import com.oracle.svm.core.genscavenge.SerialAndEpsilonGCOptions;
import com.oracle.svm.core.genscavenge.SerialGCOptions;
import jdk.graal.compiler.api.directives.GraalDirectives;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class MyGCHintsTest {
    record Data(int i) { }

    @Test
    public void testGC() throws InterruptedException {
        int n = 100_000_000;
        Object[] array1 = new Object[n];

        for (int i = 0; i < n; i++) {
            array1[i] = i;
        }

        GCHints.printGenerationsInfo("-------------BEFORE GC--------------");
        System.gc();
        GCHints.printGenerationsInfo("-------------AFTER GC--------------");

        array1 = new Object[n];

        for (int i = 0; i < n; i++) {
            array1[i] = i;
        }

        GCHints.printGenerationsInfo("-------------BEFORE GC--------------");
        System.gc();
        GCHints.printGenerationsInfo("-------------AFTER GC--------------");

        array1 = new Object[n];

        for (int i = 0; i < n; i++) {
            array1[i] = i;
        }

        System.out.println(array1[0]);
        System.out.println(array1[n - 1]);

        GCHints.printGenerationsInfo("-------------BEFORE GC--------------");
        System.gc();
        GCHints.printGenerationsInfo("-------------AFTER GC--------------");

        GCHints.printGCSummary();
    }


    //@Test
    public void testGCHeapPercent() throws InterruptedException {
        long startTime = System.nanoTime();

        GCHints.printGenerationsInfo("TEST START");

        ArrayList<Data> xs = new ArrayList<>();
        GraalDirectives.blackhole(xs);

        for (int i = 0; i < 1_000_000; i++) {
            Data x = new Data(i);
            xs.add(x);

            if (i % 500000 == 0) {
                GCHints.printGenerationsInfo("STEP " + i);
                if (i > 10_000_000 && SerialAndEpsilonGCOptions.MaximumYoungGenerationSizePercent.getValue() < 50) {
                    System.out.println("-------- UPDATING YOUNG GEN SIZE --------");
                    Thread.sleep(2000);
                    SerialAndEpsilonGCOptions.MaximumYoungGenerationSizePercent.update(90);
                }
            }
        }

        System.gc();

        GCHints.printGenerationsInfo("TEST END");
        GCHints.printGCSummary();

//        int n = 100_000_000;
//        Object[] array1 = new Object[n];
//
//        for (int i = 0; i < n; i++) {
//            array1[i] = i;
//        }
//
//        System.gc();
//
//        Assert.assertEquals("MaximumHeapSizePercent = 80", 80, (int) SerialAndEpsilonGCOptions.MaximumHeapSizePercent.getValue());
//        Assert.assertEquals("MaximumYoungGenerationSizePercent = 10", 10, (int) SerialAndEpsilonGCOptions.MaximumYoungGenerationSizePercent.getValue());
//
//        //SerialAndEpsilonGCOptions.MaximumYoungGenerationSizePercent.update(20);
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
//        Assert.assertEquals("MaximumYoungGenerationSizePercent = 10", 10, (int) SerialAndEpsilonGCOptions.MaximumYoungGenerationSizePercent.getValue());
    }

}
