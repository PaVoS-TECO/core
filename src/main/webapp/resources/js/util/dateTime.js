define(function() {
    function DateTime(year, month, day, hour, minute, second) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
    };

    DateTime.prototype.setYear = function(year) {
        this.year = year;
    };

    DateTime.prototype.getYear = function() {
        return this.year;
    };

    DateTime.prototype.setMonth = function(month) {
        this.month = month;
    };

    DateTime.prototype.getMonth = function() {
        return this.month;
    };

    DateTime.prototype.setDay = function(day) {
        this.day = day;
    };

    DateTime.prototype.getDay = function() {
        return this.day;
    };

    DateTime.prototype.setHour = function(hour) {
        this.hour = hour;
    };

    DateTime.prototype.getHour = function() {
        return this.hour;
    };

    DateTime.prototype.setMinute = function(minute) {
        this.minute = minute;
    };

    DateTime.prototype.getMinute = function() {
        return this.minute;
    };

    DateTime.prototype.setSecond = function(second) {
        this.second = second;
    };

    DateTime.prototype.getSecond = function() {
        return this.second;
    };

    DateTime.prototype.parse = function(dateTimeString) { };
    DateTime.prototype.toString = function() { };

    return DateTime;
});