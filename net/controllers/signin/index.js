var express = require("express");
var router = express.Router();
var jwt = require("jsonwebtoken");
var config = require("../../config");
var helper = require("../../helpers");
var db = config.database.object;

// sign in (login)  email and password to be checked
router.post("/", function(req, res){
	let body = req.body;
	if(helper.asserters.assertSignin(body)){ // check for email and password

		console.log(body);
		db.logIn(body[db.constants.EMAIL], body[db.constants.HASH])
		.then(function(entry){
			var token = jwt.sign({"user": entry }, config.appSecret, {
				expiresIn: config.tokenExpires  // expires in 1 hours
			});
			if (!token){
				
				helper.utils.LOG("Sign-In: " + " !token " + "[INTERNAL ERROR]");
				
				return res.status(500).json(helper.res.badRes(500, "Internal server error"));
			}

			if(entry[db.constants.ACCOUNT_TYPE] == "ORGANISER" || entry[db.constants.ACCOUNT_TYPE] == "ADMIN"){
				console.log(entry);
				db.organiserSignin(entry[db.constants.USER_ID])
				/*.then(function(entries){
					
					let toRet = {"message": "successful signin", "token": token, "user": entry};
					
					if(entry[db.constants.ACTIVE] === '0') return res.json(toRet);
					
					else toRet['organiser_signin'] = entries;
					
					return res.json(toRet);
				})
				.catch(function(errMsg){
					return res.status(400).json(helper.res.goodRes(400, errMsg));
				});*/
				//db.getAllBarstaff(entry[db.constants.USER_ID])
				.then(function(entries){
					return res.json({"message": "successful signin", "token": token, "user": entry, "signin_json": entries});
				})
				.catch(function(errMsg){
					
					helper.utils.LOG("Sign-In ERROR {ORGANISER}: " + errMsg);
					
					return res.status(400).json(helper.res.goodRes(400, errMsg));
				});
			}else if(entry[db.constants.ACCOUNT_TYPE] == "BARSTAFF"){
				console.log(entry);
				db.barStaffSignin(entry[db.constants.USER_ID])
				/*.then(function(entries){
					
					let toRet = {"message": "successful signin", "token": token, "user": entry};
					
					if(entry[db.constants.ACTIVE] === '0') return res.json(toRet);
					
					else toRet['bar_staff_signin'] = entries;
					
					return res.json(toRet);
				})
				.catch(function(errMsg){
					return res.status(400).json(helper.res.goodRes(400, errMsg));
				});*/
				//db.getJobs(entry[db.constants.USER_ID])
				.then(function(entries){
					return res.json({"message": "successful signin", "token": token, "user": entry, "signin_json": entries});
				})
				.catch(function(errMsg){
					
					helper.utils.LOG("Sign-In ERROR {BARSTAFF}: " + errMsg);
					
					return res.status(400).json(helper.res.badRes(400, errMsg));
				});
				
			}else{
				
				helper.utils.LOG("Sign-In {ADMIN}: " + body[db.constants.EMAIL]);
				
				return res.status(400).json(helper.res.badRes(400, "Invalid sign in type"));
			}
		})
		.catch(function(err){
			
			helper.utils.LOG("Sign-In ERROR {INVALID LOGIN}: " + body[db.constants.EMAIL]);
			
			return res.status(401).json(helper.res.badRes(401, "Email and/or password are incorret"));
		});
	}else{
		
		helper.utils.LOG("Sign-In ERROR {INVALID INPUT}: " + body[db.constants.EMAIL]);
		
		return res.status(401).json(helper.res.badRes(401, "Email and/or password  were nor provided"));
	}
});

module.exports = router;
