<%--
  -- This is the personal portal home page.
  --
  -- Author: Isaac Councill
  -- Author: Juan Pablo Fernandez Ramirez
  --%>
<%@ include file="IncludeTop.jsp" %>
<div class="mypagecontent">
  <div class="columns-float">
    <div class="column-one"> <!-- center column -->
      <div class="column-one-content clearfix">
        <div class="myhome_infocontainer">
          <h2 class="char_floating">Hi <%= account.getFirstName() %></h2>
          <div class="pushdown">
            <div id="box">
	          <p>Welcome to your personal portal into <fmt:message key="app.name"/></p>
	        </div> <!-- End box -->
          </div> <!-- End pushdown -->
          <h2 class="char_headers">Latest News (<a href="<fmt:message key="app.bulletin"/>">See All</a>)</h2>
          <div class="nofrills_list listformating">
            <c:forEach var="item" items="${ newsItems }">
              <div class="blockhighlight_box">
                <ul class="blockhighlight">
                  <li class="padded"><a href="<c:url value="${ item.link }"/>"><em class="title"><c:out value="${ item.title }"/></em></a> [<c:out value="${ item.date }"/>]</li>
                  <li class=""><c:out value="${ item.description }" escapeXml="false"/></li>
                </ul>
              </div> <!-- End blockhighlight_box -->
            </c:forEach>
          </div>
        </div> <!-- End myhome_infocontainer -->
      </div> <!-- End column-one-content -->
    </div> <!-- End column-one (center column) -->
    <div class="column-two"> <!-- Left column -->
      <div class="column-two-content">
      </div> <!-- End column-two-content -->
    </div> <!-- End column-two (Left column) -->
    <div class="box-clear">&nbsp;</div><!-- # needed to make sure column 3 is cleared || but IE5(PC) and OmniWeb don't like it  -->
  </div><!-- End columns-float -->
  <div class="column-three"> <!-- right column -->
    <div class="column-three-content">
      <c:if test="${ peoplesearchenabled }">
        <div class="content_box myhome_content_box clearfix">
          <h2>Searches</h2>
          <%@include file="search/MCSSearchBox.jsp"%>
        </div>
      </c:if>

      <!--
      <div class="content_box myhome_content_box">
        <h2>Statisics</h2>
        <h3>Coming soon...</h3>
      </div> --><!-- End content_box -->
    </div> <!-- End column-three-content -->
  </div> <!-- End column three (right column) -->
  <div class="box-clear">&nbsp;</div><!-- # needed to make sure column 3 is cleared || but IE5(PC) and OmniWeb don't like it  -->
  <div class="nn4clear">&nbsp;</div><!-- # needed for NN4 to clear all columns || not needed by any other browser -->
</div> <!-- End mypagecontent -->
<script type="text/javascript">
<!--
if (window != top) 
 top.location.href = location.href;
function sf() {}
function sa(){
 var elt = document.getElementById("home_tab");
 elt.setAttribute("class", "active");
 elt.setAttribute("className", "active");
 Nifty("div#box","transparent");
}
// -->
</script>
<%@include file="../shared/IncludeBottom.jsp" %>
