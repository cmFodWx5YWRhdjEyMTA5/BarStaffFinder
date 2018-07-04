let db = require('../db')

module.exports = {
	"appName": "/myLocalBartender",
	"appSecret": "cavaliers",
	"host": "https://localhost",
	"port": "1133",
	"stripe" : {
		"token" : "",
		"id" : "",
		"object" : null
	},
	"controller": {
		"bartender": {
			"path": "/bartender",
			"require": "./bartender"
		},
		"organiser": {
			"path": "/organiser",
			"require": "./organiser"
		},
		"signin": {
			"path": "/signin",
			"require": "./signin"
		},
		"signup": {
			"path": "/signup",
			"require": "./signup"
		},
		"validate": {
			"path": "/validate",
			"require": "./validate"
		},
		"event": {
			"path": "/event",
			"require": "./event"
		},
		"socket": {
			"path": "/socket",
			"require": "./socket"
		},
		"upload": {
			"path": "/upload",
			"require": "./upload"
		},
		"admin": {
			"path": "/admin",
			"require": "./admin"
		}
	},
	"database":{
		"name": "MyLocalBartender",
		"host": "localhost",
		"username": "mlbtest",
		"password": "mlbdb",
		"path": "./db",
		"object" : new db()
	},
	"email":{
		"address": "mylocalbartendertest@gmail.com",
		"password": "MyLocalBartender",
		"service": "gmail"
	},
	"account":{
		"email": "root@admin.com",
		"hash": "test",
		"id" : 0
	},
	"socket": null,
	"tokenExpires": "24h"
}
