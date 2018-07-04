var express = require("express");
var router = express.Router();
var config = require("../../config");
var helper = require("../../helpers");
var fs = require("fs");
var db = config.database.object;

// Set public path for getting the uploaded images
router.use(express.static("files/images"));

router.param("image_name", function(req, res, next, image_name){
	if(image_name){
		req.image_name = image_name;
	}else{
		return res.status(400).json(helper.res.badRes(400, "Image name is not valid"));
	}
	next();
});

router.get("/:image_name",function(req, res){
	
	helper.utils.LOG("Profile image request [ id: " + req.userid + ", image_name: " + req.image_name + " ]");
	
	res.send("image "+ req.image_name);
});

module.exports = router;
