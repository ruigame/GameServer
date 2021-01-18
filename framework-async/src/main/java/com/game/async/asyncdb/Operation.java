package com.game.async.asyncdb;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author liguorui
 * @date 2018/1/15 01:00
 */
public enum Operation {

    INSERT(AsynDBState.INSERT, new AsynDBState[] {AsynDBState.NOMAL}, (AsynDBState[]) null),

    DELETE(AsynDBState.DELETE, new AsynDBState[] {AsynDBState.NOMAL, AsynDBState.INSERT, AsynDBState.UPDATE},
            new AsynDBState[] {AsynDBState.DELETE}),

    UPDATE(AsynDBState.UPDATE, new AsynDBState[] {AsynDBState.NOMAL},
            new AsynDBState[] {AsynDBState.UPDATE, AsynDBState.INSERT}),
    ;


    public final AsynDBState STATE;
    private final Set<AsynDBState> NEED_CHANGE_OPERATION;
    private final Set<AsynDBState> CAN_OPERATION;

    private Operation(AsynDBState STATE, AsynDBState[] needChange, AsynDBState[] canOperations) {
        this.STATE = STATE;
        Set<AsynDBState> CAN_OPERATION = new HashSet<AsynDBState>();
        Set<AsynDBState> NEED_CHANGE_OPERATION = new HashSet<AsynDBState>();
        if (needChange != null) {
            for (AsynDBState state : needChange) {
                CAN_OPERATION.add(state);
                NEED_CHANGE_OPERATION.add(state);
            }
        }
        if (canOperations != null) {
            for (AsynDBState state : canOperations) {
                CAN_OPERATION.add(state);
            }
        }
        this.CAN_OPERATION = Collections.unmodifiableSet(CAN_OPERATION);
        this.NEED_CHANGE_OPERATION = Collections.unmodifiableSet(NEED_CHANGE_OPERATION);
    }

    public boolean isCanOperationAt(AsynDBState currentState) {
        return this.CAN_OPERATION.contains(currentState);
    }

    public boolean isNeedToChangeAt(AsynDBState currentState) {
        return this.NEED_CHANGE_OPERATION.contains(currentState);
    }
}
