cob.custom.customize.push(function(core, utils, ui) {

  const fileMatcher = /[$]file/;
  const canvasMatcher = /[$]convert/;

  core.customizeAllInstances((instance, presenter) => {
    if (presenter.isGroupEdit()) return;

    presenter.findFieldPs((fp) => {
      return canvasMatcher.test(fp.field.fieldDefinition.description)
             && fileMatcher.test(fp.field.fieldDefinition.description);

    }).forEach(fp => {
      fp.content()[0].querySelector("input.js-file-input").remove();
      fp.content()[0].querySelector("button.js-upload-button").remove();
    });

  });
});
