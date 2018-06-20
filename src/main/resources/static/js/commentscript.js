var urlParams;
(window.onpopstate = function () {
    var match,
        pl     = /\+/g,  // Regex for replacing addition symbol with a space
        search = /([^&=]+)=?([^&]*)/g,
        decode = function (s) { return decodeURIComponent(s.replace(pl, " ")); },
        query  = window.location.search.substring(1);

    urlParams = {};
    while (match = search.exec(query))
        urlParams[decode(match[1])] = decode(match[2]);
})();

function helper_getDateFromTimeStamp(timestampString){
        let ts = new Date(+(timestampString));
        console.log(ts.toDateString().slice(4,-5));
        return ts.toDateString().slice(4,-5).concat(", ").concat(ts.getFullYear());
};

function scroll_to(divID){
    $('html, body').animate({
        scrollTop: $("#"+divID).offset().top - 120
    },250);
};


var issueid = urlParams['issueid'];
var issue = {};

(function () {

    let issueObj = {};

    function getIssue(issueId){

        let url = "/issue/readbyid?id="+issueId;
        let init = {
            method : "GET",
            headers : {
                'Accept' : 'application/json,text/plain,*/*',
                'Content-type' : 'application/json'
            }
        };

        return fetch(url,init).then(function (value) {
            //console.log(value.json());
            return value.json();
        },function (reason) {
            console.log(reason);
            return "{'result':'failed'}";
        }).then(function (val) {

            if(val.issue.code) {
                // console.log("inside promise: "+viewdetail+val.issue);
                //alert("comment added | ticketCode: "+viewdetail.code);
                issueObj = val.issue;
                return val;
            }
            else{
                alert("ticket creation failed | reason: "+viewdetail.ok);
                return "no issue found with id "+issueId;
            }
        }).then(function (issuereturn) {

            let issueObject = issuereturn.issue;
            let issueWebSafeKey = issuereturn.issuewebsafekey;

            let issueEl = $(".issue-view-main-container .issue");

            let issueBody = $(issueEl).find(".issue-body").first();
            $(issueBody).find(".title").html(issueObject.subject);
            $(issueBody).find(".code").html("#"+issueObject.code);
            $(issueBody).find(".date").html(function(){
                let ts = new Date(issueObject.code);
                console.log(ts.toDateString().slice(4,-5));
                return ts.toDateString().slice(4,-5).concat(", ").concat(ts.getFullYear());
            });
            $(issueBody).find(".assignedTo").html(issueObject.assignedto);

            $(issueBody).find(".code").data("issueid",issueObject.code);


            let detailel = $(".issue-view-comment-box").first();
            $(detailel).find(".issue-view-comment-name").html(issueObject.assignee);
            $(detailel).find(".issue-view-comment-description").html(issueObject.description);

            issue = issuereturn;

        })

    };

    getIssue(issueid);

    $("#load-more-comments").trigger("click");

})();

