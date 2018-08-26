define(['dateTime', 'util'], function(DateTime, Util) {
    function UTCDateTime(year, month, day, hour, minute, second) {
        DateTime.call(this, year, month, day, hour, minute, second);
    };
    UTCDateTime.prototype = Object.create(DateTime.prototype);
    UTCDateTime.prototype.constructor = UTCDateTime;

    UTCDateTime.prototype.parse = function(dateTimeString) {
        var dateTimeArray = dateTimeString.split('T');
        var date = dateTimeArray[0];
        var time = dateTimeArray[1];

        var dateArray = date.split('-');
        var year = Number(dateArray[0]);
        var month = Number(dateArray[1]);
        var day = Number(dateArray[2]);

        var timeArray = time.split(':');
        var hour = Number(timeArray[0]);
        var minute = Number(timeArray[1]);
        var second = Number(timeArray[2].replace('Z', ''));

        this.setYear(year);
        this.setMonth(month);
        this.setDay(day);
        this.setHour(hour);
        this.setMinute(minute);
        this.setSecond(second);
    };

    UTCDateTime.prototype.toString = function() {
        DateTime.prototype.toString.call(this);

        return Util.addString(this.getYear(), '0', 4, false)
            + '-' 
            + Util.addString(this.getMonth(), '0', 2, false) 
            + '-' 
            + Util.addString(this.getDay(), '0', 2, false) 
            + 'T' 
            + Util.addString(this.getHour(), '0', 2, false) 
            + ':' 
            + Util.addString(this.getMinute(), '0', 2, false) 
            + ':' 
            + Util.addString(this.getSecond(), '0', 2, false)
            + 'Z';
    };

    return UTCDateTime;
});