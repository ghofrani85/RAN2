
//TODO: locales

var IDENTICAL = 0;
var PRECEDING = 2;
var FOLLOWING = 4;

function setErrorMsg(messageId) {
    message = document.getElementById(messageId).innerText;
    document.getElementById("textEditorErrorMsg").innerHTML = message;
}

function clearErrorMsg(message) {
    document.getElementById("textEditorErrorMsg").innerHTML = "";
}

function btn_fileTextEditorSelect() {
    console.log("---------------------------------------");
    var selection = document.getSelection();

    if(selection.rangeCount === 0) {
        console.log("selection.rangeCount == 0")
        setErrorMsg("errorMsgSelectRelevant");
        return;
    }

    var textAreaElement = document.getElementById("textEditorTextarea");
    var oldSelectionElement = document.getElementById("textEditorSelection");

    var anchorParentElement = selection.anchorNode.parentElement;
    var focusParentElement = selection.focusNode.parentElement;

    //check if selection is actually in the textarea and not somewhere outside
    if(! ((anchorParentElement === textAreaElement || anchorParentElement === oldSelectionElement) &&
      (focusParentElement === textAreaElement || anchorParentElement === oldSelectionElement))) {
        setErrorMsg("errorMsgOnlyTextInArea");
        return;
    }



    if (selection.getRangeAt(0).startOffset === selection.getRangeAt(0).endOffset &&
        selection.anchorNode == selection.focusNode)
    {
        console.log("start offset == end offset")
        setErrorMsg("errorMsgSelectRelevant");
        return;
    }


    var startChar, endChar;
    var selectionStartNode, selectionEndNode, startOffset, endOffset;
    if (selection.anchorNode.compareDocumentPosition(selection.focusNode) === IDENTICAL ||
        (selection.anchorNode.compareDocumentPosition(selection.focusNode) & FOLLOWING)) {
        selectionStartNode = selection.anchorNode;
        selectionEndNode = selection.focusNode;
    }

    else if (selection.anchorNode.compareDocumentPosition(selection.focusNode) & PRECEDING) {
        selectionEndNode = selection.anchorNode;
        selectionStartNode = selection.focusNode;
    }

    else {
        var value = selection.anchorNode.compareDocumentPosition(selection.focusNode);
        console.log("unexpected value while comparing positions: " + value);
    }

    //new selection is in old selecction
    if (selectionStartNode.parentElement === oldSelectionElement
        && selectionEndNode.parentElement === oldSelectionElement) {
        console.log("new selection complete in old selection");
        var offset = textAreaElement.firstChild.length;
        startChar = offset + selection.getRangeAt(0).startOffset;
        endChar = offset + selection.getRangeAt(0).endOffset;
    }

    //new selection is not complete in old selection
    //check if there is overlap in new and old selection
    else if (selectionStartNode.parentElement === selectionEndNode.parentElement) {
        //no overlap with old selection
        //we can savely remove the old selection and make a new one
        console.log("no overlap")
        removeOldSelection();
        makeNewSelection(selection.getRangeAt(0));
        selection.empty();
        return;
    }

    //there is an overlap between new and old selection
    //new selection starts in unselected text?
    else if (selectionStartNode.parentElement === textAreaElement) {
        console.log("selection anchor parent == textArea");
        startChar = selection.getRangeAt(0).startOffset;
        endChar = selectionStartNode.length + selection.getRangeAt(0).endOffset;
    }

    //new selection starts in already selected text
    else if (selectionStartNode.parentElement === oldSelectionElement) {
        console.log("selection anchor parent == oldSelection");
        //TODO: consider selection == textAreaElement.firstChild
        startChar = textAreaElement.firstChild.length + selection.getRangeAt(0).startOffset;
        endChar = textAreaElement.firstChild.length + oldSelectionElement.firstChild.length +
            selection.getRangeAt(0).endOffset;
    }

    else {
        //error
        console.log("error in function btn_fileTextEditorSelect!");
        return;
    }

    console.log("start char:" + startChar);
    console.log("end char:" + endChar);

    //now delete old mark
    removeOldSelection();
    console.log("removed old selection");
    //make new range
    var range = document.createRange();
    range.setStart(textAreaElement.firstChild, startChar);
    range.setEnd(textAreaElement.firstChild, endChar);
    makeNewSelection(range);
    console.log("made new selection");
    document.getSelection().empty();
}

function removeOldSelection() {
    var oldSelection = document.getElementById("textEditorSelection");

    if (oldSelection === null)
        return;
    //get selection content and "unwrap" it

    var parent = oldSelection.parentNode;
    //insert old content before the selection mark tag
    parent.insertBefore(oldSelection.firstChild, oldSelection);
    //remove the (now empty) selection mark tag
    parent.removeChild(oldSelection);

    parent.normalize(); //required to prevent fragmenting into multiple text nodes.
}


function makeNewSelection(range) {
    //set input to hidden form
    var startElement = document.getElementById("textEditorStart");
    var endElement = document.getElementById("textEditorEnd");
    startElement.value = range.startOffset;
    endElement.value = range.endOffset;

    //create visual mark
    var mark = document.createElement("span");
    mark.setAttribute("id", "textEditorSelection");
    range.surroundContents(mark);

    clearErrorMsg();
}



$( document ).ready(function() {
    var textAreaElement = document.getElementById("textEditorTextarea");
    var startElement = document.getElementById("textEditorStart");
    var endElement = document.getElementById("textEditorEnd");
    //check if there is an already existing selection
    if (startElement.value === "" ||  endElement.value === "")
        return;

    //else, a selection exists --> mark it.
    var range = document.createRange();
    range.setStart(textAreaElement.firstChild, startElement.value);
    range.setEnd(textAreaElement.firstChild, endElement.value);
    makeNewSelection(range);
});


