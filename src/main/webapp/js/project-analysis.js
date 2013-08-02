/*global OpenLayers*/

(function(OzTrack) {
    OzTrack.AnalysisMap = function(div, options) {
        if (!(this instanceof OzTrack.AnalysisMap)) {
            throw new Error('Constructor called as a function');
        }
        var that = this;

        that.projectMap = new OzTrack.ProjectMap(div, {
            project: options.project,
            animals: options.animals,
            extraCategories: {'analysis': {label: 'Analysis layers'}},
            onLayerSuccess: options.onLayerSuccess,
            onUpdateAnimalInfoFromLayer: options.onUpdateAnimalInfoFromLayer,
            onAnalysisCreate: options.onAnalysisCreate,
            onAnalysisDelete: options.onAnalysisDelete,
            onAnalysisError: options.onAnalysisError,
            onAnalysisSuccess: options.onAnalysisSuccess,
            onUpdateAnimalInfoFromAnalysisCreate: options.onUpdateAnimalInfoFromAnalysisCreate,
            onUpdateAnimalInfoFromAnalysisSuccess: options.onUpdateAnimalInfoFromAnalysisSuccess
        });

        that.addProjectMapLayer = function(layerTypeValue, layerTypeLabel) {
            var layerName = layerTypeLabel;
            var params = {
                projectId: $('#projectId').val(),
                fromDate: $('#fromDate').val(),
                toDate: $('#toDate').val(),
                animalIds:
                    $('input[name=animal]:not(:disabled):checked')
                        .map(function() {return $(this).val();})
                        .toArray()
                        .join(',')
            };
            $('.paramField-' + layerTypeValue).each(function() {
                if ($(this).attr('type') == 'checkbox') {
                    params[$(this).attr('name')] = $(this).is(':checked') ? 'true' : 'false';
                }
                else if ($(this).val()) {
                    params[$(this).attr('name')] = $(this).val();
                }
            });
            if (layerTypeValue == 'LINES') {
                var trajectoryLayer = that.projectMap.createTrajectoryLayer(params, 'analysis');
                that.projectMap.addLayer(trajectoryLayer.getWMSLayer());
            }
            else if (layerTypeValue == 'POINTS') {
                var detectionLayer = that.projectMap.createDetectionLayer(params, 'analysis');
                that.projectMap.addLayer(detectionLayer.getWMSLayer());
            }
            else if (layerTypeValue == 'START_END') {
                var startEndLayer = that.projectMap.createStartEndLayer(params, 'analysis');
                that.projectMap.addLayer(startEndLayer);
            }
            else {
                params.analysisType = layerTypeValue;
                that.projectMap.createAnalysisLayer(params, layerName, 'analysis');
            }
        };

        // Delegate to properties/functions of OzTrack.ProjectMap
        that.updateSize = that.projectMap.updateSize;
        that.zoomToAnimal = that.projectMap.zoomToAnimal;
        that.deleteProjectMapLayer = function(id) {
            that.projectMap.deleteProjectMapLayer(id);
            $('.projectMapLayerInfo-' + id).fadeOut({complete: function() {$(this).remove();}});
        };
        that.setAnimalVisible = that.projectMap.setAnimalVisible;
        that.createAnalysisLayer = that.projectMap.createAnalysisLayer;
        that.addAnalysisLayer = that.projectMap.addAnalysisLayer;
        that.deleteCurrentAnalysis = that.projectMap.deleteCurrentAnalysis;
        that.deleteAnalysis = that.projectMap.deleteAnalysis;
    };
}(window.OzTrack = window.OzTrack || {}));
