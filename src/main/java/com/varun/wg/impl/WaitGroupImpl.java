package com.varun.wg.impl;

import com.varun.wg.WaitGroup;
import com.varun.wg.exception.WaitGroupPanicException;

import java.util.concurrent.Semaphore;

public class WaitGroupImpl implements WaitGroup {

    private final Semaphore mutex;
    private int counter;

    public WaitGroupImpl() {
        this.counter = 0;
        this.mutex = new Semaphore(1);
    }

    @Override
    public void add(final int delta) throws WaitGroupPanicException, InterruptedException {
        this.mutex.acquire();
        this.counter += delta;
        if (this.counter < 0) {
            throw new WaitGroupPanicException();
        }
        if (this.counter == 0) {
            this.mutex.release();
            synchronized (this) {
                notifyAll();
            }
            return;
        }
        this.mutex.release();
    }

    @Override
    public void done() throws WaitGroupPanicException, InterruptedException {
        add(-1);
    }

    @Override
    public synchronized void wgWait() throws InterruptedException {
        while (this.counter > 0) {
            wait();
        }
    }
}