var comments = {

    showCommentBox : function (target,...args) {
        let arguments = args.join();
        console.log(args);
        // $(element).next(".issue-view-comment-new").css("display","block");
        $(target).parent().next(".issue-view-comment-new").slideToggle(400,"swing");
    },

    showReplyBox : function (target, ...args) {

        console.log(target);

        let closestCommentBox = $(target).closest(".issue-view-comment-box");
        console.log(closestCommentBox);
        if(closestCommentBox.hasClass("comment-parent")){
            let parentReplierName = $(closestCommentBox).find(".issue-view-comment-name").first().html();
            console.log($(target).first().closest(".issue-view-comment-name"));
            $("#add-reply textarea").val("@"+parentReplierName+": ");
        }
        $("#add-reply").hide();
        $(closestCommentBox).find("#add-reply").slideToggle(300);
        // scroll_to("add-reply");
    },


    //for populating the view
    fetchCommentsFromServer : function(issueId,cursor){

        let url = "/comments/readall?cursor="+cursor+"&issueid="+issueId;
        let init = {
            method : "GET",
            headers : {
                'Accept' : 'application/json,text/plain,*/*'
            }
        };

        loading = true;

        let nextcurs = "o";

        return fetch(url,init).then(function(val){

            //starts loading gif
            // $(window).trigger("loading");

            function x() {
                var promise = new Promise(function(resolve, reject) {

                    window.setTimeout(function () {
                        resolve('done!');
                    }, 200);
                });

                return promise;
            }

            return x().then(function() {
                if(cursor !== undefined){
                    console.log(val); // --> 'done!'
                    return val.json();
                }

                else{
                    throw "Fatal : cursor undefined";
                }
            });

        }, function (reason) {
            console.log("fetch failed due to "+reason);
            return "{'result':'failed'}"
        }).then((valuejson) => {

            if(valuejson.result === 'failed' || !valuejson.next){
                throw "end of line reached";
            }

            console.log("next cursor: "+valuejson.next);
            nextcurs = valuejson.next;
            return valuejson;
        }).then(function(value){

            //console.log(value);
            if(value.ok && nextcurs !== "") {
                let comments = value.commentsList;
                if(comments.length > 0) {
                    comments.forEach(comment => {
                        let commentId = comment.id;
                        // let commentKey = comments.subject;
                        let message = comment.message;
                        let author = comment.author;
                        // let isParent = comments.isparent;
                        let parentID = comment.parentID;
                        let issueID = comment.issueID;

                        console.log(this);

                        let commentDiv = document.createElement("div");
                        commentDiv.classList.add("issue-view-comment-box");
                        parentID === "" ? commentDiv.classList.add("comment-parent") : commentDiv.classList.add("comment-child");

                        commentDiv.setAttribute("commentID",commentId);

                        let authorEl =  document.createElement("div");
                        authorEl.classList.add("issue-view-comment-name","small-titles");
                        authorEl.innerText = author;

                        let descriptionEl =  document.createElement("div");
                        descriptionEl.classList.add("issue-view-comment-description");
                        descriptionEl.innerText = message;

                        let replyContainer =  document.createElement("div");
                        replyContainer.classList.add("issue-view-comment-date-reply-container");

                        let dateEl =  document.createElement("div");
                        dateEl.classList.add("issue-view-comment-date");
                        dateEl.innerText = helper_getDateFromTimeStamp(issueID);

                        let dotEl =  document.createElement("div");
                        dotEl.style.padding = "0px 6px";
                        dotEl.innerText = ".";

                        let replyEl =  document.createElement("div");
                        replyEl.classList.add("issue-view-comment-reply");
                        replyEl.innerText = "Reply";

                        let replyBoxEl = document.createElement("div");
                        replyBoxEl.classList.add("issue-view-comment-new");
                        replyBoxEl.setAttribute("id","add-reply");

                        let inputEl = document.createElement("input");
                        inputEl.setAttribute("placeholder","your name goes here..");
                        inputEl.setAttribute("type","text");

                        let textEl = document.createElement("textarea");
                        textEl.setAttribute("placeholder","your reply goes here..");
                        textEl.setAttribute("type","text");

                        let submitButtonEl = document.createElement("button");
                        submitButtonEl.classList.add("submit-btn");
                        submitButtonEl.innerText = "Reply";

                        replyBoxEl.append(inputEl);
                        replyBoxEl.append(textEl);
                        replyBoxEl.append(document.createElement("br"));
                        replyBoxEl.append(submitButtonEl);

                        commentDiv.append(authorEl);
                        commentDiv.append(descriptionEl);
                        commentDiv.append(replyContainer);
                        commentDiv.appendChild(replyBoxEl);

                        replyContainer.append(dateEl);
                        replyContainer.append(dotEl);
                        replyContainer.append(replyEl);

                        // document.body.appendChild(commentDiv);

                        // commentDiv.innerHTML += "<div class=\"issue-view-comment-name small-titles\">"+author+"</div>\n" +
                        //     "                <div class=\"issue-view-comment-description\">"+message+
                        //     "                </div>\n" + "\n" +
                        //     "                <div class=\"issue-view-comment-date-reply-container\">\n" +
                        //     "                    <div class=\"issue-view-comment-date-\">\n" +helper_getDateFromTimeStamp(issueID) +
                        //     "                    </div>\n" +
                        //     "                    <div style=\"padding: 0px 6px\">.</div>\n" +
                        //     "                    <div class=\"issue-view-comment-reply\">\n" +
                        //     "                        Reply\n" +
                        //     "                    </div>\n" +
                        //     "                </div>";


                        $(".issue-view-comments-section").prepend(commentDiv);
                        // document.getElementsByClassName("issue-view-comments-section")[0].appendChild(commentDiv);
                        //refresh events
                        scroll_to("load-more-comments");
                        // events.replyAdd();
                    });

                }
            }
            //loading = false;
            return value.next;
        }).catch(function(reason){
            //loading = false;
            console.log("end of results "+reason);
            loading = false;
        }).finally(()=>{
            loading = false;
            $(window).trigger("loading");
        });

    },

    fetchCommentsOnScrollHelper : function(){

        var cursor = {next:""};

        (function displayNextCursor(){

            console.log(this);
            // let caller = issue.fetchAndPopulateView.bind(issue);
            issue.fetchAndPopulateView.call(issue,6,cursor.next).then((y) =>{

                if(y) {
                    console.log("I got this: " + y);
                    cursor.next = y;
                }
            });
        })();
    },

    //publish comment helper
    postCommentToServer : function (...commentData) {


        // if isparent = true - it implies it is a primary comment (direct reply to the post)
        // if isparent = false - it implies it is a secondary comment (reply to a direct reply)

        let commentdata = commentData[0];
        let comment = {};
        comment.message = commentdata["message"];
        comment.author = commentdata["author"];
        comment.parentid = commentdata["parentid"];
        comment.issueid = commentdata["issueid"];
        comment.hasparent = commentdata["hasparent"];
        comment.issuewebsafekey = commentdata["issuekey"];
        console.log(JSON.stringify(comment));

        let url = "/comments/new";

        let init = {
            method : 'POST',
            body : JSON.stringify(comment),
            headers:{
                'Accept':'application/json,text/plain,*/*',
                'Content-type':'application/json'
            }
        };

        fetch(url,init).then(function (value) {
            console.log(value);
            return value;
        }, function (reason) {
            return reason.json();
        }).then(function (comments) {
            console.log(comments);
        });
    }
}


