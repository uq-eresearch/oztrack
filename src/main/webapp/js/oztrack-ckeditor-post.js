$(document).ready(function() {
    jQuery(".oztrack-ckeditor").ckeditor({
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