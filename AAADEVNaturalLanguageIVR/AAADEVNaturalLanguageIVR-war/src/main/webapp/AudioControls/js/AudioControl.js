/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


console.log("AudioControls.js");
/*
 * Variables
 */
var global = [];
var absolutepath = getAbsolutePath();
obtenerCredenciales();
/*
 * Event Listeners
 */

document.getElementById('closeSessionBtn').addEventListener('click', function () {
    console.log("Close Session");
    closeSessión();
});


document.getElementById('downloadAudioMap').addEventListener('click', function (e){
   e.preventDefault();
   console.log("Download File Map");
   window.open(absolutepath + "audioMap.xlsx");
});

document.getElementById('createDirectory').addEventListener('click', function (e) {
    e.preventDefault();
    console.log("Create New Folder");
    Swal.fire({
        title: '<strong>Please Put de Directory Name</strong>',
        type: 'info',
        html: '<strong>Directory Name</strong>' +
                '<hr>' +
                '<input type="text" id="inputTextDirectoryName" name="newFile">' +
                '<hr>',
        showCloseButton: true,
        showCancelButton: true,
        focusConfirm: false,
        confirmButtonText:
                '<i class="glyphicon glyphicon-thumbs-up" id="upload"></i> OK!',
        cancelButtonText:
                '<i class="glyphicon glyphicon-thumbs-down id="cancel"></i>'
    }).then((result) => {
        if (result.value) {
            creteNewFile();
        }
    });
    function creteNewFile() {
        console.log("Create File");
        console.log();
        var data = new FormData();
        data.append("action", "createFile");
        data.append("directoryName", document.getElementById('inputTextDirectoryName').value);
        var xhr = new XMLHttpRequest();
        xhr.withCredentials = false;
        xhr.addEventListener("readystatechange", function () {
            if (this.readyState === 4) {
                console.log(this.responseText);
                var jsonObjectCreateDirectory = JSON.parse(this.responseText);
                if (jsonObjectCreateDirectory.status === "ok") {
                    Swal.fire(
                            'Good job!',
                            'The folder was created successfully!',
                            'success'
                            );
                    location.reload();
                }
                if (jsonObjectCreateDirectory.status === "the file already exists") {
                    Swal.fire(
                            'Opps!',
                            'The file already exists',
                            'error'
                            );
                }
                if (jsonObjectCreateDirectory.status === "error") {
                    Swal.fire(
                            'Opps!',
                            'Error creating directory',
                            'error'
                            );
                }
            }
        });
        xhr.open("POST", absolutepath + "Admin/Audios");
        xhr.send(data);
    }

});
/*
 * funciones
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


function obtenerCredenciales() {
    var data = null;
    var xhr = new XMLHttpRequest();
    xhr.withCredentials = false;
    xhr.addEventListener("readystatechange", function () {
        if (this.readyState === 4) {
            var jsonObject = JSON.parse(this.responseText);
            var principalContainer = document.getElementById('principalContainer');
            for (var i = 0; i < jsonObject.length; i++) {
                console.log(i);
                //CREACIÓN DEL DIV CARD
                var divCard = document.createElement('DIV');
                divCard.setAttribute('class', 'card');
                //IMAGEN
                var imageCard = document.createElement('IMG');
                imageCard.setAttribute("class", "card-img-top");
                imageCard.setAttribute("style", "margin: 0 auto;");
                var subCarpetasIMG = jsonObject[i].subCarpetas.image;
                if (subCarpetasIMG === null || subCarpetasIMG === "" || subCarpetasIMG === undefined) {
                    imageCard.setAttribute("src", "img/avaya.png");
                } else {
                    imageCard.setAttribute("src", subCarpetasIMG);
                }
                //CREACION DE CARD BODY 
                var divCardBody = document.createElement('DIV');
                divCardBody.setAttribute("class", "card-body");
                //CREACION DE H5 NOMBRE DE LA CREDENCIAL

                //CREACION ROW PARA CARD BODY BUTTONS
                var divRowCardBody = document.createElement('DIV');
                divRowCardBody.setAttribute('class', 'row');
                var divRowFirstCol = document.createElement('DIV');
                divRowFirstCol.setAttribute('class', 'col');
                var h5CardName = document.createElement('H5');
                h5CardName.setAttribute("class", "card-title");
                var h5CardNameTextNode = document.createTextNode('Directory Name: ');
                var strongElement = document.createElement('STRONG');
                var strongElementTextNode = document.createTextNode(jsonObject[i].directoryName);
                strongElement.appendChild(strongElementTextNode);
                h5CardName.appendChild(h5CardNameTextNode);
                h5CardName.appendChild(strongElement);
                divRowFirstCol.appendChild(h5CardName);
                var divRowSecondCol = document.createElement('DIV');
                divRowSecondCol.setAttribute('class', 'col-md-auto');
                var pCardName = document.createElement('P');
                var pCardNameTextNode = document.createTextNode('');
                pCardName.appendChild(pCardNameTextNode);
                divRowSecondCol.appendChild(pCardName);
                var divRowThirdCol = document.createElement('DIV');
                divRowThirdCol.setAttribute('class', 'col col-lg-2');
                //BOTON EDITAR NOMBRE DEL DIRECTORIO
                var btnEditNameDirectorie = document.createElement('BUTTON');
                btnEditNameDirectorie.setAttribute('type', 'buttton');
                btnEditNameDirectorie.setAttribute('class', 'btn btn-default btn-sm');
                btnEditNameDirectorie.setAttribute('style', 'float: right;');
                btnEditNameDirectorie.setAttribute('onclick', 'changeNameDirectoorie(this)');
                var spanEditNameIcon = document.createElement('SPAN');
                spanEditNameIcon.setAttribute('class', 'glyphicon glyphicon-pencil');
                btnEditNameDirectorie.appendChild(spanEditNameIcon);
                divRowThirdCol.appendChild(btnEditNameDirectorie);
                //BOTON CAMBIAR IMAGEN
                var btnUploadNewImage = document.createElement('BUTTON');
                btnUploadNewImage.setAttribute('type', 'buttton');
                btnUploadNewImage.setAttribute('class', 'btn btn-default btn-sm');
                btnUploadNewImage.setAttribute('style', 'float: right;');
                btnUploadNewImage.setAttribute('onclick', 'changeImageDirectorie(this)');
                var spanUploadNewImage = document.createElement('SPAN');
                spanUploadNewImage.setAttribute('class', 'glyphicon glyphicon-picture');
                btnUploadNewImage.appendChild(spanUploadNewImage);
                divRowThirdCol.appendChild(btnUploadNewImage);
                //BOTON PARA BORRAR DIRECTORIO
                var btnDeleteDirectory = document.createElement('BUTTON');
                btnDeleteDirectory.setAttribute('type', 'buttton');
                btnDeleteDirectory.setAttribute('class', 'btn btn-default btn-sm');
                btnDeleteDirectory.setAttribute('style', 'float: right;');
                btnDeleteDirectory.setAttribute('onclick', 'deleteDirectoy(this)');
                var spanDeleteDirectory = document.createElement('SPAN');
                spanDeleteDirectory.setAttribute('class', 'glyphicon glyphicon-remove');
                btnDeleteDirectory.appendChild(spanDeleteDirectory);
                divRowThirdCol.appendChild(btnDeleteDirectory);
                //BOTON PARA DESCARGAR ARCHIVO ZIP
                var btnDownloadZipDirectory = document.createElement('BUTTON');
                btnDownloadZipDirectory.setAttribute('type', 'buttton')
                btnDownloadZipDirectory.setAttribute('class', 'btn btn-default btn-sm')
                btnDownloadZipDirectory.setAttribute('style', 'float: right;')
                btnDownloadZipDirectory.setAttribute('onclick', 'downloadZipFIle(this)');
                var spanDownloadZipDirectory = document.createElement('SPAN');
                spanDownloadZipDirectory.setAttribute('class', 'glyphicon glyphicon-download-alt');
                btnDownloadZipDirectory.appendChild(spanDownloadZipDirectory);
                divRowThirdCol.appendChild(btnDownloadZipDirectory);
                divRowCardBody.appendChild(divRowFirstCol);
                divRowCardBody.appendChild(divRowSecondCol);
                divRowCardBody.appendChild(divRowThirdCol);
                /***********************************************************/
                //CREACION BTN GROUP EN Y PANEL CONTENT EN
                var divBtnGroupEN = document.createElement('DIV');
                divBtnGroupEN.setAttribute("class", "btn-group btn-group-justified");
                divBtnGroupEN.setAttribute("role", "group");
                divBtnGroupEN.setAttribute("aria-label", "...");
                //CREACION DIV BOTON activeDirectorieButtonEN
                var divBtnDirectorieEN = document.createElement('DIV');
                divBtnDirectorieEN.setAttribute("class", "btn-group");
                divBtnDirectorieEN.setAttribute("role", "group");
                //CREACION DE BOTON activeDirectorieButtonEN
                var btnActiveDirectorieEN = document.createElement('BUTTON');
                btnActiveDirectorieEN.setAttribute("type", "button");
                btnActiveDirectorieEN.setAttribute("class", "btn btn-default");
                btnActiveDirectorieEN.setAttribute("onclick", "activeDirectorieButtonEN(this)");
                //ICON
                var spanActiveDirectorieEN = document.createElement('SPAN');
                spanActiveDirectorieEN.setAttribute("class", "glyphicon glyphicon-folder-close");
                //TEXTO
                var btnActiveDirectorieENText = document.createTextNode("  EN");
                btnActiveDirectorieEN.appendChild(spanActiveDirectorieEN);
                btnActiveDirectorieEN.appendChild(btnActiveDirectorieENText);
                divBtnDirectorieEN.appendChild(btnActiveDirectorieEN);
                divBtnGroupEN.appendChild(divBtnDirectorieEN);
                /******************************************************/
                //CREACION DEL DIV DeleteAudiosEN
                var divDeleteAudiosEN = document.createElement('DIV');
                divDeleteAudiosEN.setAttribute("class", "btn-group");
                divDeleteAudiosEN.setAttribute("role", "group");
                //CREACION DE BOTON DeleteAudiosEN
                var btnDeleteAudiosEN = document.createElement('BUTTON');
                btnDeleteAudiosEN.setAttribute("type", "button");
                btnDeleteAudiosEN.setAttribute("class", "btn btn-default");
                btnDeleteAudiosEN.setAttribute("onclick", "deleteAudios()");
                //ICON
                var spanDeleteAudiosEN = document.createElement('SPAN');
                spanDeleteAudiosEN.setAttribute("class", "glyphicon glyphicon-trash");
                //TEXTO
                var btnDeleteAudiosENText = document.createTextNode("  Delete Audio(s)");
                btnDeleteAudiosEN.appendChild(spanDeleteAudiosEN);
                btnDeleteAudiosEN.appendChild(btnDeleteAudiosENText);
                divDeleteAudiosEN.appendChild(btnDeleteAudiosEN);
                divBtnGroupEN.appendChild(divDeleteAudiosEN);
                /******************************************************/
                //CREACION DEL DIV UploadAudioEN
                var divUploadAudioEN = document.createElement('DIV');
                divUploadAudioEN.setAttribute("class", "btn-group");
                divUploadAudioEN.setAttribute("role", "group");
                //CREACION DE BOTON UploadAudioEN
                var btnUploadAudioEN = document.createElement('BUTTON');
                btnUploadAudioEN.setAttribute("type", "button");
                btnUploadAudioEN.setAttribute("class", "btn btn-default");
                btnUploadAudioEN.setAttribute("onclick", "uploadAudio(this)");
                btnUploadAudioEN.setAttribute("id", "English," + jsonObject[i].directoryName);
                //ICON
                var spanUploadAudioEN = document.createElement('SPAN');
                spanUploadAudioEN.setAttribute("class", "glyphicon glyphicon-upload");
                //TEXTO
                var btnUploadAudioENText = document.createTextNode("   Upload Audio");
                btnUploadAudioEN.appendChild(spanUploadAudioEN);
                btnUploadAudioEN.appendChild(btnUploadAudioENText);
                divUploadAudioEN.appendChild(btnUploadAudioEN);
                divBtnGroupEN.appendChild(divUploadAudioEN);
                //CREACION DE PANEL
                var divPanel = document.createElement('DIV');
                divPanel.setAttribute('class', 'panel panel-default toggle-content');
                divPanel.setAttribute('id', 'EN,' + jsonObject[i].directoryName);
                var resultEN = [];
                for (var k in jsonObject[i].subCarpetas.En.audiosEN)
                    resultEN.push([jsonObject[i].subCarpetas.En.audiosEN [k]]);
                for (var j = 0; j < Object.keys(jsonObject[i].subCarpetas.En.audiosEN).length; j++) {
                    var divPanelBody = document.createElement('DIV');
                    divPanelBody.setAttribute('class', 'panel-body');
                    divPanelBody.setAttribute('style', 'padding: 0px; border: 1px solid gray;');
                    var divRow = document.createElement('DIV');
                    divRow.setAttribute('class', 'row');
                    //CREACION DEL NOMBRE DEL AUDIO
                    var divAudioName = document.createElement('DIV');
                    divAudioName.setAttribute('class', 'col-md-5 text-center');
                    var h3AudioName = document.createElement('H3');
                    var h2AudioNameTextNode = document.createTextNode(Object.keys(jsonObject[i].subCarpetas.En.audiosEN)[j]);
                    h3AudioName.appendChild(h2AudioNameTextNode);
                    divAudioName.appendChild(h3AudioName);
                    divRow.appendChild(divAudioName);
                    //CREACION DEL ELEMENTO AUDIO
                    var divAudioElement = document.createElement('DIV');
                    divAudioElement.setAttribute('class', 'col-md-6');
                    var audioElement = document.createElement('AUDIO');
                    audioElement.setAttribute('controls', 'true');
                    audioElement.setAttribute('style', 'width:100%');
                    var sourceAudioElement = document.createElement('SOURCE');
                    sourceAudioElement.setAttribute('src', resultEN[j]);
                    sourceAudioElement.setAttribute('type', 'audio/wav');
                    audioElement.appendChild(sourceAudioElement);
                    divAudioElement.appendChild(audioElement);
                    divRow.appendChild(divAudioElement);
                    //CREACION DEL ELEMENTO CHEK BOX
                    var divCheckBox = document.createElement('DIV');
                    divCheckBox.setAttribute('class', 'col-md-1 text-center');
                    divCheckBox.setAttribute('style', 'padding-top: 17px;');
                    var inputElement = document.createElement('INPUT');
                    inputElement.setAttribute('type', 'checkbox');
                    inputElement.setAttribute('onclick', 'checkBox(this)');
                    divCheckBox.appendChild(inputElement);
                    divRow.appendChild(divCheckBox);
                    //Agregando a Panel Body 
                    divPanelBody.appendChild(divRow);
                    divPanel.appendChild(divPanelBody);
                }

                //CREACION BTN GROUP ES Y PANEL CONTENT ES
                var divBtnGroupES = document.createElement('DIV');
                divBtnGroupES.setAttribute("class", "btn-group btn-group-justified");
                divBtnGroupES.setAttribute("role", "group");
                divBtnGroupES.setAttribute("aria-label", "...");
                //CREACION DIV BOTON activeDirectorieButtonES
                var divBtnDirectorieES = document.createElement('DIV');
                divBtnDirectorieES.setAttribute("class", "btn-group");
                divBtnDirectorieES.setAttribute("role", "group");
                //CREACION DE BOTON activeDirectorieButtonES
                var btnActiveDirectorieES = document.createElement('BUTTON');
                btnActiveDirectorieES.setAttribute("type", "button");
                btnActiveDirectorieES.setAttribute("class", "btn btn-default");
                btnActiveDirectorieES.setAttribute("onclick", "activeDirectorieButtonES(this)");
                //ICON
                var spanActiveDirectorieES = document.createElement('SPAN');
                spanActiveDirectorieES.setAttribute("class", "glyphicon glyphicon-folder-close");
                //TEXTO
                var btnActiveDirectorieESText = document.createTextNode("  ES");
                btnActiveDirectorieES.appendChild(spanActiveDirectorieES);
                btnActiveDirectorieES.appendChild(btnActiveDirectorieESText);
                divBtnDirectorieES.appendChild(btnActiveDirectorieES);
                divBtnGroupES.appendChild(divBtnDirectorieES);
                /******************************************************/
                //CREACION DEL DIV DeleteAudiosES
                var divDeleteAudiosES = document.createElement('DIV');
                divDeleteAudiosES.setAttribute("class", "btn-group");
                divDeleteAudiosES.setAttribute("role", "group");
                //CREACION DE BOTON DeleteAudiosES
                var btnDeleteAudiosES = document.createElement('BUTTON');
                btnDeleteAudiosES.setAttribute("type", "button");
                btnDeleteAudiosES.setAttribute("class", "btn btn-default");
                btnDeleteAudiosES.setAttribute("onclick", "deleteAudios()");
                //ICON
                var spanDeleteAudiosES = document.createElement('SPAN');
                spanDeleteAudiosES.setAttribute("class", "glyphicon glyphicon-trash");
                //TEXTO
                var btnDeleteAudiosESText = document.createTextNode("  Delete Audio(s)");
                btnDeleteAudiosES.appendChild(spanDeleteAudiosES);
                btnDeleteAudiosES.appendChild(btnDeleteAudiosESText);
                divDeleteAudiosES.appendChild(btnDeleteAudiosES);
                divBtnGroupES.appendChild(divDeleteAudiosES);
                /******************************************************/
                //CREACION DEL DIV UploadAudioEN
                var divUploadAudioES = document.createElement('DIV');
                divUploadAudioES.setAttribute("class", "btn-group");
                divUploadAudioES.setAttribute("role", "group");
                //CREACION DE BOTON UploadAudioEN
                var btnUploadAudioES = document.createElement('BUTTON');
                btnUploadAudioES.setAttribute("type", "button");
                btnUploadAudioES.setAttribute("class", "btn btn-default");
                btnUploadAudioES.setAttribute("onclick", "uploadAudio(this)");
                btnUploadAudioES.setAttribute("id", "Español," + jsonObject[i].directoryName);
                //ICON
                var spanUploadAudioES = document.createElement('SPAN');
                spanUploadAudioES.setAttribute("class", "glyphicon glyphicon-upload");
                //TEXTO
                var btnUploadAudioESText = document.createTextNode("   Upload Audio");
                btnUploadAudioES.appendChild(spanUploadAudioES);
                btnUploadAudioES.appendChild(btnUploadAudioESText);
                divUploadAudioES.appendChild(btnUploadAudioES);
                divBtnGroupES.appendChild(divUploadAudioES);
                //CREACION DE PANEL
                var divPanelES = document.createElement('DIV');
                divPanelES.setAttribute('class', 'panel panel-default toggle-content');
                divPanelES.setAttribute('id', 'ES,' + jsonObject[i].directoryName);
                var resultES = [];
                for (var k in jsonObject[i].subCarpetas.Es.audiosES)
                    resultES.push([jsonObject[i].subCarpetas.Es.audiosES [k]]);
                for (var j = 0; j < Object.keys(jsonObject[i].subCarpetas.Es.audiosES).length; j++) {
                    var divPanelBody = document.createElement('DIV');
                    divPanelBody.setAttribute('class', 'panel-body');
                    divPanelBody.setAttribute('style', 'padding: 0px; border: 1px solid gray;');
                    var divRow = document.createElement('DIV');
                    divRow.setAttribute('class', 'row');
                    //CREACION DEL NOMBRE DEL AUDIO
                    var divAudioName = document.createElement('DIV');
                    divAudioName.setAttribute('class', 'col-md-5 text-center');
                    var h3AudioName = document.createElement('H3');
                    var h2AudioNameTextNode = document.createTextNode(Object.keys(jsonObject[i].subCarpetas.Es.audiosES)[j]);
                    h3AudioName.appendChild(h2AudioNameTextNode);
                    divAudioName.appendChild(h3AudioName);
                    divRow.appendChild(divAudioName);
                    //CREACION DEL ELEMENTO AUDIO
                    var divAudioElement = document.createElement('DIV');
                    divAudioElement.setAttribute('class', 'col-md-6');
                    var audioElement = document.createElement('AUDIO');
                    audioElement.setAttribute('controls', 'true');
                    audioElement.setAttribute('style', 'width:100%');
                    var sourceAudioElement = document.createElement('SOURCE');
                    sourceAudioElement.setAttribute('src', resultES[j]);
                    sourceAudioElement.setAttribute('type', 'audio/wav');
                    audioElement.appendChild(sourceAudioElement);
                    divAudioElement.appendChild(audioElement);
                    divRow.appendChild(divAudioElement);
                    //CREACION DEL ELEMENTO CHEK BOX
                    var divCheckBox = document.createElement('DIV');
                    divCheckBox.setAttribute('class', 'col-md-1 text-center');
                    divCheckBox.setAttribute('style', 'padding-top: 17px;');
                    var inputElement = document.createElement('INPUT');
                    inputElement.setAttribute('type', 'checkbox');
                    inputElement.setAttribute('onclick', 'checkBox(this)');
                    divCheckBox.appendChild(inputElement);
                    divRow.appendChild(divCheckBox);
                    //Agregando a Panel Body 
                    divPanelBody.appendChild(divRow);
                    divPanelES.appendChild(divPanelBody);
                }

                /**************************************************************/
                //CREACION BTN GROUP PT Y PANEL CONTENT PT
                var divBtnGroupPT = document.createElement('DIV');
                divBtnGroupPT.setAttribute("class", "btn-group btn-group-justified");
                divBtnGroupPT.setAttribute("role", "group");
                divBtnGroupPT.setAttribute("aria-label", "...");
                //CREACION DIV BOTON activeDirectorieButtonPT
                var divBtnDirectoriePT = document.createElement('DIV');
                divBtnDirectoriePT.setAttribute("class", "btn-group");
                divBtnDirectoriePT.setAttribute("role", "group");
                //CREACION DE BOTON activeDirectorieButtonPT
                var btnActiveDirectoriePT = document.createElement('BUTTON');
                btnActiveDirectoriePT.setAttribute("type", "button");
                btnActiveDirectoriePT.setAttribute("class", "btn btn-default");
                btnActiveDirectoriePT.setAttribute("onclick", "activeDirectorieButtonPT(this)");
                //ICON
                var spanActiveDirectoriePT = document.createElement('SPAN');
                spanActiveDirectoriePT.setAttribute("class", "glyphicon glyphicon-folder-close");
                //TEXTO
                var btnActiveDirectoriePTText = document.createTextNode("  PT");
                btnActiveDirectoriePT.appendChild(spanActiveDirectoriePT);
                btnActiveDirectoriePT.appendChild(btnActiveDirectoriePTText);
                divBtnDirectoriePT.appendChild(btnActiveDirectoriePT);
                divBtnGroupPT.appendChild(divBtnDirectoriePT);
                /******************************************************/
                //CREACION DEL DIV DeleteAudiosPT
                var divDeleteAudiosPT = document.createElement('DIV');
                divDeleteAudiosPT.setAttribute("class", "btn-group");
                divDeleteAudiosPT.setAttribute("role", "group");
                //CREACION DE BOTON DeleteAudiosPT
                var btnDeleteAudiosPT = document.createElement('BUTTON');
                btnDeleteAudiosPT.setAttribute("type", "button");
                btnDeleteAudiosPT.setAttribute("class", "btn btn-default");
                btnDeleteAudiosPT.setAttribute("onclick", "deleteAudios()");
                //ICON
                var spanDeleteAudiosPT = document.createElement('SPAN');
                spanDeleteAudiosPT.setAttribute("class", "glyphicon glyphicon-trash");
                //TEXTO
                var btnDeleteAudiosPTText = document.createTextNode("  Delete Audio(s)");
                btnDeleteAudiosPT.appendChild(spanDeleteAudiosPT);
                btnDeleteAudiosPT.appendChild(btnDeleteAudiosPTText);
                divDeleteAudiosPT.appendChild(btnDeleteAudiosPT);
                divBtnGroupPT.appendChild(divDeleteAudiosPT);
                /******************************************************/
                //CREACION DEL DIV UploadAudioPT
                var divUploadAudioPT = document.createElement('DIV');
                divUploadAudioPT.setAttribute("class", "btn-group");
                divUploadAudioPT.setAttribute("role", "group");
                //CREACION DE BOTON UploadAudioPT
                var btnUploadAudioPT = document.createElement('BUTTON');
                btnUploadAudioPT.setAttribute("type", "button");
                btnUploadAudioPT.setAttribute("class", "btn btn-default");
                btnUploadAudioPT.setAttribute("onclick", "uploadAudio(this)");
                btnUploadAudioPT.setAttribute("id", "Portugues," + jsonObject[i].directoryName);
                //ICON
                var spanUploadAudioPT = document.createElement('SPAN');
                spanUploadAudioPT.setAttribute("class", "glyphicon glyphicon-upload");
                //TEXTO
                var btnUploadAudioPTText = document.createTextNode("   Upload Audio");
                btnUploadAudioPT.appendChild(spanUploadAudioPT);
                btnUploadAudioPT.appendChild(btnUploadAudioPTText);
                divUploadAudioPT.appendChild(btnUploadAudioPT);
                divBtnGroupPT.appendChild(divUploadAudioPT);
                //CREACION DE PANEL
                var divPanelPT = document.createElement('DIV');
                divPanelPT.setAttribute('class', 'panel panel-default toggle-content');
                divPanelPT.setAttribute('id', 'PT,' + jsonObject[i].directoryName);
                var resultPT = [];
                for (var k in jsonObject[i].subCarpetas.Pt.audiosPT)
                    resultPT.push([jsonObject[i].subCarpetas.Pt.audiosPT [k]]);
                for (var j = 0; j < Object.keys(jsonObject[i].subCarpetas.Pt.audiosPT).length; j++) {
                    var divPanelBody = document.createElement('DIV');
                    divPanelBody.setAttribute('class', 'panel-body');
                    divPanelBody.setAttribute('style', 'padding: 0px; border: 1px solid gray;');
                    var divRow = document.createElement('DIV');
                    divRow.setAttribute('class', 'row');
                    //CREACION DEL NOMBRE DEL AUDIO
                    var divAudioName = document.createElement('DIV');
                    divAudioName.setAttribute('class', 'col-md-5 text-center');
                    var h3AudioName = document.createElement('H3');
                    var h2AudioNameTextNode = document.createTextNode(Object.keys(jsonObject[i].subCarpetas.Pt.audiosPT)[j]);
                    h3AudioName.appendChild(h2AudioNameTextNode);
                    divAudioName.appendChild(h3AudioName);
                    divRow.appendChild(divAudioName);
                    //CREACION DEL ELEMENTO AUDIO
                    var divAudioElement = document.createElement('DIV');
                    divAudioElement.setAttribute('class', 'col-md-6');
                    var audioElement = document.createElement('AUDIO');
                    audioElement.setAttribute('controls', 'true');
                    audioElement.setAttribute('style', 'width:100%');
                    var sourceAudioElement = document.createElement('SOURCE');
                    sourceAudioElement.setAttribute('src', resultPT[j]);
                    sourceAudioElement.setAttribute('type', 'audio/wav');
                    audioElement.appendChild(sourceAudioElement);
                    divAudioElement.appendChild(audioElement);
                    divRow.appendChild(divAudioElement);
                    //CREACION DEL ELEMENTO CHEK BOX
                    var divCheckBox = document.createElement('DIV');
                    divCheckBox.setAttribute('class', 'col-md-1 text-center');
                    divCheckBox.setAttribute('style', 'padding-top: 17px;');
                    var inputElement = document.createElement('INPUT');
                    inputElement.setAttribute('type', 'checkbox');
                    inputElement.setAttribute('onclick', 'checkBox(this)');
                    divCheckBox.appendChild(inputElement);
                    divRow.appendChild(divCheckBox);
                    //Agregando a Panel Body 
                    divPanelBody.appendChild(divRow);
                    divPanelPT.appendChild(divPanelBody);
                }



                var divFooter = document.createElement('DIV');
                divFooter.setAttribute('class', 'card-footer');
                var smallText = document.createElement('SMALL');
                smallText.setAttribute('class', 'text-muted');
                var smallTextTextNode = document.createTextNode("Created Date " + jsonObject[i].lastModification);
                smallText.appendChild(smallTextTextNode);
                divFooter.appendChild(smallText);
                //AGREGANDO A CARD BODY
                divCardBody.appendChild(divRowCardBody);
                divCardBody.appendChild(divBtnGroupEN);
                divCardBody.appendChild(divPanel);
                divCardBody.appendChild(divBtnGroupES);
                divCardBody.appendChild(divPanelES);
                divCardBody.appendChild(divBtnGroupPT);
                divCardBody.appendChild(divPanelPT);
                //Agregando elementos a DIV CARD
                divCard.appendChild(imageCard);
                divCard.appendChild(divCardBody);
