function deleteEntity(url, destUrl, message) {
    if (!confirm(message)) {
        return;
    }
    jQuery.ajax({
        url: url,
        type: 'POST',
        data: {
            '_method': 'DELETE'
        },
        error: function(xhr, textStatus, errorThrown) {
            alert('Error processing delete');
        },
        complete: function (xhr, textStatus) {
            if (textStatus == 'success') {
                window.location = destUrl;
            }
        }
    });
}

//Returns date in ISO8601 format: required by HTML 5 [1]; code taken from [2]; other discussion at [3].
//[1] http://www.whatwg.org/specs/web-apps/current-work/multipage/common-microsyntaxes.html#dates-and-Elems
//[2] https://developer.mozilla.org/en/Core_JavaScript_1.5_Reference:Global_Objects:Date
//[3] http://stackoverflow.com/questions/2573521/how-do-i-output-an-iso-8601-formatted-string-in-javascript
function padForISO8601(n) {
    return (n < 10) ? ('0' + n) : n;
}
function dateToISO8601(d) {
    return d.getUTCFullYear() + '-' + padForISO8601(d.getUTCMonth() + 1) + '-' + padForISO8601(d.getUTCDate());
}
function dateTimeToISO8601(d) {
    return dateToISO8601(d) + 'T' + padForISO8601(d.getUTCHours()) + ':' + padForISO8601(d.getUTCMinutes()) + ':' + padForISO8601(d.getUTCSeconds()) + 'Z';
}

$(document).ready(function() {
    $.datepicker.setDefaults({
        dateFormat: 'd/m/yy',
        altFormat: 'yy-mm-dd',
        changeMonth: true,
        changeYear: true
    });
    // Fix bug where clearing field doesn't clear alt field
    // http://bugs.jqueryui.com/ticket/5734
    // http://stackoverflow.com/questions/3922592/jquery-ui-datepicker-clearing-the-altfield-when-the-primary-field-is-cleared
    $('.datepicker').change(function() {
        var altField = $(this).datepicker('option', 'altField');
        if (altField && !$(this).val()) {
            $(altField).val('');
        }
    });
});