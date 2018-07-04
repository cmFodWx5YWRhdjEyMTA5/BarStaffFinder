var express = require("express");
var express = require("express");
var router = express.Router();
var config = require("../../config");
var helper = require("../../helpers");
var fs = require("fs");
var db = config.database.object;

// Middleware for user id parameter
router.param("userid", function(req, res, next, userid){
	console.log("param middleware :", userid);
	if(userid){
		req.userid = userid;
	}else{
		return res.status(400).json(helper.res.goodRes(400, "User id not valid"));
	}
	next();
});

// Middleware for event id parameter
router.param("eventid", function(req, res, next, eventid){
	console.log("param middleware :", eventid);
	if(eventid){
		req.eventid = eventid;
	}else{
		return res.status(400).json(helper.res.goodRes(400, "Event id not valid"));
	}
	next();
});

// fetch all bar staff profiles
// [firstname+lastinitial]
// [profilepicture]
// [hourly rate]
// [night rate]
// [speciality]

// Fetch users from database
router.get("/", function(req, res){
	db.getAllBarstaff(req.decoded.user[db.constants.USER_ID])
	.then(function(entries){
		return res.json(helper.res.goodRes(entries, "bar_staff_list"));
	})
	.catch(function(errMsg){
		return res.status(400).json(helper.res.goodRes(400, errMsg));
	});
})

//get barstaff availability
router.get("/availability/:userid([0-9]+)", function(req, res){
	console.log("availability");

	db.getAvailability(req.userid)
	.then(function(entry){
		
		return res.json(helper.res.goodRes(entry, "availability"));
	})
	.catch(function(err){
		return res.status(400).json(helper.res.goodRes(400, "Bartender availability not found"));
	});

});

//fetch one user from database
router.get("/:userid([0-9]+)", function(req, res){
	console.log("checking user");

	db.getUser(req.userid)
	.then(function(entry){
		return res.json(helper.res.goodRes(entry, "bar_staff"));
	})
	.catch(function(err){
		return res.status(400).json(helper.res.goodRes(400, "Bartender profile not found"));
	});
});

router.get("/calendar/:userid([0-9]+)", function(req, res){
	// TODO
	res.send("TODO CALENDAR");
});

// Update a user credential first time
router.put("/complete_profile/:userid([0-9]+)", function(req, res){
	
	if(req.userid != req.decoded.user[db.constants.USER_ID]){
		return res.status(401).json(helper.res.badRes(401, "unauthorized"));
	}
	
	if(helper.asserters.isBarstaffProfileComplete(req.body)){
		var imageName = helper.utils.setPictureName(req.userid);
		console.log("named filed ", imageName);
		console.log("path: ", helper.utils.getPicturePath(imageName));
		fs.writeFile(helper.utils.getPicturePath(imageName), new Buffer(req.body[db.constants.IMAGE], "base64"), function(err){
			if(err){
				console.log("picture could not be saved");
				return res.status(400).json(helper.res.badRes(400, "Picture could not be uploaded"));
			}else{
				req.body[db.constants.IMAGE] = helper.utils.createPictureURL(imageName);
				console.log("new prop ", req.body[db.constants.IMAGE]);
				var objs = helper.utils.setBarstaffObj(req.body);
				db.completeBarstaffRegistration(req.userid, objs[0], objs[1])
				.then(function(result){
					helper.notification.notifyUserOfEvent( req.userid, req.userid, "profileChanged", 
						{ "message" : "updated profile for: " + req.userid  } ); 
					helper.utils.LOG('onComplete registation succes');
					var userID = req.userid;
					config.socket.sockets.in('ORGANISER').emit('barStaffJoined', 
									{  userID : obj, "message" : "user joined chat" });
					return res.json(helper.res.goodRes('Registering...'));
				})
				.catch(function(err){
					//Delete image in case the image gets uploaded but the update
					//fails right after
					console.log(err);
					helper.utils.deletePicture(fs, helper.utils.getPicturePath(imageName));
					//fs.unlinkSync(helper.utils.getPicturePath(imageName));
					return res.status(400).json(helper.res.badRes(400, "Account could not be completed"));
				});
			}
		});
	}else{
		helper.utils.LOG('user was unable to complete profile');
		return res.status(400).json(helper.res.badRes(400, "Updates were not valid"));
	}
});

// Update a user credential
router.put("/:userid", function(req, res){
	console.log("update user");
	console.log(req);
	if(req.userid != req.decoded.user[db.constants.USER_ID]){
		
		helper.notification.notifyBarstaffOfEvent( req.userid, req.userid, "profileChanged", 
						{ "message" : ("updated profile for: " + req.userid ) } );
						
		return res.status(401).json(helper.res.badRes(401, "Unauthorized"));
	}

	/*if(helper.asserters.assertBarstaffUpdate(req.body)){
		db.updateProfile(req.userid, req.body)
		.then(function(result){
			return res.json(helper.res.goodRes("Account successfully update"));
		})
		.catch(function(err){
			return res.satus(400).json(helper.res.badRes(400, "Your account failed to update"));
		});
	}else{
		return res.status(400).json(helper.res.badRes(400, "Updates were not valid"));
	}*/
});

