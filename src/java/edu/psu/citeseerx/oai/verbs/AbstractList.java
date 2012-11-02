/**
 * 
 */
package edu.psu.citeseerx.oai.verbs;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import edu.psu.citeseerx.dao2.logic.CiteClusterDAO;
import edu.psu.citeseerx.domain.Author;
import edu.psu.citeseerx.domain.Document;
import edu.psu.citeseerx.domain.DocumentFileInfo;
import edu.psu.citeseerx.domain.Keyword;
import edu.psu.citeseerx.utility.DateUtils;

/**
 * Base class for all the Verbs which returns information about
 * 1 or more records in the repository
 * @author Pradeep Teregowda
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev: 814 $ $Date: 2008-12-11 14:52:55 -0500 (Thu, 11 Dec 2008) $
 */
public abstract class AbstractList extends AbstractVerb {

	protected final static int MAX_RECORDS = 1000;
	protected final static String RIGHTS = "Metadata may be used without " +
		"restrictions as long as the oai identifier remains attached to it.";
	
	private Integer maxReturnRecords = MAX_RECORDS;
	
	/**
	 * @return Maximum number of records to return when not returning
	 * the complete list
	 */
	public Integer getMaxReturnRecords() {
		return maxReturnRecords;
	} //- getMaxReturnRecords

	/**
	 * @param maxReturnRecords Maximum number of records to return when not 
	 * returning the complete list
	 */
	public void setMaxReturnRecords(Integer maxReturnRecords) {
		this.maxReturnRecords = maxReturnRecords;
	} //- setMaxReturnRecords
	
	private String contributor;
	
	/**
	 * @return The repository contributor
	 */
	public String getContributor() {
		return contributor;
	} //- getContributor

	/**
	 * @param contributor The repository contributor
	 */
	public void setContributor(String contributor) {
		this.contributor = contributor;
	} //- setContributor

	private  String viewDocURL;
	
	/**
	 * @return URL to view the document info within the library
	 */
	public String getViewDocURL() {
		return viewDocURL;
	} //- getViewDocUrl

	/**
	 * @param viewDocUrl URL to view the document info within the library
	 */
	public void setViewDocURL(String viewDocUrl) {
		this.viewDocURL = viewDocUrl;
	} //- getViewDocUrl

	private CiteClusterDAO citedao;
    
    public void setCiteClusterDAO(CiteClusterDAO citedao) {
        this.citedao = citedao;
    } //- setCiteClusterDAO
	
	/**
	 * Generates the resumption token
	 * @param doi
	 * @param totalCount
	 * @param itemsSent
	 * @return
	 */
	protected String generateResumptionTokenElement(String doi, 
			Integer totalCount, Integer itemsSent, String metadataPrefix) {
		StringBuffer buf = new StringBuffer();
		buf.append("<resumptionToken>");
		if (doi != null && totalCount != null && itemsSent != null) {
			buf.append(doi);
			buf.append("-");
			buf.append(totalCount);
			buf.append("-");
			buf.append(itemsSent);
			buf.append("-");
			buf.append(metadataPrefix);
		}
		buf.append("</resumptionToken>");
		return buf.toString();
		
	} //- generateResumptionTokenElement
	
	/**
	 * @param date
	 * @return True if the given date comply with the repository granularity.
	 * If Granularity is not valid an error is aggregated to the errors list
	 */
	protected boolean isValidGranularity(String date) {
		boolean isValid = true;
		if ("YYYY-MM-DDThh:mm:ssZ".equals(getGranularity()) &&
				date.length() != 20) {
			isValid = false;
		}else if ("YYYY-MM-DD".equals(getGranularity()) &&
				date.length() != 10) {
			isValid = false;
		}
		if (!isValid) {
			addError(new OAIError("Invalid granularity", 
					OAIError.BAD_ARGUMENT_ERROR));
		}
		return isValid;
	} //- isValidGranularity
	
