<%@ include file="../shared/IncludeTop2.jsp" %>
 <div id="primary_tabs-n-content" class="clearfix"> <!-- Contains header div -->
  <div id="primary_tabs_container">
   <div id="primary_tabs">
    <ul class="clearfix"><li><a class="page_tabs remove" href="<c:url value="/stats/articles"/>"><span>Most Cited Articles</span></a></li><li><a class="page_tabs remove" href="<c:url value="/stats/citations"/>"><span>Most Cited Citations</span></a></li><li id="current"><a class="page_tabs remove" href="<c:url value="/stats/authors"/>"><span>Most Cited Authors</span></a></li><li><a class="page_tabs remove" href="<c:url value="/stats/venues"/>"><span>Venue Impact Ratings</span></a></li></ul>   <!-- remove extra whitespace by coding in-line lists on one line -->           
   </div> <!-- End primary_tabs -->
  </div> <!-- End primary_tabs_container -->
  
  <div id="primary_content">

   <p class="para4 parafirstletters para_book"><em class="firstletter">Most Cited Computer Science Authors</em> generated from documents in the <fmt:message key="app.nameHTML"/> database as of <c:out value="${ gendate }"/>.  An entry may correspond to multiple authors (e.g. J. Smith). This list is automatically generated and may contain errors. Citation counts may differ from search results because this list is generated in batch mode whereas the database is continually updated. <!-- Homepages listed may not be for the most cited individual, especially when an entry corresponds to multiple authors. Click on HPSearch to see and update the latest homepage data.--></p>
   <div class="information_bar">
    <c:if test="${ ! empty nextPageParams && ! showingAll }">
     <a href="<c:url value="/stats/authors?${ nextPageParams }"/>">Next <c:out value="${ pageSize }"/> <img class="mini_icon remove" src="<c:url value="/icons/arrwnxt.gif"/>" alt="View Next Page" /></a>  
    </c:if>
    <c:if test="${ showingAll }">Showing all authors</c:if>
    <c:if test="${ ! showingAll }"> <a href="<c:url value="/stats/authors?all=true"/>">Show All</a></c:if>
   </div> <!-- End information_bar top -->                
   <ol class="numbered_list pushdown" start="<c:out value="${ start+1 }"/>">
    <c:forEach var="auth" items="${ authors }">
     <li><span class="padded2"><a href="<c:url value="/search?q=%22${ auth.name }%22&amp;sort=cite&amp;t=auth"/>"><c:out value="${ auth.name }"/></a> <em class="char_emphasized"><c:out value="${ auth.ncites }"/></em></span></li>
    </c:forEach>
   </ol>
   <div class="information_bar">
    <c:if test="${ ! empty nextPageParams && ! showingAll }">
     <a href="<c:url value="/stats/authors?${ nextPageParams }"/>">Next <c:out value="${ pageSize }"/> <img class="mini_icon remove" src="<c:url value="/icons/arrwnxt.gif"/>" alt="View Next Page" /></a>  
    </c:if>
    <c:if test="${ showingAll }">Showing all authors</c:if>
    <c:if test="${ ! showingAll }"> <a href="<c:url value="/stats/authors?all=true"/>">Show All</a></c:if>
   </div> <!-- End information_bar bottom -->  
            
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