<%@ include file="/WEB-INF/jsp/myciteseer/IncludeTop.jsp" %>

<html>
<head><title>Add a friend</title>
<style type="text/css">
<!--
body,td,th {
        font-family: Georgia;
        font-size: 12pt;
}
a:link {
        color: #FF0000;
        text-decoration: none;
}
a:visited {
        text-decoration: none;
        color: #C21B1F;
}
a:hover {
        text-decoration: none;
        color: #FFFFFF;
        background-color: #AC6260;
        border-top-color: #CC6633;
}
a:active {
        text-decoration: none;
        color: #FF9900;
}
-->

</style>
</head>

<body>
<h1>Add a Friend</h1>
<form action="<c:url value="/myciteseer/action/addFriend"/>" method="post">
  <table width="35%" bgcolor="f8f8ff" border="0" cellspacing="0" cellpadding="5">
    <tr>
      <td alignment="right" width="23%">Friend ID:</td>
      <spring:bind path="friend.id">
        <td width="77%">
          <input type="text" name="friend.id">
        </td>
       </spring:bind>
    </tr>
  </table>
  <br>
  <input type="submit" alignment="center" value="Add to My Network">
</form>
<h4>
<a href="javascript:window.close();">I've changed my mind - cancel and close the window</a>.
</h4>
</body>
</html>
