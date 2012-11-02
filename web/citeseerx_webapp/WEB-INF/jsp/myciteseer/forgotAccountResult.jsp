<%@ include file="IncludeTop.jsp" %>

<div class="mypagecontent"> <!-- contains left and center content -->
 <div id="login_content"> <!-- Main content -->
  <div> <!-- contains left and right columns and login information -->
   <c:if test="${ error }">
    <h3><c:out value="${ errMsg }"/></h3>
    <h3>You entered "<c:out value="${ email }"/>"</h3>
   </c:if>
   <c:if test="${ ! error }">
    <h3>Your password has been reset and a reminder has been sent to <c:out value="${ email }"/></h3>
   </c:if>
  </div> <!-- contains left and right columns and login information -->
 </div> <!-- End contains login content -->
</div> <!-- End mypagecontent -->
</div> <!-- End center_content -->
<script type="text/javascript">
<!--
if (window != top) 
 top.location.href = location.href;
function sf() {
 var elt = document.getElementById("email");
 elt.focus();
}
function sa(){
 var elt = document.getElementById("profile_tab");
 elt.setAttribute("class", "active");
 elt.setAttribute("className", "active");
}
// -->
</script>

<%@ include file="../shared/IncludeBottom.jsp" %>