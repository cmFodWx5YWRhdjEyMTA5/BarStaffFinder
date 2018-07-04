var express = require("express");
var router = express.Router();
var config = require("../../config");
var db = config.database.object;
var helper = require("../../helpers");

router.param("hash", function(req, res, next, hash){
	console.log("hash param middleware: ", hash);
	if(hash){
		req.hash = hash;
	}else{
		next(new Error("validation hash is not valid"));
	}
	next();
});

router.get("/:hash", function(req, res){

	//query the registration table to check if the hash is present 	
	//if is there move the user to the corrisponding table in not
	//return an error message

	var option = {
		root: "files/validate/"
	}

	db.confirmUser(req.hash)
	.then(function(entry){
		helper.utils.LOG("User validated[ hash : " + req.hash + " ]");
		
		res.sendFile("successful.png", option, function(err){
			if(err) res.send("successful");
		});
	})
	.catch(function(err){
		res.sendFile("unsuccessful.png", option, function(err){
			if(err) res.send("unsuccessful");
		});
	});
});

module.exports = router;
