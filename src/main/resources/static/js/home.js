$(document).ready(

    function(){

        $("#login-signup").click(()=>{
            showSignupDialogBox();
        });

        $("#login").click(function(){
            $(".jconfirm").hide();
            showLoginDialogBox();
        });
        $("#signup").click(function(){
            $(".jconfirm").hide();
            showSignupDialogBox();
        });

        function showSignupDialogBox(){
            $.confirm({
                title : false,
                escapeKey : true,
                theme : 'material',
                animationSpeed: 100,
                boxWidth : '40%',
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
        }

        function showLoginDialogBox(){
            $.confirm({
                title : false,
                escapeKey : true,
                theme : 'material',
                animationSpeed: 100,
                boxWidth : '40%',
                useBootstrap : false,
                content : '<div class = "login-signup-box">\n' +
                '        <div class="title">Login with </div>\n' +
                '        <div id="social-login">\n' +
                '            <img src="img/googe-icon.png" id="google-sign-in" class="social-signin gsign-in" onclick="authoriseWithGoogle()">\n' +
                '            <img src="img/fbsign-in.png" id = "facebook-sign-in" class="social-signin facebooksign-in">\n' +
                '            <img src = "img/github-signin.png" id ="github-sign-in" class="social-signin githubsign-in">\n' +
                '        </div>\n' +
                '        <div id ="signup" class="existing-user" >Need an account? Sign up</div>\n' +
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
)