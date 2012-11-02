<%@ include file="../shared/IncludeTop2.jsp" %>
<div id="primary_tabs-n-content" class="clearfix"> <!-- Contains header div -->
  <div id="primary_tabs_container">
   <div id="primary_tabs">
    <ul class="clearfix"><li id="current"><a class="page_tabs remove" href="<c:url value="/about/site"/>"><span>About <fmt:message key="app.name"/></span></a></li><li><a class="page_tabs remove" href="<c:url value="/about/myciteseer"/>"><span>About <fmt:message key="app.portal"/></span></a></li><li><a class="page_tabs remove" href="<c:url value="/about/team"/>"><span>The Team</span></a></li><li><a class="page_tabs remove" href="<c:url value="/about/metadata"/>"><span><fmt:message key="app.name"/> Metadata</span></a></li><li><a class="page_tabs remove" href="<c:url value="/about/previous"/>"><span><fmt:message key="app.name"/> Previous Sponsors</span></a></li><li><a class="page_tabs remove" href="<c:url value="/about/bot"/>"><span>About <fmt:message key="app.name"/> Crawler</span></a></li></ul>   <!-- remove extra whitespace by coding in-line lists on one line -->           
   </div> <!-- End primary_tabs -->
  </div> <!-- End primary_tabs_container -->

  <div id="primary_content">
    <div id="main_content" class="clearfix">  
      <div id="right-sidebar"> <!-- Contains left content -->
        <div class="inside"> <!-- to give some room between columns -->
          <div class="content_box">
            <h2>History</h2>
		    <p class="para4 para_book">
              CiteSeer was the first digital library and search engine to provide automated citation
              indexing and citation linking using the method of
              <a href="<c:url value="/viewdoc/summary?doi=10.1.1.17.1607"/>">autonomous citation indexing</a>.
            </p>                            
            <p class="para4 para_book">
              CiteSeer was developed in 1997 at the NEC Research Institute, Princeton, New Jersey,
              by <a href="http://labs.google.com/people/lawrence/">Steve Lawrence</a>,
              <a href="http://clgiles.ist.psu.edu">Lee Giles</a> and
              <a href="http://en.wikipedia.org/wiki/Kurt_Bollacker">Kurt Bollacker</a>.
              The service transitioned to the Pennsylvania State University's College of Information Sciences and Technology
              in 2003.  Since then, the project has been led by Lee Giles with technical
              and administrative direction by <a href="http://www.personal.psu.edu/%7Eigc2">Isaac Councill</a>.
            </p>
            <p class="para4 para_book">
              After serving as a public search engine
              for nearly ten years, CiteSeer, originally intended as a prototype only,
              began to scale beyond the capabilities of its original architecture.
              Since its inception, the original CiteSeer grew to
              index over 750,000 documents and served over 1.5 million requests daily,
              pushing the limits of the system&#39;s capabilities.
              Based on an analysis of problems encountered by the original system and the needs of the research community,
              a new architecture and data model was developed for the &quot;Next Generation CiteSeer,&quot; or CiteSeer<sup>x</sup>,
              in order to continue the CiteSeer legacy into the foreseeable future.
            </p>                           	
          </div><!-- End content_box -->
        </div> <!--End inside -->
	  </div><!-- End right-sidebar -->             
      <p class="para4 parafirstletters para_book"><span class="firstletters"><em class="firstletter">CiteSeer<sup>x</sup></em></span> is a scientific literature digital library and search engine that focuses primarily on the literature in computer and information science. CiteSeer<sup>x</sup> aims to improve the dissemination of scientific literature and to provide improvements in functionality, usability, availability, cost, comprehensiveness, efficiency, and timeliness in the access of scientific and scholarly knowledge.
      </p>
      <p class="para4 para_book">Rather than creating just another digital library, CiteSeer<sup>x</sup> attempts to provide resources such as algorithms, data, metadata, services, techniques, and software that can be used to promote other digital libraries. CiteSeer<sup>x</sup> has developed new methods and algorithms to index PostScript and PDF research articles on the Web. Citeseer<sup>x</sup> provides the following features.
      </p>                 
      <h2 class="topic_heading">Features</h2> 
      <p class="para4 para_book"> Place your mouse over the orange arrows <img class="icon remove" src="<c:url value="/icons/arrwdwnsm.gif"/>" alt="Details"/> to view the details for each CiteSeer feature.
      </p>                 
	  <ul class="formating">
        <li class="padded"><a href="<c:url value="/viewdoc/summary?doi=10.1.1.17.1607"/>">Autonomous Citation Indexing (ACI)</a> <a class="tooltip" tabindex="1"><img class="icon remove" src="<c:url value="/icons/arrwdwnsm.gif"/>" alt="Details" /><span>CiteSeer uses ACI to automatically create a citation index that can be used for literature search and evaluation. Compared to traditional citation indices, ACI provides improvements in cost, availability, comprehensiveness, efficiency, and timeliness.</span></a></li>
        <li class="padded">Citation statistics <a class="tooltip" tabindex="2"><img class="icon remove" src="<c:url value="/icons/arrwdwnsm.gif"/>" alt="Details"  /><span>CiteSeer computes citation statistics and related documents for all articles cited in the database, not just the indexed articles.</span></a></li>  
        <li class="padded">Reference linking <a class="tooltip" tabindex="2"><img class="icon remove" src="<c:url value="/icons/arrwdwnsm.gif"/>" alt="Details"  /><span>As with many online publishers, CiteSeer allows browsing the database using citation links.  However, CiteSeer performs this automatically.</span></a></li>
        <li class="padded">Citation context <a class="tooltip" tabindex="2"><img class="icon remove" src="<c:url value="/icons/arrwdwnsm.gif"/>" alt="Details" /><span>CiteSeer can show the context of citations to a given paper, allowing a researcher to quickly and easily see what other researchers have to say about an article of interest.</span></a></li>
        <li class="padded">Awareness and tracking <a class="tooltip" tabindex="2"><img class="icon remove" src="<c:url value="/icons/arrwdwnsm.gif"/>" alt="Details" /><span>CiteSeer provides automatic notification of new citations to given papers, and new papers matching a user profile.</span></a></li>
        <li class="padded">Related documents <a class="tooltip" tabindex="2"><img class="icon remove" src="<c:url value="/icons/arrwdwnsm.gif"/>" alt="Details" /><span>CiteSeer locates related documents using citation and word based measures and displays an active and continuously updated bibliography for each document.</span></a></li>
        <li class="padded">Full-text indexing <a class="tooltip" tabindex="2"><img class="icon remove" src="<c:url value="/icons/arrwdwnsm.gif"/>" alt="Details" /><span>CiteSeer indexes the full-text of the entire articles and citations. Full boolean, phrase and proximity search is supported.</span></a></li>
        <li class="padded">Query-sensitive summaries <a class="tooltip" tabindex="2"><img class="icon remove" src="<c:url value="/icons/arrwdwnsm.gif"/>" alt="Details" /><span>CiteSeer provides the context of how query terms are used in articles instead of a generic summary, improving the efficiency of search.</span></a></li>
        <li class="padded">Up-to-date <a class="tooltip" tabindex="2"><img class="icon remove" src="<c:url value="/icons/arrwdwnsm.gif"/>" alt="Details" /><span>CiteSeer is regularly updated based on user submissions and regular crawls.</span></a></li>
        <li class="padded">Powerful search <a class="tooltip" tabindex="2"><img class="icon remove" src="<c:url value="/icons/arrwdwnsm.gif"/>" alt="Details" /><span>CiteSeer uses fielded search to all complex queries over content, and allows the use of author initials to provide more flexible name search.</span></a></li>
        <li class="padded">Harvesting of articles <a class="tooltip" tabindex="2"><img class="icon remove" src="<c:url value="/icons/arrwdwnsm.gif"/>" alt="Details" /><span>CiteSeer automatically harvests research papers from the Web.</span></a></li>
        <li class="padded">Metadata of articles <a class="tooltip" tabindex="2"><img class="icon remove" src="<c:url value="/icons/arrwdwnsm.gif"/>" alt="Details" /><span>CiteSeer automatically extracts and provides metadata from all indexed articles.</span></a></li>
        <li class="padded">Personal Content Portal <a class="tooltip" tabindex="2"><img class="icon remove" src="<c:url value="/icons/arrwdwnsm.gif"/>" alt="Details" /><span> Personal collections, RSS-like notifications, social bookmarking, social network facilities.  Personalized search settings. Institutional data tracking possible. Transparent document submission system.</span></a></li>                              
      </ul> 
    </div> <!-- End main content -->            
  </div> <!-- End primary_content -->
</div> <!-- End primary_tabs-n-content -->

<script type="text/javascript">
<!--
if (window != top) 
top.location.href = location.href;
function sf(){}
// -->
</script>
<%@ include file="../../shared/IncludeBottom.jsp" %>
