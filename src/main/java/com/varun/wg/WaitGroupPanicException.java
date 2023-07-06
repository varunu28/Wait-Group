package com.varun.wg;

public class WaitGroupPanicException extends RuntimeException {

    public WaitGroupPanicException() {
        super("negative WaitGroup counter");
    }
}
