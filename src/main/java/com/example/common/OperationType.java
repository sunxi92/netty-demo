package com.example.common;

import com.example.common.auth.AuthOperation;
import com.example.common.auth.AuthOperationResult;
import com.example.common.keepalive.KeepaliveOperation;
import com.example.common.keepalive.KeepaliveOperationResult;
import com.example.common.order.OrderOperation;
import com.example.common.order.OrderOperationResult;

import java.util.function.Predicate;

public enum OperationType {
    AUTH(1, AuthOperation.class, AuthOperationResult.class),
    KEEPALIVE(2, KeepaliveOperation.class, KeepaliveOperationResult.class),
    ORDER(3, OrderOperation.class, OrderOperationResult.class);

    private int opCode;
    private Class<? extends Operation> operationClazz;
    private Class<? extends OperationResult> operationResultClazz;

    OperationType(int opCode, Class<? extends Operation> operationClazz, Class<? extends OperationResult> responseClass) {
        this.opCode = opCode;
        this.operationClazz = operationClazz;
        this.operationResultClazz = responseClass;
    }

    public static OperationType fromOpCode(int type) {
        return getOperationType(requestType -> requestType.opCode == type);
    }

    public static OperationType fromOperation(Operation operation) {
        return getOperationType(requestType -> requestType.operationClazz == operation.getClass());
    }

    private static OperationType getOperationType(Predicate<OperationType> predicate) {
        OperationType[] values = values();
        for (OperationType operationType : values) {
            if (predicate.test(operationType)) {
                return operationType;
            }
        }

        throw new AssertionError("no found type");
    }

    public int getOpCode() {
        return opCode;
    }

    public Class<? extends Operation> getOperationClazz() {
        return operationClazz;
    }

    public Class<? extends OperationResult> getOperationResultClazz() {
        return operationResultClazz;
    }
}
