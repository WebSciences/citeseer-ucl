<%@ include file="IncludeTop.jsp" %>
<div class="mypagecontent">
<div class="columns-float-sec">
<div class="column-one-sec"> <!-- center column -->
 <div class="column-one-content clearfix">
  <div id="center-content"> <!-- Main content -->
    <div class="inside"> <!-- to give some room between columns -->
      <div style="width:28em;margin:0 auto;">
      <form method="post" action="<c:url value="/myciteseer/action/changePassword"/>" id="changePassword" class="wform labelsLeftAligned hintsTooltip">
        <fieldset>
          <div class="oneField">
            <spring:bind path="changePasswordForm.suppliedPassword">
              <label for="<c:out value="${status.expression}"/>" class="preField">Current password:</label>
              <input type="password" size=""
                     id="<c:out value="${status.expression}"/>" 
                     name="<c:out value="${status.expression}"/>" 
                     value="<c:out value="${status.value}"/>" 
                     <c:if test="${empty status.errorMessage}">
                         class=""
                       </c:if>  
                       <c:if test="${!empty status.errorMessage}">
                         class="errFld"
                       </c:if>
              />
              <div class="field-hint-inactive" id="<c:out value="${status.expression}"/>-H">
                <span>Enter your current password</span>
              </div><br />
              <span class="errMsg" id="<c:out value="${status.expression}"/>-E">
                <c:out value="${status.errorMessage}"/>
              </span>
            </spring:bind>
          </div>
          <div class="oneField">
            <spring:bind path="changePasswordForm.newPassword">
              <label for="<c:out value="${status.expression}"/>" class="preField">New Password:</label>
              <input type="password" size=""
                     id="<c:out value="${status.expression}"/>"
                     name="<c:out value="${status.expression}"/>" 
                     value="<c:out value="${status.value}"/>"
                     <c:if test="${empty status.errorMessage}">
                         class=""
                       </c:if>  
                       <c:if test="${!empty status.errorMessage}">
                         class="errFld"
                       </c:if>
              />
              <div class="field-hint-inactive" id="<c:out value="${status.expression}"/>-H">
                <span>Enter the new password</span>
              </div><br />
              <span class="errMsg" id="<c:out value="${status.expression}"/>-E">
                <c:out value="${status.errorMessage}"/>
              </span>
            </spring:bind>
          </div>
          <div class="oneField">
            <spring:bind path="changePasswordForm.repeatedPassword">
              <label for="<c:out value="${status.expression}"/>" class="preField">Repeat new password:</label>
              <input type="password" size=""
                     id="<c:out value="${status.expression}"/>"
                     name="<c:out value="${status.expression}"/>" 
                     value="<c:out value="${status.value}"/>"
                     <c:if test="${empty status.errorMessage}">
                         class=""
                       </c:if>  
                       <c:if test="${!empty status.errorMessage}">
                         class="errFld"
                       </c:if>
              />
              <div class="field-hint-inactive" id="<c:out value="${status.expression}"/>-H">
                <span>Repeat your new password. Do not copy/paste</span>
              </div><br />
              <span class="errMsg" id="<c:out value="${status.expression}"/>-E">
                <c:out value="${status.errorMessage}"/>
              </span>
            </spring:bind>
          </div>
        </fieldset>
        <div class="actions">
          <input type="submit" class="primaryAction" id="submit-" name="submitAction" value="Change Password"/>
        </div>
      </form>
      </div>
    </div> <!-- end of inside -->
  </div> <!-- end of center-content -->

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
 var elt = document.getElementById("profile_tab");
 elt.setAttribute("class", "active");
 elt.setAttribute("className", "active");
}
// -->
</script>
    
<%@ include file="../shared/IncludeBottom.jsp" %>
  