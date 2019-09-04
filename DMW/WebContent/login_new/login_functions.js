var pleasewaitmsg = '<img src="../imgs/ajax.gif" />Please wait...';

jQuery(document).ready(function(){
	jQuery('#forgotpassword').on('click', function(e){
            new Event(e).stop();
            document.mochaUI.newWindow({
                id: '_forgotPassword',
                title: 'Forgot Secure Web Password',
                loadMethod: 'iframe',
                contentURL: 'forgot_pass.php',
                modal: true,
                width: 450,
                height: 240
            });
        });
    
    
        jQuery('#contactUs').on('click', function(e){
            new Event(e).stop();
            document.mochaUI.newWindow({
                id: '_contactUs',
                title: 'Contact IT Help Desk',
                loadMethod: 'iframe',
                contentURL: 'contact_us.php',
                draggable: true,
                modal: true,
                width: 700,
                height: 340
            });
        });
    

        jQuery('#changePassword').on('click', function(e){
            new Event(e).stop();
            document.mochaUI.newWindow({
                id: '_changePassword',
                title: 'Change Secure Web Password',
                loadMethod: 'iframe',
                contentURL: 'change_password.php',
                draggable: true,
                modal: true,
                width: 450,
                height: 310
            });
        });
    
    
        jQuery('#logoutUser').on('click', function(e){
            new Event(e).stop();
            document.mochaUI.newWindow({
                id: '_logoutUser',
                title: 'Confirm Logout',
                loadMethod: 'iframe',
                contentURL: 'logout_user.php',
                draggable: true,
                modal: true,
                width: 500,
                height: 200
            });
        });
});
function closeWindow(elname) {
    document.mochaUI.closeWindow($(elname));
}

function handleResponse() {

    if(http.readyState == 4){

        //alert(http.responseText)
        //alert(ajaxaction);
        switch (ajaxaction){

            case 'validateusergetid':
                $('pleaseWait').empty();
                var JSONtext = http.responseText;
                // convert received string to JavaScript object
                var JSONobject = JSON.parse(JSONtext);
                if (JSONobject.msg.substring(0,7) == 'ERROR: ') {
                    $('pleaseWait').empty().appendText(JSONobject.msg.substring(7));
                }else{
                    redirect_url(JSONobject.msg);
                }
                break;

            case 'validateusergetapps':
                $('pleaseWait').empty();
                var JSONtext = http.responseText;
                // convert received string to JavaScript object
                var JSONobject = JSON.parse(JSONtext);
                if (JSONobject.msg.substring(0,7) == 'ERROR: ') {
                    $('pleaseWait').empty().appendText(JSONobject.msg.substring(7));
                }else{
                    redirect_url('');
                }
                break;

            case 'closesession':
                closeWindow('_logoutUser');
                top.location = '../index.php?logout=true';
                break;

            case 'gotoapp':
                var JSONtext = http.responseText;
                // convert received string to JavaScript object
                var JSONobject = JSON.parse(JSONtext);
                if (JSONobject.msg.substring(0,7) == 'ERROR: ') {
                    $('loginStatus').empty().appendText(JSONobject.msg.substring(7));
                }else{
                    $('loginStatus').empty();
                    document.hidden.id.value = JSONobject.id;
                    document.hidden.action = JSONobject.url;
                    document.hidden.target = '_newwin'+JSONobject.idx;
                    document.hidden.submit();
                }
                break;

        }
    }
}

function login() {
    var msg = "";
    if (document.loginForm.username.value == "") msg += " Username is required.";
    if (document.loginForm.password.value == "") msg += " Password is required.";
    if (msg == "") {
        $('pleaseWait').empty().setHTML(pleasewaitmsg);
        var ajaxaction = 'validateusergetid';
        if (document.loginForm.return_url.value == 'userapplist.php') ajaxaction = 'validateusergetapps'; //This is the same process used for calling apps.
        //$('pleaseWait').empty();
        currentTime = new Date();
        createRequestObject();
        sendReq('../src/login.ajax.php', 'time,action,appname,username,password', currentTime.getTime()+','+ajaxaction+','+encodeURIComponent(document.loginForm.appname.value)+','+encodeURIComponent(document.loginForm.username.value)+','+encodeURIComponent(document.loginForm.password.value), 'POST');
    }else{
        $('pleaseWait').empty().appendText(msg);
    }
    return false;
}

function gotoapp(idx,url,appkey,version) {
    $('loginStatus').empty().setHTML(pleasewaitmsg);
    var ajaxaction = 'gotoapp';
    currentTime = new Date();
    createRequestObject();
    sendReq('../src/login.ajax.php', 'time,action,idx,url,appkey,version', currentTime.getTime()+','+ajaxaction+','+idx+','+url+','+appkey+','+version);
}

function logout(type) {
    if (type == 'Y') {
        var ajaxaction = 'closesession';
        currentTime = new Date();
        createRequestObject();
        sendReq('../src/login.ajax.php', 'time,action', currentTime.getTime()+','+ajaxaction);
    }else{
        closeWindow('_logoutUser');
    }
}

function redirect_url(id) {
    var url = document.loginForm.return_url.value;
    if (id != '') {
        if (url.indexOf('?') == -1) {
            url = url + '?id='+id;
        }else{
            url = url + '&id='+id;
        }
    }
    window.location = url;
}

function readCookie(name) {
    var nameEQ = name + "=";
    var ca = document.cookie.split(';');
    for(var i=0;i < ca.length;i++) {
        var c = ca[i];
        while (c.charAt(0)==' ') c = c.substring(1,c.length);
        if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length,c.length);
    }
    return null;
}

function isLoggedIn() {
    var ssoRememberCookie = readCookie('ssoRememberCookie');
    if (ssoRememberCookie == null) {
        window.location = 'login.php';
        return false;
    } else {
        return true;
    }
}