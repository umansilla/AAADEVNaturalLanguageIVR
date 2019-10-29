

<%@page import="service.AAADEVNaturalLanguageIVR.Bean.Usuario"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>User Admin Page</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link rel="stylesheet" type="text/css" href="UserSettings/css/style.css">
        <link rel="stylesheet" type="text/css" href="UserSettings/css/css-loader.css">
        <link rel="stylesheet" type="text/css" href="UserSettings/css/fontawsome/all.min.css">
        <link rel="stylesheet" type="text/css" href="UserSettings/css/datatables/dataTables.bootstrap4.css">
        <!-- Latest compiled and minified CSS -->
        <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css">
        <link href="//maxcdn.bootstrapcdn.com/bootstrap/3.3.0/css/bootstrap.min.css" rel="stylesheet" id="bootstrap-css">

        <!------ Include the above in your HEAD tag ---------->

    </head>
	
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

	
	<%
	Usuario usuario = (Usuario) request.getAttribute("Usuario");
	String userName = usuario.getName();
	if (userName == null || userName.isEmpty()) {
		userName = usuario.getUsername();
	}
	%>
    <body>
        <div class="loader loader-default" data-blink id="loader"></div>

		<div class="header">
		  <a class="logo">America’s International PoC Development Team</a>
		  <div class="header-right">
		    <a href="?Location=NLU">Natural Language IVR</a>
		    <a class="active" href="?Location=User">User Admin Page</a>
		    <a href="?Location=AudiosControl">Audios Control</a>
		    <a id="closeSessionBtn" style="cursor:pointer;">Close Session</a>
		    <a>Signed As: <span style="font-weight: bold;"><%= userName%></span></a>
		  </div>
		</div>
		
	    <hr>
        <div class="jumbotron" style="background-color: #c72d1c">
            <img src="UserSettings/img/avaya-01-logo-black-and-white.png" width="190" height= "60" style="display: inline;">
            <p style="display: inline; color: white; font-size: 40px;">|</p>
            <p style="display: inline; color: white; text-align: center; font-size: 30px;"> User Admin Page </p>
        </div>
        <br>
        <br>
        <br>



        <div class="container-fluid">
            <br>
            <div class="row">
                <div class="col-sm-3"><!--left col-->

                    <ul class="list-group">
                        <li class="list-group-item text-muted">Profile</li>
                        <li class="list-group-item text-right" ><span class="pull-left"><strong>Joined</strong></span><span id="fecha"></span></li>
                        <li class="list-group-item text-right" ><span class="pull-left"><strong>Real name</strong></span><span id="realName"></span></li>
                        <li class="list-group-item text-right" ><span class="pull-left"><strong>Country</strong></span><span id="countryProfile"></span></li>
                        <li class="list-group-item text-right" ><span class="pull-left"><strong>Phone</strong></span><span id="phoneProfile"></span></li>
                    </ul> 

                    <ul class="list-group">
                        <li class="list-group-item text-muted">Activity <i class="fa fa-dashboard fa-1x"></i></li>
                        <li class="list-group-item text-right"><span class="pull-left"><strong>Audio Recordings</strong></span><span id="totalAudioRecordings"></span></li>
                    </ul> 
                    <button type="button" class="btn btn-info" id="helpBtn">Help</button>
                    <div id="guide" class="embed-container" style="display: none;">
                        <iframe src="UserSettings/Frames/Home.html" width="480" height="500" frameborder="0" id="frameGuide">
                            <p>Your browser does not support iframes.</p>
                        </iframe>
                    </div>



                </div><!--/col-3-->
                <div class="col-sm-9">

                    <ul class="nav nav-tabs" id="myTab">
                        <li class="active" id="homeA" style="cursor: pointer; cursor: hand;"><a data-toggle="tab">Home</a></li>
                        <li id="bioMetricsA" style="cursor: pointer; cursor: hand;"><a data-toggle="tab">BioMetrics</a></li>
                        <li id="settingsA" style="cursor: pointer; cursor: hand;"><a data-toggle="tab">Settings</a></li>
                        <li id="cajaNacionalA" style="cursor: pointer; cursor: hand;"><a data-toggle="tab">Banco Caja Social</a></li>
                    </ul>

                    <div class="tab-content">
                        <div class="tab-pane active" id="home">
                            <h2 style="color: #333333; font-family: 'Bitter', serif; font-size: 50px; font-weight: normal; line-height: 54px; margin: 0 0 54px;">This space has been designed to perform exercises with the demos created by America's International PoC Development Team</h2>              
                            <hr>
                            <div class="row">
                                <div class="col-sm-6">
                                    <div class="card">
                                        <div class="card-body">
                                            <h5 class="card-title" style="color: #cc0000; font-weight: 900;">AAADEVCONTROLPAD</h5>
                                            <p class="card-text">Review the result of transcription and analysis of intentions and emotions..</p>
                                            <a class="btn btn-primary" style="color: #ffffff;" id="btnControlPad">Go</a>
                                        </div>
                                    </div>
                                </div>
                                <div class="col-sm-6">
                                    <div class="card">
                                        <div class="card-body">
                                            <h5 class="card-title" style="color: #cc0000; font-weight: 900;">AAADEVLOGGER</h5>
                                            <p class="card-text">Primordial use to visualize logs created by Snap-ins installed in Breeze. Validate and store audios recorded from Engagement Designer.</p>
                                            <a class="btn btn-primary" style="color: #ffffff;" id="btnLogger">Go</a>
                                        </div>
                                    </div>
                                </div>
                                <div class="col-sm-6">
                                    <div class="card">
                                        <div class="card-body">
                                            <h5 class="card-title" style="color: #cc0000; font-weight: 900;">AAADEVRECORDV3</h5>
                                            <p class="card-text">Train the tool for voice authentication supported by Verbio, select the BioMetrics tab to start</p>
                                        </div>
                                    </div>
                                </div>
                                <div class="col-sm-6">
                                    <div class="card">
                                        <div class="card-body">
                                            <h5 class="card-title" style="color: #cc0000; font-weight: 900;">VantageTTS</h5>
                                            <p class="card-text">Send and receive audio messages, in several languages ​​with use of the Vantage.</p>
                                            <a class="btn btn-primary" style="color: #ffffff;" id="btnVantage">Go</a>
                                        </div>
                                    </div>
                                </div>
                                <div class="col-sm-6" style="display: none;" id="verbioDisplay">
                                    <div class="card">
                                        <div class="card-body">
                                            <h5 class="card-title" style="color: #cc0000; font-weight: 900;">VerbioControl</h5>
                                            <p class="card-text">Verbio's official page to validate the created users.</p>
                                            <a class="btn btn-primary" style="color: #ffffff;" id="btnVerbiosOficial">Go</a>
                                        </div>
                                    </div>
                                </div>
                                <div class="col-sm-6" style="display: none;" id="ibmDisplay">
                                    <div class="card">
                                        <div class="card-body">
                                            <h5 class="card-title" style="color: #cc0000; font-weight: 900;">IBM Watson Train</h5>
                                            <p class="card-text">Official IBM site to train the Watson tool</p>
                                            <a class="btn btn-primary" style="color: #ffffff;" id="btnIBMOfficial">Go</a>
                                        </div>
                                    </div>
                                </div>
                                <div class="col-sm-6" style="display: none;" id="adminPageDisplay">
                                    <div class="card">
                                        <div class="card-body">
                                            <h5 class="card-title" style="color: #cc0000; font-weight: 900;">Administration Page</h5>
                                            <p class="card-text">Administrators only</p>
                                            <a class="btn btn-primary" style="color: #ffffff;" id="adminPage">Go</a>
                                        </div>
                                    </div>
                                </div>
                            </div>

                        </div><!--/tab-pane-->
                        <div class="tab-pane " id="bioMetrics">

                            <h2 style="color: #333333; font-family: 'Bitter', serif; font-size: 50px; font-weight: normal; line-height: 54px; margin: 0 0 54px;">This space has been designed to record, listen and train the speech recognition engine implemented by Verbio BioMetrics.</h2>
                            <hr>
                            <div>
                                <button id="createVerbioBtn" type="button" class="btn btn-primary btn-lg">Create Verbio User</button>
                                <div class="btn-group" role="group" style="display: none;" id="verbioGroupBtn">
                                    <button id="saveVerbioBtn" type="button" class="btn btn-link" >Save</button>
                                    <button id="cancelVerbioBtn" type="button" class="btn btn-link" >Cancel</button>
                                </div>

                            </div>
                            <br>
                            <br>

                            <div class="row">
                                <div class="col-md-8"><h4>Verbio User: </h4></div>
                                <div class="col-md-4"><h4 id="verbioUser"></h4></div>
                            </div>
                            <div class="row">
                                <div class="col-md-8"><h4>Registered phone: </h4></div>
                                <div class="col-md-4"><h4 id="verbioPhone"></h4></div>
                            </div>
                            <div class="row">
                                <div class="col-md-8"><h4>Trained biometrics: </h4></div>
                                <div class="col-md-4"><h4 id="verbioTrain"></h4></div>
                            </div>


                            <div id="controls">
                                <button id="recordButton" class="btn btn-lg btn-success">Record</button>
                                <button id="pauseButton" class="btn btn-lg btn-warning" disabled>Pause</button>
                                <button id="stopButton" class="btn btn-lg btn-danger " disabled>Stop</button>
                            </div>
                            <div id="formats">Format: start recording to see sample rate</div>


                            <div class="row">
                                <div class="col-md-4">
                                    <h3>Recordings</h3>
                                    <ol id="recordingsList"></ol>
                                </div>
                                <div class="col-md-8">    
                                    <h3>Uploaded to Verbio</h3>
                                    <!-- DataTables Example -->
                                    <div class="card mb-3">
                                        <div class="card-header">
                                            <i class="fas fa-file-audio"></i>
                                            Audio Recordings</div>
                                        <div class="card-body">
                                            <div class="table-responsive">
                                                <table class="table table-bordered" id="dataTable" width="100%" cellspacing="0">
                                                    <thead>
                                                        <tr>
                                                            <th>Name</th>
                                                            <th>Date Creation</th>
                                                            <th>Hour</th>
                                                            <th>Audio path</th>
                                                        </tr>
                                                    </thead>
                                                    <tbody id="tbody">

                                                    </tbody>
                                                </table>
                                            </div>
                                        </div>

                                    </div>
                                    <div id="divbtnTrain" style="display: none;">
                                        <button type="button" class="btn btn-info" id="btnTrain">Train</button>
                                    </div>
                                </div>
                            </div>



                        </div><!--/tab-pane-->
                        <div class="tab-pane" id="settings">

                            <h2 style="color: #333333; font-family: 'Bitter', serif; font-size: 50px; font-weight: normal; line-height: 54px; margin: 0 0 54px;">If you wish to change your current information, please enter the following fields.</h2>
                            <hr>
                            <div id="registrationForm" class="container">
                                <div class="form-group">

                                    <div class="col-xs-6">
                                        <label for="name"><h4>Name</h4></label>
                                        <input type="text" class="form-control" name="first_name" id="first_name" placeholder="Player name by recognition" title="enter your first name if any.">
                                    </div>
                                </div>

                                <div class="form-group">

                                    <div class="col-xs-6">
                                        <label for="phone"><h4>Phone</h4></label>
                                        <input type="text" class="form-control" name="phone" id="phone" placeholder="+5215555555555" title="enter your phone number if any.">
                                    </div>
                                </div>

                                <div class="form-group">

                                    <div class="col-xs-6">
                                        <label for="country"><h4>Country</h4></label>
                                        <input type="text" class="form-control" id="country" placeholder="México" title="enter a location">
                                    </div>
                                </div>
                                <div class="form-group">

                                    <div class="col-xs-6">
                                        <label for="password"><h4>Password</h4></label>
                                        <input type="password" class="form-control" name="password" id="password" placeholder="Enter new password if you want to change the current one" title="enter your password.">
                                    </div>
                                </div>


                                <div class="form-group">
                                    <div class="col-xs-12">
                                        <br>
                                        <button class="btn btn-lg btn-success" id="saveSettings"><i class="glyphicon glyphicon-ok-sign" ></i> Save</button>
                                        <button class="btn btn-lg" id="reset"><i class="glyphicon glyphicon-repeat"></i> Reset</button>
                                    </div>
                                </div>

                            </div>



                        </div>
                        <!-- MODIFICACIÓN 10 DE JULIO 2019 BANCO CAJA NACIONAL -->
                        <div class="tab-pane" id="cajaNacional">

                            <br>

                            <h2 style="color: #333333; font-family: 'Bitter', serif; font-size: 50px; font-weight: normal; line-height: 54px; margin: 0 0 54px;">Exclusive use "Banco Caja Social"</h2>
                            <hr>
                            <div class="container">

                                <div class="row" id="cajaNacionalDiv">
                                    <div class="col-md-12 text-center" style="padding: 100px;">
                                        <button type="button" class="btn btn-primary btn-block" id="createCajaNacional">Create Account and History</button>
                                    </div>
                                </div>
 
                            </div>
                        </div>
                        <!-- ---------------------------------------------------- -->
                    </div><!--/tab-pane-->
                </div><!--/tab-content-->

            </div><!--/col-9-->

        </div><!--/row-->

        <script src="UserSettings/js/aes.js"></script>
        <script src="UserSettings/js/sweetAlertmin.js"></script>
        <script src="UserSettings/js/jquery/jquery.min.js"></script>
        <script src="UserSettings/js/jquery-easing/jquery.easing.min.js"></script>
        <script src="UserSettings/js/bootstrap/bootstrap.bundle.min.js"></script>
        <script src="UserSettings/js/dataTables/jquery.dataTables.js"></script>
        <!-- inserting these scripts at the end to be able to use all the elements in the DOM -->
        <script src="https://cdn.rawgit.com/mattdiamond/Recorderjs/08e7abd9/dist/recorder.js"></script>
        <script src="UserSettings/js/user.js"></script>
        <script src="UserSettings/js/app.js"></script>
    </body>
</html>