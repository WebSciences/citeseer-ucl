<%@ include file="../shared/IncludeTop2.jsp" %>
 <div id="primary_tabs-n-content" class="clearfix"> <!-- Contains header div -->
  <div id="primary_tabs_container">
   <div id="primary_tabs">
    <ul class="clearfix"><li><a class="page_tabs remove" href="<c:url value="/stats/articles"/>"><span>Most Cited Articles</span></a></li><li id="current"><a class="page_tabs remove" href="<c:url value="/stats/citations"/>"><span>Most Cited Citations</span></a></li><li><a class="page_tabs remove" href="<c:url value="/stats/authors"/>"><span>Most Cited Authors</span></a></li><li><a class="page_tabs remove" href="<c:url value="/stats/venues"/>"><span>Venue Impact Ratings</span></a></li></ul>   <!-- remove extra whitespace by coding in-line lists on one line -->           
   </div> <!-- End primary_tabs -->
  </div> <!-- End primary_tabs_container -->
  
  <div id="primary_content">

   <p class="para4 parafirstletters para_book"><span class="firstletters"><em class="firstletter">Most Cited Computer Science Citations</em></span> generated from documents in the <fmt:message key="app.nameHTML"/> database as of <c:out value="${ gendate }"/>.  This list is automatically generated and may contain errors. The list is generated in batch mode and citation counts may differ from those currently in the <fmt:message key="app.nameHTML"/> database, since the database is continuously updated.</p>
   <div id="introduction">
    <div id="citedlinks" class="formating nofrills_list para2nomargin">
     <c:if test="${ empty currentlink }"><a id="currentlink" href="<c:url value="/stats/articles"/>">All Years</a> </c:if>
     <c:if test="${ ! empty currentlink }"><a href="<c:url value="/stats/articles"/>">All Years</a> </c:if>
     <c:forEach var="year" items="${ years }">
      <c:if test="${ year == currentlink }">&#124; <a id="currentlink" href="<c:url value="/stats/citations?y=${year}"/>"><c:out value="${ year }"/></a> </c:if>
      <c:if test="${ year != currentlink }">&#124; <a href="<c:url value="/stats/citations?y=${year}"/>"><c:out value="${ year }"/></a> </c:if>
     </c:forEach>
    </div>              
   </div> <!-- End introduction -->                   
   <div class="information_bar">
    <c:if test="${ ! empty nextPageParams }">
     <a href="<c:url value="/stats/citations?${ nextPageParams }"/>">Next <c:out value="${ pageSize }"/> <img class="mini_icon remove" src="<c:url value="/icons/arrwnxt.gif"/>" alt="View Next Page" /></a>  
    </c:if>
   </div> <!-- End information_bar top -->                
   <ol class="numbered_list pushdown" start="<c:out value="${ start+1 }"/>">
    <c:forEach var="doc" items="${ docs }">
     <c:if test="${ doc.inCollection }">
      <li class="padded"><span class="padded2"><em class="char_emphasized" title="citation count"><c:out value="${ doc.ncites }"/></em></span> <c:out value="${ doc.authors }"/>. <a href="<c:url value="/viewdoc/summary?cid=${ doc.cluster }"/>"><em><c:out value="${ doc.title }"/></em></a>. <c:out value="${ doc.venue }"/><c:out value="${ doc.year }"/>.</li>
     </c:if>
     <c:if test="${ ! doc.inCollection }">
      <li class="padded"><span class="padded2"><em class="char_emphasized" title="citation count"><c:out value="${ doc.ncites }"/></em></span> <c:out value="${ doc.authors }"/>. <a class="citation_only" href="<c:url value="/showciting?cid=${ doc.cluster }"/>"><em><c:out value="${ doc.title }"/></em></a>. <c:out value="${ doc.venue }"/><c:out value="${ doc.year }"/>.</li>
     </c:if>
    </c:forEach>
   </ol>
   <div class="information_bar">
    <c:if test="${ ! empty nextPageParams }">
     <a href="<c:url value="/stats/citations?${ nextPageParams }"/>">Next <c:out value="${ pageSize }"/> <img class="mini_icon remove" src="<c:url value="/icons/arrwnxt.gif"/>" alt="View Next Page" /></a>  
    </c:if>
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