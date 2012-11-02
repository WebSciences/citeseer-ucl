<%@ include file="IncludeTop.jsp" %>
<div class="mypagecontent">
  <div class="columns-float-sec">
    <div class="column-one-sec"> <!-- center column -->
      <div class="column-one-content clearfix">
        <div id="center-content"> <!-- Main content -->
          <div class="inside"> <!-- to give some room between columns -->
            <h1>Account Activation Required</h1>
            <h3>An email should have been sent to the address you supplied.<br />
                Please refer the email for instructions on how to activate your account.
            </h3>
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
