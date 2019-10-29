/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

console.log('LogIn.js');

var absolutepath = getAbsolutePath();
function getAbsolutePath() {
    var loc = window.location;
    var pathName = loc.pathname.substring(0, loc.pathname.lastIndexOf('/') + 1);
    return loc.href.substring(0, loc.href.length - ((loc.pathname + loc.search + loc.hash).length - pathName.length));
}

document.getElementById('submitbtn').addEventListener('click', function (e) {
    e.preventDefault();
    console.log("Submit");
    var email = validateEmail(document.getElementById('email').value);

    if (document.getElementById('country').value === "" || document.getElementById('country').value.length === 0) {
        Swal({
            type: 'error',
            title: 'Error',
            text: 'Please establish the country of origin.'

        });
    } else if (document.getElementById('cliente').value === "" || document.getElementById('cliente').value.length === 0 || /^\s+$/.test(document.getElementById('cliente').value) || document.getElementById('cliente').value.match(/Avaya/) || document.getElementById('cliente').value.match(/AVAYA/) || document.getElementById('cliente').value.match(/avaya/)) {
        Swal({
            type: 'error',
            title: 'Error',
            text: 'Please do not enter Avaya as a customer.'

        });
    } else if (email !== true) {
        Swal({
            type: 'error',
            title: 'Error',
            text: 'Please enter the email correctly'
        });
    } else if (document.getElementById('pass').value === "" && email !== true) {
        Swal({
            type: 'error',
            title: 'Error',
            text: 'Please enter the password'
        });
    } else {
        var encryptedAES = CryptoJS.AES.encrypt(document.getElementById('pass').value, "secret");
        var data = new FormData();
        data.append("action", "LogIn");
        data.append("Email", document.getElementById('email').value);
        data.append("Pass", encryptedAES);
        data.append("Cliente", document.getElementById('cliente').value);
        data.append("Pais", document.getElementById('country').value);
        
        var xhr = new XMLHttpRequest();
        xhr.withCredentials = false;

        xhr.addEventListener("readystatechange", function () {
            if (this.readyState === 4) {
                var result = JSON.parse(this.responseText);
                if (result.status === "ok") {
                    console.log("Result OK");

                    window.location.reload();
                }
                if (result.status === "error") {
                    Swal({
                        type: 'error',
                        title: 'Error',
                        text: 'Please retry logon'
                    });
                }
            }
        });

        xhr.open("POST", absolutepath + "LogIn");

        xhr.send(data);
    }

});

document.getElementById('forgotten').addEventListener('click', function (e) {
    e.preventDefault();
    console.log("Forgotten");
});


function validateEmail(email) {
    var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return re.test(email);
}
