function initXMLEditor() {
    
    var xmlString = document.getElementById('xmlContainer').innerHTML;
    
    var xmlString_unesc = jDecode(xmlString);
    
    xmlObj = $.parseXML(xmlString_unesc);
    
    var json = xmlToJson(xmlObj.documentElement);
    
    $('#jstree_container').jstree({
        'core' : {
            'data' : json
        }
    });
    
    $('#jstree_container').on("ready.jstree", function() {
         var tree = $('#jstree_container').jstree(true);
         
         tree.load_all();
    });
    
    $('#jstree_container').on("load_all.jstree", function() {
         var tree = $('#jstree_container').jstree(true);
         
          //Set selected nodes from artefact Data
         if(document.getElementById("selectedXML").value && document.getElementById("selectedXML").value != "") {
             
             var dataString = document.getElementById("selectedXML").value;
             var nodes = dataString.split(",");
             
             for(var i = 0; i < nodes.length; i++) {
                 
                 var node = tree.get_node('j1_' + nodes[i]);
                 
                 var pathArray = tree.get_path(node,false,true);
                 
                 for(var n = 0; n < pathArray.length; n++) {
                     var parentNode = tree.get_node(pathArray[n]);
                     tree.open_node(parentNode);
                 }
                 
                 tree.select_node(node);
             }
         }
    });
    
    
    
    $('#jstree_container').on("changed.jstree", function (node, selected, event) {
        console.log(node);
        console.log(selected);
        console.log(event);
        
        selectedNodes = selected.selected;
        document.getElementById("selectedXML").value = "";
        for(var i = 0; i < selectedNodes.length; i++) {
             nodeId = selectedNodes[i].replace("j1_","");
             
            if(i > 0) document.getElementById("selectedXML").value += "," + nodeId;
            else document.getElementById("selectedXML").value += nodeId;
        }
    });
    
}

//Decode ampersand encoding
function jDecode(str) {
    return $("<div/>").html(str).text();
}

function xmlToJson(xmlNode) {
    return {
        text: xmlNode.firstChild && xmlNode.firstChild.nodeType === 3 ? xmlNode.tagName + ":" + xmlNode.firstChild.textContent  : xmlNode.tagName + ":",
        children: [...xmlNode.children].map(childNode => xmlToJson(childNode))
    };
}

function selectArtefactData() {
    
    var tree = $('#jstree_container').jstree(true);
        
       
}