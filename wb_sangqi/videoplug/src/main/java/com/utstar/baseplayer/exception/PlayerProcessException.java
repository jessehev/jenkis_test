package com.utstar.baseplayer.exception;

/**
 * Created by Richard
 */
public class PlayerProcessException extends Exception {
    public PlayerProcessException() {
    }

    public PlayerProcessException(String detailMessage) {
        super(detailMessage);
    }

    public PlayerProcessException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public PlayerProcessException(Throwable throwable) {
        super(throwable);
    }
}
