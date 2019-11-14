function convertToStringRepresentation(timeInMs) {
    var minutes, seconds;
    minutes = Math.floor(timeInMs/(1000 * 60));
    seconds = timeInMs/1000 - (minutes * 60);

    //format to fixed decimals
    seconds = Number(seconds).toFixed(3);

    //format minutes to two digits
    if (minutes < 10)
        minutes = "0" + minutes;

    //format seconds to two leading digits
    if (seconds < 10)
        seconds = "0" + seconds;

    return minutes + ":" + seconds;
}

function getCurrentTimeInMs() {
    var media = document.getElementById("mediaPlayer");
    return Math.floor(media.currentTime * 1000);
}

function transferStartTime() {
    var start = document.getElementById("startTime");
    var startAsText = document.getElementById("startTimeText");
    start.value = getCurrentTimeInMs();
    startAsText.value = convertToStringRepresentation(getCurrentTimeInMs());
}

function transferEndTime() {
    var end = document.getElementById("endTime");
    var endAsText = document.getElementById("endTimeText");
    end.value = getCurrentTimeInMs();
    endAsText.value = convertToStringRepresentation(getCurrentTimeInMs());
}


function playRange() {
    if (setErrorIfNotValid())
        return;

    var media = document.getElementById("mediaPlayer");
    var start = document.getElementById("startTime");
    var end = document.getElementById("endTime");

    media.currentTime = start.value / 1000.0;
    media.addEventListener("timeupdate", autoPause);
    media.play();
}


function autoPause(event) {
    var start = document.getElementById("startTime");
    var media = document.getElementById("mediaPlayer");
    var end = document.getElementById("endTime");
    if(this.currentTime >= end.value / 1000.0) {
        this.pause();
        media.removeEventListener('timeupdate', autoPause);
    }
    //user moved the slider manually out of range while playing the range.
    else if(this.currentTime < start.value / 1000.0) {
        media.removeEventListener('timeupdate', autoPause);
    }
}

function setErrorIfNotValid() {
    var start = document.getElementById("startTime");
    var end = document.getElementById("endTime");
    var errorStartGtEnd = document.getElementById("errorStartGtEnd");
    var error = document.getElementsByClassName("error")[0];

    //clear old error
    error.innerHTML = "";

    if (parseInt(start.value) > parseInt(end.value))
    {
        error.innerHTML = errorStartGtEnd.innerHTML;
        return true;
    }
    return false;
}

function btn_submitApply() {
    if(setErrorIfNotValid())
        return;

    var form = document.getElementById("controlForm")
    form.submit();

}

function btn_submitNew() {
    if(setErrorIfNotValid())
        return;
    $("#inputArtefactTitle").modal()

}