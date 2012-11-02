<%@ include file="IncludeTop.jsp" %>

<script type="text/javascript" src="<c:url value="/dwr/engine.js"/>"></script>
<script type="text/javascript" src="<c:url value="/dwr/util.js"/>"></script>
<script type="text/javascript" src="<c:url value="/dwr/interface/AccountValidatorJS.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/country.js"/>"></script>

<script type="text/javascript">
<!--
DWREngine.setErrorHandler(null);
DWREngine.setWarningHandler(null);

function validateAccountInputField(element) {
  var value;
  if (element.type == 'text')
    value = element.value;
  else if (element.type == 'select-one')
    value = element.options[element.selectedIndex].value;
  AccountValidatorJS.getInputFieldValidationMessage(element.id,
    value, {
                     callback:function(dataFromServer) {
                       setInputFieldStatus(element.id, dataFromServer);
                     }
                   });
}

function setInputFieldStatus(elementId, message) {
  document.getElementById("" + elementId + "-E").innerHTML = message;
}
// -->
</script>
<div class="mypagecontent">
<div class="columns-float-sec">
<div class="column-one-sec"> <!-- center column -->
 <div class="column-one-content clearfix">
 
  <div id="center-content"> <!-- Main content -->
   <div class="inside"> <!-- to give some room between columns -->
    <c:if test="${accountForm.newAccount}">
     <form id="accountForm" method="post" class="wform labelsRightAligned hintsTooltip"
           action="<c:url value="/mcsutils/newAccount"/>">
    </c:if>
    <c:if test="${!accountForm.newAccount}">
     <form id="accountForm" method="post" class="wform labelsRightAligned hintsTooltip"
           action="<c:url value="/myciteseer/action/editAccount"/>">
    </c:if>
    <fieldset id="account_container" class="">
     <c:if test="${accountForm.newAccount}">
      <legend>Account Registration</legend>
     </c:if>
     <c:if test="${!accountForm.newAccount}">
      <legend>Account Information</legend>
     </c:if>
     <fieldset id="identifaction" class="">
      <legend>Identification</legend>
       <div class="oneField">
        <c:if test="${accountForm.newAccount}">
         <spring:bind path="accountForm.account.username">
          <label for="<c:out value="${status.expression}"/>" class="preField">User ID:&nbsp;<span class="reqMark">*</span></label>
          <input type="text" size="" name="<c:out value="${status.expression}"/>" id="<c:out value="${status.expression}"/>" value="<c:out value="${status.value}"/>" onchange="validateAccountInputField(this);" <c:if test="${empty status.errorMessage}">class="required"</c:if><c:if test="${!empty status.errorMessage}">class="required errFld"</c:if>/><br />
          <span class="errMsg" id="<c:out value="${status.expression}"/>-E"><c:out value="${status.errorMessage}"/></span>
         </spring:bind>
        </c:if>
        <c:if test="${!accountForm.newAccount}">
         <label class="preField">User ID:</label>
         <label><c:out value="${accountForm.account.username}"/></label>
        </c:if>
       </div>
       <c:if test="${accountForm.newAccount}">
        <div class="oneField">
         <spring:bind path="accountForm.account.password">
          <label for="<c:out value="${status.expression}"/>" class="preField">Enter Password:&nbsp;<span class="reqMark">*</span></label>
          <input type="password" value="<c:out value="${status.value}"/>" id="<c:out value="${status.expression}"/>" name="<c:out value="${status.expression}"/>" size="" <c:if test="${empty status.errorMessage}">class="required"</c:if><c:if test="${!empty status.errorMessage}">class="required errFld"</c:if>/><br />
          <span class="errMsg" id="<c:out value="${status.expression}"/>-E"><c:out value="${status.errorMessage}"/></span>
         </spring:bind>
        </div>
        <div class="oneField">
         <spring:bind path="accountForm.repeatedPassword">
          <label for="<c:out value="${status.expression}"/>" class="preField">Repeat Password:&nbsp;<span class="reqMark">*</span></label>
          <input type="password" value="<c:out value="${status.value}"/>" id="<c:out value="${status.expression}"/>" name="<c:out value="${status.expression}"/>" size="" <c:if test="${empty status.errorMessage}">class="required"</c:if><c:if test="${!empty status.errorMessage}">class="required errFld"</c:if>/><br />
          <span class="errMsg" id="<c:out value="${status.expression}"/>-E"><c:out value="${status.errorMessage}"/></span>
         </spring:bind>
        </div>
       </c:if>
       <c:if test="${!accountForm.newAccount}">
        <div class="oneField">
         <label class="preField">&nbsp;</label>
         <label><a href="<c:url value="/myciteseer/action/changePassword"/>" title="Change Password">Change Password</a></label>
        </div>
       </c:if>
      </fieldset>
      
          <fieldset id="account_details" class="">
            <legend>Account Information</legend>
            <div id="full_name" class="inlineSection required">
              <label class="preField">Full Name:&nbsp;
                <span class="reqMark">*</span>
              </label>
              <div class="oneField">
                <spring:bind path="accountForm.account.firstName">
                  <label for="<c:out value="${status.expression}"/>" class="inlineLabel"></label>
                  <input type="text" size="15"
                         id="<c:out value="${status.expression}"/>" 
                         name="<c:out value="${status.expression}"/>" 
                         value="<c:out value="${status.value}"/>"
                         onchange="validateAccountInputField(this);" 
                         <c:if test="${empty status.errorMessage}">
                           class=""
                         </c:if> 
                         <c:if test="${!empty status.errorMessage}">
                           class="errFld"
                         </c:if>
                  />
                  <span class="errMsg" id="<c:out value="${status.expression}"/>-E">
                    <c:out value="${status.errorMessage}"/>
                  </span>
                </spring:bind>
              </div>
              <div class="oneField">
                <spring:bind path="accountForm.account.middleName">
                  <label for="<c:out value="${status.expression}"/>" class="inlineLabel"></label>
                  <input type="text" size="2"
                         id="<c:out value="${status.expression}"/>" 
                         name="<c:out value="${status.expression}"/>" 
                         value="<c:out value="${status.value}"/>"
                         onchange="validateAccountInputField(this);"
                         <c:if test="${empty status.errorMessage}">
                           class=""
                         </c:if>
                         <c:if test="${!empty status.errorMessage}">
                           class="errFld"
                         </c:if>
                  />
                  <span class="errMsg" id="<c:out value="${status.expression}"/>-E">
                    <c:out value="${status.errorMessage}"/>
                  </span>
                </spring:bind>
              </div>
              <div class="oneField">
                <spring:bind path="accountForm.account.lastName">
                  <label for="<c:out value="${status.expression}"/>" class="inlineLabel"></label>
                  <input type="text" size="18"
                         id="<c:out value="${status.expression}"/>" 
                         name="<c:out value="${status.expression}"/>" 
                         value="<c:out value="${status.value}"/>"
                         onchange="validateAccountInputField(this);"
                         <c:if test="${empty status.errorMessage}">
                           class=""
                         </c:if>  
                         <c:if test="${!empty status.errorMessage}">
                           class="errFld"
                         </c:if>
                  />
                  <span class="errMsg" id="<c:out value="${status.expression}"/>-E">
                    <c:out value="${status.errorMessage}"/>
                  </span>
                </spring:bind>
              </div>
            </div>
            <div class="oneField">
              <spring:bind path="accountForm.account.email">
                <label for="<c:out value="${status.expression}"/>" class="preField">EMail Address:&nbsp;
                  <span class="reqMark">*</span>
                </label>
                <input type="text" size="43"
                       id="<c:out value="${status.expression}"/>" 
                       name="<c:out value="${status.expression}"/>"
                       value="<c:out value="${status.value}"/>"
                       onchange="validateAccountInputField(this);"
                       <c:if test="${empty status.errorMessage}">
                         class="validate-email required"
                       </c:if>  
                       <c:if test="${!empty status.errorMessage}">
                         class="validate-email required errFld"
                       </c:if>
                /><br />
                <span class="errMsg" id="<c:out value="${status.expression}"/>-E">
                  <c:out value="${status.errorMessage}"/>
                </span>
              </spring:bind>
            </div>
            <div class="oneField">
              <spring:bind path="accountForm.account.affiliation1">
                <label for="<c:out value="${status.expression}"/>" class="preField">Organization:&nbsp;
                  <span class="reqMark">*</span>
                </label>
                <input type="text" size="43"
                       id="<c:out value="${status.expression}"/>"
                       name="<c:out value="${status.expression}"/>" 
                       value="<c:out value="${status.value}"/>"
                       onchange="validateAccountInputField(this);"
                       <c:if test="${empty status.errorMessage}">
                         class="validate-alpha required"
                       </c:if>  
                       <c:if test="${!empty status.errorMessage}">
                         class="validate-alpha required errFld"
                       </c:if>
                />
                <div class="field-hint-inactive" id="<c:out value="${status.expression}"/>-H"><span>Primary affiliation</span></div><br />
                <span class="errMsg" id="<c:out value="${status.expression}"/>-E">
                  <c:out value="${status.errorMessage}"/>
                </span>
              </spring:bind>
            </div>
            <div class="oneField">
              <spring:bind path="accountForm.account.affiliation2">
                <label for="<c:out value="${status.expression}"/>" class="preField">Department:</label>
                <input type="text" size="43" 
                       id="<c:out value="${status.expression}"/>"
                       name="<c:out value="${status.expression}"/>"
                       value="<c:out value="${status.value}"/>"
                       <c:if test="${empty status.errorMessage}">
                         class=""
                       </c:if>  
                       <c:if test="${!empty status.errorMessage}">
                         class="errFld"
                       </c:if>
                />
                <div class="field-hint-inactive" id="<c:out value="${status.expression}"/>-H"><span>Department within your organization</span></div>
                <br />
                <span class="errMsg" id="<c:out value="${status.expression}"/>-E">
                  <c:out value="${status.errorMessage}"/>
                </span>
              </spring:bind>
            </div>
            <div class="oneField">
              <spring:bind path="accountForm.account.webPage">
                <label for="<c:out value="${status.expression}"/>" class="preField">Personal Web Page:</label>
                <input type="text" size="43"
                       id="<c:out value="${status.expression}"/>"
                       name="<c:out value="${status.expression}"/>"
                       value="<c:out value="${status.value}"/>" 
                       <c:if test="${empty status.errorMessage}">
                         class=""
                       </c:if>  
                       <c:if test="${!empty status.errorMessage}">
                         class="errFld"
                       </c:if>
                />
                <div class="field-hint-inactive" id="<c:out value="${status.expression}"/>-H"><span>Complete URL to your web page</span></div>
                <br />
                <span class="errMsg" id="<c:out value="${status.expression}"/>-E">
                  <c:out value="${status.errorMessage}"/>
                </span>
              </spring:bind>
            </div>
            <div class="oneField">
              <spring:bind path="accountForm.account.country">
                <label for="<c:out value="${status.expression}"/>" class="preField">Country:&nbsp;
                  <span class="reqMark">*</span>
                </label>
                <select id="<c:out value="${status.expression}"/>" 
                        name="<c:out value="${status.expression}"/>"
                        onchange="validateAccountInputField(this); Fill_States('account.country', 'account.province');" 
                        <c:if test="${empty status.errorMessage}">
                          class="required"
                        </c:if>  
                        <c:if test="${!empty status.errorMessage}">
                          class="required errFld"
                        </c:if>
                >
                  <c:if test="${empty accountForm.account.country}">
                    <option selected="selected">12345678901234567890</option>
                  </c:if>
                  <c:if test="${!empty accountForm.account.country}">
                    <option selected="selected" value="<c:out value="${status.value}"/>"><c:out value="${status.value}"/></option>
                  </c:if>
                </select>
                <div class="field-hint-inactive" id="<c:out value="${status.expression}"/>-H"><span>Country of residence</span></div>
                <br />
                <span class="errMsg" id="<c:out value="${status.expression}"/>-E">
                  <c:out value="${status.errorMessage}"/>
                </span>
              </spring:bind>
            </div>
            <div class="oneField">
              <spring:bind path="accountForm.account.province">
                <label for="<c:out value="${status.expression}"/>" class="preField">State/Province:</label>
                <select id="<c:out value="${status.expression}"/>"
                        name="<c:out value="${status.expression}"/>"
                        <c:if test="${empty status.errorMessage}">
                          class=""
                        </c:if>  
                        <c:if test="${!empty status.errorMessage}">
                          class="errFld"
                        </c:if>
                >
                  <c:if test="${empty accountForm.account.province}">
                    <option selected="selected">12345678901234567890</option>
                  </c:if>
                  <c:if test="${!empty accountForm.account.province}">
                    <option selected="selected" value="<c:out value="${status.value}"/>"><c:out value="${status.value}"/></option>
                  </c:if>
                </select>
                <div class="field-hint-inactive" id="<c:out value="${status.expression}"/>-H"><span>State/Provice of residence</span></div>
                <br />
                <span class="errMsg" id="<c:out value="${status.expression}"/>-E">
                  <c:out value="${status.errorMessage}"/>
                </span>
              </spring:bind>
            </div>
            <c:if test="${accountForm.newAccount}">
              <spring:bind path="accountForm.captcha">
                <div class="oneField">
                  <label class="preField">&nbsp;</label>
                  <img src="<c:url value="/captcha.jpg"/>" style="border:1px solid black" alt="" />
                </div>
                <div class="oneField">
                  <label for="<c:out value="${status.expression}"/>" class="preField">Image Text:&nbsp;
                    <span class="reqMark">*</span>
                  </label>
                  <input type="text" size="10"  
                         id="<c:out value="${status.expression}"/>"
                         name="<c:out value="${status.expression}"/>" 
                         value="" 
                         <c:if test="${empty status.errorMessage}">
                           class="required"
                         </c:if>  
                         <c:if test="${!empty status.errorMessage}">
                           class="required errFld"
                         </c:if>
                  />
                  <br />
                  <div class="field-hint-inactive" id="<c:out value="${status.expression}"/>-H"><span>Please enter the text that appears in the image</span></div>
                  <span class="errMsg" id="<c:out value="${status.expression}"/>-E">
                    <c:out value="${status.errorMessage}"/>
                  </span>
                </div>
              </spring:bind>
            </c:if>
          </fieldset>
        </fieldset>
        <div class="actions">
          <input type="submit" class="primaryAction" id="submit-" name="submitAction" value="Save Account Information" />
        </div>
        <c:if test="${ ! empty param.ticket }">
         <input type="hidden" name="ticket" value="<c:out value="${ param.ticket }"/>"/>
        </c:if>
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
  
  
<!--<div id="right-sidebar">--> <!-- contains right content-->
<!--   <div class="inside">--> <!-- to give some room between columns-->
<!--  </div>--> <!-- end of inside-->
<!--</div>--> <!-- end of right-sidebar-->

</div> <!-- End mypagecontent -->
<script type="text/javascript">
<!--
  if (window != top) 
    top.location.href = location.href;
    Fill_Country("account.country");Fill_States("account.country", "account.province");
  <c:if test="${accountForm.newAccount}">
   function sf(){
    var elt = document.getElementById("account.username");
    elt.focus();
   }
  </c:if>
  <c:if test="${!accountForm.newAccount}">
   function sf(){}
  </c:if>
  
  function sa(){
    var elt = document.getElementById("profile_tab");
    elt.setAttribute("class", "active");
    elt.setAttribute("className", "active");
  }
// -->
</script>

<%@ include file="../shared/IncludeBottom.jsp" %>