/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


console.log("Index.js");
/*
 * 
 * Variables
 */
var absolutepath = getAbsolutePath();
let angv = 0;
let sadv = 0;
let fearv = 0;
let disgv = 0;
let joyv = 0;
var checkboxes = document.getElementsByClassName('borrar_contacto');
var btn_borrar = document.getElementById('btn_borrar');
var lenguaje = document.getElementById('languageId').innerHTML;
var lenguajeArray = [];
if (lenguaje === "Español") {
    console.log(lenguaje);
    lenguajeArray = [
        "Enfado",
        "Tristeza",
        "Temor",
        "Rechazo",
        "Alegría"
    ];
}
if (lenguaje === "Portugues") {
    lenguajeArray = [
        "Zangado",
        "Tristeza",
        "Medo",
        "Rejeição",
        "Alegria"
    ];
}
if (lenguaje === "English") {
    lenguajeArray = [
        "Anger",
        "Sadness",
        "Fear",
        "Rejection",
        "Joy"
    ];
}


// Set timeout variables.
var timoutWarning = 60000; // Display warning in 1Mins.
var timoutNow = 900000; // Timeout in 2 mins.
var warningTimer;
var timeoutTimer;




/*
 * 
 * Event Listeners
 */

document.getElementById('homeRedirectPage').addEventListener('click', function () {
    console.log("Home");
    window.location.replace(absolutepath + "ControlPad?Location=home");
});

btn_borrar.addEventListener('click', function () {
    checkBoxSeleccionado();
});

document.getElementById('closeSessionBtn').addEventListener('click', function () {
    console.log("Close Session");
    closeSessión();
});



/*
 * Funciones
 */

function closeSessión() {
    var data = new FormData();
    data.append("action", "CloseSession");
    var xhr = new XMLHttpRequest();
    xhr.withCredentials = false;

    xhr.addEventListener("readystatechange", function () {
        if (this.readyState === 4) {
            console.log(this.responseText);
            var jsonObject = JSON.parse(this.responseText);
            if (jsonObject.status === "ok") {
                window.location.reload();
            }
        }
    });

    xhr.open("POST", absolutepath + "ControlPad");
    xhr.send(data);
}

function checkBoxSeleccionado() {

    if (checkboxes.length === 0) {
        Swal.fire({
            type: 'error',
            title: 'No audios selected to Delete'
        });
    } else {
        var grabaciones = [];
        for (i = 0; i < checkboxes.length; i++) {
            if (checkboxes[i].checked === true) {
                grabaciones.push(checkboxes[i].name);
            }
        }


        Swal.fire({
            title: 'Surely you want to erase the selected audios?',
            text: "",
            type: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33',
            confirmButtonText: 'Yes, delete it!'
        }).then((result) => {
            if (result.value) {
                makePostDelete();
            }
        });
    }

    function makePostDelete() {
        console.log("Delete");
        console.log(JSON.stringify(grabaciones));
        var data = new FormData();
        data.append("action", "DeleteAudios");
        data.append("AudiosArray", JSON.stringify(grabaciones));

        var xhr = new XMLHttpRequest();
        xhr.withCredentials = false;

        xhr.addEventListener("readystatechange", function () {
            if (this.readyState === 4) {
                console.log(this.responseText);
                var jsonObject = JSON.parse(this.responseText);
                if (jsonObject.status === "ok") {
                    for (var i = 0; i < jsonObject.deletedAudios.length; i++) {
                        var elementToDelete = document.getElementById(jsonObject.deletedAudios[i]);
                        elementToDelete.parentNode.removeChild(elementToDelete);
                    }
                    Swal.fire(
                            'Good job!',
                            'Audio Files Deleted!',
                            'success'
                            );
                }
            }
        });

        xhr.open("POST", absolutepath + "ControlPad");
        xhr.send(data);

    }

}

function for_check() {
    for (var i = 0; i < checkboxes.length; i++) {
        checkboxes[i].addEventListener('change', function () {
            if (this.checked) {
                this.parentNode.parentNode.classList.add('activo_check');
            } else {
                this.parentNode.parentNode.classList.remove('activo_check');
            }
        });
    }
}



function getAbsolutePath() {
    var loc = window.location;
        var pathName = loc.pathname.substring(0, loc.pathname.lastIndexOf('/') + 1);
    return loc.href.substring(0, loc.href.length - ((loc.pathname + loc.search + loc.hash).length - pathName.length));
}


