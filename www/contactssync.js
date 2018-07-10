var argscheck = require('cordova/argscheck'),
    exec = require('cordova/exec');


var successCB = function(callback) {
    return function(result) {
        callback(result);
    }
}

var contactssync = {

    init: function(callback) {
        argscheck.checkArgs('f', 'contactssync.init');
        exec(successCB(callback), callback, 'ContactsSync', 'init');
    },

    getContactFromUri: function(options, callback) {
        argscheck.checkArgs('of', 'contactssync.getContactFromUri', arguments);
        exec(successCB(callback), callback, 'ContactsSync', 'getContactFromUri',
            [options.uri]);
    },

};

module.exports = contactssync;

