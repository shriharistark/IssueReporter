$(document).ready(

    function(){

        $("#login-signup").click(()=>{
            boxes.showSignupBox();
        });

        $("body").on("click","#login",function(){
            $(".jconfirm").remove();
            boxes.showLoginBox();
        });
        $("body").on("click","#signup",function(){
            $(".jconfirm").remove();
            boxes.showSignupBox();
        });

        var boxes = {

            showSignupBox : function(){
                $.confirm({
                    title : false,
                    escapeKey : true,
                    theme : 'material',
                    animationSpeed: 400,
                    boxWidth : '30%',
                    useBootstrap : false,
                    content : '<div class = "login-signup-box">\n' +
                    '        <div class="title">Sign up with </div>\n' +
                    '        <div id="social-login">\n' +
                    '            <img src="img/googe-icon.png" id="google-sign-in" class="social-signin gsign-in" onclick="authoriseWithGoogle()">\n' +
                    '            <img src="img/fbsign-in.png" id = "facebook-sign-in" class="social-signin facebooksign-in">\n' +
                    '            <img src = "img/github-signin.png" id ="github-sign-in" class="social-signin githubsign-in">\n' +
                    '        </div>\n' +
                    '        <div id ="login" class="existing-user" >Already have a account? Log in</div>\n' +
                    '    </div>',
                    buttons : {
                        close : {
                            isHidden : true
                        }
                    },
                    backgroundDismiss : true,

                });
            },

            showLoginBox : function(){
                $.confirm({
                    title : false,
                    escapeKey : true,
                    theme : 'material',
                    animationSpeed: 400,
                    boxWidth : '30%',
                    useBootstrap : false,
                    content : '<div class = "login-signup-box">\n' +
                    '        <div class="title">Login with </div>\n' +
                    '        <div id="social-login">\n' +
                    '            <img src="img/googe-icon.png" id="google-sign-in" class="social-signin gsign-in" onclick="authoriseWithGoogle()">\n' +
                    '            <img src="img/fbsign-in.png" id = "facebook-sign-in" class="social-signin facebooksign-in">\n' +
                    '            <img src = "img/github-signin.png" id ="github-sign-in" class="social-signin githubsign-in">\n' +
                    '        </div>\n' +
                    '        <div id ="signup" class="new-user" >Need an account? Sign up</div>\n' +
                    '    </div>',
                    buttons : {
                        close : {
                            isHidden : true
                        }
                    },
                    backgroundDismiss : true,

                });
            }
        }

    }
)