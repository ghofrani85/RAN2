/**
 *	JS behind the newFeature and editFeature HTML Templates
 *
 *	Tim Gugel
 *
 */
 
 function closeModal() {
	$(".modal").modal('hide');
}

 var token = $("meta[name='_csrf']").attr("content");
 var header = $("meta[name='_csrf_header']").attr("content");
 $(document).ajaxSend(function(e,xhr,options) {
    xhr.setRequestHeader(header, token);
 });
 
function cleanupFeatureModal() {
	$("#featureform").trigger('reset');
}

function selectValue(value) {    
    var projectSelect 	= document.getElementById('projectSelect');
    projectSelect.value = value;
}

function selectProject() {    
    document.getElementById('projId_copy').value = document.getElementById('projectSelect').value;
}

function addSelectToHTML(includedInProjects) {	
	selecter 		= "<select id=\"projectSelect\" name=\"projectSelect\" onchange=\"selectProject()\" class=\"form-control\">";
	for(var i = 0; i < includedInProjects.length; i++) {
		if (i == 0) {				
			selecter 	+= "<option selected value=\"" + includedInProjects[i].id + "\">" + includedInProjects[i].title + "</option>";
		} else {			
			selecter 	+= "<option value=\"" + includedInProjects[i].id + "\">" + includedInProjects[i].title + "</option>";	
		}
	}           						
	selecter 		+= "</select>"		
	document.getElementById("initProjectSelect_copy").innerHTML = selecter;	
	document.getElementById('projId_copy').value = document.getElementById('projectSelect').value;
}

function init_copy() {	
	addSelectToHTML()
	console.log("Load Feature")
	//$.post("getFeature", {featId:document.getElementById("featId").value}, function(data, status){	
	$.post("getFeature", {featId:1}, function(data, status){	
		console.log("In function")
		document.getElementById("titleTextbar_copy").value 			= data.title
		document.getElementById("descriptionTextarea_copy").value 	= data.description
	});	
}


function init_edit() {			
	console.log("Load Feature")
	//$.post("getFeature", {featId:document.getElementById("featId").value}, function(data, status){	
	$.post("getFeature", {featId:1}, function(data, status){	
		console.log("In function")
		document.getElementById("titleTextbar_edit").value 			= data.title
		document.getElementById("descriptionTextarea_edit").value 	= data.description
	});	
}