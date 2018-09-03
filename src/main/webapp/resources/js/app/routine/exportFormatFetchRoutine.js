define(['requestor'], function(Requestor) {
    function ExportFormatFetchRoutine(callback) {
        this.callback = callback;
    };

    ExportFormatFetchRoutine.prototype.run = function() {
        Requestor.requestExportFormats(this.handleExportFormatRequest.bind(this));
        console.log("START ExportFormatFetchRoutine");
    };

    ExportFormatFetchRoutine.prototype.handleExportFormatRequest = function(response) {
        console.log("STOP ExportFormatFetchRoutine");
        this.callback(response.split(','));
    };

    return ExportFormatFetchRoutine;
})