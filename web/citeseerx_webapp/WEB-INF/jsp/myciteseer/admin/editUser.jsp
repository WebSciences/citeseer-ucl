<%@ include file="../IncludeTop.jsp" %>
<div class="mypagecontent">
<div class="columns-float-sec">
<div class="column-one-sec"> <!-- center column -->
 <div class="column-one-content clearfix">
  <c:if test="${ error }"><div><c:out value="${ errMsg }"/></div></c:if>
  <form method="get" 
        action="<c:url value="/myciteseer/action/admin/editUser"/>" 
        class="wform labelsLeftAligned hintsTooltip">
   <fieldset class="">
    <legend>Edit User</legend>
    <table class="metatable">
     <tbody>
      <tr><td>User ID:</td><td><c:out value="${ editaccount.username }"/></td></tr>
      <tr><td>First Name:</td><td><c:out value="${ editaccount.firstName }"/></td></tr>
      <tr><td>Middle Name:</td><td><c:out value="${ editaccount.middleName }"/></td></tr>
      <tr><td>Last Name:</td><td><c:out value="${ editaccount.lastName }"/></td></tr>
     </tbody>
    </table>
    <div class="oneField">
     <label class="preField">Enabled:</label>
     <c:if test="${ enabled }"><input type="checkbox" name="setenabled" checked="checked" /></c:if>
     <c:if test="${ !enabled }"><input type="checkbox" name="setenabled" /></c:if>
    </div>
    <div class="oneField">
     <label class="preField">Administrator:</label>
     <c:if test="${ admin }"><input type="checkbox" name="setadmin" checked="checked" /></c:if>
     <c:if test="${ !admin }"><input type="checkbox" name="setadmin" /></c:if>
     <input type="hidden" name="uid" value="<c:out value="${ editaccount.username }"/>"/>
     <input type="hidden" name="type" value="update"/>
    </div>
    <div class="actions">
     <input type="submit" class="primaryAction" id="submit-" name="submit" value="submit" />
    </div>
   </fieldset>
  </form>

</div> <!-- End column-one-content -->
 </div> <!-- End column-one (center column) -->
 <div class="column-two-sec"> <!-- Left column -->
   <%@ include file="IncludeLeftAdmin.jsp" %>
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
 var elt = document.getElementById("admin_tab");
 elt.setAttribute("class", "active");
 elt.setAttribute("className", "active");

 elt = document.getElementById("edit_user");
 elt.setAttribute("class", "active");
 elt.setAttribute("className", "active");
}
// -->
</script>
<%@ include file="../../shared/IncludeBottom.jsp" %>