var gblUser = "";
Ext.define( 'MyApp.controller.MyController', {
	extend : 'Ext.app.Controller',
	refs : [
		{
			ref : 'aggregateChartPanelOuter',
			selector : '#aggregateChartPanelOuter'
		}
	],
	onLaunch : function () {
		Ext.ns( 'My.Application.Globals' );
		Ext.getBody().mask( 'Obtaining user information...' );
		var gbltable = 1;
		Ext.Ajax.request( {
			url : '/DMW/api/getUser',
			method : 'GET',
			timeout : 3000000,
			success : function ( response ) {
				var defaults = Ext.JSON.decode( response.responseText );
				if ( defaults != null ) {
					Ext.getCmp( 'user-id-display' ).setText( '<b>' + defaults.firstName + " " + defaults.lastName + '</b> (' + defaults.userId + ')' );
					isAdmin = defaults.admin;
					Ext.getCmp( 'add-id-display' ).setDisabled( !isAdmin );
					gblUser = defaults.userId;
					Ext.getBody().unmask();
				} else {
					Ext.getBody().unmask();
				}
			}
		} );
	}
} );

function saveUploadData () {
	Ext.getBody().mask( 'Please Wait...' );
	Ext.Ajax.request( {
		url : '/DMW/api/validateVersion',
		method : 'GET',
		timeout : 3000000,
		success : function ( response ) {
			var defaults = Ext.JSON.decode( response.responseText );
			if ( defaults ) {
				Ext.Ajax.request( {
					url : '/DMW/api/saveUploadData',
					method : 'GET',
					timeout : 3000000,
					success : function ( response ) {
						var defaults = Ext.JSON.decode( response.responseText );
						if ( defaults ) {
							Ext.Msg.alert( "Success", "Saved Successfully" );
							Ext.getBody().unmask();
							gblUpload = false;
							dataStore.load();
						} else {
							Ext.Msg.alert( "Failure", "Failed to Save" );
							Ext.getBody().unmask();
						}
					}
				} );
			} else {
				Ext.Msg.alert( "Failed", "Failed to Save as this table has recently been changed by another user." );
				Ext.getBody().unmask();
			}
		}
	} );
}
function viewException () {
	Ext.Ajax.request( {
		url : '/DMW/api/viewException',
		method : 'GET',
		success : function ( response ) {
			console.log( response );
			var defaults = Ext.JSON.decode( response.responseText );
			if ( defaults != null ) {
				Ext.Msg.alert( defaults.shortMessage, defaults.fullMessage );
			} else {
			}
		}
	} );
}

function saveFavourites () {
	Ext.getBody().mask( 'Please Wait...' );
	Ext.Ajax.request( {
		url : '/DMW/api/saveFavourites',
		method : 'POST',
		success : function ( response ) {
			var defaults = response.responseText;
			if ( defaults == "success" ) {
				Ext.Msg.alert( "Success", "Successfully saved your favourites" );
			} else if ( defaults == "empty" ) {
				Ext.Msg.alert( "Failure", "Please select correct filters" );
			} else {
				Ext.Msg.alert( "Failure", "Failed to save your favourites <br> Please contact SCM web team for more details." );
			}
			Ext.getBody().unmask();
		}
	} );
}