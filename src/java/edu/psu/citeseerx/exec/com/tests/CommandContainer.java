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
package edu.psu.citeseerx.exec.com.tests;

import java.util.*;
import java.io.*;

public class CommandContainer implements Serializable {

    public static final String requestKey = "request";
    public static final String requestVal = "this is a request";
    public static final String responseKey = "response";
    public static final String responseVal = "this is a response";
    
    public Hashtable request = new Hashtable();
    public Hashtable response = new Hashtable();
    
    ArrayList crap = new ArrayList(10000);
    public CommandContainer() {
        String junk = ";asjdflkjkljlksjdfgpoihasgpiouhqew;lkjas;dflkjasdgiouh";
        for (int i=0; i<10000; i++) {
            crap.add(i, junk);
        }
    }
    
}
