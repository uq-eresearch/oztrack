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
     jQuery(".ckeditor").ckeditor({
         language: 'en-au',
         toolbar: [
             {name: 'clipboard', items : ['Cut','Copy','Paste','PasteText','PasteFromWord','-','Undo','Redo']},
             {name: 'basicstyles', items : ['Bold','Italic','Underline','Subscript','Superscript','-','RemoveFormat']},
             {name: 'paragraph', items : ['NumberedList','BulletedList','-','Outdent','Indent','-','JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock','-','Table','HorizontalRule','SpecialChar']},
             '/',
             {name: 'styles', items : ['Styles','Format','Font','FontSize']},
             {name: 'colors', items : ['TextColor','BGColor']},
             {name: 'insert', items : ['Link','Unlink','Image']},
             {name: 'document', items : ['Source']}
         ]
     });
});
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