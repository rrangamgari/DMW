Ext.Loader.setPath('Ext.ux', '/web-commons/extjs/ext-5.1.3/examples/ux');
Ext.application({
    requires : [ 'Ext.container.Viewport', 'MyApp.view.Viewport', 'Ext.grid.*',
	    'Ext.ux.RowExpander', 'Ext.ux.form.MultiSelect',
	    'Ext.ux.form.ItemSelector', 'Ext.selection.CellModel'],
    controllers : [ 'MyController' ],
    models : [ 'MyModel', 'MyModel1' ],
    stores : [ 'MyStore' ],
    views : [ 'Viewport' ],
    autoCreateViewport : true,
    name : 'MyApp'
});
Ext.define('simple-combo', {
    extend : 'Ext.form.ComboBox',
    alias : 'widget.simple-combo',
    multiSelect : false,
    displayField : 'name',
    valueField : 'name',
    width : 300,
    labelWidth : 150,
    queryMode : 'local',
    listConfig: { loadMask: true }
});