<%@ include file="IncludeTop.jsp" %>
<div class="mypagecontent">
<div class="columns-float-sec">
<div class="column-one-sec"> <!-- center column -->
 <div class="column-one-content clearfix">
  <h2 class="char_headers">Documents I've tagged as &quot;<c:out value="${ tag }"/>&quot;</h2>
  <div id="primary_content">
    <div class="information_bar char_increased">
      <c:if test="${ ! empty nextpageparams }"><a href="<c:url value="/myciteseer/action/viewTaggedDocs${ nextpageparams }"/>">Next &#8594;</a></c:if>Found <c:out value="${ nresults }"/> papers.
    </div> <!-- End information bar -->
    <div class="searchresult">
      <c:if test="${!empty papers}">
        <c:forEach var="paper" items="${papers}">
          <div class="blockhighlight_box"> <!-- List Item -->
            <ul class="blockhighlight">
              <li class="padded"><a class="paper-tips remove doc_details" href="<c:url value="/viewdoc/summary?doi=${paper.doc.doi}"/>" title="<c:out value="${paper.doc.title}"/>::&lt;strong&gt;Abstract:&lt;/strong&gt; <c:out value="${paper.doc.abstract}"/>&lt;br /&gt;&lt;strong&gt;Venue:&lt;/strong&gt; <c:out value="${paper.doc.venue}"/>"><em class="title"><c:out value="${paper.doc.title}"/></em></a>
                <c:if test="${ ! empty paper.coins}">
                  <span class="Z3988" title="<c:out value="${paper.coins}" />">&nbsp;</span>
                </c:if>
              </li>
              <li class="author char6 padded">by <c:out value="${paper.doc.authors}"/></li>
              <c:if test="${paper.doc.year > 0}"><li class="author char6 padded">Year: <c:out value="${paper.doc.year}"/></li></c:if>
              <li class="char_increased padded"><c:if test="${ paper.doc.ncites > 0 }"><a class="citation remove" href="<c:url value="/showciting?cid=${ paper.doc.cluster }"/>" title="number of citations">Cited by <c:out value="${ paper.doc.ncites }"/> (<c:out value="${ paper.doc.selfCites }"/> self)</a> &ndash;</c:if>
                <span class="actionspan"><a href="<c:url value="/myciteseer/action/editTags?tag=${ tag }&amp;doi=${ paper.doc.doi }&amp;type=del"/>" title="Delete">Delete Tag</a>&nbsp;&ndash;</span>
                <span class="actionspan" onclick="addToCartProxy(<c:out value="${paper.doc.cluster}"/>)">Add To MetaCart</span> <span id="cmsg_<c:out value="${ paper.doc.cluster }"/>" class="cartmsg"></span>
              </li>
            </ul>
          </div> <!-- End List Item -->
        </c:forEach>
      </c:if>
      <c:if test="${empty papers}"><span class="char_increased">No papers have been found.</span></c:if>
    </div> <!-- End search results -->
    <div class="information_bar char_increased">
      <c:if test="${ ! empty nextpageparams }"><a href="<c:url value="/myciteseer/action/viewTaggedDocs${ nextpageparams }"/>">Next &#8594;</a></c:if>Found <c:out value="${ nresults }"/> papers.
    </div> <!-- End information bar -->
  </div> <!-- End primary content -->
</div> <!-- End column-one-content -->
 </div> <!-- End column-one (center column) -->
 <div class="column-two-sec"> <!-- Left column -->
  <div class="column-two-content">
  </div> <!-- End column-two-content -->
 </div> <!-- End column-two (Left column) -->
 <div class="box-clear">&nbsp;</div><!-- # needed to make sure column 3 is cleared || but IE5(PC) and OmniWeb don't like it  -->
</div><!-- End columns-float -->
 <div class="column-three-sec"> <!-- right column -->
  <div class="column-three-content"></div>
 </div> <!-- End column-three -->
 <div class="box-clear">&nbsp;</div><!-- # needed to make sure column 3 is cleared || but IE5(PC) and OmniWeb don't like it  -->
 <div class="nn4clear">&nbsp;</div><!-- # needed for NN4 to clear all columns || not needed by any other browser -->
</div> <!-- End mypagecontent -->

<script type="text/javascript">
<!--
if (window != top) 
 top.location.href = location.href;
function sf(){}
function sa(){
 var elt = document.getElementById("tags_tab");
 elt.setAttribute("class", "active");
 elt.setAttribute("className", "active");
}

$$('div.blockhighlight_box').each(function(elem) {
  // add offblock class to each collection item
  elem.addClass('offblock');
 
  // Add mouse over and out events to each collection item.
  elem.addEvent('mouseover', function(event) {
   var event = new Event(event);
   event.stop();
   elem.removeClass('offblock');
   elem.addClass('overblock');
  });
 
 elem.addEvent('mouseout', function(event) {
   var event = new Event(event);
   event.stop();
   elem.removeClass('overblock');
   elem.addClass('offblock');
  });
});
     
window.addEvent('domready', function(){
 /* paperTips */
 var paperTips = new Tips($$('.paper-tips'), {
  maxTitleChars:150,
  className:'paper-tool'
 });
});
// -->
</script>
<%@ include file="../shared/IncludeBottom.jsp" %>