package com.varun.wg.exception;

public class WaitGroupPanicException extends RuntimeException {

    public WaitGroupPanicException() {
        super("negative WaitGroup counter");
    }
}
