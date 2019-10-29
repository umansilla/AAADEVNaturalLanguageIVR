<%-- 
    Document   : AudioControl
    Created on : Aug 12, 2019, 4:31:58 PM
    Author     : umansilla
--%>
<%@page import="service.AAADEVNaturalLanguageIVR.Bean.Usuario"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>

<%@include file="/AudioControls/templates/header.jsp"%>
<%
	Usuario usuario = (Usuario) request.getAttribute("Usuario");
	String userName = usuario.getName();
	if (userName == null || userName.isEmpty()) {
		userName = usuario.getUsername();
	}
%>

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

<body>
	<div class="loader loader-default" data-blink id="loaderDisplay"></div>

		<div class="header">
		  <a class="logo">America’s International PoC Development Team</a>
		  <div class="header-right">
		    <a href="?Location=NLU">Natural Language IVR</a>
		    <a href="?Location=User">User Admin Page</a>
		    <a class="active" href="?Location=AudiosControl">Audios Control</a>
		    <a id="closeSessionBtn" style="cursor:pointer;">Close Session</a>
		    <a>Signed As: <span style="font-weight: bold;"><%= userName%></span></a>
		  </div>
		</div>
		
	    <hr>
	<div class="jumbotron" style="background-color: #c72d1c">
		<img src="NaturalLanguage/img/avaya-01-logo-black-and-white.png"
			width="190" height="60" style="display: inline;">
		<p style="display: inline; color: white; font-size: 40px;">|</p>
		<p
			style="display: inline; color: white; text-align: center; font-size: 30px;">Audios Control Natural Language IVR</p>
	</div>


	<div class="container">
		<div class="row">
			<div class="col">
				<button type="button" class="btn btn-primary btn-block"
					id="createDirectory">Create New Directory</button>
			</div>
			<div class="col">
				<button type="button" class="btn btn-info btn-block"
					id="downloadAudioMap">Download Audio Map</button>
			</div>
		</div>
	</div>
	<hr>
	<div class="container" id="principalContainer"></div>
	<script src="AudioControls/js/AudioControl.js"></script>
	<script src="AudioControls/js/sweetAlertmin.js"></script>
</body>

</html>
