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
package edu.psu.citeseerx.dao;

import java.util.*;
import edu.psu.citeseerx.utility.*;

public class RepositoryMap {

    private final ConfigurationKey accessKey = new AccessKey();
    
    private final HashMap<String,String> repositoryMap =
        new HashMap<String,String>();
    
    
    public RepositoryMap() {
        
        try {
            ConfigurationManager cm = new ConfigurationManager();
            Object repositories = cm.getProperty("repositories.repository.id",
                    accessKey);
            if (repositories instanceof Collection) {
                int size = ((Collection)repositories).size();
                for (int i=0; i<size; i++) {
                    String id = cm.getString("repositories.repository("+i+").id",
                            accessKey);
                    String path =
                        cm.getString("repositories.repository("+i+").path",
                                accessKey);
                    repositoryMap.put(id, path);
                }
            } else {
                String id = cm.getString("repositories.repository.id", accessKey);
                String path = cm.getString("repositories.repository.path",
                        accessKey);
                repositoryMap.put(id, path);
            }
        } catch (Exception e) {
            System.err.println("Warning: no config found for repository map.");
        }
        
    }  //- RepositoryMap
    
    
    public String buildFilePath(String repID, String relPath)
    throws UnknownRepositoryException {
        String repPath = repositoryMap.get(repID);
        if (repPath == null) {
            System.out.println("REP ID: " + repID);
            throw new UnknownRepositoryException(repID);
        }
        return repPath + System.getProperty("file.separator")+relPath;
        
    }  //- buildFilePath
    
    
    public String getRepositoryPath(String repID)
    throws UnknownRepositoryException {
        String repPath = repositoryMap.get(repID);
        if (repPath == null) {
            throw new UnknownRepositoryException(repID);
        }
        return repPath;
        
    }  //- getRepositoryPath
    
    
}  //- class RepositoryMap


class AccessKey extends ConfigurationKey {}
