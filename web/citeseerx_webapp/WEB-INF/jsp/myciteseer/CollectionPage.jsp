<%@ include file="IncludeTop.jsp" %>
<script type="text/javascript" src="<c:url value="/js/mooPrompter.js"/>"></script>
<div class="mypagecontent clearfix">
<div class="columns-float-sec">
<div class="column-one-sec"> <!-- center column -->
 <div class="column-one-content clearfix">
  <c:if test="${error}">
    <div class="error">
      <h3><c:out value="${ errorMsg }"/></h3>
    </div>
  </c:if>
  <c:if test="${!error}">
    <c:if test="${collectionForm.newCollection}">
      <h2 class="char_headers">Create a Collection</h2>
    </c:if>
    <c:if test="${!collectionForm.newCollection}">
      <h2 class="char_headers">Edit a Collection</h2>
    </c:if>
    <div class="content">
      <c:if test="${collectionForm.newCollection}">
        <form method="post" 
             action="<c:url value="/myciteseer/action/addCollection"/>" 
             id="collection_form" class="wform labelsLeftAligned hintsTooltip">
      </c:if>
      <c:if test="${!collectionForm.newCollection}">
        <form method="post" 
              action="<c:url value="/myciteseer/action/editCollection"/>" 
             id="collection_form" class="wform labelsLeftAligned hintsTooltip">
      </c:if>
      <fieldset id="collection" class="">
        <legend>Collection</legend>
        <spring:bind path="collectionForm.collection.name">
          <div class="oneField">
            <label for="<c:out value="${status.expression}"/>" class="preField">Name&nbsp;
              <span class="reqMark">*</span>
            </label>
            <input type="text" size="40" 
                   id="<c:out value="${status.expression}"/>" 
                   name="<c:out value="${status.expression}"/>"
                   value="<c:out value="${status.value}"/>" 
                   <c:if test="${empty status.errorMessage}">class="required"</c:if>
                   <c:if test="${!empty status.errorMessage}">class="required errFld"</c:if>
            />
            <div class="field-hint-inactive" id="<c:out value="${status.expression}"/>-H">
              <span>Example: My Papers</span>
            </div>
            <br />
            <span class="errMsg" id="<c:out value="${status.expression}"/>-E">
              <c:out value="${status.errorMessage}"/>
            </span>
          </div>
        </spring:bind>
        <div class="oneField">
          <spring:bind path="collectionForm.collection.description">
            <label for="<c:out value="${status.expression}"/>" class="preField">Description</label>
              <textarea cols="40" rows="4" id="<c:out value="${status.expression}"/>" 
                        name="<c:out value="${status.expression}"/>" 
                        <c:if test="${empty status.errorMessage}">class=""</c:if>
                        <c:if test="${!empty status.errorMessage}">class="errFld"</c:if>
              ><c:out value="${status.value}"/></textarea>
              <div class="field-hint-inactive" id="<c:out value="${status.expression}"/>-H">
                <span>Example: Papers I've authored</span>
              </div>
              <br />
              <span class="errMsg" id="<c:out value="${status.expression}"/>-E">
                <c:out value="${status.errorMessage}"/>
              </span>
            </spring:bind>
          </div>
        </fieldset>
        <c:if test="${collectionForm.addPaper}">
          <spring:bind path="collectionForm.paperID">
            <input type="hidden" 
                   id="<c:out value="${status.expression}"/>" 
                   name="<c:out value="${status.expression}"/>" 
                   value="<c:out value="${collectionForm.paperID}"/>">
          </spring:bind>
        </c:if>
        <c:if test="${!collectionForm.newCollection}">
          <spring:bind path="collectionForm.collection.collectionID">
            <input type="hidden" 
                   id="<c:out value="${status.expression}"/>" 
                   name="<c:out value="${status.expression}"/>" 
                  value="<c:out value="${status.value}"/>">
          </spring:bind>
        </c:if>
        <div class="actions">
          <input type="submit" class="primaryAction" id="submit-" name="submit" value="submit" />
          <c:if test="${!collectionForm.newCollection}">
            <c:if test="${collectionForm.collection.deleteAllowed}">
              &nbsp;
              <a href="<c:url value="/myciteseer/action/deleteCollection?cid=${collectionForm.collection.collectionID}"/>" title="Delete <c:out value="${collection.name}"/>"
                 class="delete-collection">Delete</a>
            </c:if>
          </c:if>
        </div>
      </form>
    </div>
  </c:if>
 </div> <!-- End column-one-content -->
</div> <!-- End column-one (center column) -->
<div class="column-two"> <!-- Left column -->
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

 elt = document.getElementById("add_collection");
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