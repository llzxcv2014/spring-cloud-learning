package com.lin.learn.eureka;

import java.util.concurrent.CountDownLatch;

public class ReorderTest {

    static int a, b, x, y;

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 1000000; i++) {
            CountDownLatch latch = new CountDownLatch(2);
            new Thread(() -> {
               a = 1;
               x = b;
               latch.countDown();
            }).start();
            new Thread(() -> {
               b = 1;
               y = a;
               latch.countDown();
            });
            latch.await();
            if ((x + y) == 0) {
                System.out.println("第" + i + "次出现指令重排序");
            }
            x = y = a = b;
        }
    }
}
