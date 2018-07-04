var express = require("express");
var router = express.Router();
var config = require("../../config");
var helper = require("../../helpers");
var db = config.database.object;


/*------------ Start of param middleware ---------------*/

// Event id middleware 
router.param("eventid", function(req, res, next, eventid){
	console.log("param middleware eventid:", eventid);
	if(eventid){
		req.eventid = eventid;
	}else{
		return res.status(400).json(helper.res.badRes(400, "No jobs/events is not valid"));
	}
	next();
});

// Organiser id middleware
router.param("organiserid", function(req, res, next, organiserid){
	console.log("param middleware organiserid:", organiserid);
	if(organiserid){
		req.organiserid = organiserid;
	}else{
		return res.status(400).json(helper.res.badRes(400, "Organiser id is not valid"));
	}
	next();
});

// Bartender id middleware
router.param("bartenderid", function(req, res, next, bartenderid){
	console.log("param middleware bartenderid:", bartenderid);
	if(bartenderid){
		req.bartenderid = bartenderid;
	}else{
		return res.status(400).json(helper.res.badRes(400, "Bartender id is not valid"));
	}
	next();
});

/*------------ End of param middleware ---------------*/

/*------------ Start of general events functions ---------------*/

// fetch events from database
router.get("/", function(req, res){
	console.log(req);
	db.getJobs(req.decoded.user[db.constants.USER_ID])
	.then(function(entries){
		return res.json(helper.res.goodRes(entries,"job_list"));
	})
	.catch(function(err){
		console.log(errMsg);
		return res.status(400).json(helper.res.badRes(400, errMsg));
	});
})

/*------------ End of general events functions ---------------*/

/*------------ Start of organiser/jobs funtionalities ---------------*/

//Create job for a specific organiser
router.post("/", function(req, res){

	if(req.body[db.constants.USER_ID] != req.decoded.user[db.constants.USER_ID]){
		return res.status(401).json(helper.res.badRes(401, "unauthorized"));
	}

	console.log(req.body);
	if(helper.asserters.assertJobCreation(req.body)){
		console.log(req.body);
		console.log("create new publib job");
		db.createPublicJob(req.body)
		.then(function(succMsg){
			var orgID = req.body[db.constants.USER_ID];
			config.socket.sockets.in('BARSTAFF').emit('jobCreated', 
				{  orgID: succMsg[1], "message" : "user joined chat" });
			return res.json(helper.res.goodRes(succMsg[0]));
		})
		.catch(function(errMsg){
			return res.status(400).json(helper.res.badRes(400, errMsg));
		});
	}else{
		return res.status(400).json(helper.res.badRes(400, "Invalid job creation"));
	}
});

//Create private job for a specific organiser
router.post("/:bartenderid([0-9]+)", function(req, res){
	console.log("create new publib job");

	if(req.body[db.constants.USER_ID] != req.decoded.user[db.constants.USER_ID]){
		return res.status(401).json(helper.res.badRes(401, "unauthorized"));
	}

	if(helper.asserters.assertJobCreation(req.body)){
	//if(true){
		console.log(req.bartenderid + " bartenderid");
		db.createPrivateJob(req.bartenderid, req.body)
		.then(function(succMsg){
			return res.json(helper.res.goodRes(succMsg));
		})
		.catch(function(errMsg){
			return res.status(400).json(helper.res.badRes(400, errMsg));
		});
	}else{
		return res.status(400).json(helper.res.badRes(400, "Invalid job creation"));
	}
});

//Fetch all events of an organiser 
router.get("/:organiserid([0-9]+)", function(req, res){
	console.log("get all event for an organisers");

	if(req.organiserid != req.decoded.user[db.constants.USER_ID]){
		return res.status(401).json(helper.res.badRes(401, "unauthorized"));
	}

	db.getAllOrganiserPublicJobs(req.organiserid)
	 .then(function(entries){
		return res.json(helper.res.goodRes(entries, "job_list"));
	})
	.catch(function(errMsg){
		return res.status(400).json(helper.res.badRes(400, errMsg));
	});
});

// Update an event of a user
router.put("/modify/:organiserid([0-9]+)/:eventid([0-9]+)/", function(req, res){
	console.log("update event");
	if(req.organiserid != req.decoded.user[db.constants.USER_ID]){
		return res.status(401).json(helper.res.badRes(401, "unauthorized"));
	}
	
	/*if(helper.checkKeys(req.body)){
		db.updateJob(req.eventid, req.body)
		.then(function(result){
			return res.json({"message" : "Job successfully updated"});
		})
		.catch(function(err){
			return res.status(400).json({
					"error_code" : 400,
					"error_message" : "Job failed to update"
					});
		});
	}else{
		return res.status(400).json({
				"error_code" : 400,
				"error_message" : "Updates were not valid"
				});
	}*/
});

// Delete an event of a user 
router.put("/remove/:organiserid([0-9]+)/:eventid([0-9]+)", function(req, res){
	console.log("delete event");

	if(req.organiserid != req.decoded.user[db.constants.USER_ID]){
		return res.status(401).json(helper.res.badRes(401, "unauthorized"));
	}

	db.deletePublicJob(req.eventid, req.organiserid)
	.then(function(succMsg){
		return res.json(helper.res.goodRes(succMsg));
	})
	.catch(function(errMsg){
		console.log(errMsg);
		return res.status(400).json(helper.res.badRes(400, errMsg));
	});
});

/*------------ End of organiser funtionalities ---------------*/

function notifyUserOfEvent(from_user_id, to_user_id, event_ID, eventToSend, additional_data){
	
	let io = config.socket;
	let room = getRoom(data.to_user_id);
	let isRoom = io.sockets.adapter.rooms[room];
		
	if(isRoom){
		console.log("notify ",to_user_id, " user ", from_user_id, " data: ", additional_data);
		io.sockets.in(room).emit(eventToSend, { [db.constants.USER_ID] : from_user_id, [db.constants.JOB_ID] : event_ID, "data" : additional_data} ) ;
	}else{
		io.sockets.in(getRoom(from_user_id)).emit(eventToSend, { "message": ("could not notify of event, " + eventToSend) } );
		console.log("user ", to_user_id, " user does not excist", " for event: ", eventToSend, " from: ", from_user_id);
	}
}

function getRoom(id){
	return "/"+id;
}

module.exports = router;

module.exports = router;
