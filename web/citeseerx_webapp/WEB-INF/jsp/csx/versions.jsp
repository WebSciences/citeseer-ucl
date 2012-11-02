<%@ include file="viewdoc_header.jsp" %>
<div id="main_content">  
  <div id="right-sidebar"> <!-- Contains left content -->
    <div class="inside"> <!-- to give some room between columns -->
      <div class="content_box">
        <h2>Versions</h2>
        <div class="version_nav">
          <ul><c:forEach var="vers" begin="0" end="${ maxversion }"><li<c:if test="${ vers==thisversion }"> class="active"</c:if>><a href="<c:url value="/viewdoc/versions?doi=${ doi }&amp;version=${ vers }"/>">Version <c:out value="${ vers }"/></a></li></c:forEach></ul>
        </div> <!-- End Version Nav -->
      </div> <!-- end content_box -->
    </div> <!--End inside -->
  </div> <!-- End of right-sidebar --> 
  <c:if test="${ error }"><br/><br/><h2 class="topic_heading"><c:out value="${ errMsg }"/></h2><br/><br/></c:if>
  <c:if test="${ ! error }">
    <h2 class="topic_heading">Metadata Version <c:out value="${ thisversion }"/></h2>
    <c:if test="${ user != null }"> 
      <h3 class="bluetext">User correction supplied by <i><c:out value="${ user }"/></i></h3>
    </c:if>
    <table class="metatable">
      <thead><tr><th>Datum</th><th>Value</th><th>Source</th></tr></thead>
      <tbody>
        <c:if test="${ ! empty titlev }"><tr><td>TITLE</td><td><c:out value="${ titlev }"/></td><td><c:out value="${ title_src }"/></td></tr></c:if>
        <c:forEach var="auth" items="${ authv }">
          <tr><td>AUTHOR NAME</td><td><c:out value="${ auth.name }"/></td><td><c:out value="${ auth.nameSrc }"/></td></tr>
          <c:if test="${ ! empty auth.affil }"><tr><td>AUTHOR AFFIL</td><td><c:out value="${ auth.affil }"/></td><td><c:out value="${ auth.affilSrc }"/></td></tr></c:if>
          <c:if test="${ ! empty auth.addr }"><tr><td>AUTHOR ADDR</td><td><c:out value="${ auth.addr }"/></td><td><c:out value="${ auth.addrSrc }"/></td></tr></c:if>
        </c:forEach>
        <c:if test="${ ! empty absv }"><tr><td>ABSTRACT</td><td><c:out value="${ absv }"/></td><td><c:out value="${ abs_src }"/></td></tr></c:if>
        <c:if test="${ ! empty yearv }"><tr><td>YEAR</td><td><c:out value="${ yearv }"/></td><td><c:out value="${ year_src }"/></td></tr></c:if>
        <c:if test="${ ! empty venuev }"><tr><td>VENUE</td><td><c:out value="${ venuev }"/></td><td><c:out value="${ venue_src }"/></td></tr></c:if>
        <c:if test="${ ! empty ventypev }"><tr><td>VENUE TYPE</td><td><c:out value="${ ventypev }"/></td><td><c:out value="${ ventype_src }"/></td></tr></c:if>
        <c:if test="${ ! empty pagesv }"><tr><td>PAGES</td><td><c:out value="${ pagesv }"/></td><td><c:out value="${ pages_src }"/></td></tr></c:if>
        <c:if test="${ ! empty volv }"><tr><td>VOLUME</td><td><c:out value="${ volv }"/></td><td><c:out value="${ vol_src }"/></td></tr></c:if>
        <c:if test="${ ! empty numv }"><tr><td>NUMBER</td><td><c:out value="${ numv }"/></td><td><c:out value="${ num_src }"/></td></tr></c:if>
        <c:if test="${ ! empty techv }"><tr><td>TECH</td><td><c:out value="${ techv }"/></td><td><c:out value="${ tech_src }"/></td></tr></c:if>
        <c:if test="${ ! empty citesv }"><tr><td>CITATIONS</td><td><c:out value="${ citesv }"/></td><td><c:out value="${ cites_src }"/></td></tr></c:if>
      </tbody>
    </table>
  </c:if>
</div> <!-- End main_content -->
<%@ include file="viewdoc_footer.jsp" %>