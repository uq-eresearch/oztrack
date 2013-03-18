/*global confirm, alert*/
function deleteEntity(url, destUrl, message) {
    if (!confirm(message)) {
        return;
    }
    $.ajax({
        url: url,
        type: 'POST',
        data: {
            '_method': 'DELETE'
        },
        error: function(xhr, textStatus, errorThrown) {
            alert('Error processing delete');
        },
        success: function (data,textStatus, jqXHR) {
            window.location = destUrl;
        }
    });
}

function initHelpPopover(helpPopover) {
    $('<a class="help-popover-icon" href="javascript:void(0);">')
        .insertBefore(helpPopover)
        .popover({
            container: 'body',
            placement: 'right',
            trigger: 'click',
            html: true,
            title: helpPopover.attr('title'),
            content: helpPopover.html()
        });
}
$(document).ready(function() {
    $.datepicker.setDefaults({
        dateFormat: 'yy-mm-dd',
        altFormat: 'yy-mm-dd',
        changeMonth: true,
        changeYear: true,
        firstDay: 1 // make first day of week Monday (default is Sunday)
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
    $('.help-popover').each(function() {initHelpPopover($(this));});
});
$(document).click(function (e) {
    $('.help-popover-icon').filter(function(i) {return this !== e.target;}).popover('hide');
});
