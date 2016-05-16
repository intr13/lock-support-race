package ru.intr13.samples;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;

public class Main1 {

    public static void main(String[] args) throws Exception {
        Compiler.disable();
        for (int i = 0; i < 1000000; i++) {
            System.out.println("------------------------- " + i);
            final AtomicBoolean flag = new AtomicBoolean(true);
            final Thread t1 = new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("t1: start");
                    while (flag.get()) {
                    }
                    System.out.println("t1: unpark");
                    System.out.println("t1: park t1");
                    LockSupport.park();
                    System.out.println("t1: unpark t1 - " + Thread.currentThread().getState());
                    System.out.println("t1: end");
                }
            }, "t1: " + i);
            final Thread t2 = new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("t2: start");
                    while (flag.get()) {
                    }
                    System.out.println("t2: unpark");
                    System.out.println("t2: send unpark t1: " + t1.getState());
                    LockSupport.unpark(t1);
                    for (int j = 0; j < 4 && t1.getState() == Thread.State.WAITING; j++) {
                        System.out.println("t2: check un park t1 - " + Arrays.toString(t1.getStackTrace()));
                    }
                    System.out.println("t2: end");
                }
            }, "t2: " + i);
            System.out.println("start");
            t1.start();
            t2.start();
            while (t1.getState() != Thread.State.RUNNABLE || t2.getState() != Thread.State.RUNNABLE) {
            }
            System.out.println("unpark");
            flag.set(false);
            System.out.println("join");
            t1.join();
            t2.join();
            System.out.println("end");
        }
    }
}
