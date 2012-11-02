<%@ include file="IncludeTop.jsp" %>
<script type="text/javascript" src="<c:url value="/js/mooPrompter.js"/>"></script>
<div class="mypagecontent">
<div class="columns-float-sec">
<div class="column-one-sec"> <!-- center column -->
 <div class="column-one-content clearfix">
  <div class="content">
   <c:if test="${!empty collections}">
    <table class="datatable">
     <thead>
      <tr>
       <th>Name</th><th>Description</th><th>Views</th>
      </tr>
     </thead>
     <tbody>
     <c:forEach var="collection" items="${collections}" varStatus="status">
      <c:if test="${(status.count%2)==0}">
      <tr class="even">
      </c:if>
      <c:if test="${(status.count%2)!=0}">
      <tr class="odd">
      </c:if>
       <td><c:out value="${collection.name}"/></td>
       <td><c:out value="${collection.description}"/></td>
       <td>
        <a href="<c:url value="/myciteseer/action/viewCollectionDetails?cid=${collection.collectionID}"/>"
           title="View <c:out value="${collection.name}"/> Details">Details</a>
        <c:if test="${collection.deleteAllowed}">&nbsp;
         <a href="<c:url value="/myciteseer/action/editCollection?cid=${collection.collectionID}"/>"
            title="Edit <c:out value="${collection.name}"/>">Edit</a>
         &nbsp;
         <a href="<c:url value="/myciteseer/action/deleteCollection?cid=${collection.collectionID}"/>"
            class="delete-collection" title="Delete <c:out value="${collection.name}"/>">Delete</a>
        </c:if>
        <a href="<c:url value="/myciteseer/action/monitorCollection?cid=${collection.collectionID}"/>"
           title="Add <c:out value="${collection.name}"/> papers to monitor list">Monitor</a>
        <a href="<c:url value="/myciteseer/action/addCollectionMetaCart?cid=${collection.collectionID}"/>"
           title="Add <c:out value="${collection.name}"/> papers to Metadata Cart">Add to Cart</a>
       </td>
      </tr>
     </c:forEach>
     </tbody>
    </table>
   </c:if>
   <c:if test="${empty collections}">No collections were found.</c:if>
  </div> <!-- End content -->
      
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

 elt = document.getElementById("view_collections");
 elt.setAttribute("class", "active");
 elt.setAttribute("className", "active");
}

window.addEvent('domready', function(){

 /* Delete collection note confirmation dialog  */
 DelNoteConfirm = new mooPrompter({
    name: 'delete-conf',
    BoxStyles: {
            'width': 300
    }                  
 });
 
 $$('a.delete-collection').each(function(elem) {
    elem.addEvent('click', function(event) {
        var event = new Event(event);
        event.stop();
        
        // Present the confirmation dialog.
        DelNoteConfirm.confirm("Are you sure you want to delete this collection?", {
            textBoxBtnOk: 'Yes',
            textBoxBtnCancel: 'No',
            onComplete: function(returnValue) {
            if (returnValue) {
                top.location.href = elem.href;
                
            }
        }});
    });
 });

});

// -->
</script>
<%@ include file="../shared/IncludeBottom.jsp" %>