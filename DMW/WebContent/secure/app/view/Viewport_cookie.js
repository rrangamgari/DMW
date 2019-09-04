// Header for the UI
Ext
		.define(
				'MyApp.view.Viewport',
				{
					extend : 'Ext.container.Viewport',
					layout : 'border',
					initComponent : function () {
						var me = this;
						this.items = [
								{
									xtype : 'toolbar',
									region : 'north',
									id : 'main-tb',
									cls : 'main_tb',
									height : 60,
									items : [
											{
												xtype : 'box',
												autoEl : {
													tag : 'img',
													src : '/web-commons/logo.png'
												}

											},
											{
												xtype : 'tbfill'
											},
											{
												text : '<b style="font-family: Calibri,Verdana, Helvetica, Arial, sans-serif; font-size: 20pt; color:#01B2AA; font-weight: bold;">Data Management Workbench</b><br>'
														+ '<b align=right style="color:blue">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Alpha release</b>',
												xtype : 'tbtext'
											}, {
												xtype : 'tbfill'
											}, {
												xtype : 'tbtext',
												text : 'Welcome, '
											}, {
												xtype : 'tbtext',
												id : 'user-id-display',
												text : '<b>Anonymous</b>'
											}, {
												xtype : 'tbseparator'
											}, {
												text : 'Add Table',
												disabled : true,
												id : 'add-id-display',
												handler : showDialog
											}, {
												xtype : 'tbseparator'
											}, {
												text : 'Feedback',
												handler : function () {
													window.location.href = "mailto:planningsystems.helpdesk@maximintegrated.com?cc=ravinder.rangamgari@maximintegrated.com,mukesh.joshi@maximintegrated.com&subject=[DMW Application]";
												}
											}, {
												xtype : 'tbseparator'
											}, {
												text : 'Logout',
												cls : 'download',
												handler : function () {
													window.location.href = '/DMW/logout.jsp';
												}
											}, {
												xtype : 'tbseparator'
											}, {
												xtype : 'tbtext',
												id : 'version-txt',
												text : 'v 1.0'
											}
									]
								}, {
									// xtype : 'basic-panels',
									// define : 'Ext.Container',
									// extend : 'Ext.Panel',
									region : 'center',
									id : 'main-panel',
									// title : 'Container Panel',
									layout : {
										type : 'hbox', // Arrange child items
										// vertically
										align : 'stretch', // Each takes up
										// full width
										height : 200,
										padding : 5
									},
									defaults : {
										xtype : 'panel',
										// width : '100%',
										bodyPadding : 10,
										frame : true
									},
									items : [
											{
												xtype : 'treepanel',
												region : 'left',
												title : 'Tables  Names',
												id : "tree_id",
												collapsed : false,
												collapsible : true,
												collapseDirection : Ext.Component.DIRECTION_LEFT,
												useArrows : true,
												rootVisible : false,
												store : store,
												width : '20%',
												listeners : {
													itemclick : function ( view, node ) {
														// /alert(node.data.text);
														var parentNode = node.parentNode;
														if ( !parentNode.isFirstInstance ) {
															getTableInfo( node.data.name, false );
															Ext.getCmp( "widget-id" ).setTitle( node.data.text );
														}
													}
												}
											}, {
												xtype : 'splitter',
												width : '0.5px',
											}, {
												xtype : 'panel',
												flex : 1,
												title : 'Edit Tables ',
												collapsed : false,
												collapsible : false,
												id : 'widget-id',
												// collapseDirection :
												// Ext.Component.DIRECTION_RIGHT,
												width : '79%',
												html : "<div id='widget'></div>"
											}
									],
									listeners : {
										afterrender : {
											// fn : loadUser,
											// me.onMainCenterPanelAfterRender,

											scope : me
										}
									}
								}
						];
						// MyViewport.superclass.initComponent.call(this);
						me.callParent( arguments );
					}
				} );

