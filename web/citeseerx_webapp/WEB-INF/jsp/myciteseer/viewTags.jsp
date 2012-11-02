<%@ include file="IncludeTop.jsp" %>
<div class="mypagecontent">
<div class="columns-float-sec">
<div class="column-one-sec"> <!-- center column -->
 <div class="column-one-content clearfix">
  <h2 class="char_headers">My Tags</h2>
  <div class="char_increased">
   <c:if test="${ empty tags }">No tags were found for this account.</c:if>
   <c:if test="${ ! empty tags }">
    <ul class="tagcloud">
     <c:forEach var="tag" items="${ tags }">
      <c:if test="${ tag.count < 3 }">
       <li class="tag1">
      </c:if>
      <c:if test="${ tag.count >= 3 && tag.count < 5 }">
       <li class="tag2">
      </c:if>
      <c:if test="${ tag.count >= 5 && tag.count < 8 }">
       <li class="tag3">
      </c:if>
      <c:if test="${ tag.count >= 8 && tag.count < 11 }">
       <li class="tag4">
      </c:if>
      <c:if test="${ tag.count >= 11 && tag.count < 15 }">
       <li class="tag5">
      </c:if>
      <c:if test="${ tag.count >= 15 }">
       <li class="tag6">
      </c:if>      
      <a href="<c:url value="/myciteseer/action/viewTaggedDocs?tag=${ tag.tag }"/>"><c:out value="${ tag.tag }"/></a></li>
     </c:forEach>
    </ul>      
   </c:if>
  </div>

</div> <!-- End column-one-content -->
 </div> <!-- End column-one (center column) -->
 <div class="column-two-sec"> <!-- Left column -->
  <div class="column-two-content"></div> <!-- End column-two-content -->
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
// -->
</script>
<%@ include file="../shared/IncludeBottom.jsp" %>