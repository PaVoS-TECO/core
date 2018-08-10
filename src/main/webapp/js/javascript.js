var ViewState = {
  selectedIdentifier  : '',
  selectedClusters    : '',
  selectedSensortypes : '',
  selectedTimeframe   : '',
  selectedTimestamp   : '',

  initialize: function(identifier, clusters, sensortypes, timeframe, timestamp) {
    selectedIdentifier = identifier;
    selectedClusters = clusters;
    selectedSensortypes = sensortypes;
    selectedTimeframe = timeframe;
    selectedTimestamp = timestamp;
  },

  setSelectedIdentifier: function(identifier) {
    selectedIdentifier = identifier;
  },
  getSelectedIdentifier: function() {
    return selectedIdentifier;
  },

  setSelectedClusters: function(clusters) {
    selectedClusters = clusters;
  },
  getSelectedClusters: function() {
    return selectedClusters;
  },

  setSelectedSensortypes: function(sensortypes) {
    selectedSensortypes = sensortypes;
  },
  getSelectedSensortypes: function() {
    return selectedSensortypes;
  },
  
  setSelectedTimeframe: function(timeframe) {
    selectedTimeframe = timeframe;
  },
  getSelectedTimeframe: function() {
    return selectedTimeframe;
  },

  setSelectedTimestamp: function(timestamp) {
    selectedTimestamp = timestamp;
  },
  getSelectedTimestamp: function() {
    return selectedTimestamp;
  },

  toString: function() {
    return "[" + ViewState.getSelectedIdentifier() + "]"
               +  "[" + ViewState.getSelectedClusters() + "]"
               +  "[" + ViewState.getSelectedSensortypes() + "]"
               +  "[" + ViewState.getSelectedTimeframe() + "]"
               +  "[" + ViewState.getSelectedTimestamp() + "]"
  }
};

/**
  * Wait for document to be ready for manipulation.
  */
$(document).ready(function() {
	/**
	  * Encapsulates the state switch logic of starting and stopping the
	  * current routine.
	  */
	$('#startStopButton').click(function () {
        var checked = $('input', this).is(':checked');
        $('span', this).toggleClass('glyphicon-play glyphicon-pause');
		//$(this).toggleClass('btn-primary btn-secondary');
    });
	
	/**
	  * Encapsulates the state switch logic of changing between historical/
	  * loop mode and live mode.
	  */
	$('#liveLoopButton').click(function () {
        var checked = $('input', this).is(':checked');
        $('span', this).toggleClass('glyphicon-hourglass glyphicon-repeat');
		//$(this).toggleClass('btn-primary btn-secondary');
    });
	
	$('#exportFrom-datepicker').datepicker({
        format: "dd/mm/yyyy",
		orientation: "top"
    });
	
	$('#exportTo-datepicker').datepicker({
        format: "dd/mm/yyyy",
		orientation: "top"
    });

  $('#addFavoriteButton').click(function() {
    ViewState.initialize("", "#0001, #0002", "pollution", "20-01-18 22-01-18", "21-01-18");
    $('#yourFavoriteInput').val(ViewState.toString());
  });

  $('#addFavoriteInput').change(function() {
    ViewState.setSelectedIdentifier($(this).val());
    $('#yourFavoriteInput').val(ViewState.toString());
  });

  $('#addFavoriteModalApplyButton').click(function() {
    if (typeof(Storage) !== "undefined") {
      // Code for localStorage/sessionStorage.
    } else {
      // Sorry! No Web Storage support..
    }
  });
	
	/**
	  * Iterate through all selected checkboxes of a dropdown menu and display their labels as a concatenation of each.
	  */
	$('#selectedCluster-dropdown input[type=checkbox]').each(function() {
        $(this).change(function() {
            var line = "";
            $("ul.dropdown-menu input[type=checkbox]").each(function() {
                if($(this).is(":checked")) {
                    line += $("+ span", this).text() + ";";
                }
            });
            $('#selectedCluster-inputForm').val(line);
        });
    });
    
  $("#sensortable tr").click(function() {
    $(this).toggleClass('sensortable-tr-clicked');
  });
});

/**
  * Fill the table with the submitted tableID with the data encapsulated in the json file.
  * 
  * @param {*} tableID the table identifier
  * @param {*} json the json
  */
function setTableContent(tableID, json) {
  var columns = getColumnHeaders(tableID, json);

  for (var i = 0; i < json.length; i++) {
    var row$ = $('<tr/>');
    for (var colIndex = 0; colIndex < columns.length; colIndex++) {
      var cellValue = json[i][columns[colIndex]];
      if (cellValue == null) cellValue = "";
      row$.append($('<td/>').html(cellValue));
    }
    $(tableID).append(row$);
  }
}

/**
  * Adds a header row to the table and returns the set of columns.
  * 
  * @param {*} tableID the table identifier
  * @param {*} json the json
  */