// List of all jobs bartender has applied
router.get("/applications/:userid([0-9]+)", function(req, res){
	if(req.userid != req.decoded.user[db.constants.USER_ID]){
		return res.status(401).json(helper.res.badRes(401, "unauthorized"));
	}

	db.getApplications(req.userid)
	.then(function(entries){
			return res.json(helper.res.goodRes(entries, "applications"));
	})
	.catch(function(errMsg){
		return res.status(400).json(helper.res.badRes(400, errMsg));
	});
});

// Bartender apply for a public job
router.post("/applications/:userid([0-9]+)/:eventid([0-9]+)", function(req, res){

	if(req.userid != req.decoded.user[db.constants.USER_ID]){
		return res.status(401).json(helper.res.badRes(401, "unauthorized"));
	}

	console.log("the message ",req.body[db.constants.MESSAGE]);
	// req.body contains the addictional text if the bartender has one
	if(helper.asserters.assertJobApplication(req.body)){
		db.applyJob(req.userid, req.eventid, req.body[db.constants.MESSAGE])
		.then(function(obj){ // index 0 succMsg, index 1 organiser id
		
			var orgId = obj[1].org_ID;
			//Send to the organiser who made this job
			helper.notification.notifyUserOfEvent(req.userid, orgId, req.eventid,
				"jobApplied", { "message" : "user: " + req.userid + "applied to: " + req.eventid } );

			console.log("OK I GOT PAST THE FUNCTION");
			return res.json(helper.res.goodRes(obj[0]));
			
		})
		.catch(function(errMsg){
			return res.status(400).json(helper.res.badRes(400, errMsg));
		});
	}else{
		return res.status(400).json(helper.res.badRes(400, "Invalid job application"));
	}
});

// widthdraw application 
router.put("/applications/:userid([0-9]+)/:eventid([0-9]+)", function(req, res){
	if(req.userid != req.decoded.user[db.constants.USER_ID]){
		
		return res.status(401).json(helper.res.badRes(401, "unauthorized"));
	}

	db.deleteApplication(req.userid, req.eventid)
	.then(function(succMsg){
		
		helper.notification.notifyUserOfEvent( req.userid, "<org_id>", "applicationwidthdraw", 
						{ "message" : ("widthraw : " + req.userid + " event_id: " + req.params.eventid) } );
		
		helper.notification.notifyUserOfEvent( req.userid, req.userid, "applicationwidthdraw", 
						{ "message" : ("widthraw : " + req.userid + " event_id: " + req.params.eventid) } );
		
		return res.json(helper.res.goodRes(succMsg));
	})
	.catch(function(errMsg){
		return res.status(400).json(helper.res.badRes(400, errMsg));
	});
});

// List of all private jobs bartender has received
router.get("/offers/:userid([0-9]+)", function(req, res){

	if(req.userid != req.decoded.user[db.constants.USER_ID]){
		return res.status(401).json(helper.res.badRes(401, "unauthorized"));
	}

	db.getPrivateJobsForBarstaff(req.userid)
	.then(function(entries){
			
		helper.notification.notifyUserOfEvent(req.userid, orgID, req.eventid,
			"acceptPriviteJob", { "message" : ("user: " + req.userid + "accepted invite for: " + req.eventid) } );
			return res.json(helper.res.goodRes(entries, "offers"));
	})
	.catch(function(errMsg){
		console.log(errMsg);
		return res.status(400).json(helper.res.badRes(400, errMsg));
	});
});

// Accept private job request 
router.put("/response_accept/:userid([0-9]+)/:eventid([0-9]+)", function(req, res){
	
	if(req.userid != req.decoded.user[db.constants.USER_ID]){
		return res.status(401).json(helper.res.badRes(401, "unauthorized"));
	}

	console.log(req.userid);
	console.log(req.eventid);
	db.acceptPrivateJob(req.userid, req.eventid)
	.then(function(obj){ // index 0 succMsg , index 1 organiser id
		var orgID = obj[1];
		helper.notification.notifyUserOfEvent(req.userid, orgID, req.eventid,
			"acceptPriviteJob", { "message" : ("user: " + req.userid + "accepted invite for: " + req.eventid) } );
		return res.json(helper.res.goodRes(obj[0]));
	})
	.catch(function(errMsg){
		return res.status(400).json(helper.res.badRes(400, errMsg));
	});
});

// Decline private job request
router.put("/response_reject/:userid([0-9]+)/:eventid([0-9]+)", function(req, res){

	if(req.userid != req.decoded.user[db.constants.USER_ID]){
		return res.status(401).json(helper.res.badRes(401, "unauthorized"));
	}

	db.rejectPrivateJob(req.userid, req.eventid)
	.then(function(obj){ // index 0 succMsg, index 1 organiser id
		
		helper.notification.notifyUserOfEvent(req.userid, obj[1], req.eventid,
			"rejectPriviteJob", { "message" : ("user: " + req.userid + "accepted invite for: " + req.eventid) } );
		return res.json(helper.res.goodRes(obj[0]));
	})
	.catch(function(errMsg){
		return res.status(400).json(helper.res.badRes(400, errMsg));
	});
});

// Delete usr account
router.put("delete/user/:userid([0-9]+)", function(req, res){

	console.log("Delete organiser event");
	
	let SQL = "DELETE FROM USER WHERE " + (db.constants.JOB_ID) + " = '"+req.params.userid+"';"
	
	console.log(sql);
	
	db.rawSql(SQL);
	
	console.log(req);

});

module.exports = router;
