var express = require("express");
var router = express.Router();
var helper = require("../../helpers");
var config = require("../../config");
var db = config.database.object;

/* ---------------------- Start user middleware ------------------------*/
//Middleware for userid
router.param("userid", function(req, res, next, userid){
	console.log("middleware for organiser ", userid);
	if(userid) req.userid = userid;
	else next(new Error("user id not valid"));
	next();
});

//Middleware for events 
router.param("eventid", function(req, res, next, eventid){
	console.log("middleware for organiser ", eventid);
	if(eventid) req.eventid = eventid;
	else next(new Error("user id not valid"));
	next();
});

/* ---------------------- End user middleware ------------------------*/

/* ---------------------- Start user functions ------------------------*/

//Get all applicants for a specific organiser
router.get("/applicants/:userid([0-9]+)", function(req, res){

	if(req.userid != req.decoded.user[db.constants.USER_ID]){
		return res.status(401).json(helper.res.badRes(401, "unauthorized"));
	}

	db.getAllApplicants(req.userid)
	.then(function(entries){
		return res.json(helper.res.goodRes(entries, "applicants_list"));
	})
	.catch(function(errMsg){
		return res.status(400).json(helper.res.goodRes(400, errMsg));
	});
});

//Get all private job request sent by a specific organiser
router.get("/request/:userid([0-9]+)", function(req, res){

	if(req.userid != req.decoded.user[db.constants.USER_ID]){
		return res.status(401).json(helper.res.badRes(401, "unauthorized"));
	}

	db.getAllPrivateJobsSent(req.userid)
	.then(function(entries){
		return res.json(helper.res.goodRes(entries, "private_request_list"));
	})
	.catch(function(errMsg){
		return res.status(400).json(helper.res.goodRes(400, errMsg));
	});
});

//Delete private job request sent by a specific organiser
router.delete("/request/:userid([0-9]+)", function(req, res){

	if(req.userid != req.decoded.user[db.constants.USER_ID]){
		return res.status(401).json(helper.res.badRes(401, "unauthorized"));
	}

	db.deletePrivateJobSent(req.userid)
	.then(function(succMsg){
		return res.json(helper.res.goodRes(succMsg));
	})
	.catch(function(errMsg){
		return res.status(400).json(helper.res.goodRes(400, errMsg));
	});
});

router.post("/assign/:userid([0-9]+)", function(req, res){

	if(req.userid != req.decoded.user[db.constants.USER_ID]){
		return res.status(401).json(helper.res.badRes(401, "unauthorized"));
	}
	
	if(helper.asserters.assertApplicantResponse(req.body)){
		db.assignPrivateJob(req.userid, req.body[db.constants.STAFF_ID], req.body[db.constants.JOB_ID])
		.then(function(succMsg){
			
			//notify user they have recieved a job
			helper.notification.notifyBarstaffOfEvent(req.body[db.constants.STAFF_ID], req.userid, "recievedPrivateJob", 
				{ [db.constants.STAFF_ID] : req.body[db.constants.STAFF_ID], "message" : "Recieved private job from " + 
				req.body[db.constants.STAFF_ID] } );
			return res.json(helper.res.goodRes(succMsg));
		})
		.catch(function(errMsg){
			console.log(errMsg);
			return res.status(400).json(helper.res.badRes(400, errMsg));
		});
	}else{
		return res.status(400).json(helper.res.badRes(400, "Invalid application could not be sent"));
	}
});

// Accept applicant (accept job request)
router.put("/response_accept/:userid([0-9]+)", function(req, res){
	
	if(req.userid != req.decoded.user[db.constants.USER_ID]){
		return res.status(401).json(helper.res.badRes(401, "unauthorized"));
	}

	if(helper.asserters.assertApplicantResponse(req.body)){
		db.acceptApplicant(req.userid, req.body[db.constants.STAFF_ID], req.body[db.constants.JOB_ID])
		.then(function(succMsg){
			
			helper.notification.notifyUserOfEvent( req.body[db.constants.STAFF_ID], req.body[db.constants.STAFF_ID], "jobAccepted", 
				{ "message" : ("accepted job " + req.body[db.constants.JOB_ID] + " from " + req.body[db.constants.STAFF_ID] ) } );
			return res.json(helper.res.goodRes(succMsg));
		})
		.catch(function(errMsg){
			console.log(errMsg);
			return res.status(400).json(helper.res.badRes(400, errMsg));
		});
	}else{
		return res.status(400).json(helper.res.badRes(400, "Invalid application response"));
	}
});

