package com.example.client.handler;


import com.example.common.OperationResult;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RequestToResponse {
    private Map<Long, OperationResultFuture> requestToResponseMap = new ConcurrentHashMap<>();

    public void addRequestToResponseMap(Long streamId, OperationResultFuture operationResultFuture){
        this.requestToResponseMap.put(streamId, operationResultFuture);
    }

    public void setRequestToResponseMap(Long streamId, OperationResult operationResult){
        OperationResultFuture orf = this.requestToResponseMap.get(streamId);
        if(orf != null){
            orf.setSuccess(operationResult);
            this.requestToResponseMap.remove(streamId);
        }
    }

}
