<%@ include file="../IncludeTop.jsp" %>
<div class="mypagecontent">
<div class="columns-float-sec">
<div class="column-one-sec"> <!-- center column -->
 <div class="column-one-content clearfix">
  <form method="get" 
        action="<c:url value="/myciteseer/action/admin/editConfiguration"/>" 
        class="wform labelsLeftAligned hintsTooltip">
   <fieldset class="">
    <legend>Edit Configuration</legend>
    <table class="datatable">
     <thead>
      <tr><th>Parameter</th><th>Enabled</th></tr>
     </thead>
     <tbody>
       <tr class="even">
        <td><label class="preField" for="setaccounts">New Accounts</label></td>
        <td>
         <c:if test="${ setaccounts }"><input type="checkbox" name="setaccounts" id="setaccounts" checked="checked"/></c:if>
         <c:if test="${ !setaccounts }"><input type="checkbox" name="setaccounts" id="setaccounts"/></c:if>
        </td> 
       </tr>
       <tr class="odd">
        <td><label class="preField" for="seturlsubmission">URL Submissions</label></td>
        <td>
         <c:if test="${ seturlsubmission }"><input type="checkbox" name="seturlsubmission" id="seturlsubmission" checked="checked"/></c:if>
         <c:if test="${ !seturlsubmission }"><input type="checkbox" name="seturlsubmission" id="seturlsubmission"/></c:if>
        </td> 
       </tr>
       <tr class="even">
        <td><label class="preField" for="setcorrections">Corrections</label></td>
        <td>
         <c:if test="${ setcorrections }"><input type="checkbox" name="setcorrections" id="setcorrections" checked="checked"/></c:if>
         <c:if test="${ !setcorrections }"><input type="checkbox" name="setcorrections" id="setcorrections"/></c:if>
        </td>
       </tr>
       <tr class="odd">
        <td><label class="preField" for="setgroups">Groups</label></td>
        <td>
         <c:if test="${ setgroups }"><input type="checkbox" name="setgroups" id="setgroups" checked="checked"/></c:if>
         <c:if test="${ !setgroups }"><input type="checkbox" name="setgroups" id="setgroups"/></c:if>
        </td> 
       </tr>
       <tr class="even">
        <td><label class="preField" for="setpeoplesearch">People Search</label></td>
        <td>
         <c:if test="${ setpeoplesearch }"><input type="checkbox" name="setpeoplesearch" id="setpeoplesearch" checked="checked"/></c:if>
         <c:if test="${ !setpeoplesearch }"><input type="checkbox" name="setpeoplesearch" id="setpeoplesearch"/></c:if>
        </td>
      </tr>
      <tr class="odd">
        <td><label class="preField" for="setpersonalportal">Personal Portal</label></td>
        <td>
         <c:if test="${ setpersonalportal }"><input type="checkbox" name="setpersonalportal" id="setpersonalportal" checked="checked"/></c:if>
         <c:if test="${ !setpersonalportal }"><input type="checkbox" name="setpersonalportal" id="setpersonalportal"/></c:if>
        </td> 
       </tr>
     </tbody>
    </table>
    <input type="hidden" name="type" value="update"/>
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

 elt = document.getElementById("edit_configuration");
 elt.setAttribute("class", "active");
 elt.setAttribute("className", "active");
}
// -->
</script>
<%@ include file="../../shared/IncludeBottom.jsp" %>