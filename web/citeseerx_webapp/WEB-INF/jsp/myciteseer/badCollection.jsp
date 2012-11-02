<%@ include file="IncludeTop.jsp" %>
<div id="main-body" class="clearfix"> <!-- contains left and center content -->
  <div id="center-content"> <!-- Main content -->
    <div class="inside"> <!-- to give some room between columns -->
      <div class="content_box">
        <div class="error">
	      <h1>Collection Error</h1>
	      <h3>No Collection with id: "<c:out value="${cid}"/>"<br/>
	        The supplied collection identifier does not match any collection in our repository.</h3>
	    </div>
      </div> <!-- End content_box -->
    </div> <!-- end of inside -->
  </div> <!-- end of center-content -->
  <!-- Left Side bar -->
  <%@ include file="IncludeLeftCollections.jsp" %>
</div> <!-- end of main-body -->
<div id="right-sidebar"> <!-- contains right content -->
  <div class="inside"> <!-- to give some room between columns -->
  </div> <!-- end of inside -->
</div> <!-- end of right-sidebar -->
<script type="text/javascript">
    <!--
      if (window != top) 
        top.location.href = location.href;
      function sf(){}
      function sa(){
        var elt = document.getElementById("collections_tab");
        elt.setAttribute("class", "active");
        elt.setAttribute("className", "active");
      }
    // -->
</script>
<%@ include file="../shared/IncludeBottom.jsp" %>