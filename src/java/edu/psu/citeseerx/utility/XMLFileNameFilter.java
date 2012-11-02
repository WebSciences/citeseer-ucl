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

import java.io.FileFilter;
import java.io.File;

/**
 * Basic FileFilter for returning only XML files from a file listing.
 *
 * @author Isaac Councill
 * @version $Rev: 662 $ $Date: 2008-07-27 09:48:44 -0400 (Sun, 27 Jul 2008) $
 */
public class XMLFileNameFilter implements FileFilter {
    
    public boolean accept(File pathname) {
        String name = pathname.getName();
        name = name.toLowerCase();
        if (pathname.getName().endsWith(".xml")) {
            return true;
        } else {
            return false;
        }
        
    }  //- accept

}  //- class XMLFileNameFilter