	/**
	 * Validate the date in both short and long granularity. Valid Granularity
	 * is checked in @see {@link AbstractVerb#isValidGranularity(String)}
	 * If date is not valid an error is aggregated to the errors list
	 * @param date
	 * @return true is the date is a valid OAI-PMH date. 
	 */
	protected boolean isValidDate(String date) {
		String shortPattern = "([0-9]{4,4})-{1,1}([0-9]{2,2})-{1,1}([0-9]{2,2})";
		String longPatter = shortPattern +
			"T{1,1}([0-9]{2,2}):{1,1}([0-9]{2,2}):{1,1}([0-9]{2,2})Z{1,1}";

		boolean valid = true;
		try {
			Pattern pattern = Pattern.compile("^"+shortPattern+"$");
			Matcher matcher = pattern.matcher(date);
			if (!matcher.find()) {
				valid = false;
			}else{
				int year = Integer.parseInt(matcher.group(1));
				int month = Integer.parseInt(matcher.group(2));
				int day = Integer.parseInt(matcher.group(3));
				int daysInMonth;
				
				if ( ((((year%4)==0) && ((year%100)!=0)) || ((year%400) == 0))
						&& (2 == month)) {
					daysInMonth = 29;
				}else{
					switch (month) {
					case 4: case 6: case 9: case 11:
						daysInMonth = 30;
						break;
					case 2:
						daysInMonth = 28;
					default:
						daysInMonth = 31;
						break;
					}
				}
				if ( (month <= 0) || (month > 12) || (day <= 0) || 
						(day > daysInMonth) || (year <= 0) ) {
					valid = false;
				}
			}
			pattern = Pattern.compile("^"+longPatter+"$");
			matcher = pattern.matcher(date);
			if (matcher.find()) {
				int hour = Integer.parseInt(matcher.group(1));
				int minutes = Integer.parseInt(matcher.group(2));
				int seconds = Integer.parseInt(matcher.group(3));
				if (hour < 0 || hour > 23 || minutes < 0 || minutes > 59 ||
						seconds < 0 || seconds > 59) {
					valid = false;
				}
			}else if ((date.length() > 10)) {
				valid = false;
			}
			Date repoMinDate = 
				DateUtils.parseDateToUTCDate(getEarliestDatestamp());
			Date otherDate = 
				DateUtils.parseDateToUTCDate(date);
			if (otherDate.before(repoMinDate)) {
				valid = false;
			}
		}catch (Exception e) {
			valid = false;
			addError(new OAIError("Invalid date", 
					OAIError.BAD_ARGUMENT_ERROR));
		}
		return valid;
	} //- isValidDate
	
	/**
	 * Determines if a date is OAI-PMH valid and if it complies with the
	 * repository granularity.
	 * Calling this method is the same that making the and of @see {@link AbstractVerb#isValidGranularity(String)}
	 * and  @see {@link AbstractVerb#isValidDate(String)} 
	 * @param date
	 * @return
	 */
	protected boolean validateDate(String date) {
		return (isValidGranularity(date) && isValidDate(date));
	} //- validateDate
	
	/**
	 * Validate if the repository provides metadata in the given format. If not
	 * false is returned and an error is created.
	 * @param metadataPrefix
	 * @return
	 */
	protected boolean isValidMetaDataPrefix(String metadataPrefix) {
		boolean isValid = false;
		
		if (metadataPrefix != null) {
			for (String prefix : metadataFormats) {
				if (metadataPrefix.equals(prefix)) {
					isValid = true;
					break;
				}
			}
		}
		if (!isValid) {
			addError(new OAIError(metadataPrefix + " not supported", 
					OAIError.CANNOT_DISEMINATE_FORMAT_ERROR));
		}
		return isValid;
	} //- isValidMetaDataPrefix
	