function getColumnHeaders(tableID, json) {
  var columnSet = [];
  var headerTr$ = $('<tr/>');

  for (var i = 0; i < json.length; i++) {
    var rowHash = json[i];
    for (var key in rowHash) {
      if ($.inArray(key, columnSet) == -1) {
        columnSet.push(key);
        headerTr$.append($('<th/>').html(key));
      }
    }
  }
  $(tableID).append(headerTr$);

  return columnSet;
}

/*
 * Initiate a leaflet map.2
 */
function createLeafletMap(container, baseMapURL, baseMapAttributes, coordinates, zoomLevel, fullscreenAvailable, mouseCoordinatesVisible) {
  
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
  
  addBaseMap(map, baseMapURL, baseMapAttributes, coordinates, zoomLevel);

  return map;
}

/**
  * Add a base map to the submitted leaflet map at the given coordinates and with the given zoom level.
  * 
  * @param {*} map the leaflet map
  * @param {*} baseMapURL the URL of the base map
  * @param {*} baseMapAttributes the attributes of the base map
  * @param {*} coordinates the initial coordinates
  * @param {*} zoomLevel the initial zoom level
  */
function addBaseMap(map, baseMapURL, baseMapAttributes, coordinates, zoomLevel) {
  var baseMap = new L.tileLayer(baseMapURL, baseMapAttributes);

  map.setView(coordinates, zoomLevel).addLayer(baseMap);
}

function getWorldMapBounds() {
  var southWest = L.latLng(-89.98155760646617, -180);
  var northEast = L.latLng(89.99346179538875, 180);
  return L.latLngBounds(southWest, northEast);
}

/**
  * Returns the initial coordinates.
  */
function getInitialCoordinates() {
  return getKarlsruheCoordinates();
}

/**
  * Returns the coordinates of Karlsruhe.
  */
function getKarlsruheCoordinates() {
	return [49.007, 8.404];
}

/**
  * Returns the initial zoom level.
  */
function getInitialZoomLevel() {
  return 10;
}

/**
  * Returns whether fullscreen is available for the leaflet map.
  */
function isFullscreenAvailable() {
  return true;
}

/**
  * Returns whether mouse coordinates are visible on the leaflet map.
  */
function isMouseCoordinatesVisible() {
  return true;
}

/**
  * Draw a point on the submitted leaflet map at the given coordinate and with the given style.
  * 
  * @param {*} map the leaflet map
  * @param {*} coordinate the coordinate
  * @param {*} style the defined style
  */
function drawPoint(map, coordinate, style) {
  var point = {
      "type": "Feature",
      "geometry": {
          "type": "Point",
          "coordinates": switchLatLon(coordinate)
      }
  };

  L.geoJSON(point, {
      style: style
  }).addTo(map);
}

/**
  * Draw a line on the submitted leaflet map at the given coordinates and with the given style.
  * 
  * @param {*} map the leaflet map
  * @param {*} coordinates the coordinates
  * @param {*} style the defined style
  */
function drawLine(map, coordinates, style) {
  var line = [{
      "type": "LineString",
      "coordinates": switchLatLonForEach(coordinates)
  }];

  L.geoJSON(line, {
      style: style
  }).addTo(map);
}

/**
  * Draw a polygon on the submitted leaflet map at the given coordinates and with the given style.
  * 
  * @param {*} map the leaflet map
  * @param {*} coordinates the coordinates
  * @param {*} style the defined style
  */
function drawPolygon(map, coordinates, style) {
  var polygon = {
      "type": "Feature",
      "geometry": {
          "type": "Polygon",
          "coordinates": [switchLatLonForEach(coordinates)]
      }
  };

  L.geoJSON(polygon, {
      style: style
  }).addTo(map);
}

/**
  * Returns the submitted coordinate with switched latitude and longitude.
  * 
  * @param {*} coordinates the submitted coordinate
  */
function switchLatLon(coordinate) {
  return [coordinate[1], coordinate[0], coordinate[2]];
}

/**
  * Returns the submitted coordinates with switched latitude and longitude.
  * 
  * @param {*} coordinates the submitted coordinates
  */
function switchLatLonForEach(coordinates) {
  formattedCoordinates = [];
  coordinates.forEach(function(element) {
    formattedCoordinates.push(switchLatLon(element));
  });
  return formattedCoordinates;
}

function displayGeoJSONFeature(map, feature, style) {
  L.geoJSON(feature, {
    style: style,
    onEachFeature: onEachFeature
  }).addTo(map);
}

function onEachFeature(feature, layer) {
  if (feature.properties && feature.properties.popupContent) {
    layer.bindPopup(feature.properties.popupContent);
  } else {
    layer.bindPopup("");
  }
  /*
  layer.on('click', function (e) {
    this.openPopup();
  });
  layer.on('mouseover', function (e) {
      this.openPopup();
  });
  layer.on('mouseout', function (e) {
      this.closePopup();
  });
  */
}

