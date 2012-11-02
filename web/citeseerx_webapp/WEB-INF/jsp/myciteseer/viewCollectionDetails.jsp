<%@ include file="IncludeTop.jsp" %>
<script type="text/javascript" src="<c:url value="/js/mooPrompter.js"/>"></script>
<div class="mypagecontent">
<div class="columns-float-sec">
<div class="column-one-sec"> <!-- center column -->
 <div class="column-one-content clearfix">
  <h2 class="char_headers"><c:out value="${collection.name}"/></h2>
  <div id="tabs_container">
   <ul class="mootabs_title">
    <li title="Papers"><div>Papers</div></li>
    <li title="Notes"><div>Notes</div></li>
   </ul>
   <div id="Papers" class="mootabs_panel">
    <div class="panel_content">
     <div id="introduction">
      <div class="padded">Order By:
        <c:if test='${ psort eq "title" }'>
         <a href="<c:url value="/myciteseer/action/viewCollectionDetails${ titleq }"/>">Title <c:if test='${sptype eq "asc"}'><img src="<c:url value="/icons/iconarrwor.gif" />" alt="Asc" /></c:if><c:if test='${sptype eq "desc"}'><img src="<c:url value="/icons/iconarrowup.gif"/>" alt="Desc"/></c:if> </a>
         &nbsp;&nbsp;<a href="<c:url value="/myciteseer/action/viewCollectionDetails${ dateq }"/>">Pub Date</a>
         &nbsp;&nbsp;<a href="<c:url value="/myciteseer/action/viewCollectionDetails${ citeq }"/>">Citations</a>
        </c:if>
        <c:if test='${ psort eq "date" }'>
         <a href="<c:url value="/myciteseer/action/viewCollectionDetails${ titleq }"/>">Title</a>
         &nbsp;&nbsp;<a href="<c:url value="/myciteseer/action/viewCollectionDetails${ dateq }"/>">Pub Date <c:if test='${sptype eq "asc"}'><img src="<c:url value="/icons/iconarrwor.gif"/>" alt="Asc" /></c:if><c:if test='${sptype eq "desc"}'><img src="<c:url value="/icons/iconarrowup.gif"/>" alt="Desc" /></c:if></a>
         &nbsp;&nbsp;<a href="<c:url value="/myciteseer/action/viewCollectionDetails${ citeq }"/>">Citations</a>
        </c:if>
        <c:if test='${ psort eq "cite" }'>
         <a href="<c:url value="/myciteseer/action/viewCollectionDetails${ titleq }"/>">Title</a>
         &nbsp;&nbsp;<a href="<c:url value="/myciteseer/action/viewCollectionDetails${ dateq }"/>">Pub Date</a>
         &nbsp;&nbsp;<a href="<c:url value="/myciteseer/action/viewCollectionDetails${ citeq }"/>">Citations <c:if test='${sptype eq "asc"}'><img src="<c:url value="/icons/iconarrwor.gif"/>" alt="Asc" /></c:if><c:if test='${sptype eq "desc"}'><img src="<c:url value="/icons/iconarrowup.gif"/>" alt="Desc" /></c:if></a>
        </c:if>
       </div>
     </div> <!-- End Introduction -->
     <div class="information_bar char_increased">
      <c:if test="${ ! empty previouspageparams }"><a href="<c:url value="/myciteseer/action/viewCollectionDetails${ previouspageparams }"/>">&#8592; Previous Page&nbsp;</a></c:if>Documents found: <c:out value="${npresults}"/> papers.
      Page <c:out value="${ppn}" /> of <c:out value="${tppn}" />
      <c:if test="${ ! empty nextpageparams }"><a href="<c:url value="/myciteseer/action/viewCollectionDetails${ nextpageparams }"/>">Next Page &#8594;</a></c:if>
     </div> <!-- End Information Bar -->
     <div class="searchresult">    
       <c:if test="${!empty papers}">
        <c:forEach var="paper" items="${papers}">
         <div class="blockhighlight_box"> <!-- List Item -->
          <ul class="blockhighlight">
            <li class="padded"><a class="paper-tips remove doc_details" href="<c:url value="/viewdoc/summary?doi=${paper.doc.doi}"/>" title="<c:out value="${paper.doc.title}"/>::&lt;strong&gt;Abstract:&lt;/strong&gt; <c:out value="${paper.doc.abstract}"/>&lt;br /&gt;&lt;strong&gt;Venue:&lt;/strong&gt; <c:out value="${paper.doc.venue}"/>"><em class="title"><c:out value="${paper.doc.title}"/></em></a>
              <c:if test="${ ! empty paper.coins}">
                <span class="Z3988" title="<c:out value="${paper.coins}" />">&nbsp;</span>
              </c:if>
            </li>
            <li class="author char6 padded">by <c:out value="${paper.doc.authors}"/></li>
            <c:if test="${paper.doc.year > 0}"><li class="author char6 padded">Year: <c:out value="${paper.doc.year}"/></li></c:if>
            <li class="char_increased padded"><c:if test="${ paper.doc.ncites > 0 }"><a class="citation remove" href="<c:url value="/showciting?cid=${ paper.doc.cluster }"/>" title="number of citations">Cited by <c:out value="${ paper.doc.ncites }"/> (<c:out value="${ paper.doc.selfCites }"/> self)</a> &ndash;</c:if>
              <span class="actionspan" onclick="addToCartProxy(<c:out value="${paper.doc.cluster}"/>)">Add To MetaCart</span> <span id="cmsg_<c:out value="${ paper.doc.cluster }"/>" class="cartmsg"></span>
            </li>
            <c:if test="${!empty paper.notes}">
              <li class="char_increased padded" >
               <div class="pnotes_container" id="pnotes_<c:out value="${paper.doc.doi}"/>">
                 <h2>Notes</h2>
                 <br />
                 <table class="datatable">
                  <c:forEach var="pNote" items="${paper.notes}">
                   <tr>
                    <td><c:out value="${pNote.note}"/></td>
                    <td>
                     <a href="<c:url value="/myciteseer/action/editPaperNote?pid=${paper.doc.doi}&amp;cid=${collection.collectionID}&amp;nid=${pNote.noteID}"/>" title="Edit note">Edit</a>&nbsp;&ndash;
                     <a href="<c:url value="/myciteseer/action/deletePaperNote?doi=${paper.doc.doi}&amp;cid=${collection.collectionID}&amp;nid=${pNote.noteID}"/>" title="Delete note" class="delete-note">Delete</a>
                    </td>
                   </tr>
                  </c:forEach>
                 </table>
               </div>
              </li>
            </c:if>
           <li class="char_increased">
            <a href="<c:url value="deletePaperCollection?pid=${paper.doc.doi}&amp;cid=${collection.collectionID}"/>" title="Delete paper from collection" class="delete-paper">Delete Paper</a>&nbsp;&ndash;
            <a href="<c:url value="addPaperNote?pid=${paper.doc.doi}&amp;cid=${collection.collectionID}"/>" title="Add note to this paper">Add Note</a>
            <c:if test="${!empty paper.notes}">
             &nbsp;&ndash;&nbsp;<a href="#" class="toggle_pnotes" id="tpn_<c:out value="${paper.doc.doi}"/>" title="Show/Hide Notes">Show Notes</a>
            </c:if>
           </li>
          </ul>
         </div> <!-- End List Item -->
        </c:forEach>
       </c:if>
       <c:if test="${empty papers}"><span class="char_increased">No papers have been added to this collection.</span></c:if>
     </div> <!-- End Results -->
     <div class="information_bar char_increased">
      <c:if test="${ ! empty previouspageparams }"><a href="<c:url value="/myciteseer/action/viewCollectionDetails${ previouspageparams }"/>">&#8592; Previous Page&nbsp;</a></c:if>Documents found: <c:out value="${npresults}"/> papers.
      Page <c:out value="${ppn}" /> of <c:out value="${tppn}" />
      <c:if test="${ ! empty nextpageparams }"><a href="<c:url value="/myciteseer/action/viewCollectionDetails${ nextpageparams }"/>">Next Page &#8594;</a></c:if>
     </div> <!-- End Information Bar -->
     <div id="conclusion">
      <div class="padded">Order By:
        <c:if test='${ psort eq "title" }'>
         <a href="<c:url value="/myciteseer/action/viewCollectionDetails${ titleq }"/>">Title <c:if test='${sptype eq "asc"}'><img src="<c:url value="/icons/iconarrwor.gif" />" alt="Asc" /></c:if><c:if test='${sptype eq "desc"}'><img src="<c:url value="/icons/iconarrowup.gif"/>" alt="Desc"/></c:if> </a>
         &nbsp;&nbsp;<a href="<c:url value="/myciteseer/action/viewCollectionDetails${ dateq }"/>">Pub Date</a>
         &nbsp;&nbsp;<a href="<c:url value="/myciteseer/action/viewCollectionDetails${ citeq }"/>">Citations</a>
        </c:if>
        <c:if test='${ psort eq "date" }'>
         <a href="<c:url value="/myciteseer/action/viewCollectionDetails${ titleq }"/>">Title</a>
         &nbsp;&nbsp;<a href="<c:url value="/myciteseer/action/viewCollectionDetails${ dateq }"/>">Pub Date <c:if test='${sptype eq "asc"}'><img src="<c:url value="/icons/iconarrwor.gif"/>" alt="Asc" /></c:if><c:if test='${sptype eq "desc"}'><img src="<c:url value="/icons/iconarrowup.gif"/>" alt="Desc" /></c:if></a>
         &nbsp;&nbsp;<a href="<c:url value="/myciteseer/action/viewCollectionDetails${ citeq }"/>">Citations</a>
        </c:if>
        <c:if test='${ psort eq "cite" }'>
         <a href="<c:url value="/myciteseer/action/viewCollectionDetails${ titleq }"/>">Title</a>
         &nbsp;&nbsp;<a href="<c:url value="/myciteseer/action/viewCollectionDetails${ dateq }"/>">Pub Date</a>
         &nbsp;&nbsp;<a href="<c:url value="/myciteseer/action/viewCollectionDetails${ citeq }"/>">Citations <c:if test='${sptype eq "asc"}'><img src="<c:url value="/icons/iconarrwor.gif"/>" alt="Asc" /></c:if><c:if test='${sptype eq "desc"}'><img src="<c:url value="/icons/iconarrowup.gif"/>" alt="Desc" /></c:if></a>
        </c:if>
       </div>
     </div> <!-- End Introduction -->
    </div> <!-- End content -->
   </div> <!-- End Papers - mootabs_panel -->
   <div id="Notes" class="mootabs_panel">
    <div class="panel_content">
     <div class="searchresult">
       <div class="padded">&nbsp;</div>
       <c:if test="${!empty collectionNotes}">
         <c:forEach var="note" items="${collectionNotes}">
          <div class="blockhighlight_box"> <!-- List Item -->
           <ul class="blockhighlight">
            <li class="char_increased padded"><c:out value="${note.note}"/></li>
            <li class="char_increased">
             <a href="<c:url value="/myciteseer/action/editCollectionNote?cid=${note.collectionID}&amp;nid=${note.noteID}"/>" title="Edit note">Edit</a>&nbsp;&ndash;
             <a href="<c:url value="/myciteseer/action/deleteCollectionNote?cid=${note.collectionID}&amp;nid=${note.noteID}"/>" title="Delete note" class="delete-note">Delete</a>
            </li>
           </ul>
          </div> <!-- End List Item -->
         </c:forEach>
       </c:if>
       <c:if test="${empty collectionNotes}">
        <div class="padded">&nbsp;</div>
        <span class="char_increased">No notes have been added to this collection.</span>
       </c:if>
       <p class="char_increased"><a href="<c:url value="addCollectionNote?cid=${collection.collectionID}"/>" title="Add note to this collection">Add Note</a></p>
     </div> <!-- End Results -->
    </div> <!-- End content -->
   </div> <!-- End Notes - mootabs_panel -->
  </div> <!-- End tabs_container -->
