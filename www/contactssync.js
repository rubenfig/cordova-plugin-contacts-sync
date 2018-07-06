cordova.define("com.rubenfig.plugin.contacts.contactssync", function(require, exports, module) {
var argscheck = require('cordova/argscheck'),
    exec = require('cordova/exec');


var successCB = function(callback) {
    return function(result) {
        callback(null, result);
    }
}

var contactssync = {

    init: function(options, callback) {
        argscheck.checkArgs('of', 'contactssync.init', arguments);
        exec(successCB(callback), callback, 'ContactsSync', 'init',
            [options.accountType, options.accountName]);
    },


};

module.exports = contactssync;

});
