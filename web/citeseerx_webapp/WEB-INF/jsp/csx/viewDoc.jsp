<%@ include file="viewdoc_header.jsp" %>
<div id="main_content">  
  <div id="right-sidebar"> <!-- Contains left content -->
    <div class="inside"> <!-- to give some room between columns -->
      <div class="content_box">
        <h2>Popular Tags</h2>
        <c:if test="${ empty tags }"><p>No tags have been applied to this document.</p></c:if>
        <c:if test="${ ! empty tags }">
          <ul class="tagcloud_doc">
            <c:forEach var="tag" items="${ tags }">
              <c:url value="/search" var="searchUrl"><c:param name="q" value="tag:\"${ tag.tag }\""/></c:url>
              <li class="tag2"><a href="<c:out value="${ searchUrl }" escapeXml="true" />"><c:out value="${ tag.tag }"/></a></li>
            </c:forEach>
          </ul>
        </c:if>
        <form method="post" action="<c:url value="/myciteseer/action/editTags"/>" enctype="application/x-www-form-urlencoded" id="tag_form">
   	      <fieldset class="noborder">
            <p>Add a tag: <input class="tag_query" type="text" name="tag" value="" size="25" />
            <input class="button" type="submit" name="submit" value="Submit" alt="submit" title="Enter a keyword for this document" /></p>
            <input type="hidden" name="doi" value="<c:out value="${ doi }"/>"/>
          </fieldset>
        </form>
      </div> <!-- End content box -->
      <c:if test="${ ! empty bibtex }">
        <div class="content_box">
          <h2>BibTeX | <span class="actionspan" onclick="addToCartProxy(<c:out value="${ clusterid }"/>)">Add To MetaCart</span></h2>
          <c:out value="${ bibtex }" escapeXml="false"/>
        </div> <!-- End content box -->
      </c:if>
      <c:if test="${ chartfile }">
        <div class="content_box">
          <h2>Years of Citing Articles</h2>
	      <img src="<c:url value="/citecharts/chart.png?doi=${ doi }&rep=${ rep }"/>" border="0"/>
        </div> <!-- End content box -->
      </c:if>
    </div> <!--End inside -->
  </div> <!-- End of right-sidebar --> 
  <h2 class="topic_heading">Abstract:</h2>  
  <p class="para4"><c:out value="${ abstract }"/></p>
  <h2 class="topic_heading">Citations</h2>
  <!-- List as title - firstauth, secondauth, et al (year) -->
  <!-- whitespace after number is visible on web page -->
  <c:if test="${empty citations}">
    <p class="para4">No citations identified.</p>
  </c:if>
  <c:if test="${!empty citations}">
    <table class="citelist">
      <c:forEach var="citation" items="${ citations }">
        <tr><td>
          <c:if test="${ citation.ncites > 0 }"><span class="char_emphasized"><c:out value="${ citation.ncites }"/></span></c:if>
        </td>
        <td>
          <c:if test="${ citation.inCollection }"><a href="<c:url value="/viewdoc/summary?cid=${ citation.cluster }"/>"><c:out value="${ citation.title }" escapeXml="true" /></a></c:if>
          <c:if test="${ ! citation.inCollection }"><a class="citation_only" href="<c:url value="/showciting?cid=${ citation.cluster }"/>"><c:out value="${ citation.title }" escapeXml="true" /></a></c:if>
          &ndash; <c:out value="${ citation.authors }"/>
          <c:if test="${ citation.year > 0 }"> - <c:out value="${ citation.year }"/></c:if>
        </td></tr>
      </c:forEach>
    </table>
  </c:if>
  <div class="parent_div_spacer"></div>
</div> <!-- End main_content -->
<%@ include file="viewdoc_footer.jsp" %>