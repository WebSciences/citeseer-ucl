<%@ include file="../shared/IncludeTop2.jsp" %>
 <div id="primary_tabs-n-content" class="clearfix"> <!-- Contains header div -->
  <div id="primary_tabs_container">
   <div id="primary_tabs">
    <ul class="clearfix"><li><a class="page_tabs remove" href="<c:url value="/about/site"/>"><span>About <fmt:message key="app.name"/></span></a></li><li><a class="page_tabs remove" href="<c:url value="/about/myciteseer"/>"><span>About <fmt:message key="app.portal"/></span></a></li><li><a class="page_tabs remove" href="<c:url value="/about/team"/>"><span>The Team</span></a></li><li id="current"><a class="page_tabs remove" href="<c:url value="/about/metadata"/>"><span><fmt:message key="app.name"/> Metadata</span></a></li><li><a class="page_tabs remove" href="<c:url value="/about/previous"/>"><span><fmt:message key="app.name"/> Previous Sponsors</span></a></li><li><a class="page_tabs remove" href="<c:url value="/about/bot"/>"><span>About <fmt:message key="app.name"/> Crawler</span></a></li></ul>   <!-- remove extra whitespace by coding in-line lists on one line -->           
   </div> <!-- End primary_tabs -->
  </div> <!-- End primary_tabs_container -->

  <div id="primary_content">
   <div id="main_content" class="clearfix">  
   
    <div id="right-sidebar"> <!-- Contains left content -->
        <div class="inside"> <!-- to give some room between columns -->
        </div> <!--End inside -->
    </div><!-- End right-sidebar -->             
        
    <p class="para4 parafirstletters para_book"><span class="firstletters"><em class="firstletter">CiteSeer<sup>x</sup></em></span> 
    is compliant with the <a href="http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm" title="OAI-PMH">Open Archives Initiative Protocol for Metadata Harvesting</a>, which is an standard proposed by <a href="http://www.openarchives.org/" title="The Open Archive Initiative"> The Open Archive Initiative</a> in order to facilitate content dissemination. 
    </p>

    <p class="para4 para_book">
    To browse or download records  programmatically from CiteSeer<sup>X</sup> OAI collection you may use our harvest url: 
    </p>
    <p class="char_bold" style="text-align:center">
      http://citeseerx.ist.psu.edu/citeseerx/oai2
    </p>                 
    <p class="para4 para_book">
    You may also browse the archive from a human interface via the <a href="http://re.cs.uct.ac.za/" title="OAI Repository Explorer">OAI Repository Explorer</a>, using the CiteSeerX archive identifier or by directly entering our harvest url. 
    </p>
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