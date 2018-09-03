require.config({
    paths: {
        'jasmine': '../vendors/jasmine/lib/jasmine-3.2.1/jasmine',
        'jasmine-html': '../vendors/jasmine/lib/jasmine-3.2.1/jasmine-html',
        'jasmine-boot': '../vendors/jasmine/lib/jasmine-3.2.1/boot',

        'color': '../resources/js/visualization/color',
        'colorTest': '../tests/unit/visualization/colorTest',
        'colorGradient': '../resources/js/visualization/colorGradient',
        'colorGradientTest': '../tests/unit/visualization/colorGradientTest',
        'multiColorGradient': '../resources/js/visualization/multiColorGradient',
        'multiColorGradientTest': '../tests/unit/visualization/multiColorGradientTest',
        
        'mathUtil': '../resources/js/util/mathUtil'
    },
    shim: {
        'jasmine-html': {
            deps: ['jasmine']
        },
        'jasmine-boot': {
            deps: ['jasmine', 'jasmine-html']
        }
    }
})

require(['jasmine-boot'], function() {
    require(['colorTest', 'colorGradientTest', 'multiColorGradientTest'], function() {
        window.onload();

        var json = 
        {
            'temperature': {
                'gradient': ['#FFFFFF', '#CB53AA', '#860083', '#1D008D', '#003FFF', '#26FEE6', '#FDFD00', '#FF1400', '#520002'],
                '_fahrenheit': [-58.0, 86.0],
                '_celsius': [-50.0, 30.0]
            },
            'pollution': {
                'gradient': ['#00ff00', '#ffff00', '#ff0000'],
                '': [0, 1]
            }
        };

        console.log(json);

        var json = '{ "temperature": { "gradient": ["#ffffff", "#cb53aa", "#860083", "#1d008d", "#003fff", "#26fee6", "#fdfd00", "#ff1400", "#520002"], "_celsius": [-50.0, 30.0], "_Fahrenheit": [-58.0, 86.0] } }'
        console.log(JSON.parse(json));

        var json = '{ "type": "FeatureCollection", "timestamp": "2018-09-03T16:31:11Z", "observationType": "pM_10", "features": [ { "type": "Feature", "properties": { "value": null, "clusterID": "recursiveRectangleGrid-2_2_3:1_0", "content": [ "recursiveRectangleGrid-2_2_3:1_0-0_0", "recursiveRectangleGrid-2_2_3:1_0-0_1", "recursiveRectangleGrid-2_2_3:1_0-1_0", "recursiveRectangleGrid-2_2_3:1_0-1_1"] }, "geometry": { "type": "Polygon", "coordinates": [ [ [ -180.0, 0.0], [ 0.0, 0.0], [ 0.0, 85.0], [ -180.0, 85.0], [ -180.0, 0.0]] ] } }, { "type": "Feature", "properties": { "value": 0.0, "clusterID": "recursiveRectangleGrid-2_2_3:0_0", "content": [ "recursiveRectangleGrid-2_2_3:0_0-0_0", "recursiveRectangleGrid-2_2_3:0_0-0_1", "recursiveRectangleGrid-2_2_3:0_0-1_0", "recursiveRectangleGrid-2_2_3:0_0-1_1"] }, "geometry": { "type": "Polygon", "coordinates": [ [ [ -180.0, -85.0], [ 0.0, -85.0], [ 0.0, 0.0], [ -180.0, 0.0], [ -180.0, -85.0]] ] } }] }';
        console.log(JSON.parse(json));
        
        var url = 'http://localhost:7700/getGeoJsonCluster?clusterID=recursiveRectangleGrid-2_2_3:1_0,recursiveRectangleGrid-2_2_3:0_0&property=pM_10';
        var xmlHttp = new XMLHttpRequest();
        xmlHttp.onreadystatechange = function() {
            if ((xmlHttp.readyState == 4) && (xmlHttp.status === 200)) {
                console.log(xmlHttp.responseText);
            }
        }
        xmlHttp.open('GET', url, true);
        xmlHttp.timeout = 5000;
        xmlHttp.ontimeout = function() {
            xmlHttp.abort;
            console.log("XMLHttpRequest Timeout >>>>> " + url);
        }
        xmlHttp.onerror = function() {
            xmlHttp.abort;
            console.log("XMLHttpRequest Error  >>>>> " + url);
        }
        xmlHttp.send();
    })
})