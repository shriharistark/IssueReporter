$(document).ready(

function(){

    // no libraries used for the signup form! :D ;)

    //default values for forms
        let default_values = {
            name : $("#name").val(),
            email : $("#email").val(),
            team : $("#team").val(),
            allValues : [$("#name").val(),$("#email").val(),$("#team").val()],
        };

        let formEvents = {
            source : '',    //for magic purposes
            clicks : 0,

            _isValidForm : function (formParams) {

                let rules = {
                    name : function(funcContainingRulesFornaming,params){
                      // if(name.length > 3 && !name.includes(default_values["name"])){
                      //     console.log("name validator passed!");
                      //     return true;
                      // }
                      // return false;
                        return funcContainingRulesFornaming.call(rules,params);
                    },

                    team : function(team){
                        if(team.length > 3 && !team.includes(default_values["team"])){
                            console.log("team validator passed!");
                            return true;
                        }
                        return false;
                    },

                    email : function(email){
                        let email_regex = /^([a-zA-Z0-9_.+-])+\@(([a-zA-Z0-9-])+\.)+([a-zA-Z0-9]{2,4})+$/;
                        if(email_regex.test(email)){
                            console.log("email validator passed");
                            return true;
                        }

                        return false;
                    }
                }

                let formTests = {
                    email : rules.email(formParams["email"]),
                    //define your custom rules here - implement the same for email as well as team fields
                    name  : rules.name(function(name){
                        if(name.length > 3 && !name.includes(default_values["name"])){
                            console.log("name validator passed!");
                            return true;
                        }
                        return false;
                    },formParams["name"]),
                    team  : rules.team(formParams["team"]),
                };

                if(formParams["email"] && formParams["name"] && formParams["team"]){

                    //passed integrity check
                    if(formTests.email && formTests.name && formTests.team){
                        return ["ok"];
                    }

                    //fail : contains default value || user has not entered any values on his own
                    else if(formParams.email === default_values.email
                        || formParams.team === default_values.team
                        || formParams.name === default_values.name){
                        console.log("contains default value(s), form integrity failed");

                        //fields with default values
                        let defaultParams = [];
                        defaultParams.push("Contains default parameters");
                        for(const param of Object.keys(formParams)){
                            //current form value == default set form value
                            if(formParams[param] === default_values[param]){
                                defaultParams.push(param);
                            }
                        }
                        return defaultParams;
                    }

                    //fail : one of the rule(s) are not satisfied
                    else{
                        //invalid message
                        let invalidParams = [];
                        invalidParams.push("Rules not satisfied");
                        for(const test of Object.keys(formTests)){
                            // console.log(formTests[test]);
                            if(!formTests[test]){
                                invalidParams.push(test);
                            }
                        }
                        return invalidParams;
                    }
                }

                else{
                    //fail : param(s) are empty
                    let emptyParams = [];
                    emptyParams.push("required params empty");
                    for(const test of Object.keys(formTests)){
                        console.log(formTests[test]);
                        if(!formTests[test]){
                            emptyParams.push(test);
                        }
                    }
                    return emptyParams;
                }
            },

            submit : function (formParams,otherparams,actionsuponinvalidparams) {

                if(!formParams){
                    return false;
                }

                let validatorResult = this._isValidForm(formParams);
                if(Array.isArray(validatorResult)){
                    if(validatorResult[0] === "ok"){
                        //do the fetch post request bla bla
                    }

                    else{
                        let message = validatorResult[0];
                        let problematic_params = validatorResult.splice(1,validatorResult.length);
                        console.log(message,problematic_params);
                        actionsuponinvalidparams(message,problematic_params);
                    }
                }else {
                    //will never happen unless the source is manually tampered
                    console.log("validator response isn't an array");
                }

            }
        };

        //magic code that does lot of magic
        $("body").on("click",function(evt){
            evt.stopImmediatePropagation();
            /*console.log($(evt.target));
            console.log("event type: ",evt.type);
            console.log(default_values.allValues);*/

            if($(evt.target).is("input")){

                formEvents.clicks++;
                // console.log("clicky click: ",formEvents.clicks);
                let event = evt;
                let _formEvents = formEvents;
                let _defaultObj = default_values.allValues;

                setTimeout(function(){
                    let value = $(event.target).val();
                    // console.log("insanity check: ",_defaultObj.includes(value),
                    //     "value ->",value,"parent ->",_defaultObj);

                    /*
                    if(event.type === "dblclick" && default_values.allValues.includes(value)){
                        console.log("clicked the text: ", $(this));
                        _formEvents.source = $(event.target).attr("id");
                        $(event.target).val("");
                    }*/

                    if(event.type === "click" &&
                        _formEvents.clicks < 2 && _formEvents.clicks > 0 &&
                        _defaultObj.includes(value)) {

                        // console.log("click block",formEvents.clicks);
                        // console.log("clicked the text: ", $(this));
                        formEvents.source = $(event.target).attr("id");
                        $(event.target).val("");
                    }
                    console.log(_defaultObj)
                    formEvents.clicks = 0;
                },300);

            }

            else{
                // console.log("clicked outside the text field: ",$(this), "event data: ", formEvents["source"]);
                // console.log("input elems: ",$("#signup-form input"));
                $("#signup-form input").each(function(index){
                    // console.log($(this));
                    if($(this).val().length <= 0){
                        setTimeout(()=>{
                            $(this).val(default_values[$(this).attr("id")]);
                        },250);
                    }
                })
            }
        });

        $("body").on("keydown",function(evt){

            // console.log(evt.metaKey);
            if(evt.metaKey && evt.keyCode === 13){
                console.log('command and enter is pressed');
                console.log("sign up from submitted using cmd+enter");

                let formParams = {
                    email : $("#email").val(),
                    name : $("#name").val(),
                    team : $("#team").val(),
                }
                formEvents.submit(formParams,'',function(message,invalidParams){
                    let cssInvalid = {
                        "border-bottom" : "2px solid #ff0006",
                        "box-shadow" : "0px 8px 10px -3px #ffbebe",
                    };

                    let cssValid = {
                        "border-bottom": "2px solid #66bd00",
                        "box-shadow": "0px 5px 8px -3px #58d246",
                    };
                    //assign invalid css to invalid parameters
                    invalidParams.forEach(param => {
                        $("#"+param).closest(".signup-field").css(cssInvalid);
                        $("#"+param).closest(".signup-field").css('border-color',"#ff0000");
                    });

                    //assigns css green for valid params - not required
                    /*for(let key of Object.keys(formParams)){
                        if(!invalidParams.includes(key)){
                            $("#"+key).closest(".signup-field").css(cssValid);
                        }
                    }*/

                    setTimeout(()=>{
                        $(".signup-field").css({
                            "border-bottom" : "",
                            "box-shadow" : "",
                        })
                    }, 3500);
                });
                //submit the form
            }
        });

        $("#submit-signup-form").on("click",function (evt) {
            console.log("sign up from submitted using click");
        });

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
            },

            showSignupForm : function (partialObject) {
                let user_object = partialObject;

            }
        }

    }
)