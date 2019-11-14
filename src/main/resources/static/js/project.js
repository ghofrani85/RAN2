/**
 * REST request and utility functions for the project view.
 */

$(document).ready(function() {
	// Activate tooltips.
	$('[data-toggle="tooltip"]').tooltip();

	// Hide error messages.
	$("#dlSuccess").hide();
	$("#dlError").hide();
});

/**
 * Close modals.
 * 
 * @returns
 */
function closeModal() {
	$(".modal").modal('hide');
}

/**
 * Deletes the form text when a modal is closed.
 * 
 * @returns
 */
function cleanUpMpdal(formid) {
	$("#" + formid).trigger('reset');
}

var token = $("meta[name='_csrf']").attr("content");
var header = $("meta[name='_csrf_header']").attr("content");
$(document).ajaxSend(function(e, xhr, options) {
	xhr.setRequestHeader(header, token);
});

/**
 * Sets the status of checkboxes in the Product/Feature-Matrix on load.
 * 
 * @returns
 */
function setCheckboxes() {
	var boxes = document.getElementsByClassName("checkbox");

	for (var i = 0; i < boxes.length; i++) {
		var id = boxes[i].id;
		var fPos = id.indexOf('f');
		var p = id.slice(1, fPos);
		var f = id.slice(fPos + 1, id.length);
		checkFeature(p, f, boxes[i]);
	}
}

/**
 * Checks if a Product contains a feature.
 * 
 * @returns
 */
function checkFeature(product, feature, box) {
	$.post("checkfeature", {
		p : product,
		f : feature
	}, function(data, status) {
		if (status == 'success') {
			if (data.content == 'true') {
				box.checked = true;
			} else {
				box.checked = false;
			}
		} else {
			box.checked = false;
		}
	});
}

/**
 * Reacts to changing checkbox status.
 * 
 * @returns
 */
function boxChanged(box) {
	var id = box.id;
	var fPos = id.indexOf('f');
	var p = id.slice(1, fPos);
	var f = id.slice(fPos + 1, id.length);

	if (box.checked == true) {
		addFeature(p, f);
	} else {
		removeFeature(p, f);
	}

	if ($('#btn' + p).hasClass('btn-success')) {
		resetDownloadButton(p);
	}
}

/**
 * Resets the download button to its initial state.
 * 
 * @param productId
 * @returns
 */
function resetDownloadButton(productId) {
	$("#btn" + productId).attr('onclick', 'prepareDownload(' + productId + ')');
	$("#btn" + productId).attr('type', 'button');
	$("#btn" + productId).addClass('btn-default');
	$("#btn" + productId).removeClass('btn-success');
	$("#submit" + productId).attr('action', '#');
	$("#dlpath" + productId).attr('value', '');
	$("#dlSuccess").hide();
}

/**
 * Sends a Request to add a feature to a product.
 * 
 * @param product
 * @param feature
 * @returns
 */
function addFeature(product, feature) {
	$.post("addfeature", {
		p : product,
		f : feature
	})
}

/**
 * Sends a Request to remove a feature from a product.
 * 
 * @param product
 * @param feature
 * @returns
 */
function removeFeature(product, feature) {
	$.post("removefeature", {
		p : product,
		f : feature
	})
}

/**
 * Sends a request to prepare the download of the product, sets the download
 * button to the target URL when the download is ready.
 * 
 * @param productId
 * @returns
 */
function prepareDownload(productId) {
	var sid = 'dl' + productId;
	var span = document.getElementById(sid);
	span.className = "loader";
	$("#downloading").show();

	$.post("generateZip", {
		productId : productId
	}, function(data, status) {
		if (data.error == true) {
			$("#dlSuccess").hide();
			$("#dlError").text(data.errorMessage);
			$("#dlError").show();
			$("#downloading").hide();
			span.className = "glyphicon glyphicon-download-alt";
		} else if (data.error == false) {
			$("#dlError").hide();
			span.className = "glyphicon glyphicon-download-alt";
			$("#btn" + productId).attr('onclick', '');
			$("#btn" + productId).attr('type', 'submit');
			$("#btn" + productId).removeClass('btn-default');
			$("#btn" + productId).addClass('btn-success');
			$("#submit" + productId).attr('action', '/downloadProduct');
			$("#dlpath" + productId).attr('value', data.url);
			$("#downloading").hide();
			$("#dlSuccess").show();
		}
	})
}

/**
 * Thumb up and down buttons
 */
function voteThumbUp(projectId) {
	$.post("voteUpProject", {
		projectId : projectId
	}, function() {
		span.className = "glyphicon glyphicon-thumbs-up";
		$("#btn" + projectId).attr('onclick', '');
		$("#btn" + projectId).attr('type', 'submit');
		$("#btn" + projectId).removeClass('btn-default');
		$("#btn" + projectId).addClass('btn-success');
		$("#submit" + projectId).attr('action', '/voteupproject');
		}
	)
}

function voteThumbDown(projectId) {
	$.post("voteUpProject", {
		projectId : projectId
	}, function() {
		span.className = "glyphicon glyphicon-thumbs-down";
		$("#btn" + projectId).attr('onclick', '');
		$("#btn" + projectId).attr('type', 'submit');
		$("#btn" + projectId).removeClass('btn-default');
		$("#btn" + projectId).addClass('btn-success');
		$("#submit" + projectId).attr('action', '/votedownproject');
		}
	)
}