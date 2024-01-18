      //TODO add the same for touchpads
//main()
function handleInstanceCustomizations() {
   const fileMatcher = /[$]file/;
   const canvasMatcher = /[$]convert/;
   cob.custom.customize.push(function (core, utils, ui) {
      core.customizeAllInstances((instance, presenter) => {
         if (presenter.isGroupEdit()) return;
         
         const canvasFPs = presenter.findFieldPs((fp) => canvasMatcher.exec( fp.field.fieldDefinition.description ) 
         && fileMatcher.exec( fp.field.fieldDefinition.description ));

         canvasFPs.forEach((fp) => {
            let fieldPresenter = fp.content()[0];
            let controlDIV = fieldPresenter.querySelector(".controls")
            controlDIV.style.display="none"
         });
      })
   });
}

handleInstanceCustomizations()