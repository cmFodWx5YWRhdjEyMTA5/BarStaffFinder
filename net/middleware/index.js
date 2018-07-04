var express = require("express");
var router = express.Router();
var jwt = require("jsonwebtoken");
var config = require("../config");
var helper = require("../helpers");
var db = config.database.object;

router.use(function(req, res, next){
	console.log("middleware:");
	console.log("body: ",req.body);
	console.log("query: ",req.query);
	console.log("headers: ", req.headers);
	console.log("method: ", req.method);
	console.log("url: ", req.url);

	var token;

	switch(req.method){
		case "POST":
			token = req.body.token;
		break;
		case "GET": 
			token = req.query.token;
		break;
		case "PUT":
			token = req.body.token;
		break;
		case "DELETE":
			token = req.body.token;
		break;
		default:
			token = req.headers["x-access-token"];
		break;
	}

	if(token){
		jwt.verify(token, config.appSecret, function(err, decoded){
			if(err){
				if(err.message == "jwt expired" && err.name == "TokenExpiredError"){
					console.log("token expired:");
					console.log(err);
				}
				return res.status(401).json(helper.res.badRes(401, "Failed to authenticate token"));
			}else{
				console.log("decoded ", decoded);
				req.decoded = decoded;
				if(req.url.includes("complete_profile")){
					// Route them in the complete_profile to allow active to be set to 1
					next();
				}
				else if(decoded.user[db.constants.ACTIVE] === 0){ // Check if user has complete the profile
					return res.status(401).json(helper.res.badRes(401, "Please complete your profile"));
				}else{
					next();
				}
			}
		});
	}else{
		return res.status(401).json(helper.res.badRes(401, "Token not provided"));
	}
});

module.exports = router;
