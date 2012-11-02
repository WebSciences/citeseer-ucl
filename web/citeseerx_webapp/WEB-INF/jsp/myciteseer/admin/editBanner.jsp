<%@ include file="../IncludeTop.jsp" %>
<div class="mypagecontent">
<div class="columns-float-sec">
<div class="column-one-sec"> <!-- center column -->
 <div class="column-one-content clearfix">
  <form method="post" 
        action="<c:url value="/myciteseer/action/admin/editBanner"/>" 
        class="wform labelsLeftAligned hintsTooltip">
   <fieldset class="">
    <legend>Edit Site Banner</legend>
    <spring:bind path="editBannerForm.banner">
     <div class="oneField">
      <textarea class="banner_edit" cols="1" rows="3"
                id="<c:out value="${status.expression}"/>" 
                name="<c:out value="${status.expression}"/>"><c:out value="${status.value}"/></textarea>
     </div>
    </spring:bind>
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

 elt = document.getElementById("edit_banner");
 elt.setAttribute("class", "active");
 elt.setAttribute("className", "active");
}
// -->
</script>
<%@ include file="../../shared/IncludeBottom.jsp" %>