package com.oracle.svm.core.genscavenge;

public class GCRequest {
    private boolean requestInProgress = false;
    private int requestCount;

    public void beginRequest() {
        if (!requestInProgress) {
            requestInProgress = true;
            requestCount++;
        }
    }

    public void endRequest() {
        requestInProgress = false;
    }

}
