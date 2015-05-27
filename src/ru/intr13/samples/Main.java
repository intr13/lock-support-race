package ru.intr13.samples;

import java.util.Arrays;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.LockSupport;

public class Main {

    public static void main(String[] args) throws Exception {
        for (int i = 0; i < 1000000; i++) {
            System.out.println("-------------------------");
            final CyclicBarrier barrier = new CyclicBarrier(2);
//            final AtomicBoolean ab1 = new AtomicBoolean(true);
//            final AtomicBoolean ab2 = new AtomicBoolean(true);
            final Thread t1 = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        barrier.await();
                        Thread.sleep(100);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
//                    ab1.set(false);
//                    while(ab1.get() || ab2.get()){
//                    }
                    LockSupport.park();
                    System.out.println("t1: end park t1 - " + Thread.currentThread().getState());
                }
            });
            Thread t2 = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        barrier.await();
                        Thread.sleep(100);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

//                    ab2.set(false);
//                    while(ab1.get() || ab2.get()){
//                    }
                    LockSupport.unpark(t1);
                    System.out.println("t2: start unpark t1 - " + t1.getState());
                    for (int j = 0; j < 4 && t1.getState() == Thread.State.WAITING; j++) {
                        System.out.println("t2: end unpark t1 - " + Arrays.toString(t1.getStackTrace()));
                    }
                }
            });
            t1.start();
            t2.start();
            Thread.sleep(100);
            t1.join();
            t2.join();
        }
    }
}
