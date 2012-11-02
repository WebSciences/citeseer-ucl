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
package edu.psu.citeseerx.ingestion;

import edu.psu.citeseerx.dao2.RepositoryMap;
import edu.psu.citeseerx.dao2.UnknownRepositoryException;
import edu.psu.citeseerx.utility.*;
import edu.psu.citeseerx.domain.*;
import java.io.*; 

/**
 * This class is used to copy document files into the main CiteSeerX
 * file storage area.
 *
 * @author Isaac Councill
 * @version $Rev: 669 $ $Date: 2008-07-27 15:54:43 -0400 (Sun, 27 Jul 2008) $
 */
public class FileSystemIngester implements FileIngester {

    private RepositoryMap repositoryMap;
    
    public void setRepositoryMap(RepositoryMap repositoryMap) {
        this.repositoryMap = repositoryMap;
    }
    
    
    protected String repositoryID;
    
    public void setRepositoryID(String repositoryID) {
        this.repositoryID = repositoryID;
    }
    
    
    private final static String sep = System.getProperty("file.separator");
    
    /**
     * Copies all file resources for a given Document object from the location
     * specified in fileBase to the repository specified within the Document
     * object.  Files to be copied include all files with the following
     * extensions appended to fileBase: pdf, ps, doc, rtf, txt, body, cite.
     * @param fileBase the path to the files that should be imported, plus
     * the base name (without extension) of the files to copy; e.g., to
     * import files /path/to/dir/1.pdf, /path/to/dir/1.body, and
     * /path/to/dir/1.txt, fileBase should be set to "/path/to/dir/1".
     */
    public void importFileData(Document doc, String fileBase)
    throws IOException, UnknownRepositoryException {
        
        System.out.println("Importing document: "+
                doc.getDatum(Document.DOI_KEY, Document.UNENCODED));

        String doi = doc.getDatum(Document.DOI_KEY, Document.UNENCODED);
        String dir = FileNamingUtils.getDirectoryFromDOI(doi);
        String fullDestDir =
            repositoryMap.getRepositoryPath(repositoryID) + sep + dir;

        String[] extensions = {
                ".pdf", ".ps", ".doc", ".rtf", ".txt", ".body", ".cite"
        };
        
        for (String ext : extensions) {
            String src = fileBase + ext;
            String dest = fullDestDir + sep + doi + ext;
            File srcFile = new File(src);
            if (!srcFile.exists()) {
                src = fileBase + ext.toUpperCase();
                srcFile = new File(src);
                if (!srcFile.exists()) {
                    if (ext.equals(".txt")) {
                        throw new IOException("txt file not found: "+
                                srcFile);
                    }
                    continue;
                }
            }
            File destFile = new File(dest);
            FileUtils.copy(srcFile, destFile);
        }
        
        doc.getFileInfo().setDatum(DocumentFileInfo.REP_ID_KEY,
                repositoryID);
        
        System.out.println("done: "+dir);
            
    }  //- importFileData
    
}  //- class FileSystemIngester