function Ver(_this) {
    // Get the modal
    var modal = document.getElementById("myModal");
// When the user clicks on the button, open the modal 

    modal.style.display = "block";

// When the user clicks anywhere outside of the modal, close it
    window.onclick = function (event) {
        if (event.target == modal) {
            modal.style.display = "none";
        }
    }

    console.log(_this.parentNode.parentNode.id);
    var wavFile = _this.parentNode.parentNode.id;
    var data = new FormData();

    data.append("action", "GetEmotions");
    data.append("wavFile", wavFile);

    var xhr = new XMLHttpRequest();
    xhr.withCredentials = false;

    xhr.addEventListener("readystatechange", function () {
        if (this.readyState === 4) {
            console.log(this.responseText);
            var jsonObject = JSON.parse(this.responseText);
            angv = (jsonObject.anger * 100);
            sadv = (jsonObject.sadness * 100);
            fearv = (jsonObject.fear * 100);
            disgv = (jsonObject.disgust * 100);
            joyv = (jsonObject.joy * 100);

            var Enfado = angv,
                    Tristeza = sadv,
                    Temor = fearv,
                    Rechazo = disgv,
                    Alegria = joyv;

            var perfil = Math.max(Enfado, Tristeza, Temor, Rechazo, Alegria);
            var variableMasAlta = Enfado === perfil ? lenguajeArray[0] :
                    Tristeza === perfil ? lenguajeArray[1] :
                    Temor === perfil ? lenguajeArray[2] :
                    Rechazo === perfil ? lenguajeArray[3] :
                    Alegria === perfil ? lenguajeArray[4] : null;


            document.getElementById('myModal').display = "none";

            var chart = new CanvasJS.Chart("chartContainer", {
                exportEnabled: true,
                animationEnabled: true,
                title: {
                    text: "Puntaje mayor " + variableMasAlta + " = " + Math.max(angv, sadv, fearv, disgv, joyv) + "%"
                },
                legend: {
                    cursor: "pointer",
                    itemclick: explodePie
                },
                data: [{
                        type: "pie",
                        startAngle: 25,
                        toolTipContent: "<b>{label}</b>: {y}%",
                        showInLegend: "false",
                        legendText: "{label}",
                        indexLabelFontSize: 16,
                        indexLabel: "{label} - {y}%",
                        dataPoints: [
                            {y: angv, label: lenguajeArray[0]},
                            {y: sadv, label: lenguajeArray[1]},
                            {y: fearv, label: lenguajeArray[2]},
                            {y: disgv, label: lenguajeArray[3]},
                            {y: joyv, label: lenguajeArray[4]}

                        ]
                    }]
            });

            chart.render();

            function explodePie(e) {
                if (typeof (e.dataSeries.dataPoints[e.dataPointIndex].exploded) === "undefined" || !e.dataSeries.dataPoints[e.dataPointIndex].exploded) {
                    e.dataSeries.dataPoints[e.dataPointIndex].exploded = true;
                } else {
                    e.dataSeries.dataPoints[e.dataPointIndex].exploded = false;
                }
                e.chart.render();

            }


        }
    });

    xhr.open("POST", absolutepath + "ControlPad");

    xhr.send(data);
}

$(document).ready(function () {
    $('#example').DataTable();

});

function restartSessionTime() {
    var data = new FormData();
    data.append("action", "RestartTimeSession");
    var xhr = new XMLHttpRequest();
    xhr.withCredentials = true;

    xhr.addEventListener("readystatechange", function () {
        if (this.readyState === 4) {
            var jsonObject = JSON.parse(this.responseText);
            if (jsonObject.status === "ok") {
                console.log("Session restarted");
            }
        }
    });

    xhr.open("POST", absolutepath + "ControlPad");
    xhr.send(data);
}


// Start timers.
function StartTimers() {
    warningTimer = setTimeout("IdleWarning()", timoutWarning);
    timeoutTimer = setTimeout("IdleTimeout()", timoutNow);
}

// Reset timers.
function ResetTimers() {
    clearTimeout(warningTimer);
    clearTimeout(timeoutTimer);
    StartTimers();
    restartSessionTime();
}

// Show idle timeout warning dialog.
function IdleWarning() {
    Swal.fire({
        title: 'Time Out Session',
        text: "The session is about to expire please click on accept to continue with the session",
        type: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Acept!'
    });
}

// Logout the user.
function IdleTimeout() {
    closeSessión();
}