function loadUser () {
	Ext.getBody().mask( 'Obtaining user information...' );
	DMWController.getUser( function ( result, exception ) {
		if ( result != null ) {
			Ext.getCmp( 'user-id-display' ).setText( '<b>' + result.firstName + " " + result.lastName + '</b> (' + result.userId + ')' );
			isAdmin = result.admin;
			// alert(isAdmin);
		} else {
			Ext.getBody().unmask();
		}
		Ext.getBody().unmask();

	} );

}
var gblUpload = false;
addNewTableWidnow = Ext.create( 'Ext.window.Window', {} );
function showDialog () {
	addNewTableWidnow = Ext.create( 'Ext.window.Window', {
		layout : {
			type : 'vbox',
			align : 'center'
		},
		layout : 'fit',
		title : "Add New Table",
		model : true,
		autoShow : true,
		listeners : {
			close : closeDialog
		},
		id : 'window-id',
		items : [
			{
				xtype : 'tabpanel',
				id : 'tab-id',
				height : 330,
				// width : 500,
				layout : 'fit',
				items : [
						{ // we will declare 3 tabs
							title : 'Primary',
							style : 'padding: 30px;',
							items : [
									{
										xtype : 'simple-combo',
										id : 'conf-id',
										fieldLabel : 'Configuration / Parent ',
										name : 'conf',
										displayField : 'name',
										valueField : 'id',
										store : confStore,
										allowBlank : false
									}, {
										xtype : 'textfield',
										id : 'table-owner-id',
										fieldLabel : 'Table Owner',
										name : 'table-owner',
										labelWidth : 150,
										value : gblUser,
										allowBlank : false
									}, {
										xtype : 'simple-combo',
										id : 'schema-name-id',
										fieldLabel : 'Schema Name',
										name : 'schema-name',
										labelWidth : 150,
										displayField : 'name',
										valueField : 'name',
										store : schemaStore,
										allowBlank : false,
										listeners : {
											select : function () {
												var value = this.getValue();
												// combo2.setValue(value);
												tableStore.load( {
													params : {
														tableName : '',
														owner : value
													}
												} );
											}
										}
									}, {
										xtype : 'simple-combo',
										id : 'table-name-id',
										fieldLabel : 'Table Name',
										name : 'table-name',
										labelWidth : 150,
										displayField : 'name',
										valueField : 'name',
										// queryMode : 'remote',
										store : tableStore,
										allowBlank : false,
										listeners : {
											select : function () {
												var value = this.getValue();
												// combo2.setValue(value);
												columnStore.load( {
													params : {
														owner : Ext.getCmp( 'schema-name-id' ).value,
														tableName : value
													}
												} );
												if ( Ext.getCmp( 'table-alias-id' ).value == "" ) {
													Ext.getCmp( 'table-alias-id' ).setValue( Ext.getCmp( 'schema-name-id' ).value + "." + Ext.getCmp( 'table-name-id' ).value )
												}
											}
										}
									}, {
										xtype : 'textfield',
										id : 'table-alias-id',
										fieldLabel : 'Table Alias',
										name : 'table-alias',
										labelWidth : 150,
										allowBlank : false
									}, {
										xtype : 'simple-combo',
										id : 'role-name-id',
										fieldLabel : 'Role',
										name : 'role-name',
										labelWidth : 150,
										displayField : 'name',
										valueField : 'id',
										store : rolesStore,
										multiSelect : true,
										allowBlank : false,
										listeners : {
											select : function () {
												// console.log( this )
												var value = this.displayTplData;
												var tabs = this.up( 'tabpanel' );
												var count = tabs.items.getCount();
												for ( var k = count - 1; k >= 4; k-- ) {
													// console.log(k);
													// console.log(tabs.items.getAt( k ));
													if ( Ext.getCmp( tabs.items.getAt( k ).title + '_ADD_ROW' ) != null ) {
														Ext.getCmp( tabs.items.getAt( k ).title + '_ADD_ROW' ).destroy();
													}
													tabs.remove( tabs.items.getAt( k ) );
												}
												for ( var i = 0; i < value.length; i++ ) {
													// tabs.remove(value[i].name,true);
													var tab = tabs.add( Ext.widget( 'panel', {
														title : value[ i ].name,
														// width:500,
														// html : 'Html ' + count
														style : 'padding: 20px;',
														items : [
																{
																	xtype : 'checkboxgroup',
																	// fieldLabel: 'Toppings',
																	// defaultType : 'checkboxfield',
																	width : 500,
																	columns : 3,
																	items : [
																			{
																				boxLabel : 'Allow Add Row',
																				name : value[ i ].name + '_ADD_ROW',
																				inputValue : 'ADD_ROW',
																				id : value[ i ].name + '_ADD_ROW',

																			}, {
																				boxLabel : 'Allow Delete Row',
																				name : value[ i ].name + '_DELETE_ROW',
																				inputValue : 'DELETE_ROW',
																				id : value[ i ].name + '_DELETE_ROW',

																			}, {
																				boxLabel : 'Allow Mass Update',
																				name : value[ i ].name + '_MASS_UPDATE',
																				inputValue : 'MASS_UPDATE',
																				id : value[ i ].name + '_MASS_UPDATE',

																			}, {
																				boxLabel : 'Allow Download',
																				name : value[ i ].name + '_DOWNLOAD',
																				inputValue : 'DOWNLOAD',
																				id : value[ i ].name + '_DOWNLOAD',

																			}, {
																				boxLabel : 'Allow Upload',
																				name : value[ i ].name + '_UPLOAD',
																				inputValue : 'UPLOAD',
																				id : value[ i ].name + '_UPLOAD',

																			}, {
																				boxLabel : 'All Columns Editable',
																				name : value[ i ].name + '_EDITABLE',
																				inputValue : value[ i ].name + '_EDITABLE',
																				id : value[ i ].name + '_EDITABLE'
																			}
																	]
																}, {
																	xtype : 'form',
																	layout : 'fit',
																	title : '<b>Custom Column Edit</b>',
																	items : [
																		{
																			title : '<b><u>Custom Column Edit</u></b>',
																			xtype : 'itemselector',
																			name : value[ i ].name + 'itemselector-name',
																			id : value[ i ].name + 'itemselector-field',
																			anchor : '100%',
																			width : 600,
																			height : 180,
																			autoScroll : true,

																			buttons : [
																					'add', 'remove'
																			],
																			imagePath : '/web-commons/ext-5.1.1/examples/ux/css/images/',
																			store : columnStore,
																			displayField : 'name',
																			valueField : 'name',
																			// value : [ '3', '4', '6' ],
																			allowBlank : false,
																			msgTarget : 'side',
																			fromTitle : 'Non Editable Columns',
																			toTitle : 'Editable Columns'
																		}
																	]
																}
														]
													} ) );

												}
											}
										}
									}, {
										xtype : 'simple-combo',
										id : 'system-user-id',
										fieldLabel : 'System User Column',
										name : 'system-user',
										labelWidth : 150,
										store : columnStore,
										allowBlank : true
									}, {
										xtype : 'simple-combo',
										id : 'system-date-id',
										fieldLabel : 'System Date Column',
										name : 'system-date',
										labelWidth : 150,
										store : columnStore,
										allowBlank : true
									}
							]
						}, {
							title : 'Pagination',
							style : 'padding: 30px;',
							items : [
									{
										xtype : 'checkboxgroup',
										items : [
											{
												boxLabel : 'Allow Pagination',
												name : 'PAGINATION',
												inputValue : 'PAGINATION',
												id : 'PAGINATION',

											}
										]
									}, {
										xtype : 'numberfield',
										id : 'pages-id',
										fieldLabel : 'Pages',
										name : 'pages',
										labelWidth : 150,
										allowBlank : false
									}, {
										xtype : 'simple-combo',
										id : 'sort-by-id',
										fieldLabel : 'Sort By Column',
										name : 'sort-by',
										labelWidth : 150,
										store : columnStore,
										allowBlank : false
									}, {
										xtype : 'simple-combo',
										id : 'sort-dir-id',
										fieldLabel : 'Sort Direction',
										name : 'sort-dir',
										labelWidth : 150,
										queryMode : 'local',
										store : [
												'ASC', 'DESC'
										],
										value : 'ASC',
										allowBlank : false
									}
							]
						}, {
							title : 'Pivot',
							style : 'padding: 30px;',
							items : [
									{
										xtype : 'checkboxgroup',
										items : [
											{
												boxLabel : 'Allow Pivot View',
												name : 'PIVOT',
												inputValue : 'PIVOT',
												id : 'PIVOT',

											}
										]
									}, {
										xtype : 'simple-combo',
										id : 'fixed-col-id',
										fieldLabel : 'Fixed Column',
										name : 'fixed-col',
										labelWidth : 150,
										store : columnStore,
										multiSelect : true,
										allowBlank : false
									}, {
										xtype : 'simple-combo',
										id : 'pivot-col-id',
										fieldLabel : 'Pivot Column',
										name : 'pivot-col',
										labelWidth : 150,
										store : columnStore,
										allowBlank : false
									}, {
										xtype : 'simple-combo',
										id : 'pivot-data-id',
										fieldLabel : 'Pivot Data',
										name : 'pivot-data',
										labelWidth : 150,
										store : columnStore,
										allowBlank : false
									}
							]
						}, {
							title : 'Spl Query',
							style : 'padding: 30px;',
							items : [
									{
										xtype : 'checkboxgroup',
										items : [
											{
												boxLabel : 'Conditional Query',
												name : 'CONDITIONAL_QUERY',
												inputValue : 'CONDITIONAL_QUERY',
												id : 'CONDITIONAL_QUERY'
											}
										]
									}, {
										xtype : 'textarea',
										id : 'query-id',
										fieldLabel : 'Query',
										name : 'query',
										labelWidth : 150,
										allowBlank : false
									}
							]
						}
				],

			}
		],
		buttons : [
				{
					text : 'Save',
					handler : saveTableDetails
				}, {
					text : 'Reset',
					handler : function () {
						// this.up('form').getForm().reset();
					}
				}
		]
	} );
}
function closeDialog () {
	addNewTableWidnow.hide();
}
function getTableInfo ( table, pivotGrid ) {
	var myMask = new Ext.LoadMask( {
		target : Ext.getCmp( 'widget-id' ),
		msg : "Loading..."
	} );
	myMask.show();

	if ( Ext.getCmp( "id_grid" ) != null ) {
		dataStore.clearFilter();
		dataStore.sorters.clear();
		gblUpload = false;
		// grid.filters.clearFilters();
	}
	if ( pivotGrid ) {
		Ext.getCmp( "widget-id" ).setTitle( Ext.getCmp( "widget-id" ).getTitle() + ' (Pivot)' );
	}
	Ext.Ajax.request( {
		url : '/DMW/api/getTableInfo',
		method : 'GET',
		params : {
			tableId : table,
			pivot : pivotGrid
		},
		reader : {
			type : 'json'
		},
		success : function ( response ) {
			myMask.hide();
			if ( Ext.getCmp( "id_grid" ) != null ) {
				Ext.getCmp( "id_grid" ).destroy();
			}
			var myData = Ext.JSON.decode( response.responseText );
			if ( myData == null || myData == 'null' ) {
				var tempData = "<div class='datagrid'><table width='" + ( window.innerWidth * 92 ) / 100 + "'><tbody><tr height='" + ( window.innerHeight * 92 ) / 100 + "' ><td align=center><b>No Data Found...</b></td></tr>";
				tempData += "</tbody></table></div>";
				document.getElementById( "widget" ).innerHTML = tempData;
			} else {
				var rowEditing = Ext.create( 'Ext.grid.plugin.RowEditing', {
					clicksToMoveEditor : 1,
					autoCancel : false
				} );
				var cellEditing = Ext.create( 'Ext.grid.plugin.CellEditing', {
					clicksToEdit : 1,
					listeners : {
						beforeedit : function ( e, editor, options ) {
							/*
							 * if ( e.rowIdx == 0 ) return false; console.log( options );
							 */

						}
					}
				} );
				var vbbar = [
						{
							text : 'Save',
							xtype : 'button',
							handler : saveDetails
						}, {
							text : 'Cancel',
							xtype : 'button',
							handler : function () {
								current = grid.store.currentPage;
								grid.store.loadPage( current );
							}
						}, {
							xtype : 'tbfill'
						}, {
							xtype : 'tbtext',
							align : 'right',
							text : '<b>Total Records : ' + myData.pageCount
						}
				];
				if ( myData.tableInfo.paging ) {
					vbbar = Ext.create( 'Ext.PagingToolbar', {
						store : dataStore,
						displayInfo : true,
						displayMsg : 'Displaying Records {0} - {1} of {2}',
						emptyMsg : "No Records to display",
						items : [
								'-', {
									text : 'Save',
									xtype : 'button',
									handler : saveDetails
								}, '-', {
									text : 'Cancel',
									xtype : 'button',
									handler : function () {
										current = grid.store.currentPage;
										grid.store.loadPage( current );
									}
								}
						/*
						 * , '-', { text : 'Download All', xtype : 'button', handler : downloadAll }
						 */]
					} );
				}
				document.getElementById( "widget" ).innerHTML = "";
				gblExtGridHeight = ( Ext.isIE7 ? ( ( Ext.getBody().getViewSize().height - 120 ) ) : ( ( Ext.getBody().getViewSize().height - 126 ) ) );

				// var fieldValues = Object.values((myData)[0]);
				var fieldValues = new Array();
				var fieldPrecision = new Array();
				var j = 0;
				for ( var key in ( myData.header.columnTypes ) ) {
					fieldValues[ j ] = myData.header.columnTypes[ key ];
					fieldPrecision[ j ] = myData.header.columnPrecision[ key ];
					j++;
				}
				var fields = Object.keys( myData.header.columnTypes );
				if ( MyApp.model.MyModel ) {
					MyApp.model.MyModel.fields = fields;
				}
				var columns = new Array();
				var columnType = new Array();
				var columnEdit = new Array();
				var rowEdit = new Array();
				var copyRowEdit = new Array();
				var p;
				var k = 0, l = 0, m = 0;
				columnEdit[ k ] = -1;
				for ( var i = 0; i < fields.length; i++ ) {
					var vAlign = 'left';
					var vrenderer = "";
					var vEditor = "";
					var vType = "";
					var vWidth = 75;
					if ( pivotGrid ) {
						vWidth = 100;
					}
					if ( myData.header.columnEdit[ fields[ i ] ] == 'Y' ) {
						columnEdit[ k++ ] = i - 1;
						// console.log( i );
					}
					// console.log(myData.header.columnEdit[fields[i]]);
					if ( fieldValues[ i ] == "date" ) {
						vAlign = 'center';
						// if ( myData.header.columnEdit[ fields[ i ] ] == 'Y' )
						{
							vEditor = {
								xtype : 'datefield',
								format : 'm/d/Y',
								allowBlank : false
							};
						}
						vType = "date";
						vrenderer = Ext.util.Format.dateRenderer( 'm/d/Y' );

					} else if ( fieldValues[ i ] == "number" ) {
						vAlign = 'right';
						// console.log(fieldPrecision[i]);
						var tempFormat = '0,0';
						if ( fieldPrecision[ i ] > 0 ) {
							tempFormat = '0,0.';
							for ( var k = 0; k < fieldPrecision[ i ]; k++ )
								tempFormat += '0';
						}
						vrenderer = Ext.util.Format.numberRenderer( tempFormat );
						// if ( myData.header.columnEdit[ fields[ i ] ] == 'Y' )
						{
							vEditor = {
								xtype : 'numberfield',
								forcePrecision : true,
								decimalPrecision : fieldPrecision[ i ]
							};
						}
						vType = "number";

					} else {
						// if ( myData.header.columnEdit[ fields[ i ] ] == 'Y' )
						{
							vEditor = {
								xtype : 'textfield'
							};
						}
						vType = "string";
					}
					console.log( getCookie( Ext.getCmp( "widget-id" ).title + "-" + fields[ i ] ) );
					var vFilter = {
						type : vType
					}
					if ( getCookie( Ext.getCmp( "widget-id" ).title + "-" + fields[ i ] ) != "" && getCookie( Ext.getCmp( "widget-id" ).title + "-" + fields[ i ] ) != "undefined" ) {
						var vFilter = {
							type : vType,
							value : getCookie( Ext.getCmp( "widget-id" ).title + "-" + fields[ i ] ),
							active : true
						}
					}
					// Ext.getCmp( "widget-id" ).setTitle

					if ( fields[ i ] == "RID" ) {
						columns.push( {
							text : fields[ i ],
							dataIndex : fields[ i ],
							hidden : true,
							hideable : false
						} );
					} else {
						columns.push( {
							text : fields[ i ],
							dataIndex : fields[ i ],
							align : vAlign,
							flex : 1,
							minWidth : vWidth,
							filter : vFilter,
							renderer : vrenderer,
							editor : vEditor,
							type : vType,
							autoSizeColumn : true
						} );
					}
					var this_modelField = {};
					this_modelField[ 'mapping' ] = fields[ i ];
					this_modelField[ 'name' ] = fields[ i ];
					this_modelField[ 'type' ] = vType;
					modelFields.push( this_modelField );
				}
				var grid = Ext.create( 'Ext.grid.Panel', {
					// title : 'Scheduled Driver Work',
					id : "id_grid_" + Math.round( ( Math.random() * 10000 ) ),
					renderTo : 'widget',
					loadMask : true,
					dockedItems : [
						{
							xtype : 'toolbar',
							dock : 'top',
							ui : 'footer',
							defaults : {
								minWidth : 100
							},
							items : [
									{
										xtype : 'tbfill'
									}, {
										text : 'Pivot View',
										xtype : 'button',

										disabled : ( !myData.tableInfo.virtualPivot || pivotGrid ),
										handler : function () {
											getTableInfo( table, true );
										}
									}, {
										text : 'Copy Record',
										iconCls : 'rec-copy',
										xtype : 'button',
										disabled : !myData.tableInfo.addRow,
										handler : function () {
											if ( grid.getSelectionModel().hasSelection() ) {
												var sm = grid.getSelectionModel().getSelection()[ 0 ];
												var rowIndex = grid.store.indexOf( sm );
												// cellEditing.cancelEdit();
												// dataStore.remove(sm.getSelection());
												var keys = Object.keys( modelFields )
												var r = Ext.create( 'MyApp.model.MyModel1', {} );
												dataStore.insert( rowIndex, r );
												// console.log( cellEditing.editors );
												// grid.getPlugin('cellEditing').editor.enable();
												// cellEditing.startEdit( rowIndex, 4 );
												/*
												 * cellEditing.startEditByPosition( { row : rowIndex, column : columnEdit[0] } );
												 */
												if ( copyRowEdit.length > 0 ) {
													for ( i = copyRowEdit.length - 1; i >= 0; i-- ) {
														if ( copyRowEdit[ i ] >= rowIndex )
															copyRowEdit[ i ] = copyRowEdit[ i ] + 1;
														// console.log( copyRowEdit[ i ] + "|" )
													}
												}
												copyRowEdit[ m++ ] = rowIndex;
												grid.getView().select( rowIndex );
												var tm = grid.getSelectionModel().getSelection()[ 0 ];
												// console.log( grid.getSelectionModel() );
												for ( var i = 0; i < modelFields.length; i++ ) {
													// console .log(sm.get( modelFields[ i ].name ));
													// tm.set( modelFields[ i ].name, "test" )
													if ( modelFields[ i ].name != "RID" ) {
														tm.set( modelFields[ i ].name, sm.get( modelFields[ i ].name ) )
														// var editor = grid.getColumnModel().getCellEditor(tm,0);
														// editor.enable();
													}
												}
												// cellEditing.cancelEdit();
											} else {
												Ext.Msg.alert( "Error", "Please select a row to copy" );
											}
										}
									}, {
										text : 'Add Record',
										iconCls : 'rec-add',
										xtype : 'button',
										disabled : !myData.tableInfo.addRow,
										handler : function () {
											cellEditing.cancelEdit();
											// Create a
											// model
											// instance
											var r = Ext.create( 'MyApp.model.MyModel1', {} );
											rowEdit[ l++ ] = 0 + rowEdit.length;
											if ( copyRowEdit.length > 0 ) {
												for ( i = copyRowEdit.length - 1; i >= 0; i-- ) {
													copyRowEdit[ i ] = copyRowEdit[ i ] + 1;
													// console.log( copyRowEdit[ i ] + "|" )
												}
											}
											dataStore.insert( 0, r );
											cellEditing.startEdit( 0, 0 );
										}
									}, {
										// itemId :
										// 'removeEmployee',
										text : 'Remove Record',
										iconCls : 'rec-remove',
										disabled : !myData.tableInfo.deleteRow,
										handler : function () {
											var sm = grid.getSelectionModel();
											cellEditing.cancelEdit();
											dataStore.remove( sm.getSelection() );
											if ( dataStore.getCount() > 0 ) {
												sm.select( 0 );
											}
										}
									}, {
										// itemId :
										// 'removeEmployee',
										text : 'Download',
										xtype : 'splitbutton',
										iconCls : 'rec-download',
										disabled : !myData.tableInfo.download,
										menu : new Ext.menu.Menu( {
											items : [
													// these items will render as dropdown menu items when the arrow is clicked:
													{
														text : 'Current Page (CSV)',
														handler : downloadCsv
													}, {
														text : 'Current Page (XLS)',
														handler : downloadXls
													}, {
														text : 'All',
														handler : downloadAll
													}
											]
										} )
									// handler : downloadAll
									}, {
										// itemId :
										// 'removeEmployee',
										text : 'Upload',
										xtype : 'button',
										iconCls : 'rec-upload',
										disabled : ( !myData.tableInfo.upload || pivotGrid ),
										handler : uploadFile
									}
							]
						}
					],
					title : "",
					height : ( Ext.isIE7 ? ( ( Ext.getBody().getViewSize().height - 120 ) ) : ( ( Ext.getBody().getViewSize().height - 126 ) ) ),
					autoScroll : true,
					// frame : true,
					resizable : true,
					layout : 'fit',
					border : false,
					style : 'border: solid #BDBDBD 1px',
					store : dataStore,
					selType : 'cellmodel',
					plugins : [
							'gridfilters', cellEditing
					],
					stateful : true,
					// stateId : 'stateful-filter-grid',
					stateEvents : [
							'columnresize', 'columnmove', 'headermousedown', 'show', 'hide'
					],
					hide : function ( ct, column, eOpts ) {
						// this.saveState;
					},
					show : function ( ct, column, eOpts ) {
						// this.saveState;
					},// stateId :
					// 'stateful-filter-grid',
					tbar : [],
					bbar : vbbar,
					columns : columns
				} );
				dataStore.load();
				/*
				 * if(getCookie("DMW_TABLE_"+table) != ""){ dataStore.load( ); dataStore.filter('FACILITY', 'EPS'); }else{ dataStore.load(); dataStore.filter('FACILITY', 'EPS'); //dataStore.filter( {property: 'FACILITY', value: 'EPS', operator:'like',active: true} ); //grid.filters.addFilter(
				 * {dataIndex: 'FACILITY', value: 'EPS', operator:'like',active: true} ); //grid.filters.clearFilters();
				 * 
				 * dataStore.filter( {property: 'FACILITY', value: 'EPS', operator:'like'} );
				 * 
				 * grid.filters.clearFilters(); grid.filters.addFilters( [{dataIndex: 'FLAG', value: 'MAX', operator:'like',active: true} , {dataIndex: 'FACILITY', value: 'EPS', operator:'like',active: true}, {dataIndex: 'WORK_WEEK', value: '2016', operator:'like',active: false}]); var filterData =
				 * grid.filters.getFilter(); Ext.Array.each(filterData, function(filter){ grid.filters.filters.getByKey(filter.field).setValue(filter.data.value) }) //grid.filters.reload(); }
				 */
				// dataStore.pageSize = myData.pageCount;
				// dataStore.clearFilter();
				// grid.setLoading(true);
				grid.on( 'beforeedit', function ( editor, e ) {
					// console.log( e.colIdx);
					for ( i = 0; i < rowEdit.length; i++ ) {
						var tr = grid.getView().getNode( rowEdit[ i ] );
						el = Ext.get( tr ).select( 'td' );
						el.addCls( 'yellow-back' );
					}
					for ( i = 0; i < copyRowEdit.length; i++ ) {
						var tr = grid.getView().getNode( copyRowEdit[ i ] );
						el = Ext.get( tr ).select( 'td' );
						el.addCls( 'red-back' );
					}
					if ( !( rowEdit.contains( e.rowIdx ) || copyRowEdit.contains( e.rowIdx ) ) && ( !columnEdit.contains( e.colIdx ) ) ) {
						return false;// this will disable the cell editing
					} else {
						return true;

					}
				} );
				grid.on( 'filterchange', function ( store, GridFilter, filterParam ) {
					// console.log( GridFilter[0]._property);
					// console.log( GridFilter[0]._value );
					// console.log( filterParam );
					for ( var k = 0; k < GridFilter.length; k++ ) {
						if ( "undefined" != GridFilter[ k ]._property )
							setCookie( Ext.getCmp( "widget-id" ).title + "-" + GridFilter[ k ]._property, GridFilter[ k ]._value, -1 );
					}
					// getCookie( Ext.getCmp( "widget-id" ).title + "-" + fields[ i ] )
				} );
			}

		}
	} );

}
Array.prototype.contains = function ( obj ) {
	var i = this.length;
	while ( i-- ) {
		if ( this[ i ] == obj ) {
			return true;
		}
	}
	return false;
}
function downloadCsv () {
	Ext.getBody().mask( 'Loading...' );
	window.open( '/DMW/api/downloadCsv' );
	Ext.getBody().unmask();
}
function downloadXls () {
	Ext.getBody().mask( 'Loading...' );
	window.open( '/DMW/api/downloadXls' );
	Ext.getBody().unmask();
}

