
//need to write code the below is sample junk code


const host = window.location.host;
const protocol = window.location.protocol;

// (function(){
//     let uri = protocol+"//"+host+"/auth/";
//     let init = {
//         method:'GET'
//     };
//     fetch(uri, init).then((response) => {
//         console.log("state set");
//     }, (fail) => {
//         console.log("error with setting the state");
//     })
// })();

function authoriseWithGoogle(){
    // const port = window.location.port; //since host is the combination of hostName and port

    // function getCook(cookiename)
    // {
    //     // Get name followed by anything except a semicolon
    //     var cookiestring=RegExp(""+cookiename+"[^;]+").exec(document.cookie);
    //     // Return everything after the equal sign, or an empty string if the cookie name not found
    //     return decodeURIComponent(!!cookiestring ? cookiestring.toString().replace(/^[^=]+./,"") : "");
    // }

    //state will be set here

        //once the state is set. Start the Oauth
        const client_id = "126208571601-ge5rng2g2bui5o46pjr73chaska7bdtf.apps.googleusercontent.com";
        const redirect_uri = protocol+"//"+host+"/auth/google";
        const scope = "" +
            " https://www.googleapis.com/auth/userinfo.email " +
            "https://www.googleapis.com/auth/userinfo.profile";
        const state = (function(cookiename){
            var cookiestring=RegExp(""+cookiename+"[^;]+").exec(document.cookie);
            // Return everything after the equal sign, or an empty string if the cookie name not found
            return decodeURIComponent(!!cookiestring ? cookiestring.toString().replace(/^[^=]+./,"") : "");
        })('auth_state');
        console.log(`state in frontend: ${state}`);

        // uri = "https://accounts.google.com/o/oauth2/v2/auth?" +
        //     "client_id="+client_id+"&" +
        //     "response_type=code&" +
        //     "scope="+scope+"&" +
        //     "redirect_uri="+redirect_uri+"&" +
        //     "state="+state;

        uri = "https://accounts.google.com/o/oauth2/v2/auth?redirect_uri="+redirect_uri
            +"&prompt=consent&response_type=code&client_id="+client_id
            +"&scope="+scope+
            "&access_type=offline"+
            "&state="+state;

        /*
        uri =
         */

        // init = {
        //     method: "GET",
        //     headers : {
        //         'Access-Control-Allow-Credentials': 'true',
        //     },
        //     mode : 'no-cors',
        //     credentials : 'omit'
        // };
        //
        // //this fetch will have google redirect the request for auth to the redirect_uri specified.
        // //check the state there and make post request to google using the auth Code given by the google
        // //will get access and refresh tokens which can be sent as a response here
        // fetch(uri, init).then((tokens) => {
        //
        // });

        window.location.href = uri;

};

//new code

