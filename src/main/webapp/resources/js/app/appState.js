define(['storageUtil'], function(StorageUtil) {
    /**
     * Encapsulates the state of the App.
     * 
     * @param {*} identifier the identifier of this AppState
     * @param {*} selectedClusters the selected clusters
     * @param {*} selectedSensortype the selected sensortype
     * @param {*} selectedExportformat the selected exportformat
     * @param {*} selectedTimeframe the selected timeframe
     * @param {*} selectedTimestamp the selected timestamp
     * @param {*} selectedLiveRefreshInterval the live refresh interval
     * @param {*} selectedHistoricalRefreshInterval the historical refresh interval
     * @param {*} automaticRefreshEnabled whether automatic refresh is enabled or not
     */
    function AppState(identifier, selectedClusters, selectedSensortype, selectedExportformat, selectedTimeframe, 
                      selectedTimestamp, selectedLiveRefreshInterval, selectedHistoricalRefreshInterval, automaticRefreshEnabled) {
        this.identifier = identifier;
        this.selectedClusters = selectedClusters;
        this.selectedSensortype = selectedSensortype;
        this.selectedExportformat = selectedExportformat;
        this.selectedTimeframe = selectedTimeframe;
        this.selectedTimestamp = selectedTimestamp;
        this.selectedLiveRefreshInterval = selectedLiveRefreshInterval;
        this.selectedHistoricalRefreshInterval = selectedHistoricalRefreshInterval;
        this.automaticRefreshEnabled = automaticRefreshEnabled;
    }

    /**
      * Set the identifier of this app state.
      * 
      * @param {*} identifier the identifier
      */
    AppState.prototype.setIdentifier = function(identifier) {
        this.identifier = identifier;
    }
    /**
      * Get the identifier of this app state.
      */
    AppState.prototype.getIdentifier = function() {
        return this.identifier;
    }
    /**
      * Set the selected clusters.
      * 
      * @param {*} selectedClusters the selected clusters.
      */
    AppState.prototype.setSelectedClusters = function(selectedClusters) {
        this.selectedClusters = selectedClusters;
    }
    /**
      * Get the selected clusters.
      */
    AppState.prototype.getSelectedClusters = function() {
        return this.selectedClusters;
    }
    /**
      * Set the selected sensortype.
      * 
      * @param {*} selectedSensortype the selected sensortype
      */
    AppState.prototype.setSelectedSensortype = function(selectedSensortype) {
        this.selectedSensortype = selectedSensortype;
    }
    /**
      * Get the selected sensortype.
      */
    AppState.prototype.getSelectedSensortype = function() {
        return this.selectedSensortype;
    }
    /**
      * Set the selected exportformat.
      * 
      * @param {*} selectedExportformat the selected exportformat
      */
    AppState.prototype.setSelectedExportformat = function(selectedExportformat) {
        this.selectedExportformat = selectedExportformat;
    }
    /**
      * Get the selected exportformat.
      */
    AppState.prototype.getSelectedExportformat = function() {
        return this.selectedExportformat;
    }
    /**
      * Set the selected timeframe.
      * 
      * @param {*} selectedTimeframe the selected timeframe
      */
    AppState.prototype.setSelectedTimeframe = function(selectedTimeframe) {
        this.selectedTimeframe = selectedTimeframe;
    }
    /**
      * Get the selected timeframe.
      */
    AppState.prototype.getSelectedTimeframe = function() {
        return this.selectedTimeframe;
    }
    /**
      * Set the selected timestamp.
      * 
      * @param {*} selectedTimestamp the selected timestamp
      */
    AppState.prototype.setSelectedTimestamp = function(selectedTimestamp) {
        this.selectedTimestamp = selectedTimestamp;
    }
    /**
      * Get the selected timestamp.
      */
    AppState.prototype.getSelectedTimestamp = function() {
        return this.selectedTimestamp;
    }
    /**
      * Set the selected live refresh interval.
      * 
      * @param {*} selectedLiveRefreshInterval the selected live refresh interval
      */
     AppState.prototype.setSelectedLiveRefreshInterval = function(selectedLiveRefreshInterval) {
        this.selectedLiveRefreshInterval = selectedLiveRefreshInterval;
    }
    /**
      * Get the selected live refresh interval.
      */
    AppState.prototype.getSelectedLiveRefreshInterval = function() {
        return this.selectedLiveRefreshInterval;
    }
    /**
      * Set the selected historical refresh interval.
      * 
      * @param {*} selectedHistoricalRefreshInterval the selected historical refresh interval
      */
     AppState.prototype.setSelectedHistoricalRefreshInterval = function(selectedHistoricalRefreshInterval) {
        this.selectedHistoricalRefreshInterval = selectedHistoricalRefreshInterval;
    }
    /**
      * Get the selected historical refresh interval.
      */
    AppState.prototype.getSelectedHistoricalRefreshInterval = function() {
        return this.selectedHistoricalRefreshInterval;
    }
    /**
      * Set whether automatic refresh is enabled or not.
      * 
      * @param {*} automaticRefreshEnabled a boolean stating whether automatic refresh is enabled
      */
    AppState.prototype.setAutomaticRefreshEnabled = function(automaticRefreshEnabled) {
        this.automaticRefreshEnabled = automaticRefreshEnabled;
    }
    /**
      * Get whether automatic refresh is enabled or not.
      */
    AppState.prototype.getAutomaticRefreshEnabled = function() {
        return this.automaticRefreshEnabled;
    }

    /**
      * Returns an instance of AppState with the same parameters as this one.
      */
    AppState.prototype.clone = function() {
        return new AppState(this.getIdentifier(), 
                            this.getSelectedClusters(),
                            this.getSelectedSensortype(),
                            this.getSelectedExportformat(),
                            this.getSelectedTimeframe(),
                            this.getSelectedTimestamp(),
                            this.getSelectedLiveRefreshInterval(),
                            this.getSelectedHistoricalRefreshInterval(),
                            this.getAutomaticRefreshEnabled());
    }

    /**
      * Store this app state on the local storage.
      */
    AppState.prototype.store = function() {
        var success = true;
        try {
            StorageUtil.addToIdentifiersArray(this.getIdentifier());
            localStorage.setItem(this.getIdentifier(), this.toString());
        } catch (e) {
            success = false;
        }
        return success;
    }

    /**
      * Delete this app state from the local storage.
      */
    AppState.prototype.delete = function() {
        var success = true;
        try {
            StorageUtil.removeFromIdentifiersArray(this.getIdentifier());
            StorageUtil.delete(this.getIdentifier());
        } catch (e) {
            success = false;
        }
        return success;
    }

    /**
      * Update this AppState with the values of the submitted other AppState.
      * 
      * @param {*} appState the other AppState
      */
    AppState.prototype.update = function(appState) {
        this.setIdentifier(appState.getIdentifier());
        this.setSelectedClusters(appState.getSelectedClusters());
        this.setSelectedSensortype(appState.getSelectedSensortype());
        this.setSelectedExportformat(appState.getSelectedExportformat());
        this.setSelectedTimeframe(appState.getSelectedTimeframe());
        this.setSelectedTimestamp(appState.getSelectedTimestamp());
        this.setSelectedLiveRefreshInterval(appState.getSelectedLiveRefreshInterval());
        this.setSelectedHistoricalRefreshInterval(appState.getSelectedHistoricalRefreshInterval());
        this.setAutomaticRefreshEnabled(appState.getAutomaticRefreshEnabled());
    }

    /**
      * Parses and sets the current AppState to the jsons content.
      * 
      * @param {*} json the submitted AppState as json
      * @returns {*} whether parsing was successful.
      */
    AppState.prototype.parse = function(json) {
        var success = true;
        try {
            var appState = new AppState(
                json.identifier, 
                json.selectedClusters, 
                json.selectedSensortype, 
                json.selectedExportformat, 
                json.selectedTimeframe, 
                json.selectedTimestamp, 
                json.selectedLiveRefreshInterval, 
                json.selectedHistoricalRefreshInterval, 
                json.automaticRefreshEnabled);
            this.update(appState);
        } catch (e) {
            success = false;
        }
        return success;
    }

    /**
      * Returns the current AppState as a String.
      */
    AppState.prototype.toString = function() {
        return JSON.stringify(this);
    }

    return AppState;
});