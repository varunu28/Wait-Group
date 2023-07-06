package com.varun.wg;

import com.varun.wg.exception.WaitGroupPanicException;
import com.varun.wg.impl.WaitGroupImpl;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class WaitGroupTest {
    private static void triggerProcesses(final WaitGroup waitGroup, final int numOfThreads, final CountDownLatch condition, final AtomicInteger value) {
        for (int i = 0; i < numOfThreads; i++) {
            new Thread(() -> {
                try {
                    condition.await();
                    value.incrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    try {
                        waitGroup.done();
                    } catch (WaitGroupPanicException | InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }).start();
        }
    }

    @Test
    public void waitGroupLinear_success() throws InterruptedException {
        // Arrange
        WaitGroup waitGroup = new WaitGroupImpl();
        int numOfThreads = 3;
        CountDownLatch condition = new CountDownLatch(1);
        AtomicInteger value = new AtomicInteger(0);

        // Act
        triggerProcesses(waitGroup, numOfThreads, condition, value);
        // add delta & release all the threads for processing
        waitGroup.add(numOfThreads);
        condition.countDown();

        waitGroup.wgWait();

        // Assert
        assertEquals(numOfThreads, value.get());
    }

    @Test
    public void waitGroupPanic_exception() {
        // WaitGroup with 0 processes
        WaitGroup waitGroup = new WaitGroupImpl();

        // Trying to mark a process as done
        assertThrows(WaitGroupPanicException.class, waitGroup::done);
    }

    @Test
    public void waitGroupNonLinear_success() throws InterruptedException {
        // Arrange
        WaitGroup waitGroup = new WaitGroupImpl();
        int numOfThreads = 6;
        CountDownLatch conditionOne = new CountDownLatch(1);
        CountDownLatch conditionTwo = new CountDownLatch(1);
        AtomicInteger value = new AtomicInteger(0);

        // Act
        triggerProcesses(waitGroup, numOfThreads / 2, conditionOne, value);
        triggerProcesses(waitGroup, numOfThreads / 2, conditionTwo, value);
        // add delta & release condition for first half of threads for processing
        waitGroup.add(numOfThreads / 2);
        conditionOne.countDown();
        // add delta & release condition for second half of threads for processing
        waitGroup.add(numOfThreads / 2);
        conditionTwo.countDown();

        waitGroup.wgWait();

        // Assert
        assertEquals(numOfThreads, value.get());
    }

    @Test
    public void waitGroupReuse_success() throws InterruptedException {
        // Arrange
        WaitGroup waitGroup = new WaitGroupImpl();
        int numOfThreads = 3;
        CountDownLatch conditionOne = new CountDownLatch(1);
        CountDownLatch conditionTwo = new CountDownLatch(1);
        AtomicInteger valueOne = new AtomicInteger(0);
        AtomicInteger valueTwo = new AtomicInteger(0);

        // Act
        triggerProcesses(waitGroup, numOfThreads, conditionOne, valueOne);
        triggerProcesses(waitGroup, numOfThreads, conditionTwo, valueTwo);
        // add delta & release first condition
        waitGroup.add(numOfThreads);
        conditionOne.countDown();
        // wait for first set of background processes that update valueOne
        waitGroup.wgWait();

        // add delta & release second condition
        waitGroup.add(numOfThreads);
        conditionTwo.countDown();
        // wait for second set of background processes that update valueTwo
        waitGroup.wgWait();

        // Assert
        assertEquals(numOfThreads, valueOne.get());
        assertEquals(numOfThreads, valueTwo.get());
    }
}