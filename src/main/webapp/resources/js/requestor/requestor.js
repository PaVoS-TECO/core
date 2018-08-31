define(['jquery', 'loadingOverlay'], function($) {
    var SERVER_URL = 'pavos-master.teco.edu';
    var PORT       = '8080';
    var BASE_URL = SERVER_URL + '/' + PORT + '/';

    var LOCALHOST = 'localhost';
    var GRAPHITE_PORT = '3000';
    var GRAPHITE_BASE_URL = LOCALHOST + ':' + GRAPHITE_PORT + '/d-solo/Vs1Usqpik/';

    return {
        requestSensortypes: function(app) {
            this.xmlHttpRequest(
                "GET",       
                (BASE_URL + 'DataServlet/Query?requestType=getObservationType'), 
                true,              
                function(xmlHttpResponseText) {
                    console.log(xmlHttpResponseText);
                });
        },

        requestClusterGeoJson: function(app, gridID, clusterID, property, time, steps) {
            this.xmlHttpRequest(
                "GET",       
                (BASE_URL 
                    + 'DataServlet/Query?requestType=getGeoJsonCluster',
                    + this.formatParameters(['gridID', 'clusterID', 'property', 'time', 'steps'],
                                            [gridID, clusterID, property, time, steps])), 
                true,              
                function(xmlHttpResponseText) {
                    console.log(xmlHttpResponseText);
                });
        },

        requestSensorGeoJson: function(app, gridID, sensorID, property) {
            this.xmlHttpRequest(
                "GET",       
                (BASE_URL 
                    + 'DataServlet/Query?requestType=getGeoJsonSensor'
                    + this.formatParameters(['gridID', 'sensorID', 'property'],
                                            [gridID, sensorID, property])), 
                true,              
                function(xmlHttpResponseText) {
                    console.log(xmlHttpResponseText);
                });
        },

        requestSensorReport: function(app, sensor, reason) {
            this.xmlHttpRequest(
                "GET",       
                (BASE_URL 
                    + 'DataServlet/Query?requestType=getGeoJsonSensor'
                    + this.formatParameters(['sensor', 'reason'],
                                            [sensor, reason])), 
                true,              
                function(xmlHttpResponseText) {
                    console.log(xmlHttpResponseText);
                });
        },

        requestGraph: function(app) {
            
        },

        requestExportFormats: function(app) {
            this.xmlHttpRequest(
                "GET",       
                (BASE_URL + 'ExportServlet/Query?requestType=getExtensions'), 
                true,              
                function(xmlHttpResponseText) {
                    console.log(xmlHttpResponseText);
                });
        },

        requestExport: function(app, extension, timeframe, observedProperties, clusters) {
            this.xmlHttpRequest(
                "GET",       
                (BASE_URL 
                    + 'DataServlet/Query?requestType=newExport' 
                    + this.formatParameters(['extension', 'timeFrame', 'observedProperties', 'clusters'],
                                            [extension, timeframe, observedProperties, clusters])), 
                true,              
                function(xmlHttpResponseText) {
                    console.log(xmlHttpResponseText);
                });
        },

        requestDownload: function(app, extension, timeframe, observedProperties, clusters) {
            this.xmlHttpRequest(
                "GET",       
                (BASE_URL 
                    + 'DataServlet/Query?requestType=tryDownload'
                    + this.formatParameters(['extension', 'timeFrame', 'observedProperties', 'clusters'],
                                            [extension, timeframe, observedProperties, clusters])), 
                true,              
                function(xmlHttpResponseText) {
                    console.log(xmlHttpResponseText);
                });
        },

        xmlHttpRequest: function(type, url, asynchronous, callback) {
            var xmlHttp = new XMLHttpRequest();
            xmlHttp.onreadystatechange = function() { 
                if (xmlHttp.readyState == 4 && xmlHttp.status == 200)
                    callback(xmlHttp.responseText);
            }
            xmlHttp.open(type, url, asynchronous);
            xmlHttp.send();
        },

        formatParameters: function(keyArray, valueArray) {
            var result = '';
            for (i = 0; i < key.Array; i++) {
                result = result + '&' + keyArray[i] + '=' + valueArray[i];
            }
            return result;
        },
      
        startLoadAnimation: function(timeout) {
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
        },

        stopLoadAnimation: function() {
            $.LoadingOverlay("hide");
        }
    }
});