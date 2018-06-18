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
        if(closestCommentBox.hasClass("comment-parent")){
            let parentReplierName = $(closestCommentBox).find(".issue-view-comment-name").first().html();
            console.log($(target).first().closest(".issue-view-comment-name"));
            $("#add-reply textarea").val("@"+parentReplierName+": ");
        }
        $("#add-reply").slideToggle(400,"swing");
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

                        commentDiv.innerHTML += "<div class=\"issue-view-comment-name small-titles\">"+author+"</div>\n" +
                            "                <div class=\"issue-view-comment-description\">"+message+
                            "                </div>\n" + "\n" +
                            "                <div class=\"issue-view-comment-date-reply-container\">\n" +
                            "                    <div class=\"issue-view-comment-date-\">\n" +helper_getDateFromTimeStamp(issueID) +
                            "                    </div>\n" +
                            "                    <div style=\"padding: 0px 6px\">.</div>\n" +
                            "                    <div class=\"issue-view-comment-reply\">\n" +
                            "                        Reply\n" +
                            "                    </div>\n" +
                            "                </div>";


                        document.getElementsByClassName("issue-view-comments-section")[0].appendChild(commentDiv);
                        // this.addIssue(status, 10, subject, issueCode, assignee, description, assignedto, 5);
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

    CommentAdd : function(){
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

        $(".issue-view-comment-reply").click(function (ev) {
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
            commentBody['parentid'] = $(evt.target).closest("#primary-comment")
            commentBody['issueid'] = issue.issuewebsafekey;
            commentBody['isparent'] = true;

            // comments.postCommentToServer(commentBody);
        })
    },

    loadCommentsHelper : function() {

        let cursorNext = "";

        $(window).on("loadComments",function (event) {
            console.log("load comments triggered...");
            event.stopPropagation();

            comments.fetchCommentsFromServer(issue.issue.code,cursorNext).then(function (nextCur) {
                cursorNext = nextCur;
            })
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