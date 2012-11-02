<%@ include file="IncludeTop.jsp" %>
<div class="mypagecontent">
<div class="columns-float-sec">
<div class="column-one-sec"> <!-- center column -->
 <div class="column-one-content clearfix">
  <h2 class="char_headers">My Collections</h2>
  <div class="content">
   <form method="post" id="collection_paper_form"
         action="<c:url value="/myciteseer/action/addPaperCollection"/>" 
         class="wform labelsLeftAligned hintsTooltip">
    <fieldset id="collections" class="">
     <legend>Collections</legend>
     <div class="oneField">
      <label class="preField">Paper:</label>
      <label><c:out value="${paperCollectionForm.paperTitle}"/></label><br />
      <spring:bind path="paperCollectionForm.paperCollection.paperID">
       <input type="hidden" id="<c:out value="${status.expression}"/>" 
                            name="<c:out value="${status.expression}"/>" 
                            value="<c:out value="${status.value}"/>">
       <span class="errMsg" id="<c:out value="${status.expression}"/>-E">
        <c:out value="${status.errorMessage}"/>
       </span>
      </spring:bind>
     </div>
     <c:if test="${!empty paperCollectionForm.collections}">
      <div class="oneField">
       <spring:bind path="paperCollectionForm.paperCollection.collectionID">
        <label for="<c:out value="${status.expression}"/>" class="preField">Collections:&nbsp;
         <span class="reqMark">*</span>
        </label>
        <select id="<c:out value="${status.expression}"/>" 
                name="<c:out value="${status.expression}"/>"
                <c:if test="${empty status.errorMessage}">class="required"</c:if>
                <c:if test="${!empty status.errorMessage}">value="<c:out value="${status.value}"/>" class="required errFld"</c:if>
        >
         <option value="">Please select...</option>
          <c:forEach var="collection" items="${paperCollectionForm.collections}">
           <option value="<c:out value="${collection.collectionID}"/>" class=""><c:out value="${collection.name}"/></option>
          </c:forEach>
        </select>
        <div class="field-hint-inactive" id="<c:out value="${status.expression}"/>-H">
         <span>Paper will be included in this collection</span>
        </div><br />
        <span class="errMsg" id="<c:out value="${status.expression}"/>-E">
         <c:out value="${status.errorMessage}"/>
        </span>
       </spring:bind>
      </div>
     </c:if>
    </fieldset>
    <div class="actions">
     <c:if test="${!empty paperCollectionForm.collections}">
      <input type="submit" class="primaryAction" id="submit-" name="submitAction" value="submit">
     </c:if>
     <a href="<c:url value="/myciteseer/action/addCollection?doi=${paperCollectionForm.paperCollection.paperID}"/>" class="secondaryAction" title="Create a new collection and add the paper to it">Create new collection</a>
    </div>
   </form>
  </div>

</div> <!-- End column-one-content -->
 </div> <!-- End column-one (center column) -->
 <div class="column-two-sec"> <!-- Left column -->
  <div class="column-two-content">
   <%@ include file="IncludeLeftCollections.jsp" %>
  </div> <!-- End column-two-content -->
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
// -->
</script>
<%@ include file="../shared/IncludeBottom.jsp" %>