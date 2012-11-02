<%--
  -- This page shows the advance search form
  -- Author: Juan Pablo Fernandez Ramirez
  --%>
<%@ include file="shared/IncludeTop2.jsp" %>
 <div id="primary_tabs-n-content" class="clearfix"> <!-- Contains header div -->
  <div id="primary_tabs_container">
   <div id="primary_tabs">
    <ul class="clearfix"><li id="current"><a class="page_tabs remove" href="<c:url value="/advanced_search"/>"><span>Advanced Search</span></a></li></ul>   <!-- remove extra whitespace by coding in-line lists on one line -->           
   </div> <!-- End primary_tabs -->
  </div> <!-- End primary_tabs_container -->
  <div id="primary_content">
   <div id="main_content" class="clearfix">
    <div class="columns-float-sec">
     <div class="column-one-sec"> <!-- center column -->
      <div class="column-one-content clearfix">
       <div id="center_content"> <!-- Main content -->
        <div class="inside"> <!-- to give some room between columns -->
         <spring:bind path="advancedSearch.*">
          <c:forEach var="msg" items="${ status.errorMessages }">
           <span class="errMsg"><c:out value="${ msg }"/></span>
          </c:forEach>
         </spring:bind>
         <form id="advancedSearch" class="wform labelsRightAligned hintsTooltip" method="post" action="<c:url value="/advanced_search"/>">
          <fieldset>
           <legend><fmt:message key="app.name"/> Advanced Search</legend>
           <fieldset>
            <legend>Text Fields</legend>
            <div class="information_bar2">Specify search terms for each metadata field of interest. Values in separate fields will be joined with an "AND".</div>
            <table class="advsearch_table">
             <spring:bind path="advancedSearch.textQuery">
              <tr><td><label for="<c:out value="${ status.expression }"/>" class="prefield">Text:&nbsp;</label></td>
              <td><input name="<c:out value="${ status.expression }"/>" id="<c:out value="${ status.expression }"/>" value="<c:out value="${status.value}"/>" type="text" size="43"/></td></tr>
             </spring:bind>
              
             <spring:bind path="advancedSearch.titleQuery">
              <tr><td><label for="<c:out value="${ status.expression }"/>" class="prefield">Title:&nbsp;</label></td>
              <td><input name="<c:out value="${ status.expression }"/>" id="<c:out value="${ status.expression }"/>" value="<c:out value="${status.value}"/>" type="text" size="43"/></td></tr>
             </spring:bind>
 
             <spring:bind path="advancedSearch.authorQuery">
              <tr><td><label for="<c:out value="${ status.expression }"/>" class="prefield">Author Name:&nbsp;</label></td>
              <td><input name="<c:out value="${ status.expression }"/>" id="<c:out value="${ status.expression }"/>" value="<c:out value="${status.value}"/>" type="text" size="43"/></td></tr>
             </spring:bind>
 
             <spring:bind path="advancedSearch.affilQuery">
              <tr><td><label for="<c:out value="${ status.expression }"/>" class="prefield">Author Affiliation:&nbsp;</label></td>
              <td><input name="<c:out value="${ status.expression }"/>" id="<c:out value="${ status.expression }"/>" value="<c:out value="${status.value}"/>" type="text" size="43"/></td></tr>
             </spring:bind>

             <spring:bind path="advancedSearch.venueQuery">
              <tr><td><label for="<c:out value="${ status.expression }"/>" class="prefield">Publication Venue:&nbsp;</label></td>
              <td><input name="<c:out value="${ status.expression }"/>" id="<c:out value="${ status.expression }"/>" value="<c:out value="${status.value}"/>" type="text" size="43"/></td></tr>
             </spring:bind>

             <spring:bind path="advancedSearch.keywordQuery">
              <tr><td><label for="<c:out value="${ status.expression }"/>" class="prefield">Keywords:&nbsp;</label></td>
              <td><input name="<c:out value="${ status.expression }"/>" id="<c:out value="${ status.expression }"/>" value="<c:out value="${status.value}"/>" type="text" size="43"/></td></tr>
             </spring:bind>

             <spring:bind path="advancedSearch.abstractQuery">
              <tr><td><label for="<c:out value="${ status.expression }"/>" class="prefield">Abstract:&nbsp;</label></td>
              <td><input name="<c:out value="${ status.expression }"/>" id="<c:out value="${ status.expression }"/>" value="<c:out value="${status.value}"/>" type="text" size="43"/></td></tr>
             </spring:bind>
            </table>
           </fieldset>
           <fieldset>
            <legend>Range Criteria</legend>
            <div class="information_bar2"><p>Specify any range criteria, including publication date ranges, minimum number of citations, and whether you wish to include records for which we have no corresponding document file (include citations).</p>
            <p>For date ranges, you may leave either the "From" or "To" field blank in order to find all matching records whose publication year is greater or less than the value you specify, respectively.</p></div>
            <table class="advsearch_table">
             <spring:bind path="advancedSearch.year">
              <tr><td><label for="<c:out value="${status.expression}"/>">Publication Year:&nbsp;</label></td>
              <td><input size="4" maxlength="4" name="<c:out value="${status.expression}"/>" id="<c:out value="${status.expression}"/>" value="<c:out value="${status.value}"/>" type="text"/>
             </spring:bind>
   
             <span class="char_emphasized">OR</span> Range
             <spring:bind path="advancedSearch.yearFrom">
              <label for="<c:out value="${status.expression}"/>">From:&nbsp;</label>
              <input size="4" maxlength="4" name="<c:out value="${status.expression}"/>" id="<c:out value="${status.expression}"/>" value="<c:out value="${status.value}"/>" type="text"/>
             </spring:bind>
   
             <spring:bind path="advancedSearch.yearTo">
              <label for="<c:out value="${status.expression}"/>">To:&nbsp;</label>
              <input size="4" maxlength="4" name="<c:out value="${status.expression}"/>" id="<c:out value="${status.expression}"/>" value="<c:out value="${status.value}"/>" type="text"/>
              </spring:bind>
             </td></tr>
   
             <spring:bind path="advancedSearch.minCitations">
              <tr><td><label for="<c:out value="${status.expression}"/>">Minimum Number of Citations:&nbsp;</label></td>
              <td><input size="4" maxlength="5" name="<c:out value="${status.expression}"/>" id="<c:out value="${status.expression}"/>" value="<c:out value="${status.value}"/>" type="text"/><span class="errMsg" id="<c:out value="${status.expression}"/>-E"><c:out value="${status.errorMessage}"/></span></td></tr>
             </spring:bind>
   
             <spring:bind path="advancedSearch.includeCites">
              <tr><td><label for="<c:out value="${status.expression}"/>">Include Citations?&nbsp;</label></td>
              <td><input type="checkbox" name="<c:out value="${status.expression}"/>" id="<c:out value="${status.expression}"/>" value="1" <c:if test="${ advancedSearch.includeCites == '1' }">checked </c:if>/></td></tr>
             </spring:bind>
            </table>
           </fieldset>
           <fieldset>
            <legend>Sorting Criteria</legend>
            <div class="information_bar2">Select a method by which your results should be sorted.</div>
            <div class="oneField">
             <spring:bind path="advancedSearch.sortCriteria">
              <label for="<c:out value="${status.expression}"/>">Sort by:&nbsp;</label>
              <select name="<c:out value="${status.expression}"/>" id="<c:out value="${status.expression}"/>" >
               <option value="cite">Citations</option>
               <option value="rlv">Relevance</option>
               <option value="date">Date (Descending)</option>
               <option value="ascdate">Date (Ascending)</option>
               <option value="recent">Recency</option>
              </select>
             </spring:bind>
            </div> <!-- End oneField -->
           </fieldset>
          </fieldset>
          <div class="actions">
           <input type="submit" class="primaryAction" id="submit-" name="submitAction" value="Search"/>
          </div>
         </form>
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
   </div> <!-- End main content -->            
  </div> <!-- End primary_content -->
 </div> <!-- End primary_tabs-n-content -->
 <script type="text/javascript">
  <!--
  if (window != top) 
  top.location.href = location.href;
  function sf(){}
  // -->
 </script>
<%@ include file="../shared/IncludeBottom.jsp" %>