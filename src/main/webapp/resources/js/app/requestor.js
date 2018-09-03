define(['jquery', 'appManager', 'util', 'gridUtil', 'loadingOverlay'], function($, AppManager, Util, GridUtil) {
    var SERVER_URL = 'http://pavos.oliver.pw';
    var PORT       = '8084';

    var LOCALHOST = 'http://localhost';
    var LOCAL_PORT = '7700';

    var GRAPHITE_PORT = '3000';
    var GRAPHITE_BASE_URL = LOCALHOST + ':' + GRAPHITE_PORT + '/d-solo/Vs1Usqpik/';

    var BASE_URL = LOCALHOST + ':' + LOCAL_PORT + '/';

    requestGridID = function(callback) {
        this.xmlHttpRequest(
            "GET",
            (BASE_URL
                + 'getGridID?'),
            true,
            callback);
    };

    requestSensortypes = function(gridID, callback) {
        this.xmlHttpRequest(
            "GET",
            (BASE_URL
                + 'getObservationTypes?'
                + this.formatParameters(['gridID'],
                                        [gridID])),
            true,
            callback);
    };

    requestColorGradients = function(callback) {
        this.xmlHttpRequest(
            "GET",
            (BASE_URL
                + 'getAllGradients?'),
            true,
            callback);
    };

    requestClusterGeoJson = function(gridID, clusterID, property, time, steps, callback) {
        this.xmlHttpRequest(
            "GET",
            (BASE_URL
                + 'getGeoJsonCluster?'
                + this.formatParameters(['gridID', 'clusterID', 'property', 'time', 'steps'],
                                        [gridID, clusterID, property, time, steps])),
            true,
            callback);
    };

    requestSensorGeoJson = function(gridID, sensorID, property, callback) {
        this.xmlHttpRequest(
            "GET",
            (BASE_URL
                + 'getGeoJsonSensor?'
                + this.formatParameters(['gridID', 'sensorID', 'property'],
                                        [gridID, sensorID, property])),
            true,
            callback);
    };

    requestSensorReport = function(sensorID, reason, callback) {
        this.xmlHttpRequest(
            "GET",
            (BASE_URL
                + 'reportSensor?'
                + this.formatParameters(['sensorID', 'reason'],
                                        [sensorID, reason])),
            true,
            callback);
    };

    requestGraph = function(live, from, to, gridID, clusterID, sensorIDs, sensorType) {
        // The rows and columns for each grid level of the submitted clusterID
        var clusterArray = clusterID.split(':')[1].split('-');
        var formatClusterID = '';
        for (i = 0; i < clusterArray.length; i++) {
            formatClusterID = formatClusterID + this.formatParameters(['var-ClusterLevel' + (i + 1)], [clusterArray[i]]);
        }

        if (live) {
            AppManager.GRAPHITE_URL = GRAPHITE_BASE_URL
                + 'main?'
                + this.formatParameters(['orgId', 'from', 'to', 'var-GridID'], [1, 'now/d', 'now', gridID]);
                + '&'
                + formatClusterID
                + '&'
                + this.formatParameters(['var-Sensor', 'var-ObservationType', 'panelId', 'theme'], [sensorIDs, sensorType, 2, 'light']);
        } else {
            // Turn UTC DateTime into plain String of numbers.
            var formatFrom = Util.replaceAll(from.toString(), ['-', 'T', ':', 'Z'], ['', '', '', '']);
            var formatTo   = Util.replaceAll(to.toString(), ['-', 'T', ':', 'Z'], ['', '', '', '']);

            AppManager.GRAPHITE_URL = GRAPHITE_BASE_URL
                + 'main?'
                + this.formatParameters(['orgId', 'from', 'to', 'var-GridID'], [1, formatFrom, formatTo, gridID]);
                + '&'
                + formatClusterID
                + '&'
                + this.formatParameters(['var-Sensor', 'var-ObservationType', 'panelId', 'theme'], [sensorIDs, sensorType, 2, 'light']);
        }
    };

    requestExportFormats = function(callback) {
        this.xmlHttpRequest(
            "GET",
            (SERVER_URL + ':' + PORT + '/'
                + 'edms/get?requestType=getExtensions'),
            true,
            callback);
    };

    requestExport = function(extension, timeframe, observedProperty, clusters, callback) {
        // var downloadID = Util.getHashCode(extension + timeframe + observedProperties + clusters);
        var downloadID = 'froststealer';
        var array = [downloadID,
                     extension, 
                     Util.concat([timeframe[0].toString(), timeframe[1].toString()], ','),
                     observedProperty,
                     Util.concat(GridUtil.clusterArrayToStringArray(clusters), ',')];

        this.xmlHttpRequest(
            "GET",
            (SERVER_URL + ':' + PORT + '/'
                + 'edms/get?requestType=newExport&'
                + this.formatParameters(['downloadID', 'extension', 'timeFrame', 'observedProperties', 'clusters'],
                                        array)),
            true,
            callback);
    };

    requestExportStatus = function(extension, timeframe, observedProperties, clusters, callback) {
        // var downloadID = Util.getHashCode(extension + timeframe + observedProperties + clusters);
        var downloadID = 'froststealer';

        this.xmlHttpRequest(
            "GET",
            (SERVER_URL + ':' + PORT + '/'
                + 'edms/get?requestType=getStatus'
                + '&downloadID='
                + downloadID),
            true,
            callback);
    };

    requestDownload = function(extension, timeframe, observedProperties, clusters) {
        // var downloadID = Util.getHashCode(extension + timeframe + observedProperties + clusters);
        var downloadID = 'froststealer';

        var requestUrl =
            (SERVER_URL + ':' + PORT + '/'
            + 'edms/get?requestType=tryDownload'
            + '&downloadID='
            + downloadID);

        var xmlHttp = new XMLHttpRequest();
        xmlHttp.open("GET", requestUrl);
        xmlHttp.responseType = "blob";

        var _this = this;
        xmlHttp.onload = function () {
            _this.saveData(this.response, (downloadID + '.' + extension));
        };
        xmlHttp.timeout = AppManager.HTTP_REQUEST_TIMEOUT;
        xmlHttp.ontimeout = function() {
            xmlHttp.abort;
            console.log("XMLHttpRequest Timeout >>>>> " + requestURL);
        }
        xmlHttp.onerror = function() {
            xmlHttp.abort;
            console.log("XMLHttpRequest Error >>>>> " + requestURL);
        }
        xmlHttp.send();
    };

    xmlHttpRequest = function(type, url, asynchronous, callback) {
        var xmlHttp = new XMLHttpRequest();
        xmlHttp.onreadystatechange = function() {
            if ((xmlHttp.readyState == 4) && (xmlHttp.status === 200)) {
                callback(xmlHttp.responseText);
            }
        }
        xmlHttp.open(type, url, asynchronous);
        xmlHttp.timeout = AppManager.HTTP_REQUEST_TIMEOUT;
        xmlHttp.ontimeout = function() {
            xmlHttp.abort;
            console.log("XMLHttpRequest Timeout >>> " + url);
            callback();
        }
        xmlHttp.onerror = function() {
            xmlHttp.abort;
            console.log("XMLHttpRequest Error  >>> " + url);
            callback();
        }
        xmlHttp.send();
    };

    formatParameters = function(keyArray, valueArray) {
        var result = keyArray[0] + '=' + valueArray[0];
        for (i = 1; i < keyArray.length; i++) {
            result = result + '&' + keyArray[i] + '=' + valueArray[i];
        }
        return result;
    };

    saveData = function(blob, fileName) {
        var a = document.createElement("a");
        document.body.appendChild(a);
        a.style = "display: none";

        var url = window.URL.createObjectURL(blob);
        a.href = url;
        a.download = fileName;
        a.click();
        window.URL.revokeObjectURL(url);
    };

    startLoadAnimation = function(timeout) {
        $.LoadingOverlay("show", {
            image          : "resources/data/PaVoSLogo-Icon.png",
            imageAnimation : "1000ms fadein",
            size           : 100,
            minSize        : 50,
            maxSize        : 200,
            fade           : [400, 400]
        });

        if (timeout != null) {
            var _this = this;
            setTimeout(function() {
                _this.stopLoadAnimation();
            }, Number(timeout));
        }
    };

    stopLoadAnimation = function() {
        $.LoadingOverlay("hide");
    };

    return {
        requestGridID,
        requestSensortypes,
        requestColorGradients,
        requestClusterGeoJson,
        requestSensorGeoJson,
        requestSensorReport,
        requestGraph,
        requestExportFormats,
        requestExport,
        requestExportStatus,
        requestDownload,
        xmlHttpRequest,
        formatParameters,
        saveData,
        startLoadAnimation,
        stopLoadAnimation
    }
});