//                divCard.appendChild(divPanel);
//                divCard.appendChild(divPanelES);
//                divCard.appendChild(divPanelPT);
                divCard.appendChild(divFooter);
                //ADJUNTAMOS DIV CARD A PRINCIPAL CONTAINER
                principalContainer.appendChild(divCard);
            }
        }
    });
    xhr.open("GET", absolutepath + "Admin/Audios");
    xhr.send(data);
}

function uploadAudio(_this) {


    console.log(_this.parentElement.children[0].id);
    var folderName = _this.parentElement.children[0].id;
    Swal.fire({
        title: '<strong>Select WAV File</strong>',
        type: 'info',
        html: '<strong>Upload In Folder ' + folderName + '</strong>' +
                '<hr>' +
                '<input type="file" id="inputNewFile" name="newFile" accept="audio/wav" multiple>' +
                '<hr>',
        showCloseButton: true,
        showCancelButton: true,
        focusConfirm: false,
        confirmButtonText:
                '<i class="glyphicon glyphicon-thumbs-up" id="upload"></i> OK!',
        cancelButtonText:
                '<i class="glyphicon glyphicon-thumbs-down id="cancel"></i>'
    }).then((result) => {
        if (result.value) {
            uploadConfirm();
        }
    });
    function uploadConfirm() {
        console.log("Confirm");
        var valorFileUploaded = document.getElementById('inputNewFile');
        console.log(valorFileUploaded.files);
        if (validate_fileupload(valorFileUploaded.value)) {
            console.log("validate_fileupload");
            var reader = new FileReader();
            reader.addEventListener("load", function () {
                console.log("Reader");
                var fileName = valorFileUploaded.files[0].name;
                var data = new FormData();
                data.append("action", "uploadFile");
                data.append("File_bin", reader.result);
                data.append("File_Name", fileName);
                data.append("File_Folder_Name", folderName);
                var xhr = new XMLHttpRequest();
                xhr.withCredentials = false;
                xhr.addEventListener("readystatechange", function () {
                    if (this.readyState === 4) {
                        console.log(this.responseText);
                        var jsonObject = JSON.parse(this.responseText);
                        console.log(jsonObject);
                        if (jsonObject.status === "ok") {
                            function sleep(ms) {
                                return new Promise(resolve => setTimeout(resolve, ms));
                            }
                            async function demo() {
                                console.log('Taking a break...');
                                await sleep(2000);
                                console.log('Two seconds later, showing sleep in a loop...');
                                var newAudio = document.getElementById(folderName);
                                var panelChildrenGroup = newAudio.parentNode.parentNode.parentNode.children;
                                for (var i = 0; i < panelChildrenGroup.length; i++) {
                                    if (panelChildrenGroup[i].id === jsonObject.language + "," + jsonObject.folderName) {
                                        var folderInsertAudio = document.getElementById(panelChildrenGroup[i].id);
                                        if (folderInsertAudio.children.length === 0) {
                                            console.log("No tiene elementos");
                                            //CREACIÖN DEL PANEL BODY DIV
                                            var divPanelBody = document.createElement('DIV');
                                            divPanelBody.setAttribute("class", "panel-body");
                                            divPanelBody.setAttribute("style", "padding: 0px; border: 1px solid gray;");
                                            //CREACION DEL ROW 
                                            var rowElement = document.createElement('DIV');
                                            rowElement.setAttribute("class", "row");
                                            //CREACION DE DIV NOMBRE WAV
                                            var divFileName = document.createElement('DIV');
                                            divFileName.setAttribute("class", "col-md-5 text-center");
                                            var h3FileName = document.createElement('H3');
                                            h3FileName.setAttribute("style", "font-size: 15px;");
                                            var FileNameTextNode = document.createTextNode(jsonObject.fileName);
                                            h3FileName.appendChild(FileNameTextNode);
                                            divFileName.appendChild(h3FileName);
                                            //CREACION DIV AUDIO
                                            var divAudio = document.createElement('DIV');
                                            divAudio.setAttribute("class", "col-md-6");
                                            var audioElement = document.createElement('AUDIO');
                                            audioElement.setAttribute("controls", "true");
                                            audioElement.setAttribute("style", "width:100%");
                                            var sourceElement = document.createElement('SOURCE');
                                            sourceElement.setAttribute("src", "Audios/" + jsonObject.folderName.toString() + "/" + jsonObject.language.toString() + "/" + jsonObject.fileName.toString());
                                            sourceElement.setAttribute("type", "audio/wav");
                                            audioElement.appendChild(sourceElement);
                                            divAudio.appendChild(audioElement);
                                            //CREACION DIV CHECKBOX
                                            var divCheckBox = document.createElement('DIV');
                                            divCheckBox.setAttribute("class", "col-md-1 text-center");
                                            divCheckBox.setAttribute("style", "padding-top: 17px;");
                                            var inputCheckBox = document.createElement('INPUT');
                                            inputCheckBox.setAttribute("type", "checkbox");
                                            inputCheckBox.setAttribute("onclick", "checkBox(this)");
                                            divCheckBox.appendChild(inputCheckBox);
                                            //APPEND DIV IN ROW
                                            rowElement.appendChild(divFileName);
                                            rowElement.appendChild(divAudio);
                                            rowElement.appendChild(divCheckBox);
                                            //APPEND ROW IN PANEL BODY
                                            divPanelBody.appendChild(rowElement);
                                            //APPEND PANEL BODY IN FOLDER
                                            folderInsertAudio.appendChild(divPanelBody);
                                        } else {
                                            console.log("Si tiene elementos");
//                                        var divPanelBody = folderInsertAudio.children[0];
                                            console.log(folderInsertAudio);
                                            for (var j = 0; j < folderInsertAudio.children.length; j++) {
                                                if (folderInsertAudio.children[j].children[0].children[0].innerText === jsonObject.fileName) {
                                                    console.log("Equals");
                                                    console.log(folderInsertAudio.children);
                                                    var element = folderInsertAudio.children[j];
                                                    element.parentNode.removeChild(element);
                                                }
                                            }
                                            //CREACIÖN DEL PANEL BODY DIV
                                            var divPanelBody = document.createElement('DIV');
                                            divPanelBody.setAttribute("class", "panel-body");
                                            divPanelBody.setAttribute("style", "padding: 0px; border: 1px solid gray;");
                                            //CREACION DEL ROW 
                                            var rowElement = document.createElement('DIV');
                                            rowElement.setAttribute("class", "row");
                                            //CREACION DE DIV NOMBRE WAV
                                            var divFileName = document.createElement('DIV');
                                            divFileName.setAttribute("class", "col-md-5 text-center");
                                            var h3FileName = document.createElement('H3');
                                            h3FileName.setAttribute("style", "font-size: 15px;");
                                            var FileNameTextNode = document.createTextNode(jsonObject.fileName);
                                            h3FileName.appendChild(FileNameTextNode);
                                            divFileName.appendChild(h3FileName);
                                            //CREACION DIV AUDIO
                                            var divAudio = document.createElement('DIV');
                                            divAudio.setAttribute("class", "col-md-6");
                                            var audioElement = document.createElement('AUDIO');
                                            audioElement.setAttribute("controls", "true");
                                            audioElement.setAttribute("style", "width:100%");
                                            var sourceElement = document.createElement('SOURCE');
                                            sourceElement.setAttribute("src", "Audios/" + jsonObject.folderName.toString() + "/" + jsonObject.language.toString() + "/" + jsonObject.fileName.toString());
                                            sourceElement.setAttribute("type", "audio/wav");
                                            audioElement.appendChild(sourceElement);
                                            divAudio.appendChild(audioElement);
                                            //CREACION DIV CHECKBOX
                                            var divCheckBox = document.createElement('DIV');
                                            divCheckBox.setAttribute("class", "col-md-1 text-center");
                                            divCheckBox.setAttribute("style", "padding-top: 17px;");
                                            var inputCheckBox = document.createElement('INPUT');
                                            inputCheckBox.setAttribute("type", "checkbox");
                                            inputCheckBox.setAttribute("onclick", "checkBox(this)");
                                            divCheckBox.appendChild(inputCheckBox);
                                            //APPEND DIV IN ROW
                                            rowElement.appendChild(divFileName);
                                            rowElement.appendChild(divAudio);
                                            rowElement.appendChild(divCheckBox);
                                            //APPEND ROW IN PANEL BODY
                                            divPanelBody.appendChild(rowElement);
                                            //APPEND PANEL BODY IN FOLDER
                                            folderInsertAudio.appendChild(divPanelBody);
                                        }

                                    }
                                }

                            }

                            demo();
                            Swal.fire(
                                    'Good job!',
                                    'The audio has been inserted correctly!',
                                    'success'
                                    );
                        }
                        if (jsonObject.status === "error") {
                            Swal.fire(
                                    'Opps!',
                                    'Error inserting audio',
                                    'error'
                                    );
                        }
                        if (jsonObject.status === "errorLimit") {
                            Swal.fire(
                                    'Opps!',
                                    'Error inserting audio, content is too big',
                                    'error'
                                    );
                        }
                    }
                });
                xhr.open("POST", absolutepath + "Admin/Audios");
                xhr.send(data);
            }, false);
            if (valorFileUploaded.files.length === 0) {
                alert("Sin archivos");
            }
            if (valorFileUploaded.files.length === 1) {
                if (valorFileUploaded.files[0]) {
                    reader.readAsDataURL(valorFileUploaded.files[0]);
                }
            }
            if (valorFileUploaded.files.length > 1) {
                if (window.File && window.FileList && window.FileReader) {
                    for (var i = 0; i < valorFileUploaded.files.length; i++) {
                        var file = valorFileUploaded.files[i];
                        var readerMultiple = new FileReader();
                        // Closure to capture the file information.
                        readerMultiple.onload = (function (file) {
                            return function (e) {
                                makePost(file.name, e.target.result, folderName);
                            };
                        })(file);

                        // Read in the image file as a data URL.
                        readerMultiple.readAsDataURL(file);
                        if (i === valorFileUploaded.files.length - 1) {
                            function sleep2(ms) {
                                return new Promise(resolve => setTimeout(resolve, ms));
                            }
                            async function demo2() {
                                console.log('Taking a break...');
                                await sleep2(3000);
                                console.log('Two seconds later, showing sleep in a loop...');
                                var principalContainer = document.getElementById('principalContainer');
                                principalContainer.innerHTML = "";
                                obtenerCredenciales();
                            }
                            demo2();
                        }

                    }


                } else {
                    console.log("Your browser does not support File API");
                    alert("Your browser does not support File API");
                }

            }

        } else {
            alert("El archivo no es .WAV");
        }

    }

    function makePost(fileName, readerResult, folderName) {
        var data = new FormData();
        data.append("action", "uploadFile");
        data.append("File_bin", readerResult);
        data.append("File_Name", fileName);
        data.append("File_Folder_Name", folderName);
        var xhr = new XMLHttpRequest();
        xhr.withCredentials = false;
        xhr.addEventListener("readystatechange", function () {
            if (this.readyState === 4) {
                var jsonObject = JSON.parse(this.responseText);
                console.log(jsonObject);
                if (jsonObject.status === "ok") {
                    Swal.fire(
                            'Good job!',
                            'The audio has been inserted correctly!',
                            'success'
                            );
                }
                if (jsonObject.status === "error") {
                    Swal.fire(
                            'Opps!',
                            'Error inserting audio',
                            'error'
                            );
                }
                if (jsonObject.status === "errorLimit") {
                    Swal.fire(
                            'Opps!',
                            'Error inserting audio, content is too big',
                            'error'
                            );
                }
            }
        });
        xhr.open("POST", absolutepath + "Admin/Audios");
        xhr.send(data);
    }



    function validate_fileupload(fileName)
    {
        var allowed_extensions = new Array("wav");
        var file_extension = fileName.split('.').pop().toLowerCase(); // split function will split the filename by dot(.), and pop function will pop the last element from the array which will give you the extension as well. If there will be no extension then it will return the filename.

        for (var i = 0; i <= allowed_extensions.length; i++)
        {
            if (allowed_extensions[i] === file_extension)
            {
                return true; // valid file extension
            }
        }

        return false;
    }

}