</div> <!-- End column-one-content -->
 </div> <!-- End column-one (center column) -->
 <div class="column-two-sec"> <!-- Left column -->
   <%@ include file="IncludeLeftCollections.jsp" %>
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
 var elt = document.getElementById("collections_tab");
 elt.setAttribute("class", "active");
 elt.setAttribute("className", "active");
}     

window.addEvent('domready', function(){
 /* paperTips */
 var paperTips = new Tips($$('.paper-tips'), {
  maxTitleChars:150,
  className:'paper-tool'
 });
 /* collection_tabs */
 var collectionTabs = new mootabs('tabs_container', {
  width:'100%',
  changeTransition: 'none',
  mouseOverClass:'over'
 });
 
 var elt = document.getElementById("view_collections");
 elt.setAttribute("class", "active");
 elt.setAttribute("className", "active");

 /* Delete papers confirmation dialog */
 DelConfirm = new mooPrompter({
    name: 'delete-conf',
    BoxStyles: {
            'width': 300
    }                  
 });

 $$('a.delete-paper').each(function(elem) {
    elem.addEvent('click', function(event) {
        var event = new Event(event);
        event.stop();
        
        // Present the confirmation dialog.
        DelConfirm.confirm("Are you sure you want to delete this paper and its related items?", {
            textBoxBtnOk: 'Yes',
            textBoxBtnCancel: 'No',
            onComplete: function(returnValue) {
            if (returnValue) {
                top.location.href = elem.href;
                
            }
        }});
    });
 }); 
 
 $$('a.delete-note').each(function(elem) {
    elem.addEvent('click', function(event) {
        var event = new Event(event);
        event.stop();
        
        // Present the confirmation dialog.
        DelConfirm.confirm("Are you sure you want to delete this note?", {
            textBoxBtnOk: 'Yes',
            textBoxBtnCancel: 'No',
            onComplete: function(returnValue) {
            if (returnValue) {
                top.location.href = elem.href;
                
            }
        }});
    });
 });
 
 $$('div.blockhighlight_box').each(function(elem) {
  // add offblock class to each collection item
  elem.addClass('offblock');
 
  // Add mouse over and out events to each collection item.
  elem.addEvent('mouseover', function(event) {
   var event = new Event(event);
   event.stop();
   elem.removeClass('offblock');
   elem.addClass('overblock');
  });
 
 elem.addEvent('mouseout', function(event) {
   var event = new Event(event);
   event.stop();
   elem.removeClass('overblock');
   elem.addClass('offblock');
  });
 });
 
 // Add paper notes show/hide functionality for every paper with notes.
 $$('a.toggle_pnotes').each(function(elem) {
 
   // Obtain the notes div.
   var notesDivId = 'pnotes' +
    elem.id.substring(elem.id.indexOf('_'));
   
   // Creates the slider
   var mySlider = new Fx.Slide(notesDivId);
   mySlider.slideOut();
 
   elem.addEvent('click', function(event) {
    var event = new Event(event);
    event.stop();
    if (elem.text == 'Show Notes') {
     elem.setText('Hide Notes');
    }else{
     elem.setText('Show Notes');;
    }
    mySlider.toggle();
   });
 });
 
});
// -->
</script>
<%@ include file="../shared/IncludeBottom.jsp" %>