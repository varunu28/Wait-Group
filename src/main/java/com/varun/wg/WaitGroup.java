package com.varun.wg;

/**
 * WaitGroup waits for a collection of processes to finish. This interface is a 1:1 mapping to Golang's
 * interface. @see <a href="https://pkg.go.dev/sync#WaitGroup">Interface description</a>.
 */
public interface WaitGroup {

    /**
     * Adds a delta to the WaitGroup counter. If the counter becomes negative after adding the delta,
     * it ends up throwing an exception. All the calls to add should happen before calling the {@link #wgWait() Wait}
     * method.
     *
     * @param delta an integer value of number of processes to be added to the WaitGroup.
     */
    void add(int delta);

    /**
     * Decrements WaitGroup counter by one.
     */
    void done();

    /**
     * Blocks until WaitGroup counter reaches zero.
     */
    void wgWait();
}
