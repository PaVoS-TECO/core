define(['jquery', 'util'], function($, Util) {
    return {
        /**
          * Add a radio button group to the element with the submitted identifier. The radio buttons names
          * and values are set by the entries of the submitted input array.
          * 
          * @param {*} identifier the identifier of the radio button container
          * @param {*} name the name of the radio button group
          * @param {*} input an array of values
          * @param {*} checked the value which is initially checked
          */
        buildRadioButtonGroup: function(identifier, name, input, checked) {
            var radioButtons = '';

            for (index = 0; index < input.length; index++) {
                var c = '';
                if (input[index] == checked) {
                    c = 'checked';
                }

                radioButtons = radioButtons
                    + '<label class="radio-inline">' 
                      + '<input type="radio" name="' + name + '" value="' + input[index] + '" ' + c + '>' + input[index] 
                    + '</label>';
            }

            $(identifier).append(radioButtons);
        },

        /**
          * Generate table content out of a header array and a content array.
          * 
          * @param {*} identifier the identifier of the table
          * @param {*} headerArray an array containing the headers of the table
          * @param {*} contentArray an array containing the content of each row
          */
        buildTableContentFromArray: function(identifier, headerArray, contentArray) {
            tableContent = this.formatTableRow(headerArray, '<th>', '</th>');

            for (row = 0; row < (contentArray.length / headerArray.length); row++) {
                var array = [];
                for (column = 0; column < headerArray.length; column++) {
                    array[column] = contentArray[(row * headerArray.length) + column];
                }
                tableContent = tableContent + this.formatTableRow(array, '<td>', '</td>');
            }

            $(identifier).append(tableContent);
        },

        /**
          * Turns every key of the submitted json into a header (duplicates are removed) and builds 
          * the rest of the table according to this. Some cells can be empty because some entries 
          * have a property that others dont have.
          * 
          * @param {*} identifier the tables identifier
          * @param {*} json the json
          */
        buildTableContentFromJson: function(identifier, json) {
            var headerArray = [];
            for (var row = 0; row < json.length; row++) {
                var rowHash = json[row];
                for (var key in rowHash) {
                    if ($.inArray(key, headerArray) == -1) {
                    headerArray.push(key);
                    }
                }
            }
            var tableContent = this.formatTableRow(headerArray, '<th>', '</th>');
        
            for (var row = 0; row < json.length; row++) {
                var array = [];
                for (var columns = 0; columns < headerArray.length; columns++) {
                    var cellValue = json[row][headerArray[columns]];
                    if (cellValue == null) {
                        cellValue = "";
                    }
                    array.push(cellValue);
                }
                tableContent = tableContent + this.formatTableRow(array, '<td>', '</td>');
            }

            $(identifier).append(tableContent);
        },

        /**
          * Build a table without an header an only one row out of the submitted array entries.
          * 
          * @param {*} identifier the tables identifier
          * @param {*} array the array
          */
        buildListTableFromArray: function(identifier, array) {
            if ((array != null) && (array.length > 0)) {
                var tableContent = '';
                for (var row = 0; row < array.length; row++) {
                    tableContent = tableContent + this.formatTableRow([array[row]], '<td>', '</td>');
                }
                $(identifier).append(tableContent);
            }
        },

        /**
          * Turns the submitted array of table cells into a row.
          * 
          * @param {*} array an array consisting of table cell content
          * @param {*} prefix the prefix that describes the table cell type
          * @param {*} suffix the suffix that describes the table cell type
          */
        formatTableRow: function(array, prefix, suffix) {
            headerRow = '<tr>'
            for(columns = 0; columns < array.length; columns++) {
                headerRow = headerRow + prefix + array[columns] + suffix;
            }
            return headerRow + '</tr>';
        }
    }
});