var events = {

    showFullDate : function(){

        $(".issue-view-comments-section").on("mouseenter",".issue-view-comment-date",function (evt) {

            evt.stopPropagation();
            let toolTipEl = document.createElement("div");
            toolTipEl.classList.add("tooltip");

            let dateContainerEl = document.createElement("div");
            dateContainerEl.classList.add("tooltiptext");

            let dateStr = $(this).closest(".issue-view-comment-box").first().attr("commentid");
            let dateTextEl = document.createTextNode(dateStr);

            dateContainerEl.appendChild(dateTextEl);
            toolTipEl.appendChild(dateContainerEl);
            console.log("it is mouse entered");
            $(this).closest(".issue-view-comment-date-reply-container").first().append(toolTipEl);


        }).mouseleave(function(){
            $(".tooltip").remove();
        });
    },

    CommentAdd : function (){
        $(".comment-container #add-comment").click(function (ev) {
            ev.preventDefault();
            console.log("comment now clicked");
            comments.showCommentBox([this]);
        });
        //
        // $(".issue-view-comment-reply").click(function () {
        //     comments.showCommentBox(this);
        // })
    },

    replyAdd : function () {

        $(".issue-view-comments-section").on("click", '.issue-view-comment-box .issue-view-comment-date-reply-container .issue-view-comment-reply'
            ,function (ev) {
            ev.preventDefault();
            console.log("reply now clicked");
            comments.showReplyBox([ev.target,this]);
        });
    },

    //on submit press - calls post to server
    //reply and comment are different
    submitComment : function () {

        $("#primary-comment .submit-btn").on("click",function () {

            let commentContainer = $("#primary-comment");

            let commentBody = [];
            commentBody['author'] = $(commentContainer).find("input").val();
            commentBody['message'] = $(commentContainer).find("textarea").val();
            commentBody['parentid'] = "";
            commentBody['issueid'] = issue.issue.code;
            commentBody['issuekey'] = issue.websafekey;
            commentBody['hasparent'] = false;

            comments.postCommentToServer(commentBody);
        });

        $("#add-reply").on("click",function (evt) {


            let commentContainer = $("#primary-comment");

            let commentBody = [];
            commentBody['author'] = $(commentContainer).find("input").val();
            commentBody['message'] = $(commentContainer).find("textarea").val();
            commentBody['parentid'] = $(evt.target).closest("")
            commentBody['issueid'] = issue.issue.code;
            commentBody['issuekey'] = issue.websafekey;
            commentBody['hasparent'] = false;

            // comments.postCommentToServer(commentBody);
        })
    },

    loadCommentsHelper : function() {

        let cursorNext = "";

        $(window).on("loadComments",function (event) {
            console.log("load comments triggered...");
            event.stopPropagation();

            if(cursorNext || cursorNext === "") {
                comments.fetchCommentsFromServer(issue.issue.code, cursorNext).then(function (nextCur) {
                    cursorNext = nextCur;
                })
            }

            else{
                console.log("end of comments");
            }
        });
    },

    readComments : function () {

        $("#load-more-comments").click(function () {
            setTimeout(function(){
                $(window).trigger("loadComments");
            },350);
        })
    },

};

events[Symbol.iterator] = function() {

    var self = this;
    var priceIndex = 0;
    let keys = [];
    for(k in self){
        keys.push(k);
    }

    // @@Iterator returns an iterator object
    return {
        // iterator object must implement next method
        next: function() {
            return priceIndex < keys.length ?
                { value: self[keys[priceIndex++]], done: false } :
                { done: true };
        }
    };
};


(function(){
    console.log(this);
    // var context = issue.readOnScroll;
    // var bound = context.bind(issue);
    // bound();

    /*
    issue.readOnScroll.call(this.issue,"");
    $(window).trigger("scroll");*/


    //issue.downvote.call(this.issue,"");

    //initialising all the event listeners
    for(let event of events){
        console.log(event);
        event();
    }

})();