// Complete profile for the first time
router.put("/complete_profile/:userid([0-9]+)", function(req, res){
	
	if(req.userid != req.decoded.user[db.constants.USER_ID]){
		return res.status(401).json(helper.res.badRes(401, "unauthorized"));
	}

	console.log(req.body);
	if(helper.asserters.isOrganiserProfileComplete(req.body)){

		helper.utils.LOG('Inside else case');
		var objs = helper.utils.setOrganiserObj(req.body);
		db.completeOrganiserRegistration(req.userid, objs[0], objs[1])
		.then(function(result){
			helper.notification.notifyUserOfEvent( req.userid, req.userid, "profileChanged", 
				{ "message" : "updated profile for: " + req.userid  } ); 
			helper.utils.LOG('onComplete registation succes');
			return res.json(helper.res.goodRes('Resistering...'));
		})
		.catch(function(err){
			//Delete image in case the image gets uploaded but the update
			//fails right after
			//helper.utils.deletePicture(fs, helper.utils.getPicturePath(imageName));
			//fs.unlinkSync(helper.utils.getPicturePath(imageName));
			return res.status(400).json(helper.res.badRes(400, "Account could not be completed"));
		});

	}else{
		return res.status(400).json(helper.res.badRes(400, "Invalid application response"));
	}
});

// Decline job request
router.put("/response_reject/:userid([0-9]+)", function(req, res){

	if(req.userid != req.decoded.user[db.constants.USER_ID]){
		return res.status(401).json(helper.res.badRes(401, "unauthorized"));
	}

	if(helper.asserters.assertApplicantResponse(req.body)){
		db.rejectApplicant(req.userid, req.body[db.constants.STAFF_ID], req.body[db.constants.JOB_ID])
		.then(function(succMsg){
			
			helper.notification.notifyUserOfEvent(req.body[db.constants.STAFF_ID], req.userid, "jobRejected",
				{ "message" : ("accepted job " + req.body[db.constants.JOB_ID] + " from " + req.body[db.constants.STAFF_ID] ) } );
			return res.json(helper.res.goodRes(succMsg));
		})
		.catch(function(errMsg){
			return res.status(400).json(helper.res.badRes(400, errMsg));
		});
	}else{
		return res.status(400).json(helper.res.badRes(400, "Invalid application response"));
	}
});


// Update a user credential
router.put("/delete/:userid", function(req, res){ 
	console.log("Upadate organiser");
	console.log(req);

	if(req.userid != req.decoded.user[db.constants.USER_ID]){
		return res.status(401).json(helper.res.badRes(401, "unauthorized"));
	
	}else{
		
		helper.notification.notifyUserOfEvent(req.body[db.constants.STAFF_ID], req.body[db.constants.STAFF_ID], "profileChanged",
				{ "message" : ("updateProfile for " + req.body[db.constants.STAFF_ID] ) } );
	}
});

// Delete organiser event
router.put("/delete/event/:eventid([0-9]+)", function(req, res){

	console.log("Delete organiser event");
	
	let SQL = "DELETE FROM JOBS WHERE " + (db.constants.JOB_ID) + " = '"+eventid+"';"
	
	console.log(sql);
	
	db.rawSql(SQL);
	
	console.log(req);
	
	let reason = req.body.reason;
	
	if(reason != undefined){
	
		helper.notification.notifyUserOfEvent(req.body[db.constants.STAFF_ID], req.body[db.constants.STAFF_ID], "jobDeleted",
			{ "message" : ("updateProfile for " + req.body[db.constants.STAFF_ID] ), "reason" : reason } );
	}

});

// Delete usr account
router.put("/delete/user/:userid([0-9]+)", function(req, res){

	console.log("Delete organiser event");
	
	let SQL = "DELETE FROM USER WHERE " + (db.constants.JOB_ID) + " = '"+req.params.userid+"';"
	
	console.log(sql);
	
	db.rawSql(SQL);
	
	console.log(req);

});
/* ---------------------- End user functions ------------------------*/


module.exports = router;
