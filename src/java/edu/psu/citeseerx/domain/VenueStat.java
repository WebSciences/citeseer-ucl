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

public class VenueStat implements Comparable<VenueStat> {

    private String name;
    private float impact;
    private String url;
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public float getImpact() {
        return impact;
    }
    
    public void setImpact(float impact) {
        this.impact = impact;
    }
    
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public int compareTo(VenueStat otherStat) {
        if (this.getImpact() > otherStat.getImpact()) return 1;
        if (this.getImpact() < otherStat.getImpact()) return -1;
        return 0;
    }
    
}
