/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

console.log("ControlRestricciones");

/* 
 *  Variables
 */
var absolutepath = getAbsolutePath();
console.log(absolutepath);

/*
 * Eventos
 */
getRestricciones();
getCDR();
setInterval(getCDR, 3000);

document.getElementById('createRestriccion').addEventListener('click', function (e) {
    e.preventDefault();
    console.log("Click en Crear");
    var Tbody = document.getElementById('Tbody');
    console.log(Tbody.children);
    var TRinput = document.createElement('TR');
    //Creatión de TD's
    //Creación de ID
    console.log("id = " + (Tbody.children.length + 1));
    var TDid = document.createElement("TD");
    var TDidTextNode = document.createTextNode((Tbody.children.length + 1));
    TDid.appendChild(TDidTextNode);
    //Creación de Cliente Input
    var TDcliente = document.createElement('TD');
    var TDclienteInput = document.createElement('INPUT');
    TDclienteInput.setAttribute('type', 'text');
    TDclienteInput.setAttribute('name', 'Cliente');
    TDclienteInput.setAttribute('id', 'clienteId');
    TDclienteInput.setAttribute('placeholder', 'Cliente');
    TDcliente.appendChild(TDclienteInput);
    //Creación de Extensión Input
    var TDextension = document.createElement('TD');
    var TDextensionInput = document.createElement('INPUT');
    TDextensionInput.setAttribute('type', 'text');
    TDextensionInput.setAttribute('name', 'Extension');
    TDextensionInput.setAttribute('id', 'extensionId');
    TDextensionInput.setAttribute('placeholder', 'Extension...');
    TDextension.appendChild(TDextensionInput);
    //Creación de Código de Restriccion.
    var TDcodigoRestriccion = document.createElement('TD');
    var TDcodigoRestriccionInput = document.createElement('INPUT');
    TDcodigoRestriccionInput.setAttribute('type', 'text');
    TDcodigoRestriccionInput.setAttribute('name', 'Codigo');
    TDcodigoRestriccionInput.setAttribute('id', 'codigoId');
    TDcodigoRestriccionInput.setAttribute('placeholder', 'Codigo de Restricción');
    TDcodigoRestriccion.appendChild(TDcodigoRestriccionInput);
    //Creación de ROL ASIGNADO
    var TDrolAsignado = document.createElement('TD');
    var TDrolAsignadoInput = document.createElement('INPUT');
    TDrolAsignadoInput.setAttribute('type', 'text');
    TDrolAsignadoInput.setAttribute('name', 'Rol');
    TDrolAsignadoInput.setAttribute('id', 'rolAsignadoId');
    TDrolAsignadoInput.setAttribute('placeholder', 'Número del 1 - 16');
    TDrolAsignado.appendChild(TDrolAsignadoInput);
    //Creación de Boton Finalizar
    var TDboton = document.createElement('TD');
    var TDbotonElement = document.createElement('BUTTON');
    TDbotonElement.setAttribute('type', 'button');
    TDbotonElement.setAttribute('class', 'btn btn-success');
    TDbotonElement.setAttribute('onclick', 'creacionDeRestriccion(this)');
    var TDbotonElementTextNode = document.createTextNode('OK!');
    TDbotonElement.appendChild(TDbotonElementTextNode);
    TDboton.appendChild(TDbotonElement);

    //Append To TD to TR
    TRinput.appendChild(TDid);
    TRinput.appendChild(TDcliente);
    TRinput.appendChild(TDextension);
    TRinput.appendChild(TDcodigoRestriccion);
    TRinput.appendChild(TDrolAsignado);
    TRinput.appendChild(TDboton);


    //Append TD to Tbody
    Tbody.appendChild(TRinput);

});


/*
 * Funciones
 */

function getCDR() {
    var data = null;

    var xhr = new XMLHttpRequest();
    xhr.withCredentials = true;

    xhr.addEventListener("readystatechange", function () {
        if (this.readyState === 4) {
            
            var obtenerTexto = this.responseText;
            var cdrLog = document.getElementById('lectureCDR');
            cdrLog.innerHTML = "";
            var arregloDeTexto = obtenerTexto.split(",");
            console.log(arregloDeTexto);
            for (var i = 0; i <= arregloDeTexto.length - 1; i++) {
                var saltoElemnt = document.createElement('BR');
                var textNode = document.createTextNode(arregloDeTexto[i]);
                cdrLog.appendChild(textNode);
                cdrLog.appendChild(saltoElemnt);
            }
            
        }
    });

    xhr.open("GET", absolutepath + "ReadCDR");
    xhr.send(data);
}


