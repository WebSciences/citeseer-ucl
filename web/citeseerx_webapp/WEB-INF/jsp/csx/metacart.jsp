<%@ include file="shared/IncludeTop.jsp" %>
  <div id="primary_content">
   <h1 class="primaryheader">MetaCart</h1>
   <div class="information_bar char_increased">
    <c:if test="${ ! empty docs }">
     <div class="left_content">Download: <a href="<c:url value="/metacart?dl=bibtex"/>">BibTeX</a> | <a href="<c:url value="/metacart?dl=refbib"/>">Refer/BibIX</a></div>
	 <div class="right_content"><form action="<c:url value="/metacart"/>" method="post"><button class="button csx" name="del" value="all">Clear</button></form></div>
	 <div class="parent_spacer"></div>
    </c:if>
   </div>
   <div id="main_content">
    <c:if test="${ error }">
     <h3><c:out value="${ errMsg }"/></h3>
    </c:if>
    <c:if test="${ empty docs }">
     <h3>Your cart currently contains no items</h3>
    </c:if>
    <c:if test="${ ! empty docs }">
<c:forEach var="doc" items="${ docs }">
<ul class="cart_item nofrills_list char_increased">
<li>
 <c:if test="${ doc.inCollection }">
  <a class="remove" href="<c:url value="/viewdoc/summary?cid=${ doc.cluster }"/>">
 </c:if>
 <c:if test="${ ! doc.inCollection }">
  <a class="remove" href="<c:url value="/showciting?cid=${ doc.cluster }"/>">
 </c:if>
 <c:if test="${ ! empty doc.title }"><c:out value="${ doc.title }"/></c:if><c:if test="${ empty doc.title }">unknown title</c:if></a><c:if test="${ doc.year > 0 }"> (<c:out value="${ doc.year }"/>)</c:if></li>
<li class="indent">by <c:if test="${ ! empty doc.authors }"><c:out value="${ doc.authors }"/></c:if><c:if test="${ empty doc.authors }">unknown authors</c:if></li>
<c:if test="${ ! empty doc.venue }"><li class="indent"><c:out value="${ doc.venue }"/></li></c:if>
<li class="indent"><form action="<c:url value="/metacart"/>" method="post"><button class="button csx" name="del" value="<c:out value="${ doc.cluster }"/>">Delete</button></form></li>
</ul>
</c:forEach>
    </c:if>
   </div> <!-- end main_content -->
   <div class="information_bar char_increased">
    <c:if test="${ ! empty docs }">
     <div class="left_content">Download: <a href="<c:url value="/metacart?dl=bibtex"/>">BibTeX</a> | <a href="<c:url value="/metacart?dl=refbib"/>">Refer/BibIX</a></div>
	 <div class="right_content"><form action="<c:url value="/metacart"/>" method="post"><button class="button csx" name="del" value="all">Clear</button></form></div>
	 <div class="parent_spacer"></div>
    </c:if>
   </div>   
  </div> <!-- end primary_content -->
<script type="text/javascript">
<!--
if (window != top) 
top.location.href = location.href;
function sf(){document.forms['<c:if test='${ empty param.t || param.t != "auth" }'>doc_search_form</c:if><c:if test='${ (! empty param.t) && param.t == "auth" }'>auth_search_form</c:if>'].q.focus();}
// -->
</script>
<%@ include file="../shared/IncludeBottom.jsp" %>