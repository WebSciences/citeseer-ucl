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

import java.io.Serializable;
import java.util.Date;
import edu.psu.citeseerx.utility.DateUtils;

/**
 * Light-weight bean container for document, citation, or cluster metadata.
 *
 * @author Isaac Councill
 * @version $Rev: 668 $ $Date: 2008-07-27 15:25:51 -0400 (Sun, 27 Jul 2008) $
 */
public class ThinDoc implements Serializable {

    private String doi;
    private Long cluster;
    private String authors;
    private String title;
    private String abs;
    private String venue;
    private String ventype;
    private int year = -1;
    private String pages;
    private int vol = -1;
    private int num = -1;
    private String publisher;
    private String tech;
    private int ncites = 0;
    private int selfCites = 0;
    private String snippet;
    private String url;
    private boolean inCollection; 
    private String observations;
    private Date updateTime;
    
    public String getAuthors() {
        return authors;
    }
    public void setAuthors(String authors) {
        this.authors = authors;
    }
    public Long getCluster() {
        return cluster;
    }
    public void setCluster(Long cluster) {
        this.cluster = cluster;
    }
    public String getDoi() {
        return doi;
    }
    public void setDoi(String doi) {
        this.doi = doi;
    }
    public int getNcites() {
        return ncites;
    }
    public void setNcites(int ncites) {
        this.ncites = ncites;
    }
    public int getSelfCites() {
        return selfCites;
    }
    public void setSelfCites(int selfCites) {
        this.selfCites = selfCites;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getVentype() {
        return ventype;
    }
    public void setVentype(String ventype) {
        this.ventype = ventype;
    }
    public String getVenue() {
        return venue;
    }
    public void setVenue(String venue) {
        this.venue = venue;
    }
    public int getYear() {
        return year;
    }
    public void setYear(int year) {
        this.year = year;
    }
    public String getAbstract() {
        return abs;
    }
    public void setAbstract(String abs) {
        this.abs = abs;
    }
    public String getSnippet() {
        return snippet;
    }
    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public boolean getInCollection() {
        return inCollection;
    }
    public void setInCollection(boolean inCollection) {
        this.inCollection = inCollection;
    }
    public String getAbs() {
        return abs;
    }
    public void setAbs(String abs) {
        this.abs = abs;
    }
    public int getNum() {
        return num;
    }
    public void setNum(int num) {
        this.num = num;
    }
    public String getPages() {
        return pages;
    }
    public void setPages(String pages) {
        this.pages = pages;
    }
    public String getPublisher() {
        return publisher;
    }
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
    public String getTech() {
        return tech;
    }
    public void setTech(String tech) {
        this.tech = tech;
    }
    public int getVol() {
        return vol;
    }
    public void setVol(int vol) {
        this.vol = vol;
    }
    public String getObservations() {
        return observations;
    }
    public void setObservations(String observations) {
        this.observations = observations;
    }
    public Date getUpdateTime() {
        return updateTime;
    }
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
    
    public String getRfc822Time() {
        return DateUtils.formatRFC822(updateTime);
    }

    public String getRfc3339Time() {
        return DateUtils.formatRFC3339(updateTime);
    }

    public String toString() {
        
        StringBuffer buf = new StringBuffer();
        
        buf.append("THINDOC\n");
        buf.append("DOI: "+doi+"\n");
        buf.append("CLUST: "+cluster+"\n");
        buf.append("AUTH: "+authors+"\n");
        buf.append("TITL: "+title+"\n");
        buf.append("ABS: "+abs+"\n");
        buf.append("VEN: "+venue+"\n");
        buf.append("VT: "+ventype+"\n");
        buf.append("YEAR: "+year+"\n");
        buf.append("PAGE: "+pages+"\n");
        buf.append("VOL: "+vol+"\n");
        buf.append("NUM: "+num+"\n");
        buf.append("PUBL: "+publisher+"\n");
        buf.append("TECH: "+tech+"\n");
        buf.append("NCITE: "+ncites+"\n");
        buf.append("SELFCITE: "+selfCites+"\n");
        buf.append("SNIP: "+snippet+"\n");
        buf.append("URL: "+url+"\n");
        buf.append("INCOL: "+inCollection+"\n"); 

        return buf.toString();
    }
    
    
}  //- class ThinDoc
