<%--
  -- This is the Systems home page.
  --
  -- Author: Isaac Councill
  -- Author: Juan Pablo Fernandez Ramirez
  --%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ include file="shared/IncludeTagLibs.jsp" %>

<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
<head profile="http://www.w3.org/2005/11/profile">
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <link rel="stylesheet" href="css/citeseerx2.css" type="text/css" />
  <!-- for mobile devices    <link type="text/css" rel="stylesheet" href="handheldstyle.css" media="handheld" /> -->
  <link rel="shortcut icon" type="image/x-icon" href="favicon.ico" />
  <%@ include file="shared/IncludeSearchLinks.jsp" %>
  <title><fmt:message key="app.name"/></title>
  <script type="text/javascript" src="<c:url value="/js/mootools.js"/>"></script>
  <script type="text/javascript" src="<c:url value="/js/mootabs.js"/>"></script>
  <script type="text/javascript" src="<c:url value="/js/searchbox.js"/>"></script>

  <meta name="keywords" content="CiteSeerX, ResearchIndex, ScienceIndex, CiteSeer, scientific citation index, autonomous citation indexing, scientific literature, computer science, digital library, metadata" />
  <meta name="description" content="Scientific Literature Digital Library incorporating autonomous citation indexing, awareness and tracking, citation context, related document retrieval, similar document identification, citation graph analysis, and query-sensitive document summaries. Advantages in terms of availability, coverage, timeliness, and efficiency. Isaac Councill and C. Lee Giles." />

</head>

<body>
  <div id="page_wrapper" class="clearfix"> <!-- Contains all the divisions (div's) within the page -->
    <!-- Used to minimize the page to 150 pixels in width   <img src="images/160rule.gif" width="150" height="5" /> -->
    
    <div id="meta_nav" class="nav"> <!-- Meta Nav, change if the user is in or out -->
      <% if (mscConfig.getPersonalPortalEnabled()) {%>
      <ul>
        <li class="nav_box">
          <% if (account != null) { %>                  
            <a href="<c:url value="/myciteseer/login"/>">Signed in as <i><%= account.getUsername() %></i></a> |
            <a href="<c:url value="/j_spring_security_logout"/>">Sign Out</a>
          <% } else { %>
            <a href="<c:url value="/myciteseer/login"/>">Sign in to MyCiteSeer<sup>X</sup></a>
          <% } %>
        </li>
      </ul>
      <% } %>
    </div><!-- End meta_nav-->
    <div id="center-content"> <!-- center content -->
      <div id="header" class="clearfix"> <!-- Contains header div -->
        <div id="logo"> <!-- Contains logo div -->
          <img src="<c:url value="/images/blank.gif"/>" alt="<fmt:message key="app.name"/>" />
        </div> <!-- End logo -->
        <h1 class="primaryheader">Scientific Literature Digital Library and Search Engine</h1>
        <%@ include file="csx/searchboxhome.jsp" %>
      </div> <!-- End header -->
      <br/>
      <div class="infobox">
	    <div class="info">Searching over <fmt:formatNumber value="${articles}"/> articles and <fmt:formatNumber value="${citations}"/> citations</div>
        <div class="info">Most Cited: <a href="<c:url value="/stats/articles"/>" title="">Documents</a> &#124; <a href="<c:url value="/stats/citations"/>" title="">Citations</a> &#124; <a href="<c:url value="/stats/authors"/>" title="">Authors</a></div>
        <div class="info"><a href="<c:url value="/stats/venues"/>" title="">Venue Impact Ratings</a></div>
      </div> <!-- End infobox -->

      <%@ include file="mirrors.jsp"  %>
      <%@ include file="sponsors.jsp"  %>  

      <p class="char1 para2"><a  href="http://clgiles.ist.psu.edu/">Contact Us</a> to Sponsor CiteSeer<sup>x</sup></p> <!-- End of char1 para2 -->
    </div> <!-- End of center-content --> 

<script type="text/javascript">
<!--
if (window != top) 
top.location.href = location.href;
function setFocusH(formid){
 var tform = document.forms[formid];
 tform.q.focus();
}
window.addEvent('domready', function(){
 <c:if test='${empty param.t || param.t != "auth"}'>
  /* Set the focus on search box of default tab*/
  $$('#docs_tab form input').filterByAttribute('name', '=', 'q')[0].focus();
 </c:if>
 <c:if test='${ (! empty param.t) && param.t == "auth" }'>
   /* Set the focus on search box of default tab*/
   $$('#auth_tab form input').filterByAttribute('name', '=', 'q')[0].focus();
 </c:if>

});
// -->
</script>
<%@ include file="shared/IncludeBottom.jsp" %>