function getRestricciones() {
    var data = new FormData();
    data.append("action", "READ");

    var xhr = new XMLHttpRequest();
    xhr.withCredentials = false;

    xhr.addEventListener("readystatechange", function () {
        if (this.readyState === 4) {
            var TBody = document.getElementById('Tbody');
            var jsonObject = JSON.parse(this.responseText);
            document.getElementById('totalRest').innerHTML = jsonObject.length;
            if (jsonObject.length < 0) {
                console.log("Menor a cero");
            } else {
                for (var i = 0; i <= jsonObject.length - 1; i++) {
                    console.log(jsonObject[i].extension);
                    var TR = document.createElement('TR');
                    //CREATE TD ID
                    var TDid = document.createElement('TD');
                    var TDidTextNode = document.createTextNode(jsonObject[i].id);
                    TDid.appendChild(TDidTextNode);
                    //CREATE TD CLIENTE
                    var TDcliente = document.createElement('TD');
                    var TDclienteTextNode = document.createTextNode(jsonObject[i].cliente);
                    TDcliente.appendChild(TDclienteTextNode);
                    //CREATE TD EXT
                    var TDext = document.createElement('TD');
                    var TDextTextNode = document.createTextNode(jsonObject[i].extension);
                    TDext.appendChild(TDextTextNode);
                    //CREATE TD CODIGO
                    var TDcod = document.createElement('TD');
                    var TDcodTextNode = document.createTextNode(jsonObject[i].codigo);
                    TDcod.appendChild(TDcodTextNode);
                    //CREATE TD ROL
                    var TDrol = document.createElement('TD');
                    var TDrolTextNode = document.createTextNode(jsonObject[i].rolAsignado);
                    TDrol.appendChild(TDrolTextNode);
                    //CREATE BUTTON
                    var TDbtnBorrar = document.createElement('TD');
                    var buttonBorrar = document.createElement('A');
                    buttonBorrar.setAttribute('class', 'glyphicon glyphicon-remove');
                    buttonBorrar.setAttribute('onclick', 'borrarParticipante(this)');
                    buttonBorrar.setAttribute('style', 'padding: 7px; cursor: pointer;')

                    var buttonEditar = document.createElement('A');
                    buttonEditar.setAttribute('class', 'glyphicon glyphicon-edit');
                    buttonEditar.setAttribute('onclick', 'editarParticipante(this)');
                    buttonEditar.setAttribute('style', 'padding: 7px; cursor: pointer;')


                    TDbtnBorrar.appendChild(buttonBorrar);
                    TDbtnBorrar.appendChild(buttonEditar);

                    //APPEND TD's en TR
                    TR.appendChild(TDid);
                    TR.appendChild(TDcliente);
                    TR.appendChild(TDext);
                    TR.appendChild(TDcod);
                    TR.appendChild(TDrol);
                    TR.appendChild(TDbtnBorrar);

                    //Append TR's in TBODY
                    TBody.appendChild(TR);
                }


            }
        }
    });

    xhr.open("POST", absolutepath + "ControlRestricciones");
    xhr.send(data);
}

function borrarParticipante(_this) {
    var participanteId = _this.parentElement.parentElement.children[0].innerText;
    Swal.fire({
        title: '¿Estas seguro de borrar la restricción con ID ' + participanteId + ' ?',
        type: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Si!'
    }).then((result) => {
        if (result.value) {
            deleteRestriccion(participanteId);
        }
    });
}



