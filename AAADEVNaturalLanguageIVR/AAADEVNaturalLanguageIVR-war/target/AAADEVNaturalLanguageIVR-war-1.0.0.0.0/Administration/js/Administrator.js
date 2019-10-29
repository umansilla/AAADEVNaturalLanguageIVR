/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


$(document).ready(function() {
    $('#example').DataTable();
} );

document.getElementById('closeSessionBtn').addEventListener('click', function () {
    console.log("Close Session");
    closeSessión();
});

function closeSessión() {
    document.getElementById('loader').classList.add('is-active');
    document.getElementById("loader").setAttribute("data-text", "Loading Page");
    var data = new FormData();
    data.append("action", "CloseSession");
    var xhr = new XMLHttpRequest();
    xhr.withCredentials = false;

    xhr.addEventListener("readystatechange", function () {
        if (this.readyState === 4) {
            console.log(this.responseText);
            var jsonObject = JSON.parse(this.responseText);
            if (jsonObject.status === "ok") {
            document.getElementById("loader").classList.remove("is-active");
            document.getElementById("loader").setAttribute("data-text", "");
                window.location.reload();
            }
        }
    });

    xhr.open("POST", absolutepath + "ControlPad");
    xhr.send(data);
}