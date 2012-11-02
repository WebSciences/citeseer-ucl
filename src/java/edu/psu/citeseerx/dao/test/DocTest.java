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
package edu.psu.citeseerx.dao.test;

import edu.psu.citeseerx.dao.*;
import edu.psu.citeseerx.dbcp.*;
import edu.psu.citeseerx.domain.*;

import javax.sql.DataSource;
import java.util.*;
import java.io.*;


public class DocTest {

    public CSXDAO csxdao;

    public DocTest() {
        DataSource ds = DBCPFactory.createDataSource(null);
        csxdao = new CSXDAO();
        csxdao.setDataSource(ds);
    }

    public static void main(String[] args) {
        int n = Integer.parseInt(args[0]);
        String path = args[1];
        try {
            DocTest docTest = new DocTest();
            List<String> dois = docTest.csxdao.getDocsToBeIndexed(n);
            for (Iterator<String> it = dois.iterator(); it.hasNext(); ) {
                String doi = it.next();
                boolean getContexts = true;
                boolean getSource = true;
                Document doc =
                    docTest.csxdao.getDocumentFromDB(doi, getContexts, getSource);
                boolean includeSysData = true;
                //System.out.println(doc.toXML(includeSysData));

                BufferedWriter writer = new BufferedWriter(new FileWriter(doi+".xml"));
                writer.write(doc.toXML(includeSysData));
                writer.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
