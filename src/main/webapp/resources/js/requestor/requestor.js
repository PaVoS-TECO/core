define(['jquery', 'loadingOverlay'], function($) {
    return {
        requestExport: function() {
            
        },

        startLoadAnimation: function(timeout) {
            $.LoadingOverlay("show", {
                image          : "resources/data/PaVoSLogo-Icon.png",
                imageAnimation : "1000ms fadein",
                size           : 100,
                minSize        : 50,
                maxSize        : 200,
                fade           : [400, 400]
            });

            if (timeout != null) {
                var _this = this;
                setTimeout(function() {
                    _this.stopLoadAnimation();
                }, Number(timeout));
            }
        },

        stopLoadAnimation: function() {
            $.LoadingOverlay("hide");
        } 
    }
});