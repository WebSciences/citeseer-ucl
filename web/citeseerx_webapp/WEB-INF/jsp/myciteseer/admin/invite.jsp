<%@ include file="../IncludeTop.jsp" %>
<div class="mypagecontent">
<div class="columns-float-sec">
<div class="column-one-sec"> <!-- center column -->
 <div class="column-one-content clearfix">
  <c:if test="${ error }"><c:out value="${ errMsg }"/></c:if>
  <c:if test="${ ! error && ! empty msg }"><c:out value="${ msg }"/></c:if>
  <form method="post" 
        action="<c:url value="/myciteseer/action/admin/invite"/>" 
        class="wform labelsLeftAligned hintsTooltip">
   <fieldset class="">
    <legend>Invite Users</legend>
    <div>Enter one or more email addresses below to invite users to join the system.</div>
    <div class="oneField">
     <textarea class="invite_field" cols="1" rows="3" name="invite"></textarea>
    </div>
    <div>Enter addresses you would like to CC.</div>
    <div class="oneField">
     <input type="text" class="invite_field" name="alsocc" value="giles@ist.psu.edu"/>
    </div>
    <div>Enter a supplementary message below.</div>
    <div class="oneField">
     <textarea class="invite_field" name="message" cols="1" rows="7">
You have been invited to join the CiteSeerX alpha preview!  Until we get the feedback system installed, please email Isaac Councill (icouncill@ist.psu.edu) and Lee Giles (giles@ist.psu.edu) directly with feedback.

Expect lots of additions and upgrades in the next few weeks, including lots of new documents.

Enjoy!
   Isaac
     </textarea>
    </div>
    <div class="oneField">
     <label>CC me </label><input type="checkbox" name="cc" checked="checked" />
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

 elt = document.getElementById("invite_users");
 elt.setAttribute("class", "active");
 elt.setAttribute("className", "active");
}
// -->
</script>
<%@ include file="../../shared/IncludeBottom.jsp" %>