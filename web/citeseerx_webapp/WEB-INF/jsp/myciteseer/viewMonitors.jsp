<%@ include file="IncludeTop.jsp" %>
<div class="mypagecontent">
<div class="columns-float-sec">
<div class="column-one-sec"> <!-- center column -->
 <div class="column-one-content clearfix">
    <h2 class="char_headers">Papers I'm Monitoring</h2>
    <div id="primary_content">
      <div id="introduction">
        <div class="padded">Order By:
          <c:if test='${ psort eq "title" }'>
           <a href="<c:url value="/myciteseer/action/viewMonitors${ titleq }"/>">Title <c:if test='${sptype eq "asc"}'><img src="<c:url value="/icons/iconarrwor.gif" />" alt="Asc" /></c:if><c:if test='${sptype eq "desc"}'><img src="<c:url value="/icons/iconarrowup.gif"/>" alt="Desc"/></c:if> </a>
           &nbsp;&nbsp;<a href="<c:url value="/myciteseer/action/viewMonitors${ dateq }"/>">Pub Date</a>
           &nbsp;&nbsp;<a href="<c:url value="/myciteseer/action/viewMonitors${ citeq }"/>">Citations</a>
          </c:if>
          <c:if test='${ psort eq "date" }'>
           <a href="<c:url value="/myciteseer/action/viewMonitors${ titleq }"/>">Title</a>
           &nbsp;&nbsp;<a href="<c:url value="/myciteseer/action/viewMonitors${ dateq }"/>">Pub Date <c:if test='${sptype eq "asc"}'><img src="<c:url value="/icons/iconarrwor.gif"/>" alt="Asc" /></c:if><c:if test='${sptype eq "desc"}'><img src="<c:url value="/icons/iconarrowup.gif"/>" alt="Desc" /></c:if></a>
           &nbsp;&nbsp;<a href="<c:url value="/myciteseer/action/viewMonitors${ citeq }"/>">Citations</a>
          </c:if>
          <c:if test='${ psort eq "cite" }'>
           <a href="<c:url value="/myciteseer/action/viewMonitors${ titleq }"/>">Title</a>
           &nbsp;&nbsp;<a href="<c:url value="/myciteseer/action/viewMonitors${ dateq }"/>">Pub Date</a>
           &nbsp;&nbsp;<a href="<c:url value="/myciteseer/action/viewMonitors${ citeq }"/>">Citations <c:if test='${sptype eq "asc"}'><img src="<c:url value="/icons/iconarrwor.gif"/>" alt="Asc" /></c:if><c:if test='${sptype eq "desc"}'><img src="<c:url value="/icons/iconarrowup.gif"/>" alt="Desc" /></c:if></a>
          </c:if>
         </div>
      </div> <!-- End Introduction -->
      <div class="information_bar char_increased">
        <c:if test="${ ! empty previouspageparams }"><a href="<c:url value="/myciteseer/action/viewMonitors${ previouspageparams }"/>">&#8592; Previous Page&nbsp;</a></c:if>Documents found: <c:out value="${npresults}"/> papers.
      Page <c:out value="${ppn}" /> of <c:out value="${tppn}" />
      <c:if test="${ ! empty nextpageparams }"><a href="<c:url value="/myciteseer/action/viewMonitors${ nextpageparams }"/>">Next Page &#8594;</a></c:if>
      </div> <!-- End information bar -->
      <div class="searchresult">
         <c:if test="${ empty monitors }">No papers are currently being monitored.</c:if>
         <c:if test="${ ! empty monitors }">
           <c:forEach var="paper" items="${monitors}">
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
                   <span class="actionspan"><a href="<c:url value="/myciteseer/action/editMonitors?&amp;doi=${ paper.doc.doi }&amp;type=del"/>" title="Delete">Delete</a>&nbsp;&ndash;</span>
                   <span class="actionspan" onclick="addToCartProxy(<c:out value="${paper.doc.cluster}"/>)">Add To MetaCart</span> <span id="cmsg_<c:out value="${ paper.doc.cluster }"/>" class="cartmsg"></span>
                 </li>
               </ul>
             </div> <!-- End List Item -->
           </c:forEach>
         </c:if>
       </div> <!-- End search results -->
      <div class="information_bar char_increased">
        <c:if test="${ ! empty nextpageparams }"><a href="<c:url value="/myciteseer/action/viewMonitors${ nextpageparams }"/>">Next &#8594;</a></c:if>Found <c:out value="${ npresults }"/> papers.
      </div> <!-- End information bar -->
      <div id="conclusion">
        <div class="padded">Order By:
          <c:if test='${ psort eq "title" }'>
           <a href="<c:url value="/myciteseer/action/viewMonitors${ titleq }"/>">Title <c:if test='${sptype eq "asc"}'><img src="<c:url value="/icons/iconarrwor.gif" />" alt="Asc" /></c:if><c:if test='${sptype eq "desc"}'><img src="<c:url value="/icons/iconarrowup.gif"/>" alt="Desc"/></c:if> </a>
           &nbsp;&nbsp;<a href="<c:url value="/myciteseer/action/viewMonitors${ dateq }"/>">Pub Date</a>
           &nbsp;&nbsp;<a href="<c:url value="/myciteseer/action/viewMonitors${ citeq }"/>">Citations</a>
          </c:if>
          <c:if test='${ psort eq "date" }'>
           <a href="<c:url value="/myciteseer/action/viewMonitors${ titleq }"/>">Title</a>
           &nbsp;&nbsp;<a href="<c:url value="/myciteseer/action/viewMonitors${ dateq }"/>">Pub Date <c:if test='${sptype eq "asc"}'><img src="<c:url value="/icons/iconarrwor.gif"/>" alt="Asc" /></c:if><c:if test='${sptype eq "desc"}'><img src="<c:url value="/icons/iconarrowup.gif"/>" alt="Desc" /></c:if></a>
           &nbsp;&nbsp;<a href="<c:url value="/myciteseer/action/viewMonitors${ citeq }"/>">Citations</a>
          </c:if>
          <c:if test='${ psort eq "cite" }'>
           <a href="<c:url value="/myciteseer/action/viewMonitors${ titleq }"/>">Title</a>
           &nbsp;&nbsp;<a href="<c:url value="/myciteseer/action/viewMonitors${ dateq }"/>">Pub Date</a>
           &nbsp;&nbsp;<a href="<c:url value="/myciteseer/action/viewMonitors${ citeq }"/>">Citations <c:if test='${sptype eq "asc"}'><img src="<c:url value="/icons/iconarrwor.gif"/>" alt="Asc" /></c:if><c:if test='${sptype eq "desc"}'><img src="<c:url value="/icons/iconarrowup.gif"/>" alt="Desc" /></c:if></a>
          </c:if>
         </div>
      </div> <!-- End Introduction -->
    </div> <!-- End primary_content -->
</div> <!-- End column-one-content -->
 </div> <!-- End column-one (center column) -->
 <div class="column-two-sec"> <!-- Left column -->
   <%@ include file="IncludeLeftSubscriptions.jsp" %>
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
 var elt = document.getElementById("subscriptions_tab");
 elt.setAttribute("class", "active");
 elt.setAttribute("className", "active");

 elt = document.getElementById("view_monitors");
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
