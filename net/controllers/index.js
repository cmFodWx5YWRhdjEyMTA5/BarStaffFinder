var express = require("express");
var router = express.Router();
var config = require("../config");
var auth = require("../middleware");

// io is the socket object to be injected into the chat router 
module.exports = function(io){
		//Module to handle bar staff entries
		router.use(config.controller.bartender.path, auth, require(config.controller.bartender.require));
		//Module to handle orginizer entries
		router.use(config.controller.organiser.path, auth, require(config.controller.organiser.require));
		//Moudule to handle event adverts entries
		router.use(config.controller.event.path, auth, require(config.controller.event.require));
		//Module to handle the chat service
		router.use(config.controller.socket.path, auth, require(config.controller.socket.require)(io));
		//Module to upload pictures 
		router.use(config.controller.upload.path, /*auth,*/ require(config.controller.upload.require));

		//Module to handle sign up (registration) process
		router.use(config.controller.signup.path, require(config.controller.signup.require));
		//Module to handle the signin (log in) process
		router.use(config.controller.signin.path, require(config.controller.signin.require));
		//Module to handle email validation
		router.use(config.controller.validate.path, require(config.controller.validate.require));

		//Module to handle amin pages
		router.use(config.controller.admin.path, require(config.controller.admin.require));

		return router;
};
