define(['util', 'mathUtil', 'leaflet'], function(Util, MathUtil) {
    var columnsPer = 10;
    var rowsPer = 10;
    var gridLevel = 1;
    var gridLayerGroup;

    return {
        /**
         * Draw a point on the submitted leaflet map at the given coordinate and with the given style.
         * 
         * @param {*} map the leaflet map
         * @param {*} coordinate the coordinate
         * @param {*} style the defined style
         */
        drawPoint: function(map, coordinate, style) {
            var point = {
                "type": "Feature",
                "geometry": {
                    "type": "Point",
                    "coordinates": this.switchLatLon(coordinate)
                }
            };

            L.geoJSON(point, {
                style: style
            }).addTo(map);
        },

        /**
         * Draw a line on the submitted leaflet map at the given coordinates and with the given style.
         * 
         * @param {*} map the leaflet map
         * @param {*} coordinates the coordinates
         * @param {*} style the defined style
         */
        drawLine: function(map, coordinates, style) {
            var line = [{
                "type": "LineString",
                "coordinates": this.switchLatLonForEach(coordinates)
            }];

            L.geoJSON(line, {
                style: style
            }).addTo(map);
        },

        /**
         * Draw a polygon on the submitted leaflet map at the given coordinates and with the given style.
         * 
         * @param {*} map the leaflet map
         * @param {*} coordinates the coordinates
         * @param {*} style the defined style
         */
        drawPolygon: function(map, coordinates, style) {
            var polygon = {
                "type": "Feature",
                "geometry": {
                    "type": "Polygon",
                    "coordinates": [this.switchLatLonForEach(coordinates)]
                }
            };

            L.geoJSON(polygon, {
                style: style
            }).addTo(map);
        },

        /**
         * Displays the submitted GeoJson on the leaflet map.
         * 
         * @param {*} map the leaflet map
         * @param {*} geoJson the submitted geoJson
         * @param {*} style the defined style
         */
        displayGeoJSONWithStyle: function(map, geoJson, style) {
            L.geoJSON(geoJson, {
                style: style,
                onEachFeature: this.bindPopupContent
            }).addTo(map);
        },

        /**
         * Returns the submitted coordinate with switched latitude and longitude.
         * 
         * @param {*} coordinates the submitted coordinate
         */
        switchLatLon: function(coordinate) {
            return [coordinate[1], coordinate[0], coordinate[2]];
        },
        
        /**
         * Returns the submitted coordinates with switched latitude and longitude.
         * 
         * @param {*} coordinates the submitted coordinates
         */
        switchLatLonForEach: function(coordinates) {
            var formattedCoordinates = [];
            var LeafletUtil = this;
            coordinates.forEach(function(element) {
                formattedCoordinates.push(LeafletUtil.switchLatLon(element));
            });
            return formattedCoordinates;
        },

        // ----------------------------------------------------------------------------------------------- //
        // ----------------------------------------------------------------------------------------------- //
        // ----------------------------------------------------------------------------------------------- //
        
        bindPopupContent: function(feature, layer) {
            if (feature.properties && feature.properties.popupContent) {
                layer.bindPopup(feature.properties.popupContent);
            } else {
                layer.bindPopup("");
            }
        },

        /*
        * Initiate a leaflet map.
        */
       createLeafletMap: function(container, baseMapURL, baseMapAttributes, coordinates, zoomLevel, fullscreenAvailable, mouseCoordinatesVisible) {
        
            var map = new L.Map(container, {
                fullscreenControl: fullscreenAvailable,
                fullscreenControlOptions: {
                    position: 'topleft'
                }
            });
        
            if (mouseCoordinatesVisible) {
                L.control.coordinates({
                    position:"topright",
                    enableUserInput: false,
                    useLatLngOrder: true,
                    markerType: L.Icon, //optional default L.marker
                    markerProps: {}, //optional default {},
                }).addTo(map);
            }
            
            this.addBaseMap(map, baseMapURL, baseMapAttributes, coordinates, zoomLevel);
        
            return map;
        },
        
        /**
         * Add a base map to the submitted leaflet map at the given coordinates and with the given zoom level.
         * 
         * @param {*} map the leaflet map
         * @param {*} baseMapURL the URL of the base map
         * @param {*} baseMapAttributes the attributes of the base map
         * @param {*} coordinates the initial coordinates
         * @param {*} zoomLevel the initial zoom level
         */
        addBaseMap: function(map, baseMapURL, baseMapAttributes, coordinates, zoomLevel) {
            var baseMap = new L.tileLayer(baseMapURL, baseMapAttributes);

            map.setView(coordinates, zoomLevel).addLayer(baseMap);
        },

        initializeGrid: function(map) {
            gridLayerGroup = this.createGrid(map.getBounds(), this.calculateGridLevel(map.getZoom()), columnsPer, rowsPer);
            gridLayerGroup.addTo(map);

            // Used to access local scope
            var LeafletUtil = this;
            map.on("moveend", function () {
                LeafletUtil.updateGrid(map);
            });
            map.on('zoomend', function(e) {
                LeafletUtil.handleZoom(map);
            });
        },

        updateGrid: function(map) {
            this.resetGrid(map);
            gridLayerGroup = this.createGrid(map.getBounds(), this.calculateGridLevel(map.getZoom()), columnsPer, rowsPer);
            gridLayerGroup.addTo(map);
        },

        handleZoom: function(map) {
            var newGridLevel = this.calculateGridLevel(map.getZoom());

            if (gridLevel != newGridLevel) {
                gridLevel = newGridLevel;

                this.updateGrid(map);
            }
        },

        calculateGridLevel: function(zoom) {
            array = [4, 7, 10, 13, 17];

            var tempGridLevel = 0;
            for (index = 0; index < array.length; index++) {
                tempGridLevel++;
                if (zoom <= array[index]) {
                    return tempGridLevel;
                }
            }
            return tempGridLevel; // Error
        },

        // function calculateGridLevel(zoom) {
        //   switch(true) {
        //     case zoom <= 4:
        //       return 1;
        //       break;
        //     case zoom <= 7:
        //       return 2;
        //       break;
        //     case zoom <= 10:
        //       return 3;
        //       break;
        //     case zoom <= 13:
        //       return 4;
        //       break;
        //     case zoom <= 17:
        //       return 5;
        //       break;
        //     default:
        //       break;
        //   }
        // }

        resetGrid: function(map) {
            map.removeLayer(gridLayerGroup);
        },

        createGrid: function(bounds, currentGridLevel, columnsPerCluster, rowsPerCluster) {
            // The grid
            var grid = L.layerGroup();

            // The starting coordinate of the map (latitude = -90 and longitude = -180)
            var latitudeStart = -90;
            var longitudeStart = -180;

            // The possible range of the map (latitude = 180 and longitude = 360 by default)
            var latitudeRange = 180;
            var longitudeRange = 360;

            // The size of each cluster at the given grid level
            var clusterSizeLatitude = Math.abs(this.splitRange(latitudeRange, columnsPerCluster, currentGridLevel));
            var clusterSizeLongitude = Math.abs(this.splitRange(longitudeRange, rowsPerCluster, currentGridLevel));

            // Extract the latitude and longitude values of the currently regarded bounds
            var minLatitude = bounds.getSouthWest().lat;
            var minLongitude = bounds.getSouthWest().lng;
            var maxLatitude = bounds.getNorthEast().lat;
            var maxLongitude = bounds.getNorthEast().lng;

            // Calculate the cluster rows and columns that have to be displayed to fully overlap submitted bounds
            var minClusterColumn = Math.floor(minLatitude / clusterSizeLatitude) + (Math.pow(columnsPer, currentGridLevel) / 2);
            var minClusterRow = Math.floor(minLongitude / clusterSizeLongitude) + (Math.pow(rowsPer, currentGridLevel) / 2);
            var maxClusterColumn = Math.floor(maxLatitude / clusterSizeLatitude) + 1 + (Math.pow(columnsPer, currentGridLevel) / 2);
            var maxClusterRow = Math.floor(maxLongitude / clusterSizeLongitude) + 1 + (Math.pow(rowsPer, currentGridLevel) / 2);

            for (column = minClusterColumn; column <= maxClusterColumn; column++) {
                for (row = minClusterRow; row <= maxClusterRow; row++) {
                    // color = (function(m,s,c){return (c ? arguments.callee(m,s,c-1) : '#') + s[m.floor(m.random() * s.length)]})(Math,'0123456789ABCDEF',5);
                    var color = Util.rgbToHex(Util.getRandomInt(75, 200), Util.getRandomInt(175, 255), Util.getRandomInt(0, 75));
                    var weight  = 0.25;
                    
                    var clusterLatitude = latitudeStart + (column * clusterSizeLatitude);
                    var clusterLongitude = longitudeStart + (row * clusterSizeLongitude);

                    var clusterBounds = [[clusterLatitude, clusterLongitude], [clusterLatitude + clusterSizeLatitude, clusterLongitude + clusterSizeLongitude]];
                    // L.rectangle(clusterBounds, {color: color, weight: weight}).bindPopup(MathUtil.mod(row, rowsPer) + "-" + MathUtil.mod(column, columnsPer)).addTo(grid);
                    L.rectangle(clusterBounds, {color: color, weight: weight}).bindPopup(MathUtil.mod(column, columnsPer) + "-" + MathUtil.mod(row, rowsPer)).addTo(grid);
                }
            }

            return grid;
        },

        splitRange: function(range, objectsPerCluster, gridLevel) {
            return (range / Math.pow(objectsPerCluster, gridLevel));
        },

        getWorldMapBounds: function() {
            var southWest = L.latLng(-89.98155760646617, -180);
            var northEast = L.latLng(89.99346179538875, 180);
            return L.latLngBounds(southWest, northEast);
        }
    }
});