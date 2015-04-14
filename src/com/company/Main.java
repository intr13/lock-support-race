package com.company;

import java.util.Arrays;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.LockSupport;

public class Main {

    public static void main(String[] args) throws Exception {
        for (int i = 0; i < 1000000; i++) {
            System.out.println("-------------------------");
            final CyclicBarrier barrier = new CyclicBarrier(2);
            final Thread t1 = new Thread(() -> {
                System.out.println("t1: start");
                try {
                    barrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
                System.out.println("t1: start park t1 - " + Thread.currentThread().getState());
                LockSupport.park();
                System.out.println("t1: end park t1 - " + Thread.currentThread().getState());
            });
            t1.start();
            Thread t2 = new Thread(() -> {
                System.out.println("t2: start");
                try {
                    barrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
                System.out.println("t2: start unpark t1 - " + t1.getState());
                do {
                    LockSupport.unpark(t1);
                } while (t1.getState() != Thread.State.RUNNABLE);
                for (int j = 0; j < 4 && t1.getState() == Thread.State.WAITING; j++) {
                    System.out.println("t2: end unpark t1 - " + Arrays.toString(t1.getStackTrace()));
                }
            });
            t2.start();
            t1.join();
            t2.join();
        }
    }
}
