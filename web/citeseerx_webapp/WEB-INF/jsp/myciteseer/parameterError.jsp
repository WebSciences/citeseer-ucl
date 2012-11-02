<%@ include file="IncludeTop.jsp" %>
<div class="mypagecontent">
  <div class="columns-float-sec">
    <div class="column-one-sec"> <!-- center column -->
      <div class="column-one-content clearfix">
        <div class="error">
          <h1>There was an error interpreting your request</h1>
          <h3><c:out value="${ errMsg }"/></h3>
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
// -->
</script>

<%@ include file="../shared/IncludeBottom.jsp" %>