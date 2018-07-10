var argscheck = require('cordova/argscheck'),
    exec = require('cordova/exec');


var successCB = function(callback) {
    return function(result) {
        callback(result);
    }
}

var errorCB = function(callback) {
    return function(result) {
        callback(result);
    }
}

var contactssync = {

    init: function(callback, error) {
        argscheck.checkArgs('of', 'contactssync.init');
        exec(successCB(callback), errorCB(error), 'ContactsSync', 'init');
    },

    getContactFromUri: function(options, callback, error) {
        argscheck.checkArgs('of', 'contactssync.getContactFromUri', arguments);
        exec(successCB(callback), errorCB(error), 'ContactsSync', 'getContactFromUri',
            [options.uri]);
    },

};

module.exports = contactssync;