	/**
	 * Validates the resumption token. If the resumption token is not valid 
	 * errors are added to the error list
	 * @param resumptionToken
	 * @return
	 */
	protected boolean isValidResumptionToken(String resumptionToken) {
		boolean isValid = true;
		
		String doiRegExpr = 
			"^[0-9]{1,}\\.[0-9]{1,}\\.[0-9]{1,}\\.[0-9]{1,}\\.[0-9]{1,4}$";

		String[] tokens = resumptionToken.split("-");
		
		if (tokens.length != 4) {
			isValid = false;
		}else{
			Pattern pattern = Pattern.compile(doiRegExpr);
			Matcher matcher = pattern.matcher(tokens[0]);
			if (!matcher.find()) {
				isValid = false;
			}
			try {
				Integer.parseInt(tokens[1]);
				Integer.parseInt(tokens[2]);
			}catch (NumberFormatException e) {
				isValid = false;
			}
			if (!isValidMetaDataPrefix(tokens[3])) {
				isValid = false;
			}
		}
		
		if (!isValid) {
			addError(new OAIError("The value of the resumptionToken argument " +  
					"is invalid", OAIError.BAD_RESUMPTION_TOKEN_ERROR));
		}
		return isValid;
	} //- isValidResumptionToken
	
	/**
	 * Returns a date in UTC format that conforms to the repository
	 * granularity.
	 * @param date
	 * @return
	 */
	protected String buildDatestamp(Date date) {
		String stamp = null;
		if ("YYYY-MM-DDThh:mm:ssZ".equals(getGranularity())) {
			stamp = DateUtils.formatDateTimeISO8601UTC(date);
		}else if ("YYYY-MM-DD".equals(getGranularity())) {
			stamp = DateUtils.formatDateISO8601UTC(date);
		}
		return stamp;
	} //- buildDatestamp
	
	/**
	 * Returns the repository identifier for the given item
	 * @param doi
	 * @return
	 */
	protected String buildIdentifier(String doi) {
		return OAI_SCHEMA + getDelimiter() + getRepositoryIdentifier() + 
			getDelimiter() + doi;
	} //- buildIdentifier
	
	/**
	 * Builds an OAI-PMH record header
	 * @param doi	Internal identifier
	 * @param date	date associated with the record
	 * @return the OAI-PMH header.
	 */
	protected String buildRecordHeader(String doi, Date date) {
		StringBuffer buf = new StringBuffer();
		buf.append("<header>");
		buf.append("<identifier>");
		buf.append(buildIdentifier(doi));
		buf.append("</identifier>");
		buf.append("<datestamp>");
		buf.append(buildDatestamp(date));
		buf.append("</datestamp>");
		buf.append("</header>");
		return buf.toString();
	} //- buildRecordHeader
	
