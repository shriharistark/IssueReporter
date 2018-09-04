$(document).ready(

    function(){


        $.confirm({

            title : 'login/signup',
            content : '<div>\n' +
            '        <img src="img/GsignIn.png" id="google-sign-in" onclick="authoriseWithGoogle()">\n' +
            '    </div>'

        });
    }
)