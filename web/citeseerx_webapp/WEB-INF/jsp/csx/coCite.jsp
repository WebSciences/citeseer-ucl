<%@ include file="viewdoc_header.jsp" %>
<div id="main_content">  
  <div id="right-sidebar"> <!-- Contains left content -->
    <div class="inside"> <!-- to give some room between columns -->
    </div> <!--End inside -->
  </div> <!-- End of right-sidebar --> 
  <h2 class="topic_heading">Documents Related by Co-Citation</h2>
  <!-- List as title - firstauth, secondauth, et al (year) -->
  <!-- whitespace after number is visible on web page -->
  <c:if test="${empty citations}">
    <p class="para4">No related documents identified.</p>
  </c:if>
  <c:if test="${!empty citations}">
    <table class="citelist">
      <c:forEach var="citation" items="${ citations }">
        <tr><td>
          <c:if test="${ citation.ncites > 0 }"><span class="char_emphasized"><c:out value="${ citation.ncites }"/></span></c:if>
        </td><td>
          <c:if test="${ citation.inCollection }"><a href="<c:url value="/viewdoc/summary?cid=${ citation.cluster }"/>"><c:out value="${ citation.title }"/></a></c:if>
          <c:if test="${ ! citation.inCollection }"><a class="citation_only" href="<c:url value="/showciting?cid=${ citation.cluster }"/>"><c:out value="${ citation.title }"/></a></c:if>
          &ndash; <c:out value="${ citation.authors }"/>
          <c:if test="${ citation.year > 0 }"> - <c:out value="${ citation.year }"/></c:if>
        </td></tr>
      </c:forEach>
    </table>
  </c:if>
</div> <!-- End main_content -->
<%@ include file="viewdoc_footer.jsp" %>