function downloadAll () {
	Ext.getBody().mask( 'Loading...' );
	window.open( '/DMW/api/downloadAll' );
	/*
	 * Ext.Ajax.request( { url : '/DMW/api/downloadAll', method : 'GET', success : function ( response ) { console.log(response); var defaults = response.responseText; console.log(defaults); if ( defaults != null ) { Ext.DomHelper.append(Ext.getBody(), { tag: 'iframe', frameBorder: 0, width: 0,
	 * height: 0, css: 'display:none;visibility:hidden;height:0px;', src: response.downloadUrl }); Ext.getBody().unmask(); } else { Ext.getBody().unmask(); } } } );
	 */
	Ext.getBody().unmask();
}
function closeUploadDialog () {
	// alert();
	uploadWindo.hide();
}

function uploadFile () {
	if ( Ext.getCmp( "form-file" ) != null ) {
		Ext.getCmp( "form-file" ).destroy();
	}
	uploadWindo = Ext.create( 'Ext.window.Window', {
		layout : {
			type : 'vbox',
			align : 'center'
		},
		height : 100,
		width : 300,
		title : "Upload File",
		model : true,
		autoShow : true,
		listeners : {
		// close : closeUploadDialog
		},
		items : [
				{
					xtype : 'tbfill'
				}, {
					xtype : 'form',
					layout : 'fit',
					items : [
							{
								xtype : 'tbfill'
							}, {
								xtype : 'filefield',
								id : 'form-file',
								emptyText : 'Select a file',
								/* fieldLabel : 'File', */
								name : 'file',
								width : 270,
								buttonText : 'Browse'/*
														 * , buttonOnly : true
														 */
							}
					],
					buttons : [
							{
								text : 'Upload',
								handler : function () {
									/*
									 * console .log(Ext .getCmp('form-file').value);
									 */
									var form = this.up( 'form' ).getForm();
									form.submit( {
										url : '/DMW/api/uploadExcel',
										waitMsg : 'Uploading your file...',
										success : function ( fp, o ) {
											Ext.Msg.alert( "Success", "Successfully uploaded" );
											gblUpload = true;
											uploadWindo.hide();
											dataStore.load( {
												params : {
													upload : "true"
												}
											} );
											dataStore.each( function ( record ) {
												record.beginEdit();
											} );
										},
										failure : function ( fp, o ) {
											Ext.Msg.alert( "Failure", "Failed to upload" );
										}
									} );
								}
							}, {
								text : 'Reset',
								handler : function () {
									this.up( 'form' ).getForm().reset();
								}
							}
					]
				}
		]

	} );

}
function saveDetails () {
	// Ext.getBody().mask( 'Please Wait...' );

	if ( dataStore.getModifiedRecords().length > 0 ) {
		Ext.getBody().mask( 'Please Wait...' );
		console.log( 'store has dirty records' );
	} else if ( dataStore.getRemovedRecords().length > 0 ) {
		Ext.getBody().mask( 'Please Wait...' );
		console.log( 'store has dirty records' );
	}
	dataStore.sync( {
		success : function ( batch, options ) {
			Ext.getBody().unmask();
			Ext.Msg.alert( "Success", "Success" );
		},
		failure : function ( batch, options ) {
			Ext.getBody().unmask();
			Ext.each( batch.exceptions, function ( operation ) {
				if ( operation.hasException() ) {

					Ext.log.warn( 'error message: ' + operation.responseText );
				}
			} );
		},
		callback : function ( batch, options ) {
			Ext.getBody().unmask();
			var operations = batch.operations;
			// console.log(batch.proxy.getReader().jsonData.msg);
			/*
			 * for ( var x in operations) { var operation = operations[x]; if (operation.request) { console.log('operation.request ---> ', operation.request); } if (operation.response) { console.log('operation.response ---> ', operation.response); var object = Ext.decode(
			 * operation.response.responseText, false); console.log('success --->', object.success); } }
			 */
		}
	} );
	if ( gblUpload )
		saveUploadData();
}
function saveTableDetails () {
	// Ext.getBody().mask( 'Obtaining user information...' );
	// console.log( Ext.getCmp( 'fixed-col-id' ) );
	var myMask = new Ext.LoadMask( {
		target : Ext.getCmp( 'window-id' ),
		msg : "Saving ..."
	} );
	var rolesArray = Ext.getCmp( 'role-name-id' ).displayTplData;
	var rolesDetArray = new Array();

	for ( i = 0; i < rolesArray.length; i++ ) {
		var valEditableColumns = '';
		if ( Ext.getCmp( rolesArray[ i ].name + 'itemselector-field' ).value == null ) {
			// alert("sadsadasd");
		} else {
			valEditableColumns = Ext.getCmp( rolesArray[ i ].name + 'itemselector-field' ).value.join();
		}
		rolesDetArray[ i ] = {
			vroleName : rolesArray[ i ].name,
			vroleId : rolesArray[ i ].id,
			vAddRow : Ext.getCmp( rolesArray[ i ].name + '_ADD_ROW' ).rawValue,
			vDelRow : Ext.getCmp( rolesArray[ i ].name + '_DELETE_ROW' ).rawValue,
			vMassUpdate : Ext.getCmp( rolesArray[ i ].name + '_MASS_UPDATE' ).rawValue,
			vDownload : Ext.getCmp( rolesArray[ i ].name + '_DOWNLOAD' ).rawValue,
			vUpload : Ext.getCmp( rolesArray[ i ].name + '_UPLOAD' ).rawValue,
			vEditable : Ext.getCmp( rolesArray[ i ].name + '_EDITABLE' ).rawValue,
			vEditableColumns : valEditableColumns
		};
		// console.log( rolesArray[ i ].name );
	}
	if ( addTableValidation() ) {

		myMask.show();

		Ext.Ajax.request( {
			url : '/DMW/api/saveNewTableInfo',
			method : 'POST',
			params : {
				confId : Ext.getCmp( 'conf-id' ).value,
				owner : Ext.getCmp( 'table-owner-id' ).value,
				alias : Ext.getCmp( 'table-alias-id' ).value,
				tableName : Ext.getCmp( 'table-name-id' ).value,
				schemaName : Ext.getCmp( 'schema-name-id' ).value,
				systemUser : Ext.getCmp( 'system-user-id' ).value,
				systemDate : Ext.getCmp( 'system-date-id' ).value,

				roleName : Ext.getCmp( 'role-name-id' ).value.join(),// since
				// this is
				// array we
				// used to
				// join to
				// send all
				// values
				pivot : Ext.getCmp( 'PIVOT' ).rawValue,
				fixedCol : Ext.getCmp( 'fixed-col-id' ).value.join(),
				pivotCol : Ext.getCmp( 'pivot-col-id' ).value,
				pivotData : Ext.getCmp( 'pivot-data-id' ).value,

				pagination : Ext.getCmp( 'PAGINATION' ).rawValue,
				pageCount : Ext.getCmp( 'pages-id' ).value,
				sortBy : Ext.getCmp( 'sort-by-id' ).value,
				sortDir : Ext.getCmp( 'sort-dir-id' ).value,

				// editable : Ext.getCmp( 'EDITABLE' ).rawValue,
				// editableColumns : Ext.getCmp( 'itemselector-field' ).value.join(),
				additionalRole : {
					array : Ext.encode( rolesDetArray )
				},
				condQuery : Ext.getCmp( 'CONDITIONAL_QUERY' ).rawValue,
				query : Ext.getCmp( 'query-id' ).value
			},
			success : function ( response ) {
				var defaults = Ext.JSON.decode( response.responseText );
				// alert( defaults );
				if ( defaults ) {
					Ext.Msg.alert( 'Success', 'Successfully Saved...' );
					closeDialog()
					myMask.hide();
				} else {
					myMask.hide();
				}
			},
			failure : function ( response ) {
				// console.log( proxy );
				myMask.hide();
				Ext.Msg.alert( 'Exception', response.statusText + "<br><br><a href='#' onclick='viewException()'>Click here view more details</a>" );

			}
		} );
	} else {
		Ext.Msg.alert( "ERROR", "Please enter the above details" );
	}
}
function addTableValidation () {

	var bool = false;
	Ext.getCmp( 'conf-id' ).isValid();
	Ext.getCmp( 'table-owner-id' ).isValid();
	Ext.getCmp( 'table-alias-id' ).isValid();
	Ext.getCmp( 'table-name-id' ).isValid();
	Ext.getCmp( 'schema-name-id' ).isValid();
	Ext.getCmp( 'role-name-id' ).isValid();
	if ( Ext.getCmp( 'conf-id' ).isValid() && Ext.getCmp( 'table-owner-id' ).isValid() && Ext.getCmp( 'table-alias-id' ).isValid() && Ext.getCmp( 'table-name-id' ).isValid() && Ext.getCmp( 'schema-name-id' ).isValid() && Ext.getCmp( 'role-name-id' ).isValid() ) {
		if ( Ext.getCmp( 'PAGINATION' ).rawValue ) {

			Ext.getCmp( 'pages-id' ).isValid();
			Ext.getCmp( 'sort-by-id' ).isValid();
			Ext.getCmp( 'sort-dir-id' ).isValid();
			if ( Ext.getCmp( 'pages-id' ).isValid() && Ext.getCmp( 'sort-by-id' ).isValid() && Ext.getCmp( 'sort-dir-id' ).isValid() ) {
				bool = true;
			} else {
				return false;
			}
		}
		if ( Ext.getCmp( 'PIVOT' ).rawValue ) {

			Ext.getCmp( 'fixed-col-id' ).isValid();
			Ext.getCmp( 'pivot-col-id' ).isValid();
			Ext.getCmp( 'pivot-data-id' ).isValid();
			if ( Ext.getCmp( 'fixed-col-id' ).isValid() && Ext.getCmp( 'pivot-col-id' ).isValid() && Ext.getCmp( 'pivot-data-id' ).isValid() ) {
				bool = true;
			} else {
				return false;
			}
		}

		if ( Ext.getCmp( 'CONDITIONAL_QUERY' ).rawValue ) {
			if ( Ext.getCmp( 'query-id' ).isValid() ) {
				bool = true;
			} else {
				return false;
			}
		} /*
			 * else { return true; }
			 */
		if ( !( Ext.getCmp( 'CONDITIONAL_QUERY' ).rawValue || Ext.getCmp( 'PAGINATION' ).rawValue ) || Ext.getCmp( 'CONDITIONAL_QUERY' ).rawValue ) {
			bool = true;
		}
	} else {
		return false;
	}
	return bool;
}