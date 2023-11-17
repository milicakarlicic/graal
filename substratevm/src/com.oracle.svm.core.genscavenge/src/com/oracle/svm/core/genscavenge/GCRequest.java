package com.oracle.svm.core.genscavenge;

public class GCRequest {
    private static boolean requestInProgress = false;
    private static int requestCount;

    public static boolean isRequestInProgress() {
        return requestInProgress;
    }

    public static int getRequestCount() {
        return requestCount;
    }

    public static void beginRequest() {
        if (!requestInProgress) {
            requestInProgress = true;
            requestCount++;
        }
    }

    public static void endRequest() {
        requestInProgress = false;
    }

}