function editarParticipante(_this) {
    console.log("Editar");
    console.log(_this.parentElement.parentElement.children);
    //Cliente
    var TDcliente = _this.parentElement.parentElement.children[1];
    var TDclienteValue = TDcliente.innerText;
    TDcliente.innerText = "";
    var TDclienteInput = document.createElement('INPUT');
    TDclienteInput.setAttribute('type', 'text');
    TDclienteInput.setAttribute('name', 'Cliente');
    TDclienteInput.setAttribute('id', 'clienteIdEdit');
    TDclienteInput.setAttribute('placeholder', TDclienteValue);
    TDcliente.appendChild(TDclienteInput);
    //Extension
    var TDext = _this.parentElement.parentElement.children[2];
    var TDextValue = TDext.innerText;
    TDext.innerHTML = "";
    var TDextInput = document.createElement('INPUT');
    TDextInput.setAttribute('type', 'text');
    TDextInput.setAttribute('name', 'Extension');
    TDextInput.setAttribute('id', 'extensionIdEdit');
    TDextInput.setAttribute('placeholder', TDextValue);
    TDext.appendChild(TDextInput);
    //Codigo
    var TDcod = _this.parentElement.parentElement.children[3];
    var TDcodValue = TDcod.innerText;
    TDcod.innerHTML = "";
    var TDcodInput = document.createElement('INPUT');
    TDcodInput.setAttribute('type', 'text');
    TDcodInput.setAttribute('name', 'codigo');
    TDcodInput.setAttribute('id', 'codIdEdit');
    TDcodInput.setAttribute('placeholder', TDcodValue);
    TDcod.appendChild(TDcodInput);
    //ROL
    var TDrol = _this.parentElement.parentElement.children[4];
    var TDrolValue = TDrol.innerText;
    TDrol.innerHTML = "";
    var TDrolInput = document.createElement('INPUT');
    TDrolInput.setAttribute('type', 'text');
    TDrolInput.setAttribute('name', 'rol');
    TDrolInput.setAttribute('id', 'rolIdEdit');
    TDrolInput.setAttribute('placeholder', TDrolValue);
    TDrol.appendChild(TDrolInput);
    //Acciones
    var TDaccion = _this.parentElement.parentElement.children[5];
    TDaccion.innerHTML = "";
    var TDbotonElement = document.createElement('BUTTON');
    TDbotonElement.setAttribute('type', 'button');
    TDbotonElement.setAttribute('class', 'btn btn-success');
    TDbotonElement.setAttribute('onclick', 'editRestriccion(this)');
    var TDbotonElementText = document.createTextNode('OK!');

    var TDbotonCancel = document.createElement('BUTTON');
    TDbotonCancel.setAttribute('type', 'button');
    TDbotonCancel.setAttribute('class', 'btn btn-danger');
    TDbotonCancel.setAttribute('onclick', 'cancelEdit()');
    var TDbotonCancelElementText = document.createTextNode('Cancel');

    TDbotonCancel.appendChild(TDbotonCancelElementText);
    TDbotonElement.appendChild(TDbotonElementText);
    TDaccion.appendChild(TDbotonElement);
    TDaccion.appendChild(TDbotonCancel);

}

function cancelEdit() {
    document.getElementById('Tbody').innerHTML = "";
    getRestricciones();
}

function editRestriccion(_this) {
    var valorBool = true;
    var valorId = _this.parentElement.parentElement.children[0].innerText;
    var valorCliente = document.getElementById('clienteIdEdit').value;
    var valorExtension = document.getElementById('extensionIdEdit').value;
    var valorCodigo = document.getElementById('codIdEdit').value;
    var valorRolAsignado = document.getElementById('rolIdEdit').value;
    if (!/^([0-9])*$/.test(valorCliente)) {
        alert("El valor en Cliente no es numérico");
        valorBool = false;
    }
    if (!/^([0-9])*$/.test(valorExtension)) {
        alert("El valor en Extension no es numérico");
        valorBool = false;
    }
    if (!/^([0-9])*$/.test(valorCodigo)) {
        alert("El valor en Código no es numérico");
        valorBool = false;
    }

    if (!/^([0-9])*$/.test(valorRolAsignado)) {
        alert("El valor en ROL no es numérico");
        valorBool = false;
    }
    if (valorCliente === "") {
        alert("Favor de llenar el campo de Cliente");
        valorBool = false;
    }
    if (valorExtension === "") {
        alert("Favor de llenar el campo de Extensión");
        valorBool = false;
    }
    if (valorCodigo === "") {
        alert("Favor de llenar el campo de valorCodigo");
        valorBool = false;
    }
    if (valorRolAsignado === "") {
        alert("Favor de llenar el campo de valorRolAsignado");
        valorBool = false;
    }
    if (valorRolAsignado < 1 || valorRolAsignado > 16) {
        alert("Favor de solo ingresar un valor de ROL del 1 al 16");
        valorBool = false;
    }
    if (valorBool) {
        editExtension(valorId, valorExtension, valorCodigo, valorRolAsignado, valorCliente);
    }
}

function getAbsolutePath() {
    var loc = window.location;
    var pathName = loc.pathname.substring(0, loc.pathname.lastIndexOf('/') + 1);
    return loc.href.substring(0, loc.href.length - ((loc.pathname + loc.search + loc.hash).length - pathName.length));
}

function creacionDeRestriccion(_this) {
    var valorBool = true;
    var valorId = _this.parentElement.parentElement.children[0].innerText;
    var valorCliente = document.getElementById('clienteId').value;
    var valorExtension = document.getElementById('extensionId').value;
    var valorCodigo = document.getElementById('codigoId').value;
    var valorRolAsignado = document.getElementById('rolAsignadoId').value;

    if (!/^([0-9])*$/.test(valorCliente)) {
        alert("El valor en Cliente no es numérico");
        valorBool = false;
    }
    if (!/^([0-9])*$/.test(valorExtension)) {
        alert("El valor en Extension no es numérico");
        valorBool = false;
    }
    if (!/^([0-9])*$/.test(valorCodigo)) {
        alert("El valor en Código no es numérico");
        valorBool = false;
    }

    if (!/^([0-9])*$/.test(valorRolAsignado)) {
        alert("El valor en ROL no es numérico");
        valorBool = false;
    }
    if (valorCliente === "") {
        alert("Favor de llenar el campo de Cliente");
        valorBool = false;
    }
    if (valorExtension === "") {
        alert("Favor de llenar el campo de Extensión");
        valorBool = false;
    }
    if (valorCodigo === "") {
        alert("Favor de llenar el campo de valorCodigo");
        valorBool = false;
    }
    if (valorRolAsignado === "") {
        alert("Favor de llenar el campo de valorRolAsignado");
        valorBool = false;
    }
    if (valorRolAsignado < 1 || valorRolAsignado > 16) {
        alert("Favor de solo ingresar un valor de ROL del 1 al 16");
        valorBool = false;
    }
    if (valorBool) {
        creaciónDeExtension(valorId, valorExtension, valorCodigo, valorRolAsignado, valorCliente);
    }
}