	/**
	 * Builds an AOI-PMH record based on doc
	 * @param doc
	 * @param relatedDocuments
	 * @return
	 */
	protected String buildDocumentRecord(Document doc) {
		StringBuffer buf = new StringBuffer();
		buf.append("<record>");
		buf.append(buildRecordHeader(doc.getDatum(Document.DOI_KEY), 
				doc.getVersionTime()));
		buf.append("<metadata>");
		buf.append("<oai_dc:dc ");
		buf.append("xmlns:oai_dc=");
		buf.append("\"http://www.openarchives.org/OAI/2.0/oai_dc/\" ");
		buf.append("xmlns:dc=");
		buf.append("\"http://purl.org/dc/elements/1.1/\" ");
		buf.append("xmlns:xsi=");
		buf.append("\"http://www.w3.org/2001/XMLSchema-instance\" ");
		buf.append("xsi:schemaLocation=");
		buf.append("\"http://www.openarchives.org/OAI/2.0/oai_dc/ " );
		buf.append("http://www.openarchives.org/OAI/2.0/oai_dc.xsd\"");
		buf.append(">");
		buf.append("<dc:title>");
		buf.append(doc.getDatum(Document.TITLE_KEY, true));
		buf.append("</dc:title>");
		for (Author creator : doc.getAuthors()) {
			/*
			 * All the authors are creators of the document
			 */
			buf.append("<dc:creator>");
			buf.append(creator.getDatum(Author.NAME_KEY, true));
			buf.append("</dc:creator>");
		}
		for (Keyword keyword : doc.getKeywords()) {
			buf.append("<dc:subject>");
			buf.append(keyword.getDatum(Keyword.KEYWORD_KEY, true));
			buf.append("</dc:subject>");
		}
		buf.append("<dc:description>");
		buf.append(doc.getDatum(Document.ABSTRACT_KEY, true));
		buf.append("</dc:description>");
		/*
		 * this is the library since it contributes with metadata about
		 * the resource
		 */
		buf.append("<dc:contributor>");
		buf.append(getContributor());
		buf.append("</dc:contributor>");
		buf.append("<dc:publisher>");
		String publisher = doc.getDatum(Document.PUBLISHER_KEY, true);
		publisher = (null == publisher) ? "unknown" : publisher;
		buf.append(publisher);
		buf.append("</dc:publisher>");
		
		/*
		 * A point of time associated with the resource:
		 * Version Time (last time it was modified, eg. metadata was corrected).
		 * Crawl date. 
		 */
		buf.append("<dc:date>");
		buf.append(buildDatestamp(doc.getVersionTime()));
		buf.append("</dc:date>");
		
		DocumentFileInfo dFileInfo = doc.getFileInfo();
		String cDate = null;
		try {
			SimpleDateFormat sDF = new SimpleDateFormat("MMM dd, yyyy");
			Date crawlDate = sDF.parse(dFileInfo.getDatum(
					DocumentFileInfo.CRAWL_DATE_KEY));
			cDate = buildDatestamp(crawlDate); 
		}catch (ParseException e) {
			e.printStackTrace();
			cDate = null;
		}
		if (cDate != null) {
			buf.append("<dc:date>");
			buf.append(cDate);
			buf.append("</dc:date>");
		}
		List<String> urls = dFileInfo.getUrls();
		if ((urls != null) && (urls.size() > 0)) {
			buf.append("<dc:format>");
			if (dFileInfo.getUrls().get(0).contains(".pdf")) {
				buf.append("application/pdf");
			}else if (dFileInfo.getUrls().get(0).contains(".ps")) {
				buf.append("application/postscript");
			}else if (dFileInfo.getUrls().get(0).contains(".gz")) {
				buf.append("application/zip");
			}
			buf.append("</dc:format>");
		}
		
		buf.append("<dc:type>");
		buf.append("text");
		buf.append("</dc:type>");
		
		/*
		 * In the header we already put the OAI identifier as requested by the 
		 * OAI-PMH specification. Here we put the URL to the record within the
		 * library. So, someone using the harvested data can retrieve the document
		 * either using the OAI-PMH identifier from the header or by going to the
		 * library using this URL
		 */
		buf.append("<dc:identifier>");
		try {
			buf.append(URLEncoder.encode(getViewDocURL(), "UTF-8"));
		}catch (UnsupportedEncodingException e) {
			// This should happen.
			System.err.println(e.getMessage());
			buf.append(getViewDocURL());
		}
		buf.append(doc.getDatum(Document.DOI_KEY));
		buf.append("</dc:identifier>");
		buf.append("<dc:source>");
		buf.append(dFileInfo.getUrls().get(0));
		buf.append("</dc:source>");
		buf.append("<dc:language>");
		buf.append("en");
		buf.append("</dc:language>");
		
		// get papers (only paper id) cited by this one.
		List<String> papers = 
			citedao.getCitedDocumentIDs(doc.getDatum(Document.DOI_KEY));
		for (String paper : papers) {
			buf.append("<dc:relation>");
			buf.append(buildIdentifier(paper));
			buf.append("</dc:relation>");
		}
		
		// get papers (only paper id) which cites this one.
		papers = 
			citedao.getCitingDocumentIDs(doc.getDatum(Document.DOI_KEY));
		for (String paper : papers) {
			buf.append("<dc:relation>");
			buf.append(buildIdentifier(paper));
			buf.append("</dc:relation>");
		}
		buf.append("<dc:rights>");
		buf.append(RIGHTS);
		buf.append("</dc:rights>");
		buf.append("</oai_dc:dc>");
		buf.append("</metadata>");
		buf.append("</record>");
		
		return buf.toString();
	} //- buildDocumentRecord
	
	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.oai.verbs.AbstractVerb#doProcess(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected abstract String doProcess(HttpServletRequest request)
			throws OAIVerbException;

} //- Class AbstractList
