<%@ include file="../shared/IncludeTop2.jsp" %>
 <div id="primary_tabs-n-content" class="clearfix"> <!-- Contains header div -->
  <div id="primary_tabs_container">
   <div id="primary_tabs">
    <ul class="clearfix"><li><a class="page_tabs remove" href="<c:url value="/stats/articles"/>"><span>Most Cited Articles</span></a></li><li><a class="page_tabs remove" href="<c:url value="/stats/citations"/>"><span>Most Cited Citations</span></a></li><li><a class="page_tabs remove" href="<c:url value="/stats/authors"/>"><span>Most Cited Authors</span></a></li><li id="current"><a class="page_tabs remove" href="<c:url value="/stats/venues"/>"><span>Venue Impact Ratings</span></a></li></ul>   <!-- remove extra whitespace by coding in-line lists on one line -->           
   </div> <!-- End primary_tabs -->
  </div> <!-- End primary_tabs_container -->
  
  <div id="primary_content">
   <p class="para4 parafirstletters para_book"><span class="firstletters"><em class="firstletter">Estimated Venue Impact Factors.</em></span> Generated from documents in the <fmt:message key="app.nameHTML"/> database as of <c:out value="${ gendate }"/>.  This list is automatically generated and may contain errors.</p>
   <p class="para4 para_book">Impact is estimated based on Garfield's traditional impact factor.</p>
   <div id="introduction">
    <div id="citedlinks" class="formating nofrills_list para2nomargin">Choose Window:
     <c:forEach var="year" items="${ years }">
      <c:if test="${ year == currentlink }">&#124; <a id="currentlink" href="<c:url value="/stats/venues?y=${year}"/>"><c:out value="${ year }"/></a> </c:if>
      <c:if test="${ year != currentlink }">&#124; <a href="<c:url value="/stats/venues?y=${year}"/>"><c:out value="${ year }"/></a> </c:if>
     </c:forEach>
    </div>              
   </div> <!-- End introduction -->
   <div class="information_bar">
    Only venues with at least 25 articles are shown. Venue details obtained from <a href="http://dblp.uni-trier.de/">DBLP</a> by <a href="http://www.informatik.uni-trier.de/~ley/">Michael Ley</a>. Only venues contained in DBLP are included.
   </div> <!-- End information_bar top -->
   <ol class="numbered_list pushdown" start="<c:out value="1"/>">
    <c:forEach var="venue" items="${ venues }">
     <li><span class="padded2"><a href="<c:url value="${ venue.url }"/>"><c:out value="${ venue.name }" escapeXml="false"/></a> <em class="char_emphasized"><c:out value="${ venue.impact }"/></em></span></li>
    </c:forEach>
   </ol>
   <div class="information_bar">
    Only venues with at least 25 articles are shown. Venue details obtained from <a href="http://dblp.uni-trier.de/">DBLP</a> by <a href="http://www.informatik.uni-trier.de/~ley/">Michael Ley</a>. Only venues contained in DBLP are included.
   </div> <!-- End information_bar top -->
            
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