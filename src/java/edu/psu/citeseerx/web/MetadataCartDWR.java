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
package edu.psu.citeseerx.web;

import uk.ltd.getahead.dwr.WebContext;
import uk.ltd.getahead.dwr.WebContextFactory;

import edu.psu.citeseerx.dao2.logic.CiteClusterDAO;
import edu.psu.citeseerx.domain.ThinDoc;

import javax.servlet.http.*;
import java.util.HashMap;

public class MetadataCartDWR {

    public final static String CART_ATTR = "metacart";
    
    private CiteClusterDAO citedao;

    public void setCiteClusterDAO(CiteClusterDAO citedao) {
        this.citedao = citedao;
    }
    
    
    private int maxItems = 50;
    
    public void setMaxItems(int maxItems) {
        this.maxItems = maxItems;
    }
    
    
    public static final String MAX_REACHED = "Max cart size exceeded: ";
    public static final String ADD = "Added to cart";
    public static final String ERR = "Error in cart manipulation";
    public static final String PRIOR = "Already added";
    public static final String REM = "Removed from cart";
    public static final String NREM = "Not in cart";
    
    public String addToCart(Long clusterid) {
    
        try {            
            WebContext ctx = WebContextFactory.get();
            HttpServletRequest request = ctx.getHttpServletRequest();
            HttpSession session = request.getSession(true);
            
            Object obj = session.getAttribute(CART_ATTR);
            HashMap<Long, ThinDoc> set;
            if (obj != null) {
                set = (HashMap<Long, ThinDoc>)obj; 
            } else {
                set = new HashMap<Long, ThinDoc>();
                session.setAttribute(CART_ATTR, set);
            }
            if (set.containsKey(clusterid)) {
                return PRIOR;
            }
            if (set.size() == maxItems) {
                return MAX_REACHED + maxItems;
            }
            ThinDoc doc = citedao.getThinDoc(clusterid);
            set.put(clusterid, doc);

            /* make sure session gets replicated if need be */
            session.setAttribute(CART_ATTR, set);
            
            return ADD;
            
        } catch (Exception e) {
            e.printStackTrace();
            return ERR;
        }
        
    }  //- addToCart
    
    
    public String removeFromCart(Long clusterid) {
        
        try {
            WebContext ctx = WebContextFactory.get();
            HttpServletRequest request = ctx.getHttpServletRequest();
            HttpSession session = request.getSession(true);
            
            Object obj = session.getAttribute(CART_ATTR);
            HashMap<Long, ThinDoc> set;
            if (obj != null) {
                set = (HashMap<Long, ThinDoc>)obj;
                if (set.containsKey(clusterid)) {
                    set.remove(clusterid);
                    return REM;
                }
            }
            return NREM;
            
        } catch (Exception e) {
            e.printStackTrace();
            return ERR;
        }
        
    }  //- removeFromCart
    
    
    public void addToCart(HttpServletRequest request, Long clusterid) {
        HttpSession session = request.getSession(true);
        
        Object obj = session.getAttribute(CART_ATTR);
        HashMap<Long, ThinDoc> set;
        if (obj != null) {
            set = (HashMap)obj; 
        } else {
            set = new HashMap<Long, ThinDoc>();
            session.setAttribute(CART_ATTR, set);
        }
        if (set.containsKey(clusterid)) {
            return;
        }
        ThinDoc doc = citedao.getThinDoc(clusterid);
        set.put(clusterid, doc);

        /* make sure session gets replicated if need be */
        session.setAttribute(CART_ATTR, set);
        
    }  //- addToCart
    
    
    public void removeFromCart(HttpServletRequest request, Long clusterid) {
        HttpSession session = request.getSession(true);
        
        Object obj = session.getAttribute(CART_ATTR);
        HashMap<Long, ThinDoc> set;
        if (obj != null) {
            set = (HashMap)obj;
            if (set.containsKey(clusterid)) {
                set.remove(clusterid);
            }
        }
        
    }  //- removeFromCart
                
}  //- class MetadataCartDWR
