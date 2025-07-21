package com.webmonitor.util;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.LockSupport;

@Slf4j
public final class IntervalLimiter {

  private static final ConcurrentHashMap<Object, AtomicLong> LAST = new ConcurrentHashMap<>();

  private IntervalLimiter() {
  }

  /**
   * 阻塞，直到距离该 key 上一次调用的时间 ≥ intervalMs
   *
   * @param key        任意对象（String、Integer、枚举……都会 toString 做 key）
   * @param intervalMs 最小间隔毫秒数（可随时变）
   */
  public static void awaitNext(Object key, long intervalMs) {
    if (key == null) throw new IllegalArgumentException("key is null");
    if (intervalMs < 0) throw new IllegalArgumentException("intervalMs < 0");

    AtomicLong last = LAST.computeIfAbsent(key.toString(), k -> new AtomicLong(0));
    long intervalNanos = intervalMs * 1_000_000L;

    // 自旋 + park 等待
    while (true) {
      long now = System.nanoTime();
      long prev = last.get();
      long next = prev + intervalNanos;
      if (now >= next) {
        if (last.compareAndSet(prev, now)) return; // 获得通行权
      } else {
        log.debug("{} 阻塞 {} ms", key, (next - now) / 1_000_000);
        LockSupport.parkNanos(next - now);        // 阻塞到下一次
      }
    }
  }


  public static void main(String[] args) throws InterruptedException {
    final String key = "sameKey";
    final long intervalMs = 1000;              // 全局 1000 ms 一次
    final int threads = 10;                   // 10 个线程并发
    final CountDownLatch start = new CountDownLatch(1);
    final CountDownLatch done = new CountDownLatch(threads);
    final AtomicLong lastSuccess = new AtomicLong(0);

    for (int i = 0; i < threads; i++) {
      final int id = i;
      new Thread(() -> {
        try {
          start.await();                      // 一起起跑
          long t0 = System.currentTimeMillis();
          IntervalLimiter.awaitNext(key, intervalMs);
          long t1 = System.currentTimeMillis();
          long gap = t1 - lastSuccess.getAndSet(t1);
          System.out.printf("线程 %d 通过，gap=%d ms%n", id, gap);
        } catch (InterruptedException ignored) {
        } finally {
          done.countDown();
        }
      }).start();
    }

    // 让所有线程几乎同时起跑
    start.countDown();
    done.await();

    System.out.println("全部结束");
  }
}