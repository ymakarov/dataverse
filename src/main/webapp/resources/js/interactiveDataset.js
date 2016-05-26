
/**
 * RP: For prototype purposes, need to move this server side
 */
function renderInteractiveDatasetPanel(){
    
    var env = nunjucks.configure({ autoescape: true });
            
    // async filters must be known at compile-time
    env.addFilter(env.addFilter('jsonPretty', function(someJSON) {
        return JSON.stringify(someJSON, null, 4);
    }));

    // If the "interactiveDatasetJSON" is available, render the Interactive Dataset panel
    if (typeof interactiveDatasetJSON !== 'undefined') {

         // (1) Add information to the header metadata
         if (!($('#interactiveDatasetHeaderInfo').length)){
             var headerInfoHTML =  nunjucks.render('interactive_dataset_templates/header_data.html', interactiveDatasetJSON);
             //alert(headerInfoHTML);
             $('#header-metadata-panel').append(headerInfoHTML);     
        }

        // (2) Show visualization within panel
        if (!($('#id_interactive_ds_panel').is(":visible"))){
            var datasetPanelHTML =  nunjucks.render('interactive_dataset_templates/dataset_panel.html', interactiveDatasetJSON);
            $('#id_interactive_ds_panel').html(datasetPanelHTML).show();
        }
    }
}