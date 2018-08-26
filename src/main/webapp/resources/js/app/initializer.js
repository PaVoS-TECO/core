define(['jquery', 'app', 'appState', 'recursiveRectangleGrid','recursiveRectangleCluster', 'bounds', 'dynamicHtmlBuilder', 'utcDateTime', 'leafletUtil', 'geoJsonUtil', 'storageUtil', 'requestor',
        'leaflet', 'bootstrapDatetimepicker', 'bootstrapTouchspin'],
function($, App, AppState, RecursiveRectangleGrid, RecursiveRectangleCluster, Bounds, DynamicHtmlBuilder, UTCDateTime, LeafletUtil, GeoJsonUtil, StorageUtil, Requestor) {
    // Latitude - Longitude
    var KARLSRUHE = [49.007, 8.404];
    var KARLSRUHE_TECO = [49.013, 8.424];

    var LEAFLET_MAP_CONTAINER = 'mapContainer';
    var BASEMAP_URL = 'http://{s}.basemaps.cartocdn.com/light_all/{z}/{x}/{y}.png';

    var BASEMAP_ATTRIBUTION = { attribution: 'Positron', minZoom: 2, maxZoom: 15 };
    var INITIAL_COORDINATES = KARLSRUHE;
    var INITIAL_ZOOMLEVEL = 10;
    var IS_FULLSCREEN_AVAILABLE = true;
    var IS_MOUSE_COORDINATES_VISIBLE = true;

    var exportModalTemp;
    var favoritesModalTemp;
    var addFavoritesModalTemp;
    var timeSettingsModalTemp;

    var grid;
    var leafletMap;

    return {
        init: function(app){
            exportModalTemp = app.getAppState().clone();
            sensortypeModalTemp = app.getAppState().clone();
            favoritesModalTemp = new AppState();
            addFavoritesModalTemp = app.getAppState().clone();
            timeSettingsModalTemp = app.getAppState().clone();

            this.initLeafletMap(app);
            this.initExportModal(app);
            this.initSensortypeModal(app);
            this.initFavoritesModal(app);
            this.initAddFavoriteModal(app);
            this.initContentTable(app);
            this.initStartStopUpdateButtons(app);
            this.initTimeSettingsModal(app);
        },

        initLeafletMap: function(app) {
            leafletMap = LeafletUtil.createLeafletMap(
                LEAFLET_MAP_CONTAINER,
                BASEMAP_URL, BASEMAP_ATTRIBUTION,
                INITIAL_COORDINATES, INITIAL_ZOOMLEVEL,
                IS_FULLSCREEN_AVAILABLE, IS_MOUSE_COORDINATES_VISIBLE);

            // temp
            grid = new RecursiveRectangleGrid(new Bounds([-90, -180], [90, 180]), 10, 10, 5);
            // temp

            leafletMap.on("moveend", function () {
                var leafletMapBounds = leafletMap.getBounds();
                console.log(grid.getClustersContainedInBounds(new Bounds([leafletMapBounds._southWest.lat, leafletMapBounds._southWest.lng],
                                                                         [leafletMapBounds._northEast.lat, leafletMapBounds._northEast.lng]), 
                                                                         LeafletUtil.calculateGridLevel(leafletMap.getZoom())));
            });

            leafletMap.on("zoomend", function () {
                var leafletMapBounds = leafletMap.getBounds();
                console.log(grid.getClustersContainedInBounds(new Bounds([leafletMapBounds._southWest.lat, leafletMapBounds._southWest.lng],
                                                                         [leafletMapBounds._northEast.lat, leafletMapBounds._northEast.lng]), 
                                                                         LeafletUtil.calculateGridLevel(leafletMap.getZoom())));
            });
            
            

            LeafletUtil.initializeGrid(leafletMap);
        },

        initExportModal: function(app) {
            // Adding Z (-> symbolizes +00:00) at the end of the format doesn't work yet
            $('#exportFrom-datetimepicker').datetimepicker({
                format: "yyyy-mm-ddThh:ii:00",
                orientation: "top"
            });
            // Adding Z (-> symbolizes +00:00) at the end of the format doesn't work yet
            $('#exportTo-datetimepicker').datetimepicker({
                format: "yyyy-mm-ddThh:ii:00",
                orientation: "top"
            });
            
            // Insert current app state into each component when export modal is opened
            $('#exportModal').on('shown.bs.modal', function (e) {
                $('#exportFrom-datetimepicker').val(app.getAppState().getSelectedTimeframe()[0].toString());
                $('#exportTo-datetimepicker').val(app.getAppState().getSelectedTimeframe()[1].toString());
                $('#selectedClusters-inputForm').val(JSON.stringify(app.getAppState().getSelectedClusters()));
                $('input[name=' + 'exportModalSensorTypeRadioButtons' + '][value=' + app.getAppState().getSelectedSensortype() + ']').prop("checked",true);
                $('input[name=' + 'exportModalExportFormatRadioButtons' + '][value=' + app.getAppState().getSelectedExportformat() + ']').prop("checked",true);
                exportModalTemp = app.getAppState().clone();
            })

            $('#exportFrom-datetimepicker').change(function() {
                var from = new UTCDateTime();
                from.parse($(this).val());
                exportModalTemp.setSelectedTimeframe([from, exportModalTemp.getSelectedTimeframe()[1]]);
            });

            $('#exportTo-datetimepicker').change(function() {
                var to = new UTCDateTime();
                to.parse($(this).val());
                exportModalTemp.setSelectedTimeframe([exportModalTemp.getSelectedTimeframe()[0], to]);
            });

            $('#exportModalselectedClustersDropdownInBounds').click(function() {
                var leafletMapBounds = leafletMap.getBounds();
                var bounds = new Bounds([leafletMapBounds._southWest.lat, leafletMapBounds._southWest.lng],
                                        [leafletMapBounds._northEast.lat, leafletMapBounds._northEast.lng]);
                var gridLevel = LeafletUtil.calculateGridLevel(leafletMap.getZoom());

                $('#selectedClusters-inputForm').val(JSON.stringify(grid.getClustersContainedInBounds(bounds, gridLevel)));
                $('#selectedClusters-inputForm').trigger('change');
            });

            $('#exportModalselectedClustersDropdownSelected').click(function() {
                $('#selectedClusters-inputForm').val(JSON.stringify(app.getAppState().getSelectedClusters()));
                $('#selectedClusters-inputForm').trigger('change');
            });

            $('#exportModalselectedClustersDropdownReset').click(function() {
                $('#selectedClusters-inputForm').val('[{"clusterID": null}]');
                $('#selectedClusters-inputForm').trigger('change');
            });

            $('#selectedClusters-inputForm').change(function() {
                var clusterIDArray = JSON.parse($(this).val());
                exportModalTemp.setSelectedClusters(clusterIDArray);
            });

            $('input[name=' + 'exportModalSensorTypeRadioButtons' + ']').change(function() {
                exportModalTemp.setSelectedSensortype(this.value);
            });

            $('input[name=' + 'exportModalExportFormatRadioButtons' + ']').change(function() {
                exportModalTemp.setSelectedExportformat(this.value);
            });

            $('#exportModalFavoritesButton').click(function() {
                $('#exportModal').modal('toggle');
                
                // Wait to prevent body shift to the left -> modal bug
                setTimeout(function() {
                    $('#favoritesModal').modal('toggle');
                }, 400);
            });

            $('#exportModalAddFavoriteButton').click(function() {
                addFavoritesModalTemp = exportModalTemp.clone();

                $('#exportModal').modal('toggle');

                // Wait to prevent body shift to the left -> modal bug
                setTimeout(function() {
                    $('#addFavoriteModal').modal('toggle');
                }, 400);
            });

            $("#exportModalApplyButton").click(function() {
                // Requestor.startLoadAnimation(3500);
                // $("#exportModal").modal('toggle');
                console.log(exportModalTemp);
            });

            $("#exportModalCancelButton").click(function() {
                Requestor.stopLoadAnimation();
            });
        },

        initSensortypeModal: function(app) {
            $('#sensortypeModal').on('shown.bs.modal', function(){
                sensortypeModalTemp.setSelectedSensortype(app.getAppState().getSelectedSensortype());
                $('input[name=' + 'sensorTypeModalSensorTypeRadioButtons' + '][value=' + app.getAppState().getSelectedSensortype() + ']').prop("checked",true);
            });

            $('input[name=' + 'sensorTypeModalSensorTypeRadioButtons' + ']').change(function() {
                sensortypeModalTemp.setSelectedSensortype(this.value);
            });

            $('#sensortypeModalApplyButton').click(function() {
                app.getAppState().setSelectedSensortype(sensortypeModalTemp.getSelectedSensortype());
                addFavoritesModalTemp.setSelectedSensortype(sensortypeModalTemp.getSelectedSensortype());

                $('#sensortypeModal').modal('toggle');
            });
        },

        initFavoritesModal: function(app) {
            $("#favoritesModal").on('shown.bs.modal', function(){
                favoritesModalTemp = new AppState();

                // Update table
                $("#favoritesTable tr").remove();
                DynamicHtmlBuilder.buildListTableFromArray('#favoritesTable', JSON.parse(StorageUtil.getIdentifiersArray()));

                // Reset output field
                $("#selectedFavoriteOutput").val("");
            });

            /**
              * When the table cell with the favorite identifeir is clicked the app state object is shown in the
              * favourite output textfield. 
              */
             $(document).on("click", "#favoritesTable td", function() {
                if (favoritesModalTemp.parse(JSON.parse(localStorage.getItem($(this)[0].innerHTML)))) {
                    $("#selectedFavoriteOutput").val(app.getAppState().toString());
                } else {
                    alert("The selected favorite has an incompatible format.");
                }
            });

            $('#favoritesModalDeleteButton').click(function() {
                selectedFavoriteOutputContent = $('#selectedFavoriteOutput').val();
                if (selectedFavoriteOutputContent !== '') {
                    // Delete favorite from local storage
                    favoritesModalTemp.parse(JSON.parse(selectedFavoriteOutputContent));
                    favoritesModalTemp.delete();
                    $('#selectedFavoriteOutput').val('');

                    // Update table
                    $("#favoritesTable tr").remove();
                    DynamicHtmlBuilder.buildListTableFromArray('#favoritesTable', JSON.parse(StorageUtil.getIdentifiersArray()));
                }
            });

            $('#favoritesModalEditButton').click(function() {
                addFavoritesModalTemp.update(favoritesModalTemp);

                $('#favoritesModal').modal('toggle');

                // Wait to prevent body shift to the left -> modal bug
                setTimeout(function() {
                    $('#addFavoriteModal').modal('toggle');
                }, 400);
            });

            $('#favoritesModalApplyButton').click(function() {
                app.getAppState().update(favoritesModalTemp);
                exportModalTemp.update(favoritesModalTemp);
                addFavoritesModalTemp.update(favoritesModalTemp);
                timeSettingsModalTemp.update(favoritesModalTemp);

                $('#favoritesModal').modal('toggle');
            });
        },

        initAddFavoriteModal: function(app) {
            $("#addFavoriteModal").on('shown.bs.modal', function(){
                $('#yourFavoriteInput').val(addFavoritesModalTemp.toString());
            });

            $('#addFavoriteInput').change(function() {
                addFavoritesModalTemp.setIdentifier($(this).val());
                $('#yourFavoriteInput').val(addFavoritesModalTemp.toString());
            });

            $('#addFavoriteModalApplyButton').click(function() {
                if (addFavoritesModalTemp.store()) {
                    $('#addFavoriteModal').modal('toggle');
                }
            });
        },
        
        initContentTable: function(app) {
            var sensorTableHeaderArray = ['id', 'temperature_celsius'];
            var sensorTableContentArray = ['recursiveRectangleGrid-10_10_5:6_5-3_4', '', 'recursiveRectangleGrid-10_10_5:6_5-4_8', '21.3'];
            DynamicHtmlBuilder.buildTableContentFromArray('#sensortable', sensorTableHeaderArray, sensorTableContentArray);

            $("#sensortable tr").click(function() {
                $(this).toggleClass('sensortable-tr-clicked');
            });
        },

        initStartStopUpdateButtons: function(app) {
            /**
             * Encapsulates the state switch logic of starting and stopping the
             * current routine, as well as updating the current context.
             */
            $('#startStopUpdateButton').click(function () {
                var checked = $('input', this).is(':checked');
                $('span', this).toggleClass('glyphicon-play glyphicon-pause');
            });

            /**
             * Encapsulates the state switch logic of changing between historical
             * mode and live mode.
             */
            $('#liveHistoricalButton').click(function () {
                var checked = $('input', this).is(':checked');
                $('span', this).toggleClass('glyphicon-hourglass glyphicon-repeat');
            });
        },

        initTimeSettingsModal: function(app) {
            // Adding Z (-> symbolizes +00:00) at the end of the format doesn't work yet
            $('#timeSettingsFrom-datetimepicker').datetimepicker({
                format: "yyyy-mm-ddThh:ii:00",
                orientation: "top"
            });
            // Adding Z (-> symbolizes +00:00) at the end of the format doesn't work yet
            $('#timeSettingsTo-datetimepicker').datetimepicker({
                format: "yyyy-mm-ddThh:ii:00",
                orientation: "top"
            });

            $('#timeSettingsLiveRefreshIntervalSpinner').TouchSpin({
                initval: 15000,
                min: 10000,
                max: 600000,
                step: 500,
                decimals: 0,
                maxboostedstep: 30000,
                postfix: 'ms'
            });

            $('#timeSettingsHistoricalRefreshIntervalSpinner').TouchSpin({
                initval: 1500,
                min: 750,
                max: 60000,
                step: 50,
                decimals: 0,
                maxboostedstep: 3000,
                postfix: 'ms'
            });

            $('#timeSettingsModal').on('shown.bs.modal', function() {
                timeSettingsModalTemp.update(app.getAppState());

                $('#timeSettingsFrom-datetimepicker').val(timeSettingsModalTemp.getSelectedTimeframe()[0]);
                $('#timeSettingsTo-datetimepicker').val(timeSettingsModalTemp.getSelectedTimeframe()[1]);
                $('#timeSettingsLiveRefreshIntervalSpinner').val(timeSettingsModalTemp.getSelectedLiveRefreshInterval());
                $('timeSettingsHistoricalRefreshIntervalSpinner').val(timeSettingsModalTemp.getSelectedHistoricalRefreshInterval());
                if (timeSettingsModalTemp.getAutomaticRefreshEnabled()) {
                    $('input[name=' + 'timeSettingsAutomaticManualRefreshRadioButtons' + '][value=' + 'Automatic' + ']').prop("checked",true);
                } else {
                    $('input[name=' + 'timeSettingsAutomaticManualRefreshRadioButtons' + '][value=' + 'Manual' + ']').prop("checked",true);
                }
            });

            $('#timeSettingsFrom-datetimepicker').change(function() {
                var from = new UTCDateTime();
                from.parse($(this).val());
                timeSettingsModalTemp.setSelectedTimeframe([from, timeSettingsModalTemp.getSelectedTimeframe()[1]]);
            });

            $('#timeSettingsTo-datetimepicker').change(function() {
                var to = new UTCDateTime();
                to.parse($(this).val());
                timeSettingsModalTemp.setSelectedTimeframe([timeSettingsModalTemp.getSelectedTimeframe()[0], to]);
            });

            $('#timeSettingsLiveRefreshIntervalSpinner').change(function() {
                timeSettingsModalTemp.setSelectedLiveRefreshInterval($(this).val());
            });

            $('#timeSettingsHistoricalRefreshIntervalSpinner').change(function() {
                timeSettingsModalTemp.setSelectedHistoricalRefreshInterval($(this).val());
            });

            $('input[name=' + 'timeSettingsAutomaticManualRefreshRadioButtons' + ']').change(function() {
                if (this.value === 'Automatic') {
                    timeSettingsModalTemp.setAutomaticRefreshEnabled(true);
                } else {
                    timeSettingsModalTemp.setAutomaticRefreshEnabled(false);
                }
            });

            $('#timeSettingsModalApplyButton').click(function() {
                app.getAppState().update(timeSettingsModalTemp);
                addFavoritesModalTemp.update(timeSettingsModalTemp);

                $('#timeSettingsModal').modal('toggle');
            });
        },
    }
});