/*
function displayRectangleGrid(map, columns, rows, centerLatitude, centerLongitude, width) {
  // The bounds of each rectangle
	var latOffset = width;
	var lonOffset = 1.5 * width;
	// The number of rows and columns
	var color   = "#ff7800";
	var weight  = 0.25;
	// The start coordinates (the upper left corner)
	var latitude  = centerLatitude - ((columns / 2) * latOffset);
	var longitude = centerLongitude - ((rows / 2) * lonOffset);
	// The container for the created rectangles
	var rectangles = Array(columns * rows);
	
	for (column = 0; column < columns; column++) {
	  for (row = 0; row < rows; row++) {
	    color = (function(m,s,c){return (c ? arguments.callee(m,s,c-1) : '#') + s[m.floor(m.random() * s.length)]})(Math,'0123456789ABCDEF',5);
	  
	    var newLatitude = latitude + (column * latOffset);
      var newLongitude = longitude + (row * lonOffset);
        
      var upperLeft  = [newLatitude, newLongitude];
      var lowerRight = [newLatitude + latOffset, newLongitude + lonOffset];
        
      var bounds = [upperLeft, lowerRight];
	    rectangles[column * rows + row] = L.rectangle(bounds, {color: color, weight: weight}).addTo(map);
	  }
	}
}
*/

var columnsPer = 10;
var rowsPer = 10;
var gridLevel = 1;
var gridLayerGroup;

function initializeGrid(map) {
  gridLayerGroup = createGrid(map.getBounds(), calculateGridLevel(map.getZoom()), columnsPer, rowsPer);
  gridLayerGroup.addTo(map);

  map.on("moveend", function () {
    updateGrid(map);
  });

  map.on('zoomend', function(e) {
    handleZoom(map);
  });
}

function updateGrid(map) {
  resetGrid(map);
  gridLayerGroup = createGrid(map.getBounds(), calculateGridLevel(map.getZoom()), columnsPer, rowsPer);
  gridLayerGroup.addTo(map);
}

function handleZoom(map) {
  var newGridLevel = calculateGridLevel(map.getZoom());

  if (gridLevel != newGridLevel) {
    gridLevel = newGridLevel;

    updateGrid(map);
  }
}

function calculateGridLevel(zoom) {
  switch(true) {
    case zoom <= 4:
      return 1;
      break;
    case zoom <= 7:
      return 2;
      break;
    case zoom <= 10:
      return 3;
      break;
    case zoom <= 13:
      return 4;
      break;
    case zoom <= 17:
      return 5;
      break;
    default:
      break;
  }
}

function resetGrid(map) {
  map.removeLayer(gridLayerGroup);
}

function createGrid(bounds, currentGridLevel, columnsPerCluster, rowsPerCluster) {
  // The grid
  var grid = L.layerGroup();

  // The starting coordinate of the map (latitude = -90 and longitude = -180)
  var latitudeStart = -90;
  var longitudeStart = -180;

  // The possible range of the map (latitude = 180 and longitude = 360 by default)
  var latitudeRange = 180;
  var longitudeRange = 360;

  // The size of each cluster at the given grid level
  var clusterSizeLatitude = Math.abs(splitRange(latitudeRange, columnsPerCluster, currentGridLevel));
  var clusterSizeLongitude = Math.abs(splitRange(longitudeRange, rowsPerCluster, currentGridLevel));

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
      var color = rgbToHex(getRandomInt(75, 200), getRandomInt(175, 255), getRandomInt(0, 75));
      var weight  = 0.25;
      
	    var clusterLatitude = latitudeStart + (column * clusterSizeLatitude);
      var clusterLongitude = longitudeStart + (row * clusterSizeLongitude);

      var clusterBounds = [[clusterLatitude, clusterLongitude], [clusterLatitude + clusterSizeLatitude, clusterLongitude + clusterSizeLongitude]];
      L.rectangle(clusterBounds, {color: color, weight: weight}).addTo(grid);
    }
  }

  return grid;
}

function splitRange(range, objectsPerCluster, gridLevel) {
  return (range / Math.pow(objectsPerCluster, gridLevel));
}

// UTILITY METHODS

function componentToHex(c) {
  var hex = c.toString(16);
  return hex.length == 1 ? "0" + hex : hex;
}

function rgbToHex(r, g, b) {
  return "#" + componentToHex(r) + componentToHex(g) + componentToHex(b);
}

/**
 * Returns a random integer between min (inclusive) and max (inclusive)
 * Using Math.round() will give you a non-uniform distribution!
 */
function getRandomInt(min, max) {
  return Math.floor(Math.random() * (max - min + 1)) + min;
}