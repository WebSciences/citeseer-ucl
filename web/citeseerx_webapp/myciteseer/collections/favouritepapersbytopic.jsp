<%@ page
    language="java"
    import="javax.naming.*,javax.rmi.PortableRemoteObject,java.util.*, java.io.*, java.sql.*, java.lang.*"        %>
<%
	//String name=request.getParameter("name");
	String name = (String)session.getAttribute("username");
	int userid = Integer.parseInt((String)session.getAttribute("userid"));
	session.setAttribute("username", name);
	String foldername        = ((String)request.getParameter("foldername"));
	
 	int[] u_paperid = new int[10000];
	String[] u_papername = new String[10000];
	int t_length = 0;
	int folder_length = 0;

       	Connection con = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        Statement sm = null;
        try{
                String mysqluser = "yasong";
                String mysqlpass = "";
                String url = "jdbc:mysql://localhost/citeseer";
                Class.forName("com.mysql.jdbc.Driver").newInstance();

                //String name = "yasong";
                //String password = "citeseerx";
                //String  name = request.getParameter("loginname");
                //String password = request.getParameter("loginpass");
                if(!name.equals("")){
                        con = DriverManager.getConnection(url,mysqluser.toLowerCase(), mysqlpass.toLowerCase());
                        System.out.println("Connection Successful!");
			//  select each paper based on folder name


                        String  paperids="select did from favouritepapers where userid = ? AND folder = ? group by did";
                        ps = con.prepareStatement(paperids);
                        ps.setInt(1,userid);
			ps.setString(2,foldername);
                        rs = ps.executeQuery();
                        while(rs.next())
                        {
				int temp = rs.getInt("did");
				if(temp > 0){
	                                u_paperid[t_length] = temp;
                                	t_length ++;
				}
                        }

 			for(int i = 0; i < t_length; i++){
                                String papernames = "select title from papers where id = ?";
                                ps = con.prepareStatement(papernames);
                                ps.setInt(1, u_paperid[i]);
                                rs = ps.executeQuery();
                                if(rs.next()){
                                        u_papername[i] = rs.getString("title");
                                }
                        }

                }
        }catch(Exception e){
                e.printStackTrace();
                        //response.sendRedirect("error/loginFalse.jsp");
        }
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>MyCiteSeer- Paper List</title>
</head>

<body>
	<br><a href ="main.jsp">back to main</a>
	<br>
	<br>
	<center><h1>Folder [<%=foldername%>] by User <%=userid%>:<%=name%></h1></center>
	<br>

<table border="1" align="center">
  <tr>
    <td>
      <div align="center">Papers</div>
    </td>
        <td> All Tags </td>
  </tr>

        <% for (int i = 0; i < t_length; i++) {
                //String link = "http://citeseer.ist.psu.edu/"+tag_did[i]+".html";
                String link = "taggingframe.jsp?did="+u_paperid[i]+"&userid="+userid;
        %>
                <tr>
                        <td>
                        <a href="<%=link%>"><%=u_papername[i]%>
                        </a>
                        </td>
                        <td>
                        <%
                        String  papertags="select tagid from tagmap where did = ? group by tagid";
                        ps = con.prepareStatement(papertags);
                        ps.setInt(1,u_paperid[i]);
                        rs = ps.executeQuery();
                        while(rs.next())
                        {
                                int temp = rs.getInt("tagid");
                                String  query="select tagname,count from tags where id = ?";
                                ps = con.prepareStatement(query);
                                ps.setInt(1,temp);
                                ResultSet rs1 = null;
                                rs1 = ps.executeQuery();
                                String tempname="NULL";
                                int tempcount = 0;
                                if(rs1.next()){
                                        tempname = rs1.getString("tagname");
                                        tempcount = rs1.getInt("count");
                                }

                                String color = "black";

                                String link1 = "papertags.jsp?tagid="+temp+"&tagname="+tempname;

                                if(tempcount >= 20){
                                        color = "#000000";
                                }
                                else if(tempcount >= 10){
                                        color="#333333";
                                }
                                else if(tempcount >= 5){
                                        color = "#666666";
                                }
                                else if(tempcount >= 3){
                                        color = "#999999";
                                }
                                else{
                                        color = "#bbbbbb";
                                }


                        %>
                  <a href="<%=link1%>"><font size="<%=tempcount%>" color="<%=color%>"><%=tempname%></font></a>
                        <%
                        }

                        %>
                        </td>
                </tr>
	<% } %>
</table>

</body>
</html>
