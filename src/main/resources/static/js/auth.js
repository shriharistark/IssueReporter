
//need to write code the below is sample junk code


const host = window.location.host;
const protocol = window.location.protocol;

function authoriseWithGoogle(){
    // const port = window.location.port; //since host is the combination of hostName and port

    //state will be set here
    fetch("/auth").then((response) => {
        console.log("state set");
    }, (fail) => {
        console.log("error with setting the state");
    }).then(function () {

        //once the state is set. Start the Oauth
        const client_id = "126208571601-fitl8ba1afjkb8on2v64fg8gfdf6efc5.apps.googleusercontent.com";
        const redirect_uri = protocol+"//"+host+"/auth/google";
        const scope = "openid email";
        let state = '<%=session.getAttribute("state")%>';

        const uri = "https://accounts.google.com/o/oauth2/v2/auth?" +
            " client_id="+client_id+"&" +
            " response_type=code&" +
            " scope="+scope+"&" +
            " redirect_uri="+redirect_uri+"&" +
            " state="+state;

        let init = {
            method: "GET",
        };

        //this fetch will have google redirect the request for auth to the redirect_uri specified.
        //check the state there and make post request to google using the auth Code given by the google
        //will get access and refresh tokens which can be sent as a response here
        fetch(uri, init).then((tokens) => {
            return tokens.json();
        }).then(credentials_json => {
            let credentials = JSON.parse(credentials_json);
            console.log(credentials);
        });
    })

};

//new code

