package com.project.razorpay.common.exception;

import lombok.Getter;

@Getter
public class InvalidStateTransitionException extends RuntimeException {

    private final String fromState;
    private final String toEvent;
    private final String errorCode;

    public InvalidStateTransitionException(String fromState, String event) {
        super("Invalid transition from " + fromState + " with event " + event);
        this.fromState = fromState;
        this.toEvent = event;
        this.errorCode = "Invalid Capture Request";
    }
}
