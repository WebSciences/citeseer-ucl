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
package edu.psu.citeseerx.myciteseer.web;

import edu.psu.citeseerx.myciteseer.domain.MCSConfiguration;
import edu.psu.citeseerx.myciteseer.domain.logic.*;

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

import de.nava.informa.core.ChannelIF;
import de.nava.informa.core.ItemIF;
import de.nava.informa.core.ParseException;
import de.nava.informa.impl.basic.ChannelBuilder;
import de.nava.informa.impl.basic.Item;
import de.nava.informa.parsers.FeedParser;
import de.nava.informa.utils.ItemComparator;


public class AccountHomeController implements Controller {
    
    private MyCiteSeerFacade myciteseer;

    public void setMyCiteSeer(MyCiteSeerFacade myciteseer) {
        this.myciteseer = myciteseer;
    }
    
    
    private String newsUrl;
    
    public void setNewsUrl(String newsUrl) {
        this.newsUrl = newsUrl;
    }
    
    
    private int maxNewsItems = 5;
    
    public void setMaxNewsItems(int maxNewsItems) {
        this.maxNewsItems = maxNewsItems;
    }
    
    
    public ModelAndView handleRequest(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        Map<String, Object> model = new HashMap<String, Object>();
        boolean peopleSearchEnabled = false;
        try {
        	MCSConfiguration config = myciteseer.getConfiguration();
        	peopleSearchEnabled = config.getPeopleSearchEnabled();
        }catch (SQLException e) {
			peopleSearchEnabled = false;
		}
        model.put("peoplesearchenabled", peopleSearchEnabled);
        if (newsUrl != null) {
            try {
                model.put("newsItems", readNews(newsUrl));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new ModelAndView("index", model);
        
    }  //- handleRequest
    
    
    protected List<Item> readNews(String url)
    throws IOException, ParseException {
        
        ChannelIF channel = FeedParser.parse(new ChannelBuilder(), url);

        Set<ItemIF> items = channel.getItems();
        ItemIF[] list = new ItemIF[items.size()];
        list = items.toArray(list);
        java.util.Arrays.sort(list, new ItemComparator(true));

        List<Item> itemsCol = new ArrayList<Item>();
        for (ItemIF item : list) {
            Item it = (Item)item;
            itemsCol.add(it);
        }

        if (itemsCol.size() > maxNewsItems) {
            return itemsCol.subList(0, maxNewsItems);
        } else {
            return itemsCol;
        }
        
    }  //- readNews
    
}  //- class AccountHomeController
