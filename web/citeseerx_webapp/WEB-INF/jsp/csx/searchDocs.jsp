<%--
  -- Shows search results
  --
  -- Author: Isaac Councill
  -- Author: Juan Pablo Fernandez Ramirez
  --%>
<%@ include file="shared/IncludeTop.jsp" %>
  <div id="page_wrapper" class="clearfix"> <!-- Contains all the divisions (div's) within the page (not including top navigation bar, search box and meta information) -->
    <div id="center_content" class="clearfix"> <!-- Contains header div -->
      <div id="primary_content">
        <h1 class="primaryheader">Searching for <c:if test='${ param.t == "auth" }'>authors named </c:if><span class="char3"><c:out value="${ query }"/></span> &ndash; sorted by <c:if test='${ sorttype eq "rlv" }'>Relevance.</c:if><c:if test='${ sorttype eq "cite" }'>Number of Citations</c:if><c:if test='${ sorttype eq "date" }'>Year (Descending)</c:if><c:if test='${ sorttype eq "ascdate" }'>Year (Ascending)</c:if><c:if test='${ sorttype eq "recent" }'>Recency</c:if> </h1>
        <div id="introduction" class="char_increased padded">
          <div class="padded">Order by:
            <c:if test='${ sorttype eq "rlv" }'>
              <a href='<c:url value="/search?${ citeq }"/>'>Citations</a> |
              <a href="<c:url value="/search?${ dateq }"/>">Year (Descending)</a> |
              <a href="<c:url value="/search?${ ascdateq }"/>">Year (Ascending)</a> |
              <a href="<c:url value="/search?${ timeq }"/>">Recency</a>
            </c:if>
            <c:if test='${ sorttype eq "cite" }'>
              <a href="<c:url value="/search?${ rlvq }"/>">Relevance</a> |
              <a href="<c:url value="/search?${ dateq }"/>">Year (Descending)</a> |
              <a href="<c:url value="/search?${ ascdateq }"/>">Year (Ascending)</a> |
              <a href="<c:url value="/search?${ timeq }"/>">Recency</a>
            </c:if>
            <c:if test='${ sorttype eq "date" }'>
              <a href="<c:url value="/search?${ rlvq }"/>">Relevance</a> |
              <a href="<c:url value="/search?${ citeq }"/>">Citations</a> |
              <a href="<c:url value="/search?${ ascdateq }"/>">Year (Ascending)</a> |
              <a href="<c:url value="/search?${ timeq }"/>">Recency</a>
            </c:if>
            <c:if test='${ sorttype eq "ascdate" }'>
              <a href="<c:url value="/search?${ rlvq }"/>">Relevance</a> |
              <a href="<c:url value="/search?${ citeq }"/>">Citations</a> |
              <a href="<c:url value="/search?${ dateq }"/>">Year (Descending)</a> |
              <a href="<c:url value="/search?${ timeq }"/>">Recency</a>
            </c:if>
            <c:if test='${ sorttype eq "recent" }'>
              <a href="<c:url value="/search?${ rlvq }"/>">Relevance</a> |
              <a href="<c:url value="/search?${ citeq }"/>">Citations</a> |
              <a href="<c:url value="/search?${ dateq }"/>">Year (Descending)</a> |
              <a href="<c:url value="/search?${ ascdateq }"/>">Year (Ascending)</a>                  
            </c:if>
          </div>
          <div class="padded">Try your query at:
            <c:url value="http://scholar.google.com/scholar" var="googleScholar"><c:param name="q" value="${ param.q }"/><c:param name="hl" value="en" /><c:param name="btnG" value="Search"/></c:url>
            <c:url value="http://search.yahoo.com/search" var="yahoo"><c:param name="p" value="${ param.q }"/></c:url>
            <c:url value="http://www.ask.com/web" var="ask"><c:param name="q" value="${ param.q }"/></c:url>
            <c:url value="http://search.live.com/results.aspx" var="msLive"><c:param name="q" value="${ param.q }"/></c:url>
            <c:url value="http://liinwww.ira.uka.de/csbib/index" var="CBS"><c:param name="query" value="${ param.q }"/><c:param name="submit" value="Search"/></c:url>
            <a href="<c:out value="${ googleScholar }" escapeXml="true"/>" title="Google Scholar search engine">Scholar</a> &#124;
            <a href="<c:out value="${ yahoo }" escapeXml="true"/>" title="Yahoo Web Search">Yahoo!</a> &#124;
            <a href="<c:out value="${ ask }" escapeXml="true"/>" title="Ask.com Search Engine">Ask</a> &#124;
            <a href="<c:out value="${ msLive }" escapeXml="true"/>" title="MSN Search is now Live Search">MS Live</a> &#124;
            <a href="<c:out value="${ msLive }" escapeXml="true"/>" title="Collection of Computer Science Bibliographies">CSB</a>
          </div>                
        </div> <!-- End introduction -->
        <div class="information_bar char_increased">
         <div class="left_content">
          <a class="remove" href="<c:url value="/help/search"/>"><img class="icon" src="<c:url value="/images/iconqust.gif"/>" alt="Help&#33;"/></a>
          <fmt:formatNumber value="${ nfound }" type="number"/> documents found<c:if test="${ nfound > 0 }">, showing <fmt:formatNumber value="${ start+1 }" type="number"/> through <c:if test="${ (start+nrows) <= nfound }"><fmt:formatNumber value="${ start+nrows }" type="number"/></c:if><c:if test="${ (start+nrows) > nfound }"><fmt:formatNumber value="${ nfound }" type="number"/></c:if>.</c:if>
          <c:if test="${ ! empty nextpageparams }"><a href="<c:url value="/search?${ nextpageparams }"/>">Next <c:out value="${ nrows }"/> &#8594;</a></c:if>
         </div>
         <div class="right_content"><a href="<c:url value="/search?${ atom }"/>"><img src="<c:url value="/icons/atom.gif"/>" alt="ATOM"/></a> <a href="<c:url value="/search?${ rss }"/>"><img src="<c:url value="/icons/rss.gif"/>" alt="RSS"/></a></div>
         <div style="clear:both;"></div>
        </div> <!-- End information_bar top -->
        <div id="main_content"> 
          <div class="searchresult">
            <c:if test="${ nfound == 0 && !error }">
              <br/><br/>
              <span class="char_increased">Your search &ndash; <span class="char_emphasized"><c:out value="${ param.q }"/></span> &ndash; did not match any documents.</span>
              <br/><br/>
            </c:if>
            <c:if test="${ error }">
              <br/><br/>
              <span class="char_increased"><c:out value="${ errorMsg }" escapeXml="false"/></span>
              <br/><br/>
            </c:if>
            <!--LRIS-->
            <c:forEach var="hit" items="${ hits }" varStatus="status">
              <!--RIS-->  
              <c:if test="${ hit.inCollection }">
                <div class="blockhighlight_box">
                  <ul class="blockhighlight">
                    <li class="padded">
                      <c:if test="${ ! empty hit.abstract }">
                        <a class="paper-tips" href="#" title="&lt;h3&gt;Abstract&lt;/h3&gt;::<c:out value="${hit.abstract}" escapeXml="true"/>"><img class="icon remove" src="<c:url value="/icons/iconarrwor.gif"/>" alt=""/></a>
			          </c:if>
                      <c:if test="${ empty hit.abstract }">
                        <a class="paper-tips" href="#" title="&lt;h3&gt;Abstract&lt;/h3&gt;::No abstract found"><img class="icon remove" src="<c:url value="/icons/iconarrwor.gif"/>" alt=""/></a>
                      </c:if>
                      <a class="remove doc_details" href="<c:url value="/viewdoc/summary?doi=${ hit.doi }"/>"><em class="title"><c:if test="${ ! empty hit.title }"><c:out value="${ hit.title }" escapeXml="false"/></c:if><c:if test="${ empty hit.title }">unknown title</c:if></em></a>
                      <c:if test="${ ! empty coins[status.index]}">
                        <span class="Z3988" title="<c:out value="${coins[status.index]}" />">&nbsp;</span>
                      </c:if>
                    </li>
                    <li class="author char6 padded">by <c:if test="${ ! empty hit.authors }"><c:out value="${ hit.authors }"/></c:if>
                      <c:if test="${ empty hit.authors }">unknown authors</c:if>
                      <c:if test="${ ! empty hit.year && hit.year > 0 }"> &#8212; <c:out value="${ hit.year }"/></c:if>
                      <c:if test="${ ! empty hit.venue }"> &#8212; <c:out value="${ hit.venue }"/></c:if>
                    </li>
                    <li class="doc_clipping char_increased padded">&#0133;<c:out value="${ hit.snippet }" escapeXml="false"/>&#0133;</li>
                    <li class="char_increased"><c:if test="${ hit.ncites > 0 }"><a class="citation remove" href="<c:url value="/showciting?cid=${ hit.cluster }"/>" title="number of citations">Cited by <c:out value="${ hit.ncites }"/> (<c:out value="${ hit.selfCites }"/> self)</a> &ndash;</c:if>
                      <span class="actionspan" onclick="addToCartProxy(<c:out value="${hit.cluster}"/>)">Add To MetaCart</span> <span id="cmsg_<c:out value="${ hit.cluster }"/>" class="cartmsg"></span>
                    </li>
			      </ul>
                </div> <!-- End blockhighlight_box -->
              </c:if>
              <c:if test="${ ! hit.inCollection }">
                <div class="blockhighlight_box">
                  <ul class="blockhighlight">
                    <li class="padded"><span class="char_decreased">&#91;CITATION&#93;</span>
                      <em class="title"><c:if test="${ ! empty hit.title }"><c:out value="${ hit.title }" escapeXml="false"/></c:if>
                      <c:if test="${ empty hit.title }">unknown title</c:if></em>
                      <c:if test="${ ! empty coins[status.index]}">
                        <span class="Z3988" title="<c:out value="${coins[status.index]}" />">&nbsp;</span>
                      </c:if>
                     </li>
                    <li class="author char6 padded">by <c:if test="${ ! empty hit.authors }"><c:out value="${ hit.authors }"/></c:if>
                      <c:if test="${ empty hit.authors }">unknown authors</c:if>
                      <c:if test="${ ! empty hit.year && hit.year > 0 }"> &#8212; <c:out value="${ hit.year }"/></c:if>
                      <c:if test="${ ! empty hit.venue }"> &#8212; <c:out value="${ hit.venue }"/></c:if>
                    </li>
                    <li class="char_increased"><c:if test="${ hit.ncites > 0 }"><a class="citation remove" href="<c:url value="/showciting?cid=${ hit.cluster }"/>" title="number of citations">Cited by <c:out value="${ hit.ncites }"/> (<c:out value="${ hit.selfCites }"/> self)</a> &ndash;</c:if>
                      <span class="actionspan" onclick="addToCartProxy(<c:out value="${hit.cluster}"/>)">Add To MetaCart</span> <span id="cmsg_<c:out value="${ hit.cluster }"/>" class="cartmsg"></span>
                    </li>
                  </ul>
                </div> <!-- blockhighlight_box -->
              </c:if>
              <!--end RIS-->
            </c:forEach>
            <!--End LRIS -->
          </div> <!-- End searchresult -->
        </div> <!-- End main_content -->
        <div class="information_bar char_increased">
          <div class="left_content">
            <a class="remove" href="<c:url value="/help/search"/>">
            <img class="icon" src="<c:url value="/images/iconqust.gif"/>" alt="Help&#33;"/></a>
            <c:if test="${ nfound > 0 }">Showing <fmt:formatNumber value="${ start+1 }" type="number"/> through <c:if test="${ (start+nrows) <= nfound }"><fmt:formatNumber value="${ start+nrows }" type="number"/></c:if><c:if test="${ (start+nrows) > nfound }"><fmt:formatNumber value="${ nfound }" type="number"/></c:if>.</c:if>
            <c:if test="${ ! empty nextpageparams }"><a href="<c:url value="/search?${ nextpageparams }"/>">Next <c:out value="${ nrows }"/> &#8594;</a></c:if>
          </div>
          <div class="right_content"><a href="<c:url value="/search?${ atom }"/>"><img src="<c:url value="/icons/atom.gif"/>" alt="ATOM"/></a> <a href="<c:url value="/search?${ rss }"/>"><img src="<c:url value="/icons/rss.gif"/>" alt="RSS"/></a></div>
            <div style="clear:both;"></div>
          </div>
          <div id="conclusion">		
            <div class="para4">
              Try your query at:
              <a href="<c:out value="${ googleScholar }" escapeXml="true"/>" title="Google Scholar search engine">Scholar</a> &#124;
              <a href="<c:out value="${ yahoo }" escapeXml="true"/>" title="Yahoo Web Search">Yahoo!</a> &#124;
              <a href="<c:out value="${ ask }" escapeXml="true"/>" title="Ask.com Search Engine">Ask</a> &#124;
              <a href="<c:out value="${ msLive }" escapeXml="true"/>" title="MSN Search is now Live Search">MS Live</a> &#124;
              <a href="<c:out value="${ msLive }" escapeXml="true"/>" title="Collection of Computer Science Bibliographies">CSB</a>
            </div>
          </div> <!-- End conclusion -->
      </div> <!-- End primary_content -->
    </div> <!-- End center-content -->
<script type="text/javascript">
<!--
if (window != top) 
top.location.href = location.href;
function sf(){document.forms['<c:if test='${ empty param.t || param.t != "auth" }'>doc_search_form</c:if><c:if test='${ (! empty param.t) && param.t == "auth" }'>auth_search_form</c:if>'].q.focus();}

 $$('div.blockhighlight_box').each(function(elem) {
  // add offblock class to each search result
  elem.addClass('offblock');
 
  // Add mouse over and out events to search results.
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
