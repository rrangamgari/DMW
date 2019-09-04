Ext.require([ 'Ext.direct.*', 'Ext.data.*', 'Ext.grid.*', 'Ext.util.Format',
		'Ext.tree.*', 'Ext.ux.form.*' ]);
Ext.application({
	name : 'Ext5',
	appFolder : '/app',
	//requires: ['app.view.Viewport' ],
	paths : {
		Ext : './extjs'
	},
	enableQuickTips : true,
	launch : function() {

		// Ext.MessageBox.alert("Welcome",'Launching Ext JS 5 app');
		// console.log('Launching Ext JS 6 app');
		new MyViewport({
			renderTo : Ext.getBody()
		}).show();
		// Ext.getBody().mask('Obtaining user information...');

	}
});

// Header for the UI
var MyViewport = Ext
		.extend(
				Ext.Viewport,
				{
					layout : 'border',
					initComponent : function() {
						this.items = [
								{
									xtype : 'toolbar',
									region : 'north',
									id : 'main-tb',
									cls : 'main_tb',
									// height : 60,
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
												text : '<b style="font-family: Calibri,Verdana, Helvetica, Arial, sans-serif; font-size: 20pt; color:#01B2AA; font-weight: bold;">ABB UI </b>',
												xtype : 'tbtext'
											},
											{
												xtype : 'tbfill'
											},
											{
												xtype : 'tbtext',
												text : 'Welcome, '
											},
											{
												xtype : 'tbtext',
												id : 'user-id-display',
												text : '<b>Anonymous</b>'
											},
											{
												xtype : 'tbseparator'
											},
											{
												text : 'Feedback',
												cls : 'download',
												// iconCls : 'email_edit',
												handler : function() {
													window.location.href = "mailto:planningsystems.helpdesk@maximintegrated.com?cc=ravinder.rangamgari@maximintegrated.com,mukesh.joshi@maximintegrated.com&subject=[eMRB Reports]";
												}
											},
											{
												xtype : 'tbseparator'
											},
											{
												text : 'Logout',
												cls : 'download',
												handler : function() {
													window.location.href = '/eMRBReports/logout.jsp';
												}
											}, {
												xtype : 'tbseparator'
											}, {
												xtype : 'tbtext',
												id : 'version-txt',
												text : 'v 1.0'
											} ]
								},
								{
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
												collapsed : false,
												collapsible : true,
												collapseDirection : Ext.Component.DIRECTION_LEFT,
												useArrows : true,
												rootVisible : false,
												store : store,
												width : '20%',
												listeners : {
													itemclick : function(view,
															node) {
														alert(node.data.text);
														var parentNode = node.parentNode;
														if (parentNode) {
															alert(parentNode
																	.get('text'));
														}
													}
												}
											},
											{
												xtype : 'splitter',
												width : '0.5px',
											},
											{
												xtype : 'panel',
												flex : 1,
												title : 'Edit Tables ',
												collapsed : false,
												collapsible : false,
												// collapseDirection :
												// Ext.Component.DIRECTION_RIGHT,
												width : '79%',
												html : "Kcxzcxzcxzcxzzcxzcxzcxzcxzcext"
											} ],
									listeners : {
										'tabchange' : function(tabPanel, tab) {
											setCookie("emrb-tabs", tab.id, -1);
										}
									}
								} ];
						MyViewport.superclass.initComponent.call(this);
					}
				});
var store = Ext.create('Ext.data.TreeStore', {
	root : {
		expanded : true,
		children : [ {
			text : "I2WEB",
			leaf : false,
			expanded : false,
			children : [ {
				text : "app_user_profile",
				leaf : true
			}, {
				text : "app_user_access",
				leaf : true
			}, {
				text : "app_user_access_sso",
				leaf : true
			} ]
		}, {
			text : "CPAPP",
			leaf : false
		}, {
			text : "MRBDATA",
			leaf : false
		}, {
			text : "CPDATA",
			leaf : false
		}, {
			text : "I2WEBDATA",
			leaf : false
		}, {
			text : "dfuser",
			leaf : false
		} ]
	}
});