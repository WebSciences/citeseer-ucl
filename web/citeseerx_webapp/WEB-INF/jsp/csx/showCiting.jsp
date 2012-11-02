<%@ include file="shared/IncludeTop.jsp" %>
  <div id="center_content" class="clearfix"> <!-- Contains header div -->
    <div id="primary_content">
      <h1 class="primaryheader">Searching for <span class="char3">CITATIONS</span> to document sorted by <c:if test='${ sorttype eq "cite" }'>Number of Citations</c:if><c:if test='${ sorttype eq "date" }'>Year (Descending)</c:if><c:if test='${ sorttype eq "ascdate" }'>Year (Ascending)</c:if><c:if test='${ sorttype eq "recent" }'>Recency</c:if>:</h1>
	  <div id="introduction">
	    <div class="char_increased_alot padded">
	     <c:if test="${ inCollection }"><a href="<c:url value="/viewdoc/summary?cid=${cid}"/>"></c:if>
		 <c:if test="${! empty title }"><c:out value="${ title }"/></c:if>
		 <c:if test="${ empty title }">Unknown Title</c:if>
		 <c:if test="${ inCollection }"></a></c:if>
         <c:if test="${ ! empty year }"> (<c:out value="${ year }"/>) </c:if>
        </div>
  		<div class="char_increased char_indented char_mediumvalue padded">
         <c:if test="${ ! empty authors }">by <c:out value="${ authors }"/></c:if>
    	 <c:if test="${ empty authors }">unknown authors</c:if>
		</div>
  		<div class="char_increased  char_indented char6 padded">
         <c:if test="${ ! empty venue }"><c:out value="${ venue }"/></c:if>
        </div>
        <div class="char_increased char_indented"><span class="actionspan" onclick="addToCartProxy(<c:out value="${ cid }"/>)">Add To MetaCart</span> <span id="cmsg_<c:out value="${ cid }"/>" class="cartmsg"></span></div>
        <div class="char_increased pushdown">Order by:
         <c:if test='${ sorttype eq "cite" }'>
          <a href="<c:url value="/showciting?${ dateq }"/>">Year (Descending)</a> |
          <a href="<c:url value="/showciting?${ ascdateq }"/>">Year (Ascending)</a> |
          <a href="<c:url value="/showciting?${ timeq }"/>">Recency</a>
         </c:if>
         <c:if test='${ sorttype eq "date" }'>
          <a href="<c:url value="/showciting?${ citeq }"/>">Citations</a> |
          <a href="<c:url value="/showciting?${ ascdateq }"/>">Year (Ascending)</a> |
          <a href="<c:url value="/showciting?${ timeq }"/>">Recency</a>
         </c:if>
         <c:if test='${ sorttype eq "ascdate" }'>
          <a href="<c:url value="/showciting?${ citeq }"/>">Citations</a> |
          <a href="<c:url value="/showciting?${ dateq }"/>">Year (Descending)</a> |
          <a href="<c:url value="/showciting?${ timeq }"/>">Recency</a>
         </c:if>
         <c:if test='${ sorttype eq "recent" }'>
          <a href="<c:url value="/showciting?${ citeq }"/>">Citations</a> |
          <a href="<c:url value="/showciting?${ dateq }"/>">Year (Descending)</a> |
          <a href="<c:url value="/showciting?${ ascdateq }"/>">Year (Ascending)</a>                   
         </c:if>
        </div>        
      </div> <!-- End introduction -->
      <div class="information_bar char_increased">
        <div class="left_content">
          <a class="remove" href="help.html"><img class="icon" src="<c:url value="/images/iconqust.gif"/>" alt="Help&#33;"/></a>
          <c:out value="${ ncites }"/> citations found<c:if test="${ ncites > 0 }">, showing <c:out value="${ start+1 }"/> through <c:if test="${ (start+nrows) <= ncites }"><c:out value="${ start+nrows }"/></c:if><c:if test="${ (start+nrows) > ncites }"><c:out value="${ ncites }"/></c:if>.</c:if>
          <c:if test="${ ! empty nextpageparams }"><a href="<c:url value="/showciting?${ nextpageparams }"/>">Next <c:out value="${ nrows }"/> &#8594;</a></c:if>
        </div>
        <div class="right_content"><a href="<c:url value="/showciting?${ atom }"/>"><img src="<c:url value="/icons/atom.gif"/>" alt="ATOM"/></a> <a href="<c:url value="/showciting?${ rss }"/>"><img src="<c:url value="/icons/rss.gif"/>" alt="RSS"/></a></div>
        <div style="clear:both;"></div>
      </div> <!-- End information_bar top -->
      <div id="main_content"> 
        <div class="searchresult">
          <c:if test="${ ncites == 0 && !error }">
            <br/><br/>
            <span class="char_increased">No citations were found for this document.</span>
            <br/><br/>
          </c:if>
          <c:if test="${ error }">
            <br/><br/>
            <span class="char_increased"><c:out value="${ errorMsg }" escapeXml="false"/></span>
            <br/><br/>
          </c:if>
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
                    <!--<a class="tooltip" href="#"><img class="icon remove" alt="" src="<c:url value="/icons/iconarrwor.gif"/>"/><span><c:if test="${ ! empty hit.abstract }"><h3>Abstract</h3><c:out value="${ hit.abstract }" escapeXml="false" /></c:if><c:if test="${ empty hit.abstract }">No abstract found.</c:if></span></a><a class="remove doc_details" href="<c:url value="/viewdoc/summary?doi=${ hit.doi }"/>"><em class="title"><c:if test="${ ! empty hit.title }"><c:out value="${ hit.title }" escapeXml="false"/></c:if><c:if test="${ empty hit.title }">unknown title</c:if></em></a>-->
                    <c:if test="${ ! empty coins[status.index]}">
                      <span class="Z3988" title="<c:out value="${coins[status.index]}" />">&nbsp;</span>
                    </c:if>
                  </li>
                  <li class="author char6 padded">by <c:if test="${ ! empty hit.authors }"><c:out value="${ hit.authors }"/></c:if>
                    <c:if test="${ empty hit.authors }">unknown authors</c:if>
                    <c:if test="${ ! empty hit.year && hit.year > 0 }"> &#8212; <c:out value="${ hit.year }"/></c:if>
                    <c:if test="${ ! empty hit.venue }"> &#8212; <c:out value="${ hit.venue }"/></c:if>
                  </li>
                  <li class="char_increased padded"><c:if test="${ hit.ncites > 0 }"><a class="citation remove" href="<c:url value="/showciting?cid=${ hit.cluster }"/>" title="number of citations">Cited by <c:out value="${ hit.ncites }"/> (<c:out value="${ hit.selfCites }"/> self)</a> &ndash; </c:if>
                    <span class="actionspan" onclick="showContextField(<c:out value="${hit.cluster}"/>, <c:out value="${cid}"/>)">Show/Hide Context</span> &ndash;
                    <span class="actionspan" onclick="addToCartProxy(<c:out value="${hit.cluster}"/>)">Add To MetaCart</span> <span id="cmsg_<c:out value="${ hit.cluster }"/>" class="cartmsg"></span>
                  </li>
                  <li class="char_increased padded"><div class="cite_context" id="context_<c:out value="${ hit.cluster }"/>"><div id="context_cont_<c:out value="${ hit.cluster }"/>"></div></div></li>
                </ul>
              </div>
            </c:if>
            <c:if test="${ ! hit.inCollection }">
              <div class="blockhighlight_box">
                <ul class="blockhighlight">
                  <li class="padded"><span class="char_decreased">&#91;CITATION&#93;</span>
                    <em class="title"><c:if test="${ ! empty hit.title }"><c:out value="${ hit.title }" escapeXml="false"/></c:if>
                    <c:if test="${ empty hit.title }">unknown title</c:if></em>
                  </li>
                  <li class="author char6 padded">by <c:if test="${ ! empty hit.authors }"><c:out value="${ hit.authors }"/></c:if>
                    <c:if test="${ empty hit.authors }">unknown authors</c:if>
                    <c:if test="${ ! empty hit.year && hit.year > 0 }"> &#8212; <c:out value="${ hit.year }"/></c:if>
                    <c:if test="${ ! empty hit.venue }"> &#8212; <c:out value="${ hit.venue }"/></c:if>
                  </li>
                  <li class="char_increased padded"><c:if test="${ hit.ncites > 0 }"><a class="citation remove" href="<c:url value="/showciting?cid=${ hit.cluster }"/>" title="number of citations">Cited by <c:out value="${ hit.ncites }"/> (<c:out value="${ hit.selfCites }"/> self)</a> &ndash; </c:if>
                    <span class="actionspan" onclick="showContextField(<c:out value="${hit.cluster}"/>, <c:out value="${cid}"/>)">Show/Hide Context</span> &ndash;
                    <span class="actionspan" onclick="addToCartProxy(<c:out value="${hit.cluster}"/>)">Add To MetaCart</span> <span id="<c:out value="${ hit.cluster }"/>_cmsg" class="cartmsg"></span>
                  </li>
                  <li class="char_increased padded"><div class="cite_context" id="context_<c:out value="${ hit.cluster }"/>"><div id="context_cont_<c:out value="${ hit.cluster }"/>"></div></div></li>
                </ul>
              </div>
            </c:if>
          </c:forEach>
        </div> <!-- End searchresult -->
      </div> <!-- End main_content -->
      <div class="information_bar char_increased">
        <div class="left_content">
          <a class="remove" href="help.html"><img class="icon" src="<c:url value="/images/iconqust.gif"/>" alt="Help&#33;"/></a>
          <c:if test="${ ncites > 0 }"><span>Showing results <c:out value="${ start+1 }"/> through <c:if test="${ start+nrows <= ncites }"><c:out value="${ start+nrows }"/></c:if><c:if test="${ start+nrows > ncites }"><c:out value="${ ncites }"/></c:if>.</span></c:if>
          <c:if test="${ ! empty nextpageparams }"><a href="<c:url value="/showciting?${ nextpageparams }"/>">Next <c:out value="${ nrows }"/> &#8594;</a></c:if>
        </div>
        <div class="right_content"><a href="<c:url value="/showciting?${ atom }"/>"><img src="<c:url value="/icons/atom.gif"/>" alt="ATOM"/></a> <a href="<c:url value="/showciting?${ rss }"/>"><img src="<c:url value="/icons/rss.gif"/>" alt="RSS"/></a></div>
        <div style="clear:both;"></div>
      </div> <!-- End information_bar -->
    </div> <!-- End primary_content --> 
  </div> <!-- End center_content -->
<script type="text/javascript">
<!--
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
