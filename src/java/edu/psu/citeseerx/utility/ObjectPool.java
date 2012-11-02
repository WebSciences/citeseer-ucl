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
package edu.psu.citeseerx.utility;

import java.util.*;

/**
 * Generic Object pool implementation.
 *
 * @author Isaac Councill
 * @version $Rev: 662 $ $Date: 2008-07-27 09:48:44 -0400 (Sun, 27 Jul 2008) $
 */
public class ObjectPool<T> {

    private long expirationTime;
    private Hashtable<T,Long> locked = new Hashtable<T,Long>();
    private Hashtable<T,Long> unlocked = new Hashtable<T,Long>();
    
    /**
     * Adds an object to the pool.
     * @param obj
     */
    public synchronized void add(T obj) {
        long now = System.currentTimeMillis();
        unlocked.put(obj, new Long(now));
    }
    
    /**
     * Checks out an object from the pool or null if there is no object
     * to lease.
     * @return
     */
    public synchronized T lease() {
        long now = System.currentTimeMillis();
        T obj = null;
        if (unlocked.size() > 0) {
            Enumeration<T> e = unlocked.keys();
            obj = e.nextElement();
            unlocked.remove(obj);
            locked.put(obj, new Long(now));
        }
        return obj;
        
    }  //- lease
    
    /**
     * Returns a previously leased object to the pool.
     * @param obj
     */
    public synchronized void returnObject(T obj) {
        locked.remove(obj);
        unlocked.put(obj, new Long(System.currentTimeMillis()));
    }

}  //- class ObjectPool
