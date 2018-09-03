define(function() {
    /**
      * Encapsulates a date and time.
      * 
      * @param {*} year the year
      * @param {*} month the month 
      * @param {*} day the day
      * @param {*} hour the hour
      * @param {*} minute the minute
      * @param {*} second the second
      */
    function DateTime(year, month, day, hour, minute, second) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
    };

    /**
      * Set the year.
      * 
      * @param {*} year the year
      */
    DateTime.prototype.setYear = function(year) {
        this.year = year;
    };
    /**
      * Get the year.
      */
    DateTime.prototype.getYear = function() {
        return this.year;
    };
    /**
      * Set the month.
      * 
      * @param {*} month the month
      */
    DateTime.prototype.setMonth = function(month) {
        this.month = month;
    };
    /**
      * Get the month.
      */
    DateTime.prototype.getMonth = function() {
        return this.month;
    };
    /**
      * Set the day.
      * 
      * @param {*} day the day
      */
    DateTime.prototype.setDay = function(day) {
        this.day = day;
    };
    /**
      * Get the day.
      */
    DateTime.prototype.getDay = function() {
        return this.day;
    };
    /**
      * Set the hour.
      * 
      * @param {*} hour the hour
      */
    DateTime.prototype.setHour = function(hour) {
        this.hour = hour;
    };
    /**
      * Get the hour.
      */
    DateTime.prototype.getHour = function() {
        return this.hour;
    };
    /**
      * Set the minute.
      * 
      * @param {*} minute the minute
      */
    DateTime.prototype.setMinute = function(minute) {
        this.minute = minute;
    };
    /**
      * Get the minute.
      */
    DateTime.prototype.getMinute = function() {
        return this.minute;
    };
    /**
      * Set the second.
      * 
      * @param {*} second the second
      */
    DateTime.prototype.setSecond = function(second) {
        this.second = second;
    };
    /**
      * Get the second.
      */
    DateTime.prototype.getSecond = function() {
        return this.second;
    };

    /**
      * Parse and update this DateTime with the submitted datetime string.
      * 
      * @param {*} dateTimeString the datetime string
      */
    DateTime.prototype.parse = function(dateTimeString) { };
    /**
      * Return this DateTime as a string.
      */
    DateTime.prototype.toString = function() { };

    return DateTime;
});