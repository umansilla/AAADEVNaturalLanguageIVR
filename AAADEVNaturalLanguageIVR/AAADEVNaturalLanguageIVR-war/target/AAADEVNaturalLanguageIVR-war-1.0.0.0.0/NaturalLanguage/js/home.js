/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

console.log("Home.js");

/*
 * Variables
 */

var absolutepath = getAbsolutePath();


/*
 * Eventos
 */

document.getElementById('esSelect').addEventListener('click', function () {
    console.log("Español");
    changeLanguaje("es");
});

document.getElementById('ptSelect').addEventListener('click', function () {
    console.log("Portugués");
    changeLanguaje("pt");
});

document.getElementById('enSelect').addEventListener('click', function () {
    console.log("Ingles");
    changeLanguaje("en");
});


document.getElementById('homeRedirectPage').addEventListener('click', function () {
    window.location.reload();
});

document.getElementById('closeSessionBtn').addEventListener('click', function () {
    console.log("Close Session");
    var data = new FormData();
    data.append("action", "CloseSession");
    var xhr = new XMLHttpRequest();
    xhr.withCredentials = false;

    xhr.addEventListener("readystatechange", function () {
        if (this.readyState === 4) {
            console.log(this.responseText);
            var jsonObject = JSON.parse(this.responseText);
            if (jsonObject.status === "ok") {
            	window.location.replace(absolutepath + "ControlPad");
                Swal.fire(
                        'Good job!',
                        'Session Closed!',
                        'success'
                        );

            }
        }
    });

    xhr.open("POST", absolutepath + "ControlPad");
    xhr.send(data);
});

/*
 * Funciones
 */

function changeLanguaje(language) {
    var data = new FormData();
    data.append("action", "ChangeLanguage");
    data.append("language", language);

    var xhr = new XMLHttpRequest();
    xhr.withCredentials = false;

    xhr.addEventListener("readystatechange", function () {
        if (this.readyState === 4) {
            console.log(this.responseText);
            var jsonObj = JSON.parse(this.responseText);
            if(jsonObj.status === "ok"){
            	window.location.replace(absolutepath + "ControlPad?Location=NLU");
            }
        }
    });

    xhr.open("POST", absolutepath + "ControlPad");

    xhr.send(data);
}

function getAbsolutePath() {
    var loc = window.location;
    var pathName = loc.pathname.substring(0, loc.pathname.lastIndexOf('/') + 1);
    return loc.href.substring(0, loc.href.length - ((loc.pathname + loc.search + loc.hash).length - pathName.length));
}
