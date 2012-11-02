<%@ include file="IncludeTop.jsp" %>
<div class="mypagecontent">
  <div class="columns-float-sec">
    <div class="column-one-sec"> <!-- center column -->
      <div class="column-one-content clearfix">
        <div id="center-content"> <!-- Main content -->
          <div class="inside"> <!-- to give some room between columns -->
            <c:if test="${success == 'true'}">
              <h1>Account Activation Successful</h1>
              <br />
              <h3>Welcome, <c:out value="${username}"/><br /><br />
                  You can now <a href="<c:url value="/myciteseer/action/accountHome"/>">log into your account</a>.
              </h3>
            </c:if>
            <c:if test="${success != 'true'}">
              <div class="error">
                <h1>Account Activation Failed</h1>
                <h3>Please check the url that you used to access this page.</h3>
              </div>
            </c:if>
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
  <!--<div id="right-sidebar">--> <!-- contains right content-->
  <!--   <div class="inside">--> <!-- to give some room between columns-->
  <!--  </div>--> <!-- end of inside-->
  <!--</div>--> <!-- end of right-sidebar-->
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
