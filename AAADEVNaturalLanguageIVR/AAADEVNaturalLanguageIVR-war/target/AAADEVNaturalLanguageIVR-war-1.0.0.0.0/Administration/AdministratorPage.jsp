<%-- 
    Document   : AdministratorPage
    Created on : Sep 18, 2019, 9:13:39 AM
    Author     : umansilla
--%>

<%@page import="service.AAADEVNaturalLanguageIVR.Bean.Usuario"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Administration Page</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <!-- Use This for -->
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
        <link rel="stylesheet" href="https://cdn.datatables.net/1.10.19/css/dataTables.bootstrap.min.css">
        <script src="https://code.jquery.com/jquery-3.3.1.js"></script>
        <script src="https://cdn.datatables.net/1.10.19/js/jquery.dataTables.min.js"></script>
        <script src="https://cdn.datatables.net/1.10.19/js/dataTables.bootstrap.min.js"></script>
        <style>
            * {box-sizing: border-box;}

            body { 
                margin: 0;
                font-family: Arial, Helvetica, sans-serif;
            }

            .header {
                overflow: hidden;
                background-color: #f1f1f1;
                padding: 20px 10px;
            }

            .header a {
                float: left;
                color: black;
                text-align: center;
                padding: 12px;
                text-decoration: none;
                font-size: 18px; 
                line-height: 25px;
                border-radius: 4px;
            }

            .header a.logo {
                font-size: 25px;
                font-weight: bold;
            }

            .header a:hover {
                background-color: #ddd;
                color: black;
            }

            .header a.active {
                background-color: dodgerblue;
                color: white;
            }

            .header-right {
                float: right;
            }

            @media screen and (max-width: 500px) {
                .header a {
                    float: none;
                    display: block;
                    text-align: left;
                }

                .header-right {
                    float: none;
                }
            }
        </style>
    </head>
    <%
        List<Usuario> listUsuarios = (List<Usuario>) request.getAttribute("ListUsuarios");
    	Usuario usuario = (Usuario) request.getAttribute("Usuario");
    	String userName = usuario.getName();
    	if (userName == null || userName.isEmpty()) {
    		userName = usuario.getUsername();
    	}
    %>
    <body>
        <div class="header">
            <a class="logo">America’s International PoC Development Team</a>
            <div class="header-right">
                <a href="?Location=NLU">Natural Language IVR</a>
                <a class="active" href="?Location=home">Home</a>
                <a href="?Location=User">User Admin Page</a>
                <a href="?Location=AudiosControl">Audios Control</a>
                <a id="closeSessionBtn" style="cursor:pointer;">Close Session</a>
                <a>Signed As: <span style="font-weight: bold;"><%= userName %></span></a>
            </div>
        </div>

        <hr>
        <div class="jumbotron" style="background-color: #c72d1c">
            <img src="Administration/img/avaya-01-logo-black-and-white.png" width="190" height= "60" style="display: inline;">
            <p style="display: inline; color: white; font-size: 40px;">|</p>
            <p style="display: inline; color: white; text-align: center; font-size: 30px;">Administration Page </p>
        </div>
        <div class="container-fluid">
            <table id="example" class="table table-striped table-bordered" style="width:100%">
                <thead>
                    <tr>
                        <th>User Name</th>
                        <th>Name</th>
                        <th>Phone</th>
                        <th>Creation Date</th>
                        <th>Creation Time</th>
                        <th>Country</th>
                        <th>Verbio User</th>
                        <th>Trained</th>
                        <th>Account Banco Caja Social</th>
                        <th>Balance Banco Caja Social</th>
                        <th>Movements Banco Caja Social</th>
                        <!-- <th>Actions</th> -->
                    </tr>
                </thead>
                <tbody>

                    <% for (Usuario user : listUsuarios) {%>
                    <tr id=<%= user.getId()%>>
                        <td> <%= user.getUsername()%> </td>
                        <td> <%= user.getName()%> </td>
                        <td> <%= user.getPhone()%> </td>
                        <td> <%= user.getFecha()%> </td>
                        <td> <%= user.getHora()%> </td>
                        <td> <%= user.getCountry()%> </td>
                        <td> <%= user.getVerbiouser()%> </td>
                        <td> <%= user.getTrain()%> </td>
                        <td> <%= user.getCuenta()%> </td>
                        <%
                            if (user.getSaldo().isEmpty()) {%>  
                        <td></td>
                        <% } else {%>  
                        <td> $ <%= user.getSaldo()%> </td>
                        <% } %>

                        <%
                            if (user.getHistoricoList() == null) {%>  
                        <td></td>
                        <% } else {%>  
                        <td> <%= user.getHistoricoList().size()%> </td>
                        <% } %>
                        <!--
                        <td> 
                            <button type="button" class="btn btn-warning " onclick="Edit">Edit</button>
                            <button type="button" class="btn btn-danger " onclick="Delete">Delete</button>
                        </td>
                        -->
                    </tr>
                    <% }%>
                </tbody>
                <tfoot>
                    <tr>
                        <th>User Name</th>
                        <th>Name</th>
                        <th>Phone</th>
                        <th>Creation Date</th>
                        <th>Creation Time</th>
                        <th>Country</th>
                        <th>Verbio User</th>
                        <th>Trained</th>
                        <th>Account Banco Caja Social</th>
                        <th>Balance Banco Caja Social</th>
                        <th>Movements Banco Caja Social</th>
                        <!-- <th>Actions</th> -->
                    </tr>
                </tfoot>
            </table>
        </div>
        <script src="Administration/js/sweetAlertmin.js"></script>
        <script src="Administration/js/jquery.canvasjs.min.js"></script>
        <script src="Administration/js/Administrator.js"></script>
    </body>
</html>
