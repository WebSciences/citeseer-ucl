<%@ include file="IncludeTop.jsp" %>

<div class="mypagecontent"> <!-- contains left and center content -->
  <div id="login_content"> <!-- Main content -->
    <div> <!-- contains left and right columns and login information -->
      <h2>Forgot your username or password?</h2>
      <p class="char_increased">If you have forgotten your username or password, you may have your password
        reset here.  By entering your email address in the text area below and clicking
        "Reset", a new password will be generated for you and your login details will 
        be sent to the email address you enter.</p>
      <p><b>NOTE:</b> You must enter the email address
        that you specified for the account upon registering.</p>
      <form method="post" action="<c:url value="/forgotaccount"/>" class="labelsRightAligned">
	    <fieldset id="mylogin" class="noborder">
          <!-- <legend>Log In</legend> -->                            
          <div class="left-column_content "> <!-- Contains content in left col -->
            <div> <!-- contains left content -->
	          <div class="oneField">
                <label for="email" class="preField">Email:</label> <input type="text" id="email" name="email" size="20"/>
              </div> <!-- end .oneField -->
            </div>
          </div>
        </fieldset>
        <div class="actions para2">
          <input type="submit" class="primaryAction button" id="submit" name="tfa_submitAction" value="Reset" alt="Reset Password" />
  	    </div> <!-- End actions -->
      </form>                                         
    </div> <!-- contains left and right columns and login information -->
  </div> <!-- End contains login content -->
</div> <!-- End mypagecontent -->
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