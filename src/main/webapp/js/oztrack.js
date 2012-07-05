$(document).ready(function() {
     $.datepicker.setDefaults({
         dateFormat: 'dd/mm/yy'
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