<%@ include file="viewdoc_header.jsp" %>
<div id="main_content">  
 <div id="right-sidebar"> <!-- Contains left content -->
  <div class="inside"> <!-- to give some room between columns -->
  </div> <!--End inside -->
 </div> <!-- End of right-sidebar --> 
 <br/>
 <c:if test="${ ! correctionsEnabled }">
   <h3><font color="red">Corrections are currently disabled!</font></h3>
 </c:if>
 <c:if test="${ correctionsEnabled }">
   <c:if test="${ error }">
     <h3><font color="red"><c:out value="${ errMsg }"/></font></h3>
   </c:if>
   <h3>Correct metadata errors for this document (login required)</h3>
   <p class="char_increased">It may be helpful to open the document via the "View/Download" button
   above.</p>
   <br/>
   <p class="char_increased">Fields marked with<span class="reqMark">*</span> are required.</p>
  
   <spring:bind path="correction.*">
    <c:forEach var="error" items="${status.errorMessages}">
     <br/><span class="errMsg"><c:out value="${error}"/></span>
    </c:forEach>
   </spring:bind>
   
   <form id="correctionForm" method="post" action="" class="wform labelsRightAligned">
    <fieldset>
     <legend>Correction Form</legend>
     <span class="oneField">
      <spring:bind path="correction.title">
       <label for="<c:out value="${ status.expression }"/>" class="preField">Title:&nbsp;<span class="reqMark">*</span></label>
       <input type="text" size="80" id="<c:out value="${ status.expression }"/>" name="<c:out value="${ status.expression }"/>" value="<c:out value="${ status.value }"/>" />
       <span class="errMsg" id="<c:out value="${status.expression}"/>-E"><c:out value="${status.errorMessage}"/></span>
      </spring:bind>
     </span>
     <fieldset id="authorList">
      <legend>Authors: Please preserve correct order</legend>
      <div id="repeat_block">
      <fieldset id="author_0" class="repeat">
        <span class="removeLink"><span class="actionspan" onclick="moveAuthor(0, 'down');">Move Down</span></span>    
        <span class="oneField">
         <spring:bind path="correction.authors[0].name">
          <label for="<c:out value="${ status.expression }"/>" class="preField">Name:&nbsp;<span class="reqMark">*</span></label>
          <input type="text" size="40" id="<c:out value="${ status.expression }"/>" name="<c:out value="${ status.expression }"/>" value="<c:out value="${ status.value }"/>" class=""/>
          <span class="errMsg" id="<c:out value="${status.expression}"/>-E"><c:out value="${status.errorMessage}"/></span>
         </spring:bind>
        </span>
        <span class="oneField">
         <spring:bind path="correction.authors[0].affil">
          <label for="<c:out value="${ status.expression }"/>" class="preField">Affiliation:</label>
          <input type="text" size="40" id="<c:out value="${ status.expression }"/>" name="<c:out value="${ status.expression }"/>" value="<c:out value="${ status.value }"/>" class=""/>
          <span class="errMsg" id="<c:out value="${status.expression}"/>-E"><c:out value="${status.errorMessage}"/></span>
         </spring:bind>
        </span>
        <span class="oneField">
         <spring:bind path="correction.authors[0].address">
          <label for="<c:out value="${ status.expression }"/>" class="preField">Address:</label>
          <input type="text" size="40" id="<c:out value="${ status.expression }"/>" name="<c:out value="${ status.expression }"/>" value="<c:out value="${ status.value }"/>" class=""/>
          <span class="errMsg" id="<c:out value="${status.expression}"/>-E"><c:out value="${status.errorMessage}"/></span>
         </spring:bind>
        </span>
        <span class="oneField">
         <spring:bind path="correction.authors[0].email">
          <label for="<c:out value="${ status.expression }"/>" class="preField">Email:</label>
          <input type="text" size="40" id="<c:out value="${ status.expression }"/>" name="<c:out value="${ status.expression }"/>" value="<c:out value="${ status.value }"/>" class=""/>
          <span class="errMsg" id="<c:out value="${status.expression}"/>-E"><c:out value="${status.errorMessage}"/></span>
         </spring:bind>
        </span>     
        <spring:bind path="correction.authors[0].order">
         <c:if test="${ ! empty status.value }">
          <input type="hidden" id="<c:out value="${ status.expression }"/>" name="<c:out value="${ status.expression }"/>" value="<c:out value="${ status.value }"/>"/>
         </c:if>
         <c:if test="${ empty status.value }">
          <input type="hidden" id="<c:out value="${ status.expression }"/>" name="<c:out value="${ status.expression }"/>" value="1"/>
         </c:if>
        </spring:bind>      
        <spring:bind path="correction.authors[0].deleted">
         <input type="hidden" id="<c:out value="${ status.expression }"/>" name="<c:out value="${ status.expression }"/>" value="<c:out value="${ status.value }"/>" class=""/>
        </spring:bind>
      </fieldset>
  
      <c:forEach var="counter" begin="1" end="${ correction.numberOfAuthors - 1 }">
       <fieldset id="author_<c:out value="${ counter }"/>" class="removeable" <c:if test="${ correction.authors[counter].deleted }">style="display:none"</c:if>>
         <span class="removeLink"><span class="actionspan" onclick="moveAuthor(<c:out value="${ counter }"/>, 'up');">Move Up</span> | <span class="actionspan" onclick="moveAuthor(<c:out value="${ counter }"/>, 'down');">Move Down</span></span>
         <span class="oneField">
          <spring:bind path="correction.authors[${ counter }].name">
           <label for="<c:out value="${ status.expression }"/>" class="preField">Name:&nbsp;<span class="reqMark">*</span></label>
           <input type="text" size="40" id="<c:out value="${ status.expression }"/>" name="<c:out value="${ status.expression }"/>" value="<c:out value="${ status.value }"/>" class=""/>
           <span class="errMsg" id="<c:out value="${status.expression}"/>-E"><c:out value="${status.errorMessage}"/></span>
          </spring:bind>
         </span>
         <span class="oneField">
          <spring:bind path="correction.authors[${ counter }].affil">
           <label for="<c:out value="${ status.expression }"/>" class="preField">Affiliation:</label>
           <input type="text" size="40" id="<c:out value="${ status.expression }"/>" name="<c:out value="${ status.expression }"/>" value="<c:out value="${ status.value }"/>" class=""/>
           <span class="errMsg" id="<c:out value="${status.expression}"/>-E"><c:out value="${status.errorMessage}"/></span>
          </spring:bind>
         </span>
         <span class="oneField">
          <spring:bind path="correction.authors[${ counter }].address">
           <label for="<c:out value="${ status.expression }"/>" class="preField">Address:</label>
           <input type="text" size="40" id="<c:out value="${ status.expression }"/>" name="<c:out value="${ status.expression }"/>" value="<c:out value="${ status.value }"/>" class=""/>
           <span class="errMsg" id="<c:out value="${status.expression}"/>-E"><c:out value="${status.errorMessage}"/></span>
          </spring:bind>
         </span>
         <span class="oneField">
          <spring:bind path="correction.authors[${ counter }].email">
           <label for="<c:out value="${ status.expression }"/>" class="preField">Email:</label>
           <input type="text" size="40" id="<c:out value="${ status.expression }"/>" name="<c:out value="${ status.expression }"/>" value="<c:out value="${ status.value }"/>" class=""/>
           <span class="errMsg" id="<c:out value="${status.expression}"/>-E"><c:out value="${status.errorMessage}"/></span>
          </spring:bind>
         </span>
         <spring:bind path="correction.authors[${ counter }].order">
          <input type="hidden" id="<c:out value="${ status.expression }"/>" name="<c:out value="${ status.expression }"/>" value="<c:out value="${ status.value }"/>"/>
         </spring:bind>
         <spring:bind path="correction.authors[${ counter }].deleted">
          <input type="hidden" id="<c:out value="${ status.expression }"/>" name="<c:out value="${ status.expression }"/>" value="<c:out value="${ status.value }"/>" class=""/>
         </spring:bind>
         <span class="removeLink actionspan" onclick="deleteSection('author_<c:out value="${ counter }"/>', 'authors[<c:out value="${ counter }"/>].deleted');">Remove This Author</span>
       </fieldset>
      </c:forEach>
      </div>
      <span class="duplicateLink actionspan" onclick="repeat('repeat_block');">Add Another Author</span>
     </fieldset>
      
     <span class="oneField">
      <spring:bind path="correction.abs">
       <label for="<c:out value="${ status.expression }"/>" class="preField">Abstract:</label>
       <textarea rows="10" cols="60" id="<c:out value="${ status.expression }"/>" name="<c:out value="${ status.expression }"/>"><c:out value="${ status.value }"/></textarea>
       <span class="errMsg" id="<c:out value="${status.expression}"/>-E"><c:out value="${status.errorMessage}"/></span>
      </spring:bind>
     </span>
     <span class="oneField">
      <spring:bind path="correction.venue">
       <label for="<c:out value="${ status.expression }"/>" class="preField">Publication Venue:</label>
       <input type="text" size="50" id="<c:out value="${ status.expression }"/>" name="<c:out value="${ status.expression }"/>" value="<c:out value="${ status.value }"/>"/>
       <span class="errMsg" id="<c:out value="${status.expression}"/>-E"><c:out value="${status.errorMessage}"/></span>
      </spring:bind>
     </span>
     <span class="oneField">
      <spring:bind path="correction.venType">
       <label for="<c:out value="${ status.expression }"/>" class="preField">Venue Type:</label>
       <input type="text" size="50" id="<c:out value="${ status.expression }"/>" name="<c:out value="${ status.expression }"/>" value="<c:out value="${ status.value }"/>"/>
       <span class="errMsg" id="<c:out value="${status.expression}"/>-E"><c:out value="${status.errorMessage}"/></span>
      </spring:bind>
     </span>
     <span class="oneField">
      <spring:bind path="correction.year">
       <label for="<c:out value="${ status.expression }"/>" class="preField">Publication Year:</label>
       <input type="text" size="4" id="<c:out value="${ status.expression }"/>" name="<c:out value="${ status.expression }"/>" value="<c:out value="${ status.value }"/>"/>
       <span class="errMsg" id="<c:out value="${status.expression}"/>-E"><c:out value="${status.errorMessage}"/></span>
      </spring:bind>
     </span>
     <span class="oneField">
      <spring:bind path="correction.vol">
       <label for="<c:out value="${ status.expression }"/>" class="preField">Volume:</label>
       <input type="text" size="4" id="<c:out value="${ status.expression }"/>" name="<c:out value="${ status.expression }"/>" value="<c:out value="${ status.value }"/>"/>
       <span class="errMsg" id="<c:out value="${status.expression}"/>-E"><c:out value="${status.errorMessage}"/></span>
      </spring:bind>
     </span>
     <span class="oneField">
      <spring:bind path="correction.num">
       <label for="<c:out value="${ status.expression }"/>" class="preField">Number:</label>
       <input type="text" size="4" id="<c:out value="${ status.expression }"/>" name="<c:out value="${ status.expression }"/>" value="<c:out value="${ status.value }"/>"/>
       <span class="errMsg" id="<c:out value="${status.expression}"/>-E"><c:out value="${status.errorMessage}"/></span>
      </spring:bind>
     </span>
     <span class="oneField">
      <spring:bind path="correction.pages">
       <label for="<c:out value="${ status.expression }"/>" class="preField">Pages:</label>
       <input type="text" size="20" id="<c:out value="${ status.expression }"/>" name="<c:out value="${ status.expression }"/>" value="<c:out value="${ status.value }"/>"/>
       <span class="errMsg" id="<c:out value="${status.expression}"/>-E"><c:out value="${status.errorMessage}"/></span>
      </spring:bind>
     </span>
     <span class="oneField">
      <spring:bind path="correction.publisher">
       <label for="<c:out value="${ status.expression }"/>" class="preField">Publisher:</label>
       <input type="text" size="50" id="<c:out value="${ status.expression }"/>" name="<c:out value="${ status.expression }"/>" value="<c:out value="${ status.value }"/>"/>
       <span class="errMsg" id="<c:out value="${status.expression}"/>-E"><c:out value="${status.errorMessage}"/></span>
      </spring:bind>
     </span>
     <span class="oneField">
      <spring:bind path="correction.pubAddr">
       <label for="<c:out value="${ status.expression }"/>" class="preField">Publisher Address:</label>
       <input type="text" size="50" id="<c:out value="${ status.expression }"/>" name="<c:out value="${ status.expression }"/>" value="<c:out value="${ status.value }"/>"/>
       <span class="errMsg" id="<c:out value="${status.expression}"/>-E"><c:out value="${status.errorMessage}"/></span>
      </spring:bind>
     </span>
     <span class="oneField">
      <spring:bind path="correction.tech">
       <label for="<c:out value="${ status.expression }"/>" class="preField">Tech Report Number:</label>
       <input type="text" size="50" id="<c:out value="${ status.expression }"/>" name="<c:out value="${ status.expression }"/>" value="<c:out value="${ status.value }"/>"/>
       <span class="errMsg" id="<c:out value="${status.expression}"/>-E"><c:out value="${status.errorMessage}"/></span>
      </spring:bind>
     </span>
     <spring:bind path="correction.paperID">
       <input type="hidden" id="<c:out value="${ status.expression}"/>" name="<c:out value="${ status.expression }"/>" value="<c:out value="${ status.value }"/>"/>
     </spring:bind>
     <input type="hidden" id="doi" name="doi" value="<c:out value="${ correction.paperID }"/>"/>
    </fieldset>
    <div class="actions">
     <input type="submit" class="primaryAction" id="submit-" name="submitAction" value="Submit Correction" />
    </div>
   </form>
   <div id="repeatCounter" style="display:none;"><c:out value="${ correction.numberOfAuthors }"/></div>
  </c:if>
<br/><br/>
</div> <!-- End main_content -->
<%@ include file="viewdoc_footer.jsp" %>
