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
package edu.psu.citeseerx.domain;

import java.util.ArrayList;

/**
 * Bean container used for calculating author citation statistics.
 *
 * @author Isaac Councill
 * @version $Rev: 668 $ $Date: 2008-07-27 15:25:51 -0400 (Sun, 27 Jul 2008) $
 */
public class AuthorStatContainer {

    private String[] authors;
    private int ncites;
    
    public String[] getAuthors() {
        return authors;
    }
    
    public int getNcites() {
        return ncites;
    }
    
    private long cluster;

    public void setCluster(long cluster) {
        this.cluster = cluster;
    }
    
    public long getCluster() {
        return cluster;
    }
    
    
    public AuthorStatContainer(String authorList, int ncites) {
        buildAuthorList(authorList);
        this.ncites = ncites;
    }
    
    
    private void buildAuthorList(String authorList) {
        if (authorList == null) {
            authors = new String[0];
            return;
        }
        String[] names = authorList.split(",");
        ArrayList<String> validNames = new ArrayList<String>();
        for (int i=0; i<names.length; i++) {
            String name = normalizeAuthorName(names[i].trim());
            if (name != null) {
                validNames.add(name);
            }
        }
        authors = new String[validNames.size()];
        for (int i=0; i<validNames.size(); i++) {
            authors[i] = validNames.get(i);
        }
    }
    
    
    private String normalizeAuthorName(String name) {
        if (name == null) return null;

        name = name.replaceAll("  *", " ");
        StringBuilder builder = new StringBuilder();
        String[] tokens = name.split(" ");
        if (tokens.length>1) {
            char firstLetter = tokens[0].charAt(0);
            builder.append(firstLetter);
            builder.append(". ");
            if (tokens[tokens.length-1].length()<=1) {
                return null;
            } else {
                builder.append(tokens[tokens.length-1]);
                return builder.toString();
            }
        } else {
            return null;
        }
        
    }  //- normalizeAuthorName
    
}
