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
package edu.psu.citeseerx.citematch;

import edu.psu.citeseerx.domain.Document;

import org.json.JSONException;

/**
 * Generic specification for citation clustering implementations.
 *
 * @author Isaac Councill
 * @version $Rev: 666 $ $Date: 2008-07-27 13:57:23 -0400 (Sun, 27 Jul 2008) $
 */
public interface CitationClusterer {

    public void clusterDocument(Document doc) throws JSONException;

    public void reclusterDocument(Document doc) throws JSONException;

    public void reclusterDocument(Document newDoc, Document oldDoc)
    throws JSONException;

}
