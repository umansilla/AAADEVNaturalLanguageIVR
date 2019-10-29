<%-- 
    Document   : Home
    Created on : Aug 21, 2019, 12:11:20 PM
    Author     : umansilla
--%>
<%@page import="service.AAADEVNaturalLanguageIVR.Bean.Usuario"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Avaya And Watson Assistant IVR</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">

        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
        <link rel="stylesheet" href="https://cdn.datatables.net/1.10.19/css/dataTables.bootstrap.min.css">
        <script src="https://code.jquery.com/jquery-3.3.1.js"></script>
        <script src="https://cdn.datatables.net/1.10.19/js/jquery.dataTables.min.js"></script>
        <script src="https://cdn.datatables.net/1.10.19/js/dataTables.bootstrap.min.js"></script>
        <style>
            #demotext {
                color: #FFFFFF;
                background: #FFFFFF;
                text-shadow: 2px 2px 0 #4074b5, 2px -2px 0 #4074b5, -2px 2px 0 #4074b5, -2px -2px 0 #4074b5, 2px 0px 0 #4074b5, 0px 2px 0 #4074b5, -2px 0px 0 #4074b5, 0px -2px 0 #4074b5;
                color: #FFFFFF;
                background: #FFFFFF;
            }
        </style>
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
		    <a>Signed As: <span style="font-weight: bold;"><%= userName%></span></a>
		  </div>
		</div>
		
	    <hr>
        <div class="jumbotron" style="background-color: #c72d1c">
            <img src="NaturalLanguage/img/avaya-01-logo-black-and-white.png" width="190" height= "60" style="display: inline;">
            <p style="display: inline; color: white; font-size: 40px;">|</p>
            <p style="display: inline; color: white; text-align: center; font-size: 30px;"> Watson Assistant IVR </p>
        </div>


        <ol class="breadcrumb">
            <li><a id="homeRedirectPage" style="cursor: pointer">Home</a></li>

        </ol>

        <div class="container">
            <div class="row">
                <div class="col-md-4">
                    <a class="thumbnail" style="height: 85px; width: 250px; margin: 0 auto; cursor: pointer;" id="esSelect">
                        <div id="demotext" style="text-align: center;
                             margin: 0 auto;"><span  style="font-size: 50px" >Espa&ntilde;ol</span></div>
                    </a>
                </div>
                <div class="col-md-4">
                    <a class="thumbnail" style="height: 85px; width: 250px; margin: 0 auto; cursor: pointer;" id="ptSelect">
                        <div id="demotext" style="text-align: center;
                             margin: 0 auto;"><span  style="font-size: 50px" >Portugues</span></div>
                    </a>
                </div>
                <div class="col-md-4">
                    <a class="thumbnail" style="height: 85px; width: 250px; margin: 0 auto; cursor: pointer;" id="enSelect">
                        <div id="demotext" style="text-align: center;
                             margin: 0 auto;"><span  style="font-size: 50px" >English</span></div>
                    </a>
                </div>
            </div>
        </div>

        <script src="NaturalLanguage/js/sweetAlertmin.js"></script>
        <script src="NaturalLanguage/js/jquery.canvasjs.min.js"></script>
        <script src="NaturalLanguage/js/home.js"></script>


    </body>
</html>
