/*
 * Copyright 2007 Penn State University
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.psu.citeseerx.utility.persistentqueue;

import java.util.concurrent.*;
import java.util.*;
import javax.sql.DataSource;
import java.sql.*;

/**
 * Extension of the BlockingQueue object with provisions for storing job
 * state in a DataSource backend.
 *
 * @author Isaac Councill
 * @version $Rev: 662 $ $Date: 2008-07-27 09:48:44 -0400 (Sun, 27 Jul 2008) $
 */
public class PersistentQueue<E extends PersistentJob>
implements BlockingQueue<E> {

    protected BlockingQueue<E> queue;
    
    public BlockingQueue<E> getQueue() {
        return queue;
    }
    
    
    protected DataSource dataSource;

    public DataSource getDataSource() {
        return dataSource;
    }
    
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    
    protected PersistentQueuePopulator<E> populator;
    
    public PersistentQueuePopulator<E> getPopulator() {
        return populator;
    }
    
    public void setPopulator(PersistentQueuePopulator<E> populator) {
        this.populator = populator;
    }
    
    
    protected synchronized void repopulateJobs() throws SQLException {
        populator.populate(queue, dataSource);
    }
    
    
    private long populateDelay = 5000;
    
    class Repopulator extends Thread {
        public Repopulator() {
            this.setDaemon(true);
        }
        public void run() {
            while (true) {
                synchronized(this) {
                    if (queue.isEmpty()) {
                        try {
                            repopulateJobs();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
                try {
                    sleep(populateDelay);
                } catch (InterruptedException e) { /* ignore */ }
            }
        }
    }
    

    /**
     * Starts job population from the persistent backend.  This
     * will load old jobs that got submitted when the queue was stopped
     * or that were not processed during the last execution cycle.
     */
    public void start() {
        Repopulator repopulator = new Repopulator();
        repopulator.start();
    }
    
    
    protected boolean submitJob(E job) throws IllegalStateException {
        try {
            job.submit(dataSource);
        } catch (SQLException e) {
            throw new IllegalStateException(e.getMessage());
        }
        return true;
        
    }  //- submitJob
    
    
    protected boolean checkoutJob(E job) throws IllegalStateException {
        try {
            job.checkout(dataSource);
            if (queue.isEmpty()) {
                repopulateJobs();
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e.getMessage());
        }
        return true;
        
    }  //- checkoutJob

    
    //=============================================================
    // Subclasses
    //=============================================================

    
    class PersistentArrayQueue extends PersistentQueue<E> {
        public PersistentArrayQueue(int capacity) {
            queue = new ArrayBlockingQueue<E>(capacity);
        }
        public PersistentArrayQueue(int capacity, boolean fair) {
            queue = new ArrayBlockingQueue<E>(capacity, fair);
        }
        public PersistentArrayQueue(int capacity, boolean fair,
                Collection<? extends E> c) {
            queue = new ArrayBlockingQueue<E>(capacity, fair, c);
        }
        
    }  //- class PersistentArrayQueue
    
    
    class PersistentLinkedQueue extends PersistentQueue<E> {
        public PersistentLinkedQueue() {
            queue = new LinkedBlockingQueue<E>();
        }
        public PersistentLinkedQueue(Collection<? extends E> c) {
            queue = new LinkedBlockingQueue<E>(c);
        }
        public PersistentLinkedQueue(int capacity) {
            queue = new LinkedBlockingQueue<E>(capacity);
        }
        
    }  //- class PersistentLinkedQueue
    
    
    class PersistentPriorityQueue extends PersistentQueue<E> {
        public PersistentPriorityQueue() {
            queue = new PriorityBlockingQueue<E>();
        }
        public PersistentPriorityQueue(Collection <? extends E> c) {
            queue = new PriorityBlockingQueue<E>(c);
        }
        public PersistentPriorityQueue(int initialCapacity) {
            queue = new PriorityBlockingQueue<E>(initialCapacity);
        }
        public PersistentPriorityQueue(int initialCapacity,
                Comparator<? super E> comparator) {
            queue = new PriorityBlockingQueue<E>(initialCapacity, comparator);
        }
        
    }  //- class PersistentPriorityQueue
    
    
    class PersistentSynchronousQueue extends PersistentQueue<E> {
        public PersistentSynchronousQueue() {
            queue = new SynchronousQueue<E>();
        }
        public PersistentSynchronousQueue(boolean fair) {
            queue = new SynchronousQueue<E>(fair);
        }
        
    }  //- class PersistentSynchronousQueue
    

    //=============================================================
    // BlockingQueue interface
    //=============================================================

    
    public boolean add(E job) throws IllegalStateException {
        System.out.println("Adding job");
        return submitJob(job);
    }
    
    
    public int drainTo(Collection<? super E> c) throws IllegalStateException {
        int nElts = queue.drainTo(c);
        try {
            for (Iterator<? super E> it = c.iterator(); it.hasNext(); ) {
                PersistentJob job = (PersistentJob)it.next();
                job.checkout(dataSource);
            }
            if (queue.isEmpty()) {
                repopulateJobs();
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e.getMessage());
        }
        return nElts;
        
    }  //- drainTo
    
    
    public int drainTo(Collection<? super E> c, int maxElements) {
        int nElts = queue.drainTo(c, maxElements);
        try {
            for (Iterator<? super E> it = c.iterator(); it.hasNext(); ) {
                PersistentJob job = (PersistentJob)it.next();
                job.checkout(dataSource);
            }
            if (queue.isEmpty()) {
                repopulateJobs();
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e.getMessage());
        }        
        return nElts;
        
    }  //- drainTo
    
    
    public boolean offer(E job) {
        try {
            submitJob(job);
        } catch (IllegalStateException e) {
            return false;
        }
        return true;
        
    }  //- offer
    
    
    public boolean offer(E job, long timeout, TimeUnit unit)
    throws InterruptedException {
        return offer(job);
    }
    
    
    public E poll(long timeout, TimeUnit unit)
    throws InterruptedException, IllegalStateException {
        E job = queue.poll(timeout, unit);
        if (job != null) {
            checkoutJob(job);
        }
        return job;
        
    }  //- poll
    
    
    public void put(E job) throws InterruptedException, IllegalStateException {
        submitJob(job);
    }
    
    
    public int remainingCapacity() {
        return Integer.MAX_VALUE;
    }
    
    
    public E take() throws InterruptedException, IllegalStateException {
        E job = queue.take();
        checkoutJob(job);
        return job;
        
    }  //- take
    
    
    //=============================================================
    // Queue interface
    //=============================================================

    
    public E element() throws NoSuchElementException {
        return queue.element();
    }
    
    public E peek() {
        return queue.peek();
    }
    
    public E poll() throws IllegalStateException {
        E job = queue.poll();
        if (job != null) {
            checkoutJob(job);
        }
        return job;
    }
    
    public E remove() throws NoSuchElementException, IllegalStateException {
        E job = queue.remove();
        checkoutJob(job);
        return job;
    }

    
    //=============================================================
    // Collection interface
    //=============================================================

    
    public boolean addAll(Collection<? extends E> c)
    throws IllegalStateException {
        for (Iterator<? extends E> it = c.iterator(); it.hasNext(); ) {
            add(it.next());
        }
        return false;
        
    }  //- addAll
    
    
    public void clear() {
        queue.clear();
    }
    
    public boolean contains(Object obj) {
        return queue.contains(obj);
    }
    
    public boolean containsAll(Collection<?> c) {
        return queue.containsAll(c);
    }
    
    public boolean equals(Object obj) {
        PersistentQueue oQueue = null;
        if (obj instanceof PersistentQueue) {
            oQueue = (PersistentQueue)obj;
        } else {
            return false;
        }
        if ((obj.getClass() == this.getClass()) &&
                (oQueue.getQueue().equals(queue))) {
            return true;
        } else {
            return false;
        }
        
    }  //- equals
    
    
    public int hashCode() {
        return queue.hashCode() + 23;
    }
    
    public boolean isEmpty() {
        return queue.isEmpty();
    }
    
    public Iterator<E> iterator() {
        return queue.iterator();
    }
    
    public boolean remove(Object obj) {
        return queue.remove(obj);
    }
    
    public boolean removeAll(Collection<?> c) throws IllegalStateException {
        boolean changed = queue.removeAll(c);
        if (queue.isEmpty()) {
            try {
                repopulateJobs();
            } catch (SQLException e) {
                throw new IllegalStateException(e.getMessage());
            }
        }
        return changed;
        
    }  //- removeAll
    
    
    public boolean retainAll(Collection<?> c) {
        boolean changed = queue.retainAll(c);
        if (queue.isEmpty()) {
            try {
                repopulateJobs();
            } catch (SQLException e) {
                throw new IllegalStateException(e.getMessage());
            }
        }
        return changed;
        
    }  //- removeAll
    
    
    public int size() {
        return queue.size();
    }
    
    public Object[] toArray() {
        return queue.toArray();
    }
    
    public <T> T[] toArray(T[] a) {
        return queue.toArray(a);
    }
    
}  //- class PersistentQueue



