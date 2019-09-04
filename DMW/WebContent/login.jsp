<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<!-- saved from url=(0284)https://sso.maxim-ic.com//app/sso/src/login.php?callback=https%3A%2F%2Fmxdpwebap01l.maxim-ic.com%2Fapp%2Fsaml%2Findex.php%3Fapp%3Dworkday%26done%3Dhttp%253A%252F%252Fwww.myworkday.com%252Fmaximintegratedproducts%252Fd%252Funifiedinbox%252Finitialinbox%252F2998%252417139.htmld&appkey= -->
<html xmlns="http://www.w3.org/1999/xhtml"><script id="tinyhippos-injected">if (window.top.ripple) { window.top.ripple("bootstrap").inject(window, document); }</script><head><meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Maxim Single Sign-On</title>
<meta http-equiv="X-UA-Compatible" content="IE=edge, chrome=1">
<meta name="description" content="Maxim Single Sign-On">
<meta name="keywords" content="php, javascript">
<meta name="Robots" content="INDEX,FOLLOW">
<link rel="stylesheet" type="text/css" href="../login_new/styleMain.css">
<link rel="icon" type="image/png" href="/web-commons/favicon.ico">



<title>Single Sign On</title>
<meta http-equiv="X-UA-Compatible" content="IE=edge, chrome=1">
<meta name="description" content="Maxim Single Sign-On">
<meta name="keywords" content="php, javascript">
<meta name="Robots" content="INDEX,FOLLOW">
<meta http-equiv="Cache-Control" content="no-store,no-cache,must-revalidate"> 
<meta http-equiv="Pragma" content="no-cache"> 
<meta http-equiv="Expires" content="-1">
<link rel="stylesheet" type="text/css" href="../login_new/styleMain.css">
<link href="https://sso.maxim-ic.com//app/sso/imgs/favicon.ico" rel="shortcut icon">
<script src="../login_new/jquery-1.10.2.js"></script>
<script type="text/JavaScript" src="../login_new/overlib_mini.js"></script>
<script type="text/javascript" src="../login_new/mootools-trunk-1475.js" charset="utf-8"></script>
<!--[if IE]>
<script type="text/javascript" src="../js/excanvas-compressed.js"></script>
<![endif]-->
<script type="text/javascript" src="../login_new/mocha.js"></script>
<script type="text/javascript" src="../login_new/ajax_main.js"></script>
<script type="text/javascript" src="../login_new/json2.js"></script>
<script type="text/javascript" src="../login_new/login_functions.js"></script>
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap-theme.min.css">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.3.0/css/font-awesome.min.css">
<script src="../login_new/bootstrap.min.js"></script>
</head>
<body style="background: 50% 0px repeat-y scroll rgb(239, 239, 239);">
                <div style="width: 80%; padding-left: 20%;">
                    <div id="loginStatus"></div>
                    <div style="background-color: #BFDEDF;height: 69px;padding:14px 12px;">
                        <img src="../login_new/logo-maxim-integrated.png" alt="Maxim Integrated">
                    </div>
                        
                    <div style="border:2px solid #BFDEDF; height: 500px; padding-top: 15%; box-shadow: 0 1px 4px #BFDEDF, 0 0 30px #BFDEDF inset">
                <h3 class="text-center">Single Sign On - Maxim Applications
                </h3>
                    <form id="loginForm" name="loginForm" method="post" action='<%= response.encodeURL("j_security_check") %>' onsubmit="javascript:document.getElementById('username').value=document.getElementById('username').value.toLowerCase();$('pleaseWait').empty().setHTML(pleasewaitmsg);">
                        <input name="appname" id="appname" type="hidden" value="">
                        <input name="action" id="action" type="hidden" value="validateusergetapps">
                        <input name="method" id="method" type="hidden" value="nonajax">
                        
                    <div class="row">
                                <div class="col-md-4"></div>
                                <div class="col-md-4">
                                    <div class="form-group">
                                        <input type="text" name="j_username" class="form-control text-center" placeholder="User Name" id="username" value="">
                                    </div>   
                                </div>
                                <div class="col-md-4"></div>
                            </div>
                            <div class="row">
                                <div class="col-md-4"></div>
                                <div class="col-md-4">
                                    <div class="form-group">
                                        <input type="password" name="j_password" class="form-control text-center" placeholder="Password" id="password" value="">
                                    </div>
                                    <div class="form-group text-center">
                                        <button type="submit" class="btn btn-info" >Sign On</button>
                                        <button type="button" class="btn btn-danger" onclick="javascript:clearText();">Clear</button>
                                    </div>
                                </div>
                                <div class="col-md-4"></div>
                                </div>
                                <br>
                                <div class="row">
                                <div class="col-md-4"></div>
                                <div class="col-md-4">
                    <div id="pleaseWait">
                        </div>
						</div>
                                <div class="col-md-4"></div>
                                </div>
                        </form>
            </div>                                                                                                                                            
                    <br>
                     <div style="width:100%;vertical-align:middle; border:1px solid #BFDEDF; box-shadow: 0 1px 4px #BFDEDF, 0 0 30px #BFDEDF inset" class="text-center">
          <div class="row">
                <div class="col-md-2">
                    &nbsp;&nbsp;&nbsp;&nbsp;<a href="mailto:SCMWebAndAnalytic@maximintegrated.com">
                        <span class="fa-stack fa-lg">
                            <i class="fa fa-square-o fa-stack-2x"></i>
                            <i class="fa fa-envelope fa-stack-1x"></i>
                        </span>
                    </a>
                            <span title="Help" style="color:#337ab7" class="fa-stack fa-lg" id="menu1" data-toggle="dropdown" aria-expanded="false">
                                    <i class="fa fa-square-o fa-stack-2x"></i>
                                    <i class="fa fa-question-circle fa-stack-1x"></i>
                            </span>
                            <ul class="dropdown-menu" role="menu" aria-labelledby="menu1">
                                <!-- <li role="presentation"><a role="menuitem" tabindex="-1" href="https://sso.maxim-ic.com//app/sso/src/login.php?callback=https%3A%2F%2Fmxdpwebap01l.maxim-ic.com%2Fapp%2Fsaml%2Findex.php%3Fapp%3Dworkday%26done%3Dhttp%253A%252F%252Fwww.myworkday.com%252Fmaximintegratedproducts%252Fd%252Funifiedinbox%252Finitialinbox%252F2998%252417139.htmld&appkey=#contactus" id="contactUs" title="Contact IT Help Desk">Contact SCM Help Desk</a></li> -->
                                <li role="presentation"><a role="menuitem" tabindex="-1" href="https://sso.maxim-ic.com//app/sso/src/login.php?callback=https%3A%2F%2Fmxdpwebap01l.maxim-ic.com%2Fapp%2Fsaml%2Findex.php%3Fapp%3Dworkday%26done%3Dhttp%253A%252F%252Fwww.myworkday.com%252Fmaximintegratedproducts%252Fd%252Funifiedinbox%252Finitialinbox%252F2998%252417139.htmld&appkey=#changepassword" id="changePassword" title="Change Secure Web Password">Change Secure Web Password</a></li>
                                <li role="presentation"><a role="menuitem" tabindex="-1" href="https://sso.maxim-ic.com//app/sso/src/login.php?callback=https%3A%2F%2Fmxdpwebap01l.maxim-ic.com%2Fapp%2Fsaml%2Findex.php%3Fapp%3Dworkday%26done%3Dhttp%253A%252F%252Fwww.myworkday.com%252Fmaximintegratedproducts%252Fd%252Funifiedinbox%252Finitialinbox%252F2998%252417139.htmld&appkey=#forgotpassword" id="forgotpassword" title="Forgot Password">Forgot Secure Web Password</a></li>
                            </ul>
                     
                    <!-- <a href="mailto:SCMWebAndAnalytic@maximintegrated.com">
                            <span class="fa-stack fa-lg">
                                <i class="fa fa-square-o fa-stack-2x"></i>
                                <i class="fa fa-comment fa-stack-1x"></i>
                            </span>
                        </a> -->
                </div>
                <div class="col-md-7 text-center">
                    <span>Best viewed and supported in IE8+ , Chrome, Firefox Browsers !</span>
                    <br>
                    <span>Powered by SCM Web Team</span>
                    <br>
                    © 2015 - <a href="https://www.maximintegrated.com/en.html">Maxim Integrated.</a>
                </div>
                
                <div class="col-md-2">
 
                   
                    <div class="col-md-4">
                        <a href="https://www.facebook.com/Maxim.IC" title="Facebook" class="facebook">
                            <span class="fa-stack fa-lg">
                                <i class="fa fa-square-o fa-stack-2x"></i>
                                <i class="fa fa-facebook fa-stack-1x"></i>
                            </span>                    
                        </a>                  
                    </div>
                    <div class="col-md-4">
                        <a href="https://twitter.com/Maxim_IC" title="Twitter" class="twitter">
                                <span class="fa-stack fa-lg">
                                    <i class="fa fa-square-o fa-stack-2x"></i>
                                    <i class="fa fa-twitter fa-stack-1x"></i>
                                </span>
                        </a>
                    </div>
                </div>
            </div>
        </div> 
        </div> 
<script type="text/javascript">
pleasewaitmsg = '<img src="../login_new/ajax.gif" />';
document.loginForm.username.focus();</script>
<div id="mochaModalOverlay" style="height: 932px; visibility: hidden; zoom: 1; opacity: 0; display: none;"></div></body></html>