function creaciónDeExtension(valorId, valorExtension, valorCodigo, valorRolAsignado, valorCliente) {
    console.log("creaciónDeExtension");
    console.log("Valor id: " + valorId + " Valor Cliente: " + valorCliente + " Valor Extensión: " + valorExtension + " Valor código: " + valorCodigo + " Valor Asignado: " + valorRolAsignado);
    var data = new FormData();
    data.append("action", "CREATE");
    data.append("cliente", valorCliente);
    data.append("valorExtension", valorExtension);
    data.append("valorCodigo", valorCodigo);
    data.append("valorRolAsignado", valorRolAsignado);

    var xhr = new XMLHttpRequest();
    xhr.withCredentials = true;

    xhr.addEventListener("readystatechange", function () {
        if (this.readyState === 4) {

            var jsonObj = JSON.parse(this.responseText);
            if (jsonObj.status === "ok") {
                document.getElementById('Tbody').innerHTML = "";
                getRestricciones();
                const Toast = Swal.mixin({
                    toast: true,
                    position: 'top-end',
                    showConfirmButton: false,
                    timer: 3000
                });

                Toast.fire({
                    type: 'success',
                    title: 'Restricción creada'
                });
            }
            if (jsonObj.error === "error") {
                Swal.fire({
                    type: 'error',
                    title: 'Error',
                    text: 'Error al crear la restriccción',
                });
            }
        }
    });

    xhr.open("POST", absolutepath + "ControlRestricciones");
    xhr.send(data);
}

function editExtension(valorId, valorExtension, valorCodigo, valorRolAsignado, valorCliente) {
    console.log("editExtensions");
    console.log("Valor id: " + valorId + " Valor Cliente: " + valorCliente + " Valor Extensión: " + valorExtension + " Valor código: " + valorCodigo + " Valor Asignado: " + valorRolAsignado);
    var data = new FormData();
    data.append("action", "UPDATE");
    data.append("valorId", valorId);
    data.append("cliente", valorCliente);
    data.append("valorExtension", valorExtension);
    data.append("valorCodigo", valorCodigo);
    data.append("valorRolAsignado", valorRolAsignado);

    var xhr = new XMLHttpRequest();
    xhr.withCredentials = false;

    xhr.addEventListener("readystatechange", function () {
        if (this.readyState === 4) {
            var jsonObj = JSON.parse(this.responseText);
            if (jsonObj.status === "ok") {
                document.getElementById('Tbody').innerHTML = "";
                getRestricciones();
                const Toast = Swal.mixin({
                    toast: true,
                    position: 'top-end',
                    showConfirmButton: false,
                    timer: 3000
                });

                Toast.fire({
                    type: 'success',
                    title: 'Restricción editada'
                });
            }
            if (jsonObj.error === "error") {
                Swal.fire({
                    type: 'error',
                    title: 'Error',
                    text: 'Error al editar la restriccción',
                });
            }
        }
    });

    xhr.open("POST", absolutepath + "ControlRestricciones");
    xhr.send(data);
}

function deleteRestriccion(participanteId) {

    var data = new FormData();
    data.append("action", "DELETE");
    data.append("id", participanteId);

    var xhr = new XMLHttpRequest();
    xhr.withCredentials = false;

    xhr.addEventListener("readystatechange", function () {
        if (this.readyState === 4) {
            var jsonObj = JSON.parse(this.responseText);
            if (jsonObj.status === "ok") {
                document.getElementById('Tbody').innerHTML = "";
                getRestricciones();
                const Toast = Swal.mixin({
                    toast: true,
                    position: 'top-end',
                    showConfirmButton: false,
                    timer: 3000
                });

                Toast.fire({
                    type: 'success',
                    title: 'Restricción borrada'
                });
            }
            if (jsonObj.error === "error") {
                Swal.fire({
                    type: 'error',
                    title: 'Error',
                    text: 'Error al borrar la restriccción',
                });
            }
        }
    });

    xhr.open("POST", absolutepath + "ControlRestricciones");

    xhr.send(data);

}