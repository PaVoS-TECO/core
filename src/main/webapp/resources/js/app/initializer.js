define(['jquery', 'app', 'appState', 'appManager', 'fetchRoutine', 'exportRoutine', 'recursiveRectangleGrid','recursiveRectangleCluster', 'bounds', 'dynamicHtmlBuilder', 'utcDateTime', 'leafletUtil', 'geoJsonUtil', 'storageUtil', 'requestor', 'mapManager',
        'leaflet', 'bootstrapDatetimepicker', 'bootstrapTouchspin'],
function($, App, AppState, AppManager, FetchRoutine, ExportRoutine, RecursiveRectangleGrid, RecursiveRectangleCluster, Bounds, DynamicHtmlBuilder, UTCDateTime, LeafletUtil, GeoJsonUtil, StorageUtil, Requestor, MapManager) {
    var exportModalTemp;
    var favoritesModalTemp;
    var addFavoritesModalTemp;
    var timeSettingsModalTemp;

    var leafletMap;
    var startStopChecked;

    return {
        start: function(){
            exportModalTemp = AppManager.APP_STATE.clone();
            sensortypeModalTemp = AppManager.APP_STATE.clone();
            favoritesModalTemp = new AppState();
            addFavoritesModalTemp = AppManager.APP_STATE.clone();
            timeSettingsModalTemp = AppManager.APP_STATE.clone();

            startStopChecked = true;

            this.initLeafletMap();
            this.initExportModal();
            this.initSensortypeModal();
            this.initFavoritesModal();
            this.initAddFavoriteModal();
            this.initContentTable();
            this.initTimetstampSlider();
            this.initStartStopUpdateButtons();
            this.initTimeSettingsModal();
        },

        initLeafletMap: function() {
            AppManager.MAP = LeafletUtil.createLeafletMap(
                AppManager.LEAFLET_MAP_CONTAINER,
                AppManager.BASEMAP_URL, AppManager.BASEMAP_ATTRIBUTION,
                AppManager.INITIAL_COORDINATES, AppManager.INITIAL_ZOOMLEVEL,
                AppManager.IS_FULLSCREEN_AVAILABLE, AppManager.IS_MOUSE_COORDINATES_VISIBLE);

            AppManager.MAP.on("moveend", function () {
                var leafletMapBounds = AppManager.MAP.getBounds();
                // console.log(AppManager.GRID.getClustersContainedInBounds(new Bounds([leafletMapBounds._southWest.lat, leafletMapBounds._southWest.lng],
                //                                                          [leafletMapBounds._northEast.lat, leafletMapBounds._northEast.lng]), 
                //                                                          LeafletUtil.calculateGridLevel(AppManager.MAP.getZoom())));
            });

            AppManager.MAP.on("zoomend", function () {
                var leafletMapBounds = AppManager.MAP.getBounds();
                // console.log(AppManager.GRID.getClustersContainedInBounds(new Bounds([leafletMapBounds._southWest.lat, leafletMapBounds._southWest.lng],
                //                                                          [leafletMapBounds._northEast.lat, leafletMapBounds._northEast.lng]), 
                //                                                          LeafletUtil.calculateGridLevel(AppManager.MAP.getZoom())));
            });

            // LeafletUtil.initializeGrid(AppManager.MAP);
        },

        initExportModal: function() {
            DynamicHtmlBuilder.buildRadioButtonGroup('#exportModalSensorTypeRadioButtons', 'exportModalSensorTypeRadioButtons', AppManager.AVAILABLE_SENSORTYPES, 'temperature_celsius');
            DynamicHtmlBuilder.buildRadioButtonGroup('#exportModalExportFormatRadioButtons', 'exportModalExportFormatRadioButtons', AppManager.AVAILABLE_EXPORTFORMATS, 'CSV');

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
                $('#exportFrom-datetimepicker').val(AppManager.APP_STATE.getSelectedTimeframe()[0].toString());
                $('#exportTo-datetimepicker').val(AppManager.APP_STATE.getSelectedTimeframe()[1].toString());
                $('#selectedClusters-inputForm').val(JSON.stringify(AppManager.APP_STATE.getSelectedClusters()));
                $('input[name=' + 'exportModalSensorTypeRadioButtons' + '][value=' + AppManager.APP_STATE.getSelectedSensortype() + ']').prop("checked",true);
                $('input[name=' + 'exportModalExportFormatRadioButtons' + '][value=' + AppManager.APP_STATE.getSelectedExportformat() + ']').prop("checked",true);
                exportModalTemp = AppManager.APP_STATE.clone();
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

                $('#selectedClusters-inputForm').val(JSON.stringify(AppManager.GRID.getClustersContainedInBounds(bounds, gridLevel)));
                $('#selectedClusters-inputForm').trigger('change');
            });

            $('#exportModalselectedClustersDropdownSelected').click(function() {
                $('#selectedClusters-inputForm').val(JSON.stringify(AppManager.APP_STATE.getSelectedClusters()));
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
                var exportRoutine = new ExportRoutine(
                    AppManager.EXPORT_TIMOUT, 
                    AppManager.EXPORT_STATUS_TIMEOUT, 
                    exportModalTemp.getSelectedExportformat(),
                    exportModalTemp.getSelectedTimeframe(),
                    exportModalTemp.getSelectedSensortype(),
                    exportModalTemp.getSelectedClusters());
                exportRoutine.run();

                $("#exportModal").modal('toggle');
            });

            // $("#exportModalCancelButton").click(function() { });
        },

        initSensortypeModal: function() {
            DynamicHtmlBuilder.buildRadioButtonGroup('#sensorTypeModalRadioButtons', 'sensorTypeModalSensorTypeRadioButtons', AppManager.AVAILABLE_SENSORTYPES, 'temperature_celsius');

            $('#sensortypeModal').on('shown.bs.modal', function(){
                sensortypeModalTemp.setSelectedSensortype(AppManager.APP_STATE.getSelectedSensortype());
                $('input[name=' + 'sensorTypeModalSensorTypeRadioButtons' + '][value=' + AppManager.APP_STATE.getSelectedSensortype() + ']').prop("checked",true);
            });

            $('input[name=' + 'sensorTypeModalSensorTypeRadioButtons' + ']').change(function() {
                sensortypeModalTemp.setSelectedSensortype(this.value);
            });

            $('#sensortypeModalApplyButton').click(function() {
                AppManager.APP_STATE.setSelectedSensortype(sensortypeModalTemp.getSelectedSensortype());
                addFavoritesModalTemp.setSelectedSensortype(sensortypeModalTemp.getSelectedSensortype());

                $('#sensortypeModal').modal('toggle');
            });
        },

        initFavoritesModal: function() {
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
                    $("#selectedFavoriteOutput").val(AppManager.APP_STATE.toString());
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
                AppManager.APP_STATE.update(favoritesModalTemp);
                exportModalTemp.update(favoritesModalTemp);
                addFavoritesModalTemp.update(favoritesModalTemp);
                timeSettingsModalTemp.update(favoritesModalTemp);

                $('#favoritesModal').modal('toggle');
            });
        },

        initAddFavoriteModal: function() {
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
        
        initContentTable: function() {
            DynamicHtmlBuilder.buildTableContentFromArray('#sensortable', AppManager.CONTENT_TABLE[0], AppManager.CONTENT_TABLE[1]);

            $("#sensortable tr").click(function() {
                // $(this).toggleClass('sensortable-tr-clicked');
            });
        },

        initTimetstampSlider: function() {
            setInterval(function(){
                if (!startStopChecked) {
                    $('#timeStampSlider').val((Number($('#timeStampSlider').val()) + 5) % 100);
                    // AppManager.MAP.trigger('moveend');
                }
            }, 750);

            $('#timeStampSlider').popover({
                title: "Title",
                content: "Content",
                placement: "bottom"
            });

            // $('#timeStampSlider').hover(function() {
            //     console.log("HELLO");
            // });
        },

        initStartStopUpdateButtons: function() {
            /**
             * Encapsulates the state switch logic of starting and stopping the
             * current routine, as well as updating the current context.
             */
            $('#startStopUpdateButton').click(function () {
                // startStopChecked = $('input', this).is(':checked');
                // $('span', this).toggleClass('glyphicon-play glyphicon-pause');
                FetchRoutine.run();
            });

            /**
             * Encapsulates the state switch logic of changing between historical
             * mode and live mode.
             */
            $('#liveHistoricalButton').click(function () {
                startStopChecked = $('input', this).is(':checked');
                $('span', this).toggleClass('glyphicon-hourglass glyphicon-repeat');
            });
        },

        initTimeSettingsModal: function() {
            DynamicHtmlBuilder.buildRadioButtonGroup('#timeSettingsAutomaticManualRefreshRadioButtons', 'timeSettingsAutomaticManualRefreshRadioButtons', AppManager.AVAILABLE_REFRESH_STATES, 'Automatic');

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
                timeSettingsModalTemp.update(AppManager.APP_STATE);

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
                AppManager.APP_STATE.update(timeSettingsModalTemp);
                addFavoritesModalTemp.update(timeSettingsModalTemp);

                $('#timeSettingsModal').modal('toggle');
            });
        },
    }
});