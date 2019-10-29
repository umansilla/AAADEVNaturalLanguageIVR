<%-- 
    Document   : index
    Created on : Aug 20, 2019, 1:53:26 PM
    Author     : umansilla
--%>

<%@page import="service.AAADEVNaturalLanguageIVR.Bean.Usuario"%>
<%@page import="java.util.List"%>
<%@page import="service.AAADEVNaturalLanguageIVR.Bean.InputIntent"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Avaya And Watson Assistant IVR</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <!-- Use This for 
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.0/css/bootstrap.min.css"> -->
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
        <link rel="stylesheet" href="https://cdn.datatables.net/1.10.19/css/dataTables.bootstrap.min.css">
        <script src="https://code.jquery.com/jquery-3.3.1.js"></script>
        <script src="https://cdn.datatables.net/1.10.19/js/jquery.dataTables.min.js"></script>
        <script src="https://cdn.datatables.net/1.10.19/js/dataTables.bootstrap.min.js"></script>
        <style>
            /* The Modal (background) */
            .modal {
                display: none; /* Hidden by default */
                position: fixed; /* Stay in place */
                z-index: 1; /* Sit on top */
                left: 0;
                top: 0;
                width: 100%; /* Full width */
                height: 100%; /* Full height */
                overflow: auto; /* Enable scroll if needed */
                background-color: rgb(0,0,0); /* Fallback color */
                background-color: rgba(0,0,0,0.4); /* Black w/ opacity */
            }

            /* Modal Content/Box */
            .modal-content {
                background-color: #fefefe;
                margin: 15% auto; /* 15% from the top and centered */
                padding: 20px;
                border: 1px solid #888;
                width: 80%; /* Could be more or less, depending on screen size */
            }

            /* The Close Button */
            .close {
                color: #aaa;
                float: right;
                font-size: 28px;
                font-weight: bold;
            }

            .close:hover,
            .close:focus {
                color: black;
                text-decoration: none;
                cursor: pointer;
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

        List<InputIntent> listIntents = (List<InputIntent>) request.getAttribute("Registros");
        Usuario usuario = (Usuario) request.getAttribute("Usuario");

        String userName = usuario.getName();
        if (userName == null || userName.isEmpty()) {
            userName = usuario.getUsername();
        }
        String language = usuario.getLanguage();
        String table[] = null;
        if (language.equals("es")) {
            language = "Español";
            table = new String[] {"Hora","Origen","Destino", "Audio", "Transcipción", "Confianza", "Intención", "Emociones", "Ver", "Record"};
        }
        if (language.equals("pt")) {
            language = "Portugues";
            table = new String[] {"Hora","Origem","Destino", "Audio", "Transcrição", "Confiança", "Intenção", "Emoções", "Ver", "RecordPt"};
        }
        if (language.equals("en")) {
            language = "English";
            table = new String[] {"Time","Origin","Destination", "Audio", "Transcription", "Trust", "Intention", "Emotions", "Watch", "RecordEn"};
        }
    %>
    <body>
    <!--  
        <nav class="navbar navbar-default text-center">
            <div class="container-fuid ">

                <div class="navbar-header text-center">
                    <h2 style="font-weight: bold; text-align: center; font-weight: 50px !important;" class="text-center navbar-text">America’s International PoC Development Team</h2>

                </div>

                <ul class="nav navbar-nav navbar-right">
                    <p class="navbar-text">Signed in as <span style="font-weight: bold;"><%= userName%></span></p>
                    <p class="navbar-text"><a class="navbar-link" id="closeSessionBtn" style="cursor:pointer;">Close Session</a></p>

                </ul>
            </div>
        </nav> 
        -->
        
		<div class="header">
		  <a class="logo">America’s International PoC Development Team</a>
		  <div class="header-right">
		    <a class="active" href="?Location=NLU">Natural Language IVR</a>
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
            <p style="display: inline; color: white; text-align: center; font-size: 30px;"> Natural Language IVR </p>
        </div>


        <ol class="breadcrumb">
            <li><a id="homeRedirectPage" style="cursor: pointer">Home</a></li>
            <li><a href=""> <%= language %>  </a></li>
        </ol>
        <div class="container-fluid">

            <div id="languageId" style="display: none"><%= language %></div>

            <table id="example" class="table table-striped table-bordered" style="width:100%">
                <thead>
                    <tr>
                        <th><%= table[0] %></th>
                        <th><%= table[1] %></th>
                        <th><%= table[2] %></th>
                        <th><%= table[3] %></th>
                        <th><%= table[4] %></th>
                        <th><%= table[5] %></th>
                        <th><%= table[6] %></th>
                        <th><%= table[7] %></th>
                        <th>
                            <button type="button" class="btn btn-danger" id="btn_borrar"><span class="glyphicon glyphicon-trash"></span></button>
                        </th>
                    </tr>
                </thead>
                <tbody>

                    <% for (InputIntent intent : listIntents) {%>
                    <tr id=<%= intent.getWavFile()%>>
                        <td><%= intent.getFechayHora()%> </td>
                        <td><%= intent.getOrigen()%> </td>
                        <td><%= intent.getDestino()%> </td>
                        <td>
                            <!-- src = https://breeze2-132.collaboratory.avaya.com/services/AAADEVCONTROLPAD/ControladorGrabaciones/web/Record/20190815_145959MDT_2302_+573107868568.wav -->
                            <audio class="democlass" controls="" src="https://breeze2-132.collaboratory.avaya.com/services/AAADEVCONTROLPAD/ControladorGrabaciones/web/<%= table[9] %>/<%= intent.getWavFile()%>"></audio>
                        </td>


                        <td><%= intent.getTranscript()%> </td>
                        <td><%= intent.getConfidence()%> </td>
                        <td><%= intent.getIntent()%> </td>
                        <td>
                            <!-- <button type="button" data-toggle="modal" data-target="#exampleModal" class="btn btn-info" onclick="Ver(this)">Ver</button> -->
                            <button type="button" class="btn btn-info btn-block" onclick="Ver(this)"><%= table[8] %></button>
                        </td>
                        <td>
                            <input type="checkbox" name=<%= intent.getWavFile()%> class="borrar_contacto" style="margin: 10px auto; display: block; width:20px; height:20px;">
                        </td>

                    </tr>

                    <% }%>

                </tbody>
                <tfoot>
                    <tr>
                        <th><%= table[0] %></th>
                        <th><%= table[1] %></th>
                        <th><%= table[2] %></th>
                        <th><%= table[3] %></th>
                        <th><%= table[4] %></th>
                        <th><%= table[5] %></th>
                        <th><%= table[6] %></th>
                        <th><%= table[7] %></th>
                    </tr>
                </tfoot>
            </table>

            <!-- The Modal -->
            <div id="myModal" class="modal" >

                <div class="modal-dialog" role="document" >


                    <div class="modal-body" >
                        <div id="chartContainer" style="height: 100%; width: 100%;"></div>


                    </div>
                </div>

            </div>

        </div>
        <script src="NaturalLanguage/js/sweetAlertmin.js"></script>
        <script src="NaturalLanguage/js/jquery.canvasjs.min.js"></script>
        <script src="NaturalLanguage/js/index.js"></script>

    </body>
</html>