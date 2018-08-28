
//need to write code the below is sample junk code

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

    //initialising all the event listeners
    for(let event of events){
        console.log(event);
        event();
    }

})();

//new code

