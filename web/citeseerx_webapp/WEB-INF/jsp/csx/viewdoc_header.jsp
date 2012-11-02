<%--
  -- This page includes Header specific information view document pages.
  --
  -- Author: Isaac Councill
  --%>
<%@ include file="shared/IncludeTop.jsp" %>
<div id="center_content" class="clearfix"> <!-- Contains header div -->
  <div id="secondary_tabs_container">
    <div id="secondary_tabs">
    <!-- to make a tab highlighted, place id="currentpage" within li tag -->
      <ul><li<c:if test='${ pagetype == "summary" }'> id="currentpage"</c:if>><a class="page_tabs remove" href="<c:url value="/viewdoc/summary?doi=${ doi }"/>"><span>Summary</span></a></li><li<c:if test='${ pagetype == "similar" }'> id="currentpage"</c:if> class="tab4sublist"><a class="page_tabs remove" href="<c:url value="/viewdoc/similar?doi=${ doi }"/>" title="Find documents related to this document"><span class="clearfix">Related Documents</span></a><ul class="tab_sublist clearfix"><li><a class="sublist_links remove" href="<c:url value="/viewdoc/similar?doi=${ doi }&amp;type=ab"/>" title="Documents that cite the same works"><span>Active Bibliography</span></a></li><li><a class="sublist_links remove" href="<c:url value="/viewdoc/similar?doi=${ doi }&amp;type=cc"/>" title="Documents cited by the same works"><span>Co-citation</span></a></li></ul></li><li<c:if test='${ pagetype == "versions" }'> id="currentpage"</c:if>><a class="page_tabs remove" href="<c:url value="/viewdoc/versions?doi=${ doi }"/>"><span>Version History</span></a></li></ul>
    </div> <!-- End secondary_tabs -->
  </div> <!-- End secondary_tabs_container -->
  <div id="primary_content">
    <h1 class="primaryheader">
      <c:if test="${! empty coins}">
        <span class="Z3988" title="<c:out value="${ coins }"/>">&nbsp;</span>
      </c:if>
      <c:out value="${ title }"/>
     <c:if test="${ ! empty year }"> (<c:out value="${ year }"/>) </c:if>
     <c:if test="${ ncites > 0 }">&#91;<a class="citation remove" href="<c:url value="/showciting?doi=${ doi }"/>" title="number of citations"><c:out value="${ ncites }"/> citations &mdash; <c:out value="${ selfCites }"/> self</a>&#93;</c:if>
    </h1>
    <div id="introduction">
      <div id="downloads" class="dlspan">
        Download:<br/>
        <c:forEach var="type" items="${ fileTypes }" varStatus="status"><c:if test="${ status.count > 1 }"> | </c:if>
          <c:url value="/viewdoc/download" var="downloadUrl"><c:param name="doi" value="${ doi }"/><c:param name="rep" value="${ rep }"/><c:param name="type" value="${ type }"/></c:url>
          <c:if test="${type == 'pdf'}">
            <a href="<c:out value="${downloadUrl}" escapeXml="true"/>" title="View or Download this document as PDF"><img src="<c:url value="/icons/pdf.gif"/>" alt="Download as a PDF"/> </a>
          </c:if>
          <c:if test="${type == 'ps'}">
            <a href="<c:out value="${downloadUrl}" escapeXml="true"/>" title="View or Download this document as PS"><img src="<c:url value="/icons/ps.gif"/>" alt="Download as a PS"/> </a>
          </c:if>
        </c:forEach>
      </div> <!-- End downloads -->
      <div class="char_increased char_indented char_mediumvalue padded">
        <c:if test="${ ! empty authors }">by <c:out value="${ authors }"/></c:if>
        <c:if test="${ empty authors }">unknown authors</c:if>
      </div>
      <div class="char_increased  char_indented char6 padded">
        <c:if test="${ ! empty venue }"><c:out value="${ venue }"/></c:if>
      </div>
      <div class="char_increased char_indented char6 padded">
        <c:out value="${ url }"/>
      </div>
      <div class="char_increased char_indented"><span class="actionspan" onclick="addToCartProxy(<c:out value="${ clusterid }"/>)">Add To MetaCart</span> <span id="cmsg_<c:out value="${ clusterid }"/>" class="cartmsg"></span></div>
    </div> <!-- End introduction -->
    <div class="information_bar clearfix"><ul class="button_nav"><li><a class="remove"  href="<c:url value="/myciteseer/action/addPaperCollection?doi=${doi}"/>" title="Add this document to your collection (Account required)"><span>Add to Collection</span></a></li><li><a class="remove" href="<c:url value="/correct?doi=${ doi }"/>" title="Submit corrections for this document (Account required)"><span>Correct Errors</span></a></li><li><a class="remove" href="<c:url value="/myciteseer/action/editMonitors?doi=${ doi }"/>" title="Monitor changes to this document (Account required)"><span>Monitor Changes</span></a></li></ul></div>
