package ru.intr13.samples;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;

public class Main {

    public static void main(String[] args) throws Exception {
        Compiler.disable();
        for (int i = 0; i < 1000000; i++) {
            System.out.println("------------------------- " + i);
            final AtomicBoolean flag = new AtomicBoolean(true);
            final AtomicBoolean flag1 = new AtomicBoolean(true);
            final AtomicBoolean flag2 = new AtomicBoolean(true);
            final Thread t1 = new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("t1: start");
                    LockSupport.park();
                    System.out.println("t1: unpark");
                    while (flag.get()) {
                        System.out.println("t1: spurious wakeup");
                        System.exit(0);
                        LockSupport.park();
                    }
                    flag2.set(false);
//                    while (flag1.get()) {
//                    }
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
                    LockSupport.park();
                    System.out.println("t2: unpark");
                    while (flag.get()) {
                        System.out.println("t2: spurious wakeup");
                        System.exit(0);
                        LockSupport.park();
                    }
                    flag1.set(false);
//                    while (flag2.get()) {
//                    }
                    System.out.println("t2: send unpark t1: " + t1.getState() + ", " + flag2);
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
            while (t1.getState() != Thread.State.WAITING || t2.getState() != Thread.State.WAITING) {
            }
            System.out.println("unpark");
            flag.set(false);
            LockSupport.unpark(t2);
            System.out.println("unpark t2");
            LockSupport.unpark(t1);
            System.out.println("unpark t1");
            System.out.println("join");
            t1.join();
            t2.join();
            System.out.println("end");
        }
    }
}
