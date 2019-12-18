

var selecting = false;

function updateImageSelection() {
    
    var ele_offsetX = document.getElementById("input_offsetX");
    var ele_offsetY = document.getElementById("input_offsetY");
    var ele_width = document.getElementById("input_width");
    var ele_height = document.getElementById("input_height");
    var image = document.getElementById("fileImage");
    var marker_offsetX = document.getElementById("offsetXMarker");
    var marker_offsetY = document.getElementById("offsetYMarker");
    var marker_width = document.getElementById("widthMarker");
    var marker_height = document.getElementById("heightMarker");
    
    var offsetX = ele_offsetX.value;
    var offsetY = ele_offsetY.value;
    var width = ele_width.value;
    var height = ele_height.value;
    
    var srcW = image.naturalWidth;
    var srcH = image.naturalHeight;
    
    var w = image.width;
    var h = image.height;

    //Check if bounds are not overstepped
    if(offsetX > srcW) {
        offsetX = srcW;
        ele_offsetX.value = offsetX;
    }
    else if(offsetX < 0) {
        offsetX = 0;
        ele_offsetX.value = offsetX;
    }
    
    if(offsetY > srcH) {
        offsetY = srcH;
        ele_offsetY.value = offsetY;
    }
    else if(offsetY < 0) {
        offsetY = 0;
        ele_offsetY.value = offsetY;
    }
      
    
    if(width > srcW - offsetX) {
        width = srcW - offsetX;
        ele_width.value = width;
    }
    else if(width < 0) {
        width = 0;
        ele_width.value = width;
    }
    
    if(height > srcH - offsetY) {
        height = srcH - offsetY;
        ele_height.value = height;
    }
    else if(height < 0) {
        height = 0;
        ele_height.value = height;
    }
    
    ele_width.max = srcW - offsetX;
    ele_height.max = srcH - offsetY;
    
    //Calculate percentage Values of Selection
    var offsetXRel = offsetX/srcW;
    var offsetYRel = offsetY/srcH;
    var widthRel = width/srcW;
    var heightRel = height/srcH;
    
    marker_offsetX.style.width = Math.min(Math.max((offsetXRel*100), 0), 100)  + "%";
    marker_offsetY.style.height = Math.min(Math.max((offsetYRel*100), 0), 100) + "%";
    marker_width.style.left = Math.min(Math.max((offsetXRel*100 + widthRel*100), 0), 100) + "%";
    marker_width.style.width = Math.min(Math.max((100 - (offsetXRel*100 + widthRel*100)), 0), 100) + "%";
    marker_height.style.top = Math.min(Math.max((offsetYRel*100 + heightRel*100), 0), 100) + "%";
    marker_height.style.height = Math.min(Math.max((100 - (offsetYRel*100 + heightRel*100)), 0), 100) + "%";
    
}

function initImageSelection() {
    
	//Prevent Enter Key to submit Form
	$(window).keydown(function(event){
		if(event.keyCode == 13) {
		  event.preventDefault();
		  updateImageSelection();
		  return false;
		}
	});
	
	
    var ele_offsetX = document.getElementById("input_offsetX");
    var ele_offsetY = document.getElementById("input_offsetY");
    var ele_width = document.getElementById("input_width");
    var ele_height = document.getElementById("input_height");
    var image = document.getElementById("fileImage");
    var marker_offsetX = document.getElementById("offsetXMarker");
    var marker_offsetY = document.getElementById("offsetYMarker");
    var marker_width = document.getElementById("widthMarker");
    var marker_height = document.getElementById("heightMarker");
    
    var srcW = image.naturalWidth;
    var srcH = image.naturalHeight;
    
    ele_offsetX.max = srcW;
    ele_offsetY.max = srcH;
    
    ele_width.max = srcW;
    ele_height.max = srcH;
    
    if(ele_width.value == 0) {
        ele_width.value = srcW;
    }
    
    if(ele_height.value == 0) {
         ele_height.value = srcH;
    }
    
    updateImageSelection();
    
    $( "#ImageContainer" ).mousedown(function(e) {
        var pos = relativeCoords(e);
     
        var ele_offsetX = document.getElementById("input_offsetX");
        var ele_offsetY = document.getElementById("input_offsetY");
        var image = document.getElementById("fileImage");
       
       var srcW = image.naturalWidth;
       var srcH = image.naturalHeight;
       
        var w = image.width;
        var h = image.height;
       
        
        ele_offsetX.value = Math.floor(pos.x * (srcW/w));
        ele_offsetY.value = Math.floor(pos.y  * (srcH/h));
        
        selecting = true;
        updateImageSelection();
    });
    
    $(document).mousemove(function(e) {
       if(selecting) {
           var pos = relativeCoords(e);
           
           var ele_offsetX = document.getElementById("input_offsetX");
           var ele_offsetY = document.getElementById("input_offsetY");
           var ele_width = document.getElementById("input_width");
           var ele_height = document.getElementById("input_height");
           
           var image = document.getElementById("fileImage");
       
           var srcW = image.naturalWidth;
           var srcH = image.naturalHeight;
           
            var w = image.width;
            var h = image.height;
            
           ele_width.value = Math.floor((pos.x * (srcW/w)) -  ele_offsetX.value);
           ele_height.value = Math.floor((pos.y  * (srcH/h)) - ele_offsetY.value);
           updateImageSelection();
       }
    });

    $( "#ImageContainer" ).mouseup(function(e) {
       var pos = relativeCoords(e);
       
       var ele_width = document.getElementById("input_width");
       var ele_height = document.getElementById("input_height");
        
       var image = document.getElementById("fileImage");
       
        var srcW = image.naturalWidth;
        var srcH = image.naturalHeight;
           
        var w = image.width;
        var h = image.height;
            
        ele_width.value = Math.floor((pos.x * (srcW/w)) -  ele_offsetX.value);
        ele_height.value = Math.floor((pos.y  * (srcH/h)) - ele_offsetY.value);
       
        selecting = false;
        updateImageSelection();
    });   

    $( "#ImageContainer" ).mouseleave(function(e) {
        selecting = false;
    });        
}

function relativeCoords ( event ) {
    var x = event.pageX - $('#ImageContainer').offset().left;
    var y = event.pageY - $('#ImageContainer').offset().top;
  return {x: x, y: y};
}
