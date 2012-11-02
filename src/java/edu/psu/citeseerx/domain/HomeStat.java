/**
 * 
 */
package edu.psu.citeseerx.domain;

/**
 * Data object container for home statistics 
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev: 810 $ $Date: 2008-12-02 14:05:57 -0500 (Tue, 02 Dec 2008) $
 */
public class HomeStat {
	
	private long documents;
		
	/**
	 * @return number of search able documents
	 */
	public long getDocuments() {
		return documents;
	} //- getDocuments
	
	/**
	 * @param documents number of search able documents
	 */
	public void setDocuments(long documents) {
		this.documents = documents;
	} //-setDocuments
	
	private long citations;
	
	/**
	 * @return number of search able citations
	 */
	public long getCitations() {
		return citations;
	} //- getCitations
	
	/**
	 * @param citations number of search able citations
	 */
	public void setCitations(long citations) {
		this.citations = citations;
	} //- setCitations
	
} //- Class HomeStat
