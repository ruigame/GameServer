package com.game.timer;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author: liguorui
 * @Date: 2020/11/24 下午3:25
 */
public class RefreshDelayQueue<E extends Delayed> extends AbstractQueue<E> implements BlockingQueue<E> {

    private transient final ReentrantLock lock = new ReentrantLock();
    private final PriorityQueue<E> q = new PriorityQueue<>();

    private Thread leader = null;

    private final Condition available = lock.newCondition();

    public RefreshDelayQueue() {

    }

    public RefreshDelayQueue(Collection<? extends E> c) {
        this.addAll(c);
    }

    public boolean add(E e) {
        return offer(e);
    }

    public boolean offer(E e) {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            q.offer(e);
            if (q.peek() == e) {
                leader = null;
                available.signal();
            }
            return true;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        if (c == null) throw new NullPointerException();
        if (c == this) throw new IllegalArgumentException();
        boolean modified = false;
        boolean notice = false;
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            for (E e : c) {
                if (q.offer(e)) {
                    modified = true;
                    if (q.peek() == e) {
                        notice = true;
                    }
                }
            }
            if (notice) {
                leader = null;
                available.signal();
            }
        } finally {
            lock.unlock();
        }
        return modified;
    }

    public void put(E e) {
        offer(e);
    }

    public boolean offer(E e, long timeout, TimeUnit unit) {
        return offer(e);
    }

    public E poll() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            E first = q.peek();
            if (first == null || first.getDelay(TimeUnit.NANOSECONDS) > 0) {
                return null;
            } else {
                return q.poll();
            }
        } finally {
            lock.unlock();
        }
    }

    public E take() throws InterruptedException {
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            for (;;) {
                E first = q.peek();
                if (first == null) {
                    available.await();
                } else {
                    long delay = first.getDelay(TimeUnit.NANOSECONDS);
                    if (delay <= 0) {
                        return q.poll();
                    } else if (leader != null) {
                        available.await();
                    } else {
                        Thread thisThread = Thread.currentThread();
                        leader = thisThread;
                        try {
                            available.awaitNanos(delay);
                        } finally {
                            if (leader == thisThread) {
                                leader = null;
                            }
                        }
                    }
                }
            }
        } finally {
            if (leader == null && q.peek() != null) {
                available.signal();
            }
            lock.unlock();
        }
    }

    public E poll(long timeout, TimeUnit unit) throws InterruptedException {
        long nanos = unit.toNanos(timeout);
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            for (;;) {
                E first = q.peek();
                if (first == null) {
                    if (nanos <= 0) {
                        return null;
                    } else {
                        nanos = available.awaitNanos(nanos);
                    }
                } else {
                    long delay = first.getDelay(TimeUnit.NANOSECONDS);
                    if (delay <= 0) {
                        return q.poll();
                    }
                    if (nanos <= 0) {
                        return null;
                    }
                    if (nanos < delay || leader != null) {
                        nanos = available.awaitNanos(nanos);
                    } else {
                        Thread thisThread = Thread.currentThread();
                        leader = thisThread;
                        try {
                            long timeLeft = available.awaitNanos(delay);
                            nanos -= delay - timeLeft;
                        } finally {
                            if (leader == thisThread) {
                                leader = null;
                            }
                        }
                    }
                }
            }
        } finally {
            if (leader == null && q.peek() != null) {
                available.signal();
            }
            lock.unlock();
        }
    }

    public E peek() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return q.peek();
        } finally {
            lock.unlock();
        }
    }

    public int size() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return q.size();
        } finally {
            lock.unlock();
        }
    }

    public int drainTo(Collection<? super E> c) {
        if (c == null) throw new NullPointerException();
        if (c == this) throw new IllegalArgumentException();
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            int n = 0;
            for (;;) {
                E first = q.peek();
                if (first == null || first.getDelay(TimeUnit.NANOSECONDS) > 0) {
                    break;
                }
                c.add(q.poll());
                ++n;
            }
            return n;
        } finally {
            lock.unlock();
        }
    }

    public int drainTo(Collection<? super  E> c, int maxElements) {
        if (c == null) throw new NullPointerException();
        if (c == this) throw new IllegalArgumentException();
        if (maxElements <= 0) return 0;
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            int n = 0;
            while(n < maxElements) {
                E first = q.peek();
                if (first == null || first.getDelay(TimeUnit.NANOSECONDS) > 0) {
                    break;
                }
                c.add(q.poll());
                ++n;
            }
            return n;
        } finally {
            lock.unlock();
        }
    }

    public void clear() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            q.clear();
        } finally {
            lock.unlock();
        }
    }

    public int remainingCapacity() {
        return Integer.MAX_VALUE;
    }

    public Object[] toArray() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return q.toArray();
        } finally {
            lock.unlock();
        }
    }

    public <T> T[] toArray(T[] a) {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return q.toArray(a);
        } finally {
            lock.unlock();
        }
    }

    public boolean remove(Object o) {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return q.remove(o);
        } finally {
            lock.unlock();
        }
    }

    public Iterator<E> iterator() {
        return new Itr(toArray());
    }

    public void refresh() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            List<E> list = new ArrayList<>(this);
            this.clear();
            this.addAll(list);
            available.signal();
        } finally {
            lock.unlock();
        }
    }

    private class Itr implements Iterator<E> {
        final Object[] array;
        int cursor;
        int lastRet;

        Itr(Object[] array) {
            lastRet = -1;
            this.array = array;
        }

        public boolean hasNext() {
            return cursor < array.length;
        }

        @SuppressWarnings("unchecked")
        public E next() {
            if (cursor >= array.length) throw new NoSuchElementException();
            lastRet = cursor;
            return (E) array[cursor++];
        }

        public void remove() {
            if (lastRet < 0) throw new IllegalStateException();
            Object x = array[lastRet];
            lastRet = -1;
            lock.lock();
            try {
                for (@SuppressWarnings("rawtypes")
                     Iterator it = q.iterator(); it.hasNext(); ) {
                    if (it.next() == x) {
                        it.remove();
                        return;
                    }
                }
            } finally {
                lock.unlock();
            }
        }
    }
}