function deleteAudios() {

    if (global.length === 0) {
        Swal.fire({
            type: 'error',
            title: 'No audios selected to Delete'
        });
    } else {
        var stringBuilder = [];
        for (var i = 0; i < global.length; i++) {
            var jsonGlobal = global[i];
            for (var key in jsonGlobal) {
                stringBuilder.push(jsonGlobal[key]);
                stringBuilder.push(" ");
            }
        }

        Swal.fire({
            title: 'Surely you want to erase the following audios?',
            text: "" + stringBuilder + "",
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
        var data = new FormData();
        data.append("action", "deleteFile");
        data.append("files_Array", JSON.stringify(global));
        var xhr = new XMLHttpRequest();
        xhr.withCredentials = false;
        xhr.addEventListener("readystatechange", function () {
            if (this.readyState === 4) {
                var jsonObject = JSON.parse(this.responseText);
                if (jsonObject.status === "ok") {
                    for (var i = global.length - 1; i > 0 - 1; i--) {
                        var jsonGlobal = global[i];
                        for (var key in jsonGlobal) {
                            var findElementToDelete = document.getElementById(key).children;
                            if (findElementToDelete.length !== 0) {
                                for (var j = 0; j < findElementToDelete.length; j++) {
                                    if (findElementToDelete[j].children[0].children[0].children[0].innerText === jsonGlobal[key]) {
                                        var elementToDelete = findElementToDelete[j];
                                        elementToDelete.parentNode.removeChild(elementToDelete);
                                        console.log("Delete " + jsonGlobal[key]);
                                        global.splice(i, 1);
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    Swal.fire(
                            'Good job!',
                            'The audio has been deleted correctly!',
                            'success'
                            );
                    console.log(global);
                }
                if (jsonObject.status === "error") {
                    Swal.fire(
                            'Opps!',
                            'Error deleting audio',
                            'error'
                            );
                }

            }
        });
        xhr.open("POST", absolutepath + "Admin/Audios");
        xhr.send(data);
        Swal.fire(
                'Deleted!',
                'Your file has been deleted.',
                'success'
                );
    }
}



function checkBox(_this) {
    var validateCheckBox = _this.checked;
    var folderNameAndLanguage = _this.parentElement.parentElement.parentElement.parentElement.id;
    if (validateCheckBox === true) {
        var obj = {};
        obj[folderNameAndLanguage] = _this.parentElement.parentElement.children[0].children[0].innerText;
        global.push(obj);
    } else {
        var obj = {};
        obj[folderNameAndLanguage] = _this.parentElement.parentElement.children[0].children[0].innerText;
        for (var i = 0; i < global.length; i++) {
            var jsonGlobal = global[i];
            for (var key in jsonGlobal) {
                if (folderNameAndLanguage === key && _this.parentElement.parentElement.children[0].children[0].innerText === jsonGlobal[key]) {
                    global.splice(i, 1);
                }

            }
        }

    }
    console.log(global);
}

function activeDirectorieButtonEN(_this) {

    var spanDirectoryIcon = _this.parentElement.parentElement.children[0].children[0].children[0];
    console.log(spanDirectoryIcon);
    if (spanDirectoryIcon.classList.contains('glyphicon-folder-close')) {
        spanDirectoryIcon.classList.remove('glyphicon-folder-close');
        spanDirectoryIcon.classList.add('glyphicon-folder-open');
    } else {
        spanDirectoryIcon.classList.add('glyphicon-folder-close');
        spanDirectoryIcon.classList.remove('glyphicon-folder-open');
    }

    // Toggle the content
    toggle(_this.parentElement.parentElement.parentElement.children[2]);
}

function activeDirectorieButtonES(_this) {
    var spanDirectoryIcon = _this.parentElement.parentElement.children[0].children[0].children[0];
    if (spanDirectoryIcon.classList.contains('glyphicon-folder-close')) {
        spanDirectoryIcon.classList.remove('glyphicon-folder-close');
        spanDirectoryIcon.classList.add('glyphicon-folder-open');
    } else {
        spanDirectoryIcon.classList.add('glyphicon-folder-close');
        spanDirectoryIcon.classList.remove('glyphicon-folder-open');
    }
    // Toggle the content
    toggle(_this.parentElement.parentElement.parentElement.children[4]);
}

function activeDirectorieButtonPT(_this) {
    var spanDirectoryIcon = _this.parentElement.parentElement.children[0].children[0].children[0];
    if (spanDirectoryIcon.classList.contains('glyphicon-folder-close')) {
        spanDirectoryIcon.classList.remove('glyphicon-folder-close');
        spanDirectoryIcon.classList.add('glyphicon-folder-open');
    } else {
        spanDirectoryIcon.classList.add('glyphicon-folder-close');
        spanDirectoryIcon.classList.remove('glyphicon-folder-open');
    }
    // Toggle the content
    toggle(_this.parentElement.parentElement.parentElement.children[6]);
}


// Show an element
var show = function (elem) {
    // Get the natural height of the element
    var getHeight = function () {
        elem.style.display = 'block'; // Make it visible
        var height = elem.scrollHeight + 'px'; // Get it's height
        elem.style.display = ''; //  Hide it again
        return height;
    };
    var height = getHeight(); // Get the natural height
    elem.classList.add('is-visible'); // Make the element visible
    elem.style.height = height; // Update the max-height

    // Once the transition is complete, remove the inline max-height so the content can scale responsively
    window.setTimeout(function () {
        elem.style.height = '';
    }, 350);
};
// Hide an element
var hide = function (elem) {

    // Give the element a height to change from
    elem.style.height = elem.scrollHeight + 'px';
    // Set the height back to 0
    window.setTimeout(function () {
        elem.style.height = '0';
    }, 1);
    // When the transition is complete, hide it
    window.setTimeout(function () {
        elem.classList.remove('is-visible');
    }, 350);
};
// Toggle element visibility
var toggle = function (elem, timing) {

    // If the element is visible, hide it
    if (elem.classList.contains('is-visible')) {
        hide(elem);
        return;
    }

    // Otherwise, show it
    show(elem);
};
function getAbsolutePath() {
    var loc = window.location;
    var pathName = loc.pathname.substring(0, loc.pathname.lastIndexOf('/') + 1);
    return loc.href.substring(0, loc.href.length - ((loc.pathname + loc.search + loc.hash).length - pathName.length));
}


function changeImageDirectorie(_this) {
    console.log("changeImageDirectorie");
    var directorieName = _this.parentElement.parentElement.children[0].children[0].children[0].innerText;
    console.log(directorieName);
    var imageSrc = _this.parentElement.parentElement.parentElement.parentElement.children[0];
    console.log(imageSrc);
    Swal.fire({
        title: '<strong>Select Image File</strong>',
        type: 'question',
        html: '<strong>Upload In Directorie ' + directorieName + '</strong>' +
                '<hr>' +
                '<input type="file" id="inputNewFileImage" name="newFileImage" accept="image/*">' +
                '<hr>',
        showCloseButton: true,
        showCancelButton: true,
        focusConfirm: false,
        confirmButtonText:
                '<i class="glyphicon glyphicon-thumbs-up" id="uploadImage"></i> OK!',
        cancelButtonText:
                '<i class="glyphicon glyphicon-thumbs-down id="cancel"></i>'
    }).then((result) => {
        if (result.value) {
            changeImageConfirm();
        }
    });
    function changeImageConfirm() {
        console.log("changeImageConfirm");
        var valorFileUploaded = document.getElementById('inputNewFileImage');
        if (validate_fileupload(valorFileUploaded.value)) {
            console.log("Es IMG");
            var reader = new FileReader();
            reader.addEventListener("load", function () {

                var data = new FormData();
                data.append("action", "modifyImage");
                data.append("folderName", directorieName);
                data.append("imageName", valorFileUploaded.files[0].name);
                data.append("fileImageBin", reader.result);
                var xhr = new XMLHttpRequest();
                xhr.withCredentials = false;
                xhr.addEventListener("readystatechange", function () {
                    if (this.readyState === 4) {
                        var jsonObject = JSON.parse(this.responseText);
                        console.log(jsonObject);
                        if (jsonObject.status === "ok") {
                            function sleep(ms) {
                                return new Promise(resolve => setTimeout(resolve, ms));
                            }
                            async function demo() {
                                console.log('Taking a break...');
                                await sleep(2000);
                                imageSrc.src = "";
                                imageSrc.src = "Audios/" + directorieName + "/IMG/" + jsonObject.imageName;
                            }

                            demo();
                            Swal.fire(
                                    'Good job!',
                                    'The image has been change correctly!',
                                    'success'
                                    );
                        }
                        if (jsonObject.status === "error") {
                            Swal.fire(
                                    'Opps!',
                                    'Error changing image',
                                    'error'
                                    );
                        }

                    }
                });
                xhr.open("POST", absolutepath + "Admin/Audios");
                xhr.send(data);
            });
            if (valorFileUploaded.files[0]) {
                reader.readAsDataURL(valorFileUploaded.files[0]);
            }

        } else {

        }
    }



    function validate_fileupload(fileName)
    {
        var allowed_extensions = new Array("jpg", "png", "jpeg");
        var file_extension = fileName.split('.').pop().toLowerCase(); // split function will split the filename by dot(.), and pop function will pop the last element from the array which will give you the extension as well. If there will be no extension then it will return the filename.

        for (var i = 0; i <= allowed_extensions.length; i++)
        {
            if (allowed_extensions[i] === file_extension)
            {
                return true; // valid file extension
            }
        }

        return false;
    }
}

function changeNameDirectoorie(_this) {
    var directorieName = _this.parentElement.parentElement.parentElement.children[0].children[0].children[0].children[0].innerText;
    var directorieSource = _this.parentElement.parentElement.parentElement.children[0].children[0].children[0].children[0];
    Swal.fire({
        title: 'Surely you want to change the name of the directory ' + directorieName + ' ?',
        text: "You won't be able to revert this!",
        type: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Yes, delete it!'
    }).then((result) => {
        if (result.value) {
            confirmationToChangeDirectorieName();
        }
        ;
    });
    function confirmationToChangeDirectorieName() {
        Swal.fire({
            title: '<strong>Enter the new directory name.</strong>',
            type: 'info',
            html: '<div class="row"><div class="col-sm-12"><form>' +
                    '<div class="input-group col">' +
                    '<span class="input-group-addon">' +
                    '<i class="glyphicon glyphicon-level-up">' +
                    '</i></span>' +
                    '<input type="text" class="form-control" id="inputDirectorieNewName" placeholder="Enter directorie new name">' +
                    '</div>' +
                    '</form>' +
                    '</div>' +
                    '</div>',
            showCancelButton: true,
            focusConfirm: false,
            confirmButtonText:
                    '<i class="glyphicon glyphicon-thumbs-up" id="upload"></i> OK!',
            cancelButtonText:
                    '<i class="glyphicon glyphicon-thumbs-down id="cancel"></i>',
            cancelButtonAriaLabel: 'Thumbs down'
        }).then((result) => {
            if (result.value) {
                validateNewNameAndMakeChange();
            }
            ;
        });
    }

    function validateNewNameAndMakeChange() {
        var inputValue = document.getElementById('inputDirectorieNewName').value;
        if (inputValue === "" || inputValue === null || inputValue === undefined) {
            Swal.fire(
                    'Opps!',
                    'Error Please complete the field to enter the new directory name.',
                    'error'
                    );
        } else {
            var lt = /</g,
                    gt = />/g,
                    ap = /'/g,
                    ic = /"/g;
            inputValue = inputValue.toString().replace(lt, "&lt;").replace(gt, "&gt;").replace(ap, "&#39;").replace(ic, "&#34;");
            var data = new FormData();
            data.append("action", "modifyDirectorieName");
            data.append("folderName", directorieName);
            data.append("newNameFolder", inputValue);
            var xhr = new XMLHttpRequest();
            xhr.withCredentials = false;
            xhr.addEventListener("readystatechange", function () {
                if (this.readyState === 4) {
                    console.log(this.responseText);
                    var jsonObject = JSON.parse(this.responseText);
                    if (jsonObject.status === "ok") {
                        directorieSource.innerText = "";
                        directorieSource.innerText = jsonObject.newNameFolder;
                        Swal.fire(
                                'Good job!',
                                'The directorie name has been changed correctly!',
                                'success'
                                );
                    }
                    if (jsonObject.status === "error") {
                        Swal.fire(
                                'Opps!',
                                'Error changing directory name.',
                                'error'
                                );
                    }
                }
            });
            xhr.open("POST", absolutepath + "Admin/Audios");
            xhr.send(data);
        }
    }
}


function deleteDirectoy(_this) {
    console.log("Delete Directory");
    var directorieName = _this.parentElement.parentElement.parentElement.children[0].children[0].children[0].children[0].innerText;
    var divCardElement = _this.parentElement.parentElement.parentElement.parentElement;
    console.log(divCardElement);
    Swal.fire({
        title: 'Are you sure you want to DELETE the entire directory? ' + directorieName + ' ?',
        text: "You won't be able to revert this!",
        type: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Yes, delete it!'
    }).then((result) => {
        if (result.value) {
            deleteDirectoryConfirmation();
        }
        ;
    });
    function deleteDirectoryConfirmation() {
        var data = new FormData();
        data.append("action", "deleteEntireDirectory");
        data.append("folderName", directorieName);
        var xhr = new XMLHttpRequest();
        xhr.withCredentials = false;
        xhr.addEventListener("readystatechange", function () {
            if (this.readyState === 4) {
                console.log(this.responseText);
                var jsonObject = JSON.parse(this.responseText);
                if (jsonObject.status === "ok") {
                    var elementToDelete = divCardElement;
                    elementToDelete.parentNode.removeChild(elementToDelete);
                    Swal.fire(
                            'Good job!',
                            'The directorie name has been deleted correctly!',
                            'success'
                            );
                }
                if (jsonObject.status === "error") {
                    Swal.fire(
                            'Opps!',
                            'Error deleting directory.',
                            'error'
                            );
                }
            }
        });
        xhr.open("POST", absolutepath + "Admin/Audios");
        xhr.send(data);
    }
}

function downloadZipFIle(_this) {
    console.log("Create and Download ZipFile");
    var directorieName = _this.parentElement.parentElement.parentElement.children[0].children[0].children[0].children[0].innerText;
    console.log(directorieName);
    document.getElementById('loaderDisplay').classList.add('is-active');
    document.getElementById("loaderDisplay").setAttribute("data-text", "Espera un momento.");
    var data = new FormData();
    data.append("action", "createZip");
    data.append("folderName", directorieName);
    var xhr = new XMLHttpRequest();
    xhr.withCredentials = false;
    xhr.addEventListener("readystatechange", function () {
        if (this.readyState === 4) {

            var jsonObject = JSON.parse(this.responseText);
            if (jsonObject.status === "ok") {
                function sleep(ms) {
                    return new Promise(resolve => setTimeout(resolve, ms));
                }
                async function demo() {
                    console.log('Taking a break...');
                    await sleep(2000);
                    document.getElementById("loaderDisplay").classList.remove("is-active");
                    document.getElementById("loaderDisplay").setAttribute("data-text", "");
                    window.open(absolutepath + jsonObject.zipFolder);
                }

                demo();
            }
            if (jsonObject.status === "error") {
                document.getElementById("loaderDisplay").classList.remove("is-active");
                document.getElementById("loaderDisplay").setAttribute("data-text", "");
                Swal.fire(
                        'Opps!',
                        'Error ziping directory.',
                        'error'
                        );
            }
        }
    });
    xhr.open("POST", absolutepath + "Admin/Audios");
    xhr.send(data);
}