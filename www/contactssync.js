var argscheck = require('cordova/argscheck'),
    exec = require('cordova/exec');


var successCB = function(callback) {
    return function(result) {
        callback(null, result);
    }
}

var contactssync = {

    all: function(options, callback) {
        argscheck.checkArgs('of', 'contactssync.all', arguments);
        exec(successCB(callback), callback, 'ContactsSync', 'all',
            [options.accountType, options.accountName]);
    },


};

module.exports = contactssync;
