package com.oracle.svm.test;

import com.oracle.svm.core.genscavenge.GCHints;
import com.oracle.svm.core.genscavenge.GCImpl;
import com.oracle.svm.core.genscavenge.GCRequest;
import com.oracle.svm.core.genscavenge.HeapChunk;
import com.oracle.svm.core.genscavenge.HeapImpl;
import com.oracle.svm.core.genscavenge.SerialAndEpsilonGCOptions;
import com.oracle.svm.core.genscavenge.SerialGCOptions;
import com.oracle.svm.core.heap.GC;
import com.oracle.svm.core.heap.GCCause;
import com.oracle.svm.core.heap.Heap;
import jdk.graal.compiler.api.directives.GraalDirectives;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Optional;

public class MyGCHintsTest {
    record Data(int i) { }

    //@Test
    public void testGC() {
        int n = 10_000_000;
        Data[] array1 = new Data[n];

        var h = HeapChunk.getEnclosingHeapChunk(array1);
        System.out.println(h.getSpace());

        GCHints.printGenerationsInfo("BEFORE GC");
        System.gc();
        GCHints.printGenerationsInfo("AFTER GC");

        Assert.assertEquals(10, (int) SerialAndEpsilonGCOptions.MaximumYoungGenerationSizePercent.getValue());

        //SerialAndEpsilonGCOptions.MaximumYoungGenerationSizePercent.update(80);

        //Assert.assertEquals(80, (int) SerialAndEpsilonGCOptions.MaximumYoungGenerationSizePercent.getValue());

        long prevYoungGenSize = 0;
        for (int j = 0; j < 5; j++) {
            for (int i = 0; i < n; i++) {
                array1[i] = new Data(i);
                long youngGenSize = GCHints.countYoungGenerationBytes();
                if (youngGenSize != 0 && youngGenSize != prevYoungGenSize) {
                    GCHints.printGenerationsInfo("LOOP STATE");
                    prevYoungGenSize = youngGenSize;
                }
            }
        }

        System.out.println(array1[0]);
        System.out.println(array1[n - 1]);

        GCHints.printGenerationsInfo("BEFORE GC");
        System.gc();
        GCHints.printGenerationsInfo("AFTER GC");

        GCHints.printGCSummary();
    }

    @Test
    public void testRequestGC() throws InterruptedException {
        Assert.assertEquals(0, GCRequest.getRequestCount());
        Assert.assertFalse(GCRequest.isRequestInProgress());
        GC gcInstance = Heap.getHeap().getGC();

        Data[] array = new Data[1_000_000_000];
        GraalDirectives.blackhole(array);

        int i = 0;

        while (true) {
            gcInstance.requestStartHint();

            array[i] = new Data(i);
            i++;

            int requestCount = GCRequest.getRequestCount();
            if (requestCount % 3 == 0) {
                gcInstance.collectionHint(false);
                System.out.println("Incremental collection hint given");
                gcInstance.collect(GCCause.HintedGC);
            } else if (requestCount % 10 == 0) {
                gcInstance.collectionHint(true);
                System.out.println("Complete collection hint given");
                gcInstance.collect(GCCause.HintedGC);
            }
            gcInstance.requestEndHint();

            GCHints.printGCSummary();

            System.out.println(array[i - 1]);

            Thread.sleep(1000);
        }

    }

   // @Test
    public void testGcOptions() {
        LinkedList<Data> l = new LinkedList<>();
        GraalDirectives.blackhole(l);

        long count = 0;

//        Assert.assertEquals(80, (int) SerialAndEpsilonGCOptions.MaximumHeapSizePercent.getValue());
//        SerialAndEpsilonGCOptions.MaximumHeapSizePercent.update(5);
//        Assert.assertEquals(5, (int) SerialAndEpsilonGCOptions.MaximumHeapSizePercent.getValue());

        for (int i = 0; i < 1_000_000_000; i++) {
            Data tmp = new Data(i);
            l.add(tmp);

            System.out.println(count++);
        }
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
