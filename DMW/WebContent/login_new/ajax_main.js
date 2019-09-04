var http = false;
var ajaxaction = "";

function sendReq(serverFileName, variableNames, variableValues) {
    var paramString = '';

    variableNames = variableNames.split(',');
    variableValues = variableValues.split(',');
    ajaxaction = variableValues[1];

    for(i=0; i<variableNames.length; i++) {
        paramString += variableNames[i]+'='+variableValues[i]+'&';
    }
    paramString = paramString.substring(0, (paramString.length-1));

    if (paramString.length == 0) {
        http.open('post', serverFileName);
    }
    else {
        //alert(paramString);
        //http.open('post', serverFileName+'?'+paramString);
        http.open('post', serverFileName);
    }
    http.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    http.onreadystatechange = handleResponse;
    http.send(paramString);
}

function createRequestObject(){
    //var obj;
    var browser = navigator.appName;

    http = false;

    if(browser == "Microsoft Internet Explorer"){
        http = new ActiveXObject("Microsoft.XMLHTTP");
    }
    else{
        http = new XMLHttpRequest();
    }
    return http;
}
function clearText() {
    document.getElementById("username").value = "";
    document.getElementById("password").value = "";
	document.getElementById("pleaseWait").innerHTML = "";
}
