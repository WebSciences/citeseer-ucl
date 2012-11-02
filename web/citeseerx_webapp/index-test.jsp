<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@ page contentType="text/html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ page import="edu.psu.citeseerx.myciteseer.web.utils.MCSUtils" %>
<%@ page import="edu.psu.citeseerx.myciteseer.domain.Account" %>

<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
<head profile="http://www.w3.org/2005/11/profile">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <link rel="stylesheet" href="css/citeseerx2.css" type="text/css" />
<!-- for mobile devices    <link type="text/css" rel="stylesheet" href="handheldstyle.css" media="handheld" /> -->
    <link rel="shortcut icon" type="image/x-icon" href="favicon.ico" />
    <link rel="search" title="CiteSeerX" type="application/opensearchdescription+xml" href="<c:url value="/search_plugins/citeseerx_general.xml"/>" />
 <link rel="search" title="CiteSeerX Author" type="application/opensearchdescription+xml" href="<c:url value="/search_plugins/citeseerx_author.xml"/>" />
 <link rel="search" title="CiteSeerX Title" type="application/opensearchdescription+xml" href="<c:url value="/search_plugins/citeseerx_title.xml"/>" />
	<title>CiteSeerX alpha</title>
    <script type="text/javascript" src="<c:url value="/js/mootools.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/js/mootabs.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/js/searchbox.js"/>"></script>

    <meta name="keywords" content="CiteSeerX, ResearchIndex, ScienceIndex, CiteSeer, scientific citation index, autonomous citation indexing, scientific literature, computer science, digital library, metadata" />
    <meta name="description" content="Scientific Literature Digital Library incorporating autonomous citation indexing, awareness and tracking, citation context, related document retrieval, similar document identification, citation graph analysis, and query-sensitive document summaries. Advantages in terms of availability, coverage, timeliness, and efficiency. Isaac Councill and C. Lee Giles." />

</head>

<%
Account account = MCSUtils.getLoginAccount();
%>

<body>
    <div id="page_wrapper" class="clearfix"> <!-- Contains all the divisions (div's) within the page -->
<!-- Used to minimize the page to 150 pixels in width   <img src="images/160rule.gif" width="150" height="5" /> -->
     	<div id="meta_nav" class="nav"> <!-- Meta Nav, change if the user is in or out -->
			<ul>
                <li class="nav_box">
                  <% if (account != null) { %>                  
                    <a href="<c:url value="/myciteseer"/>">Signed in as <i><%= account.getUsername() %></i></a> |
                    <a href="<c:url value="/j_acegi_logout"/>">Sign Out</a>
                  <% } else { %>
                    <a href="<c:url value="/myciteseer"/>">Sign in to MyCiteSeer<sup>X</sup></a>
                  <% } %>
                </li>
<!--                  <li><a href="#" title="Log In to MyCiteSeerX">Sign In</a></li>
                <li><a href="#" title="Create an account to organize your documents and network with your colleagues">About MyCiteSeerX</a></li> -->

            </ul>
        </div><!-- End meta_nav-->
        <div id="center-content"> <!-- center content -->
        <div id="header" class="clearfix"> <!-- Contains header div -->
        	<div id="logo"> <!-- Contains logo div -->
            	<img src="<c:url value="/images/CSxbeta.jpg"/>" alt="CiteSeerXbeta logo" />
            </div> <!-- End logo -->
            <h1 class="primaryheader">Scientific Literature Digital Library and Search Engine</h1>
<%@ include file="/WEB-INF/jsp/csx/searchboxhome.jsp" %>

        </div> <!-- End header -->
        <br/>
         <div class="infobox">
			 <div class="info">Searching over 1,077,967 articles and 20,328,278 citations</div>
             <div class="info">Most Cited: <a href="<c:url value="/stats/articles"/>" title="">Documents</a> &#124; <a href="<c:url value="/stats/citations"/>" title="">Citations</a> &#124; <a href="<c:url value="/stats/authors"/>" title="">Authors</a></div>
             <div class="info"><a href="<c:url value="/stats/venues"/>" title="">Venue Impact Ratings</a></div>
         </div>

<!-- Will comment this Mirrors content box will be put back in once they are ready
			<div class="content_box">
            	<h2>CiteSeer<sup>x</sup>&#64; (this will be commented out until needed)</h2>
                <p class="char1 para1">Mirrors of CiteSeer are available at the following locations:
          		</p>
                <p class="char1 para2">				    
                	<a href="http://citeseer.ittc.ku.edu/" title="University of Kansas">U. of Kansas</a> &#124; <a href="http://citeseer.csail.mit.edu" title="Massachusetts Institute of Technology">MIT</a> &#124; <a href="http://sherry.ifi.unizh.ch" title="University of Z&uuml;rich">U. of Z&uuml;rich</a> &#124; <a href="http://citeseer.comp.nus.edu.sg/cs" title="National University of Singapore">National U. of Singapore</a>
				</p>
  		    </div>
End commenting out the mirrors information -->  
       <div class="infobox">
       <div class="imageinbox"><a class="remove" href="http://www.nsf.gov/" title="Sponsored by National Science Foundation"><img id="nsf" src="images/nsf_logosm.jpg" alt="National Science Foundation logo" /></a> <a class="remove" href="http://research.microsoft.com/" title="Sponsored by Microsoft Research"><img id="msr" src="images/MSR_logo.gif" alt="Microsoft Research logo" /></a>
					</div> <!-- End imageinbox -->
                    <p class="char1 para2"><a  href="http://clgiles.ist.psu.edu/">Contact Us</a> to Sponsor CiteSeer<sup>x</sup></p> <!-- End of char1 para2 -->
       </div>
        </div> <!-- End of center-content --> 
        
        <%@ include file="/WEB-INF/jsp/csx/footerhome.jsp" %>

    </div> <!-- end of page_wrapper -->

<script type="text/javascript">
<!--
if (window != top) 
top.location.href = location.href;
function setFocusH(formid){
 var tform = document.forms[formid];
 tform.q.focus();
}
var load_method = (window.ie ? 'load' : 'domready');
window.addEvent(load_method, function(){
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

</body>
</html>
