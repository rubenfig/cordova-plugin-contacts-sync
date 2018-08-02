var argscheck = require('cordova/argscheck'),
    exec = require('cordova/exec');


var successCB = function(callback) {
    return function(result) {
        callback(result);
    }
}

var contactssync = {

    init: function(options, callback) {
        argscheck.checkArgs('of', 'contactssync.init', arguments);
        exec(successCB(callback), callback, 'ContactsSync', 'init',[options.url, options.accountName, options.appName, options.message]);
    },

    getContactFromUri: function(options, callback) {
        argscheck.checkArgs('of', 'contactssync.getContactFromUri', arguments);
        exec(successCB(callback), callback, 'ContactsSync', 'getContactFromUri',
            [options.uri]);
    },

};

module.exports = contactssync;

