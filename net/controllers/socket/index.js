const express = require("express");
let config = require("../../config");
let helper = require("../../helpers");
let db = config.database.object;
let router = express.Router();

// sets up base file path to get static files for this route
/*router.use(express.static("controllers/chat/lib"), function(req,res,next){
	console.log('sockket url:',req.url);
	next();
});*/

function getUserId(data){
	console.log("data , ", data.nickname);
	return data.nickname.substring(1);
}

function generateMLBRoom(data){
	return "MLB_" + data[db.constants.JOB_ID]+
			data[db.constants.STAFF_ID]+
			data[db.constants.ORG_ID];
}

function socketService(io){
	
	// REMINDER: nickname has user id	
	io.on('connection', function(socket){

		console.log("socket id: "+ socket.id);
		
		console.log("user connected: %s", socket.id);

		socket.on("cardUpdated", function(data){
			console.log(data);
			console.log("pound ", data.nickname);
			var userId  = data.user_ID;
			console.log(userId);
			console.log(data.stripe_token);

			config.stripe.object.customers.create({
					description: 'Customer for ' + userId, 
					source: data.stripe_token
			}, function(err, customer){
				if(err){
					console.log("THE ERROR ", err);
					helper.utils.LOG('"Customer could not be created"' + userId);
					//socket.emit("cardFailed", { "message" : "Something went wrong, please try again later" });
				}else{
					console.log(customer.sources);
					var LAST_4 = customer.sources.data[0].last4;
					var customer_id = customer.id;
					var stripe_token = data.stripe_token;
					console.log('last4: ' + LAST_4);
					console.log('userId: ' + userId);
					console.log("type ", data.type);
					db.updateUserCardDetails(userId, stripe_token, customer_id, LAST_4)
					.then(function(succMsg){
						if(data.type == "ORGANISER"){
							db.organiserSignin(userId)
							.then(function(obj){
								console.log("successful organiser");
								socket.emit("completedProfile", {"message" : "The card was successfully registered", "signin_json": obj});
							})
							.catch(function(errMsg){
								//socket.emit("cardFailed", { "message" : "Something went wrong, please try again later" });
								console.log(errMsg);
								socket.emit("completedProfile", {
											"message" : "The card was not successfully registered",
											"successful": false });
							});
						}else if(data.type == "BARSTAFF"){
							db.barStaffSignin(userId)
							.then(function(obj){
								console.log("successful barstaff");
								//io.sockets.in().emit("barStaffJoined", { "user_ID": data.user_ID, "message": "user " + data.user_ID + " joined" } );
								socket.emit("completedProfile", {"message" : "The card was successfully registered", "signin_json": obj});
							})
							.catch(function(errMsg){
								//socket.emit("cardFailed", { "message" : "Something went wrong, please try again later" });
								console.log(errMsg);
								socket.emit("completedProfile", {
											"message" : "The card was not successfully registered",
											"successful": false });
							});
						}
					})
					.catch(function(errMsg){
						console.log("sql error");
						console.log(errMsg);
					});
				}
			});
		});
	
		socket.on('test', function(data){
			console.log("token ", data.stripe_token);
			var userId  = data.user_ID;

			/*config.stripe.object.customers.create({
					description: 'Customer for ' + userId, 
					source: data.stripe_token
			}, function(err, customer){
				if(err){
					console.log("THE ERROR ", err);
					helper.utils.LOG('"Customer could not be created"' + userId);
					//socket.emit("cardFailed", { "message" : "Something went wrong, please try again later" });
				}else{
					var LAST_4 = customer.sources.data[0].last4;
					var customer_id = customer.id;
					var stripe_token = data.stripe_token;
					console.log('last4: ' + LAST_4);
					console.log('userId: ' + userId);
					console.log("type ", data.type);
					db.updateUserCardDetails(userId, stripe_token, customer_id, LAST_4)
					.then(function(succMsg){
						if(data.type == "ORGANISER"){
							db.organiserSignin(userId)
							.then(function(succMsg){
								console.log(succMsg);
								socket.emit("cardUpdateSuccess", {"message" : "The card was successfully registered" });
							})
							.catch(function(errMsg){
								//socket.emit("cardFailed", { "message" : "Something went wrong, please try again later" });
								console.log(errMsg);
							});
						}else if(data.type == "BARSTAFF"){
							db.barStaffSignin(userId)
							.then(function(succMsg){
								console.log(succMsg);
								console.log("fkdfjkj");
							//io.sockets.in().emit("barStaffJoined", { "user_ID": data.user_ID, "message": "user " + data.user_ID + " joined" } );
								socket.emit("cardUpdateSuccess", {"message" : "The card was successfully registered" });
							})
							.catch(function(errMsg){
								//socket.emit("cardFailed", { "message" : "Something went wrong, please try again later" });
								console.log(errMsg);
							});
						}
					})
					.catch(function(errMsg){
						console.log("sql error");
						console.log(errMsg);
					});
			*/
			console.log('test: ');
			socket.emit('cardUpdated',{"data": "dataLaaea" });
				
		});
			//JOB ID, ORG ID, STAFF ID
		socket.on("barStaffApplied", function(data){
					
			var room = generateMLBRoom(data);
			console.log("ROOM ", room);
			socket.join( room);
		});

		socket.on("initSession", function(data){
			
			//Todo add verification
			let room = generateMLBRoom(data);
			socket.join(room);
			console.log('session tarted: '+ room);
		});

		socket.on("setupConnection", function(data){
			
			socket.nickname = setRoomName(data.user_ID);

			console.log("LOG SETUP CONNECTION ==========================================================", socket.id);
			console.log("user IDIDIDIDID ", socket.id);
			console.log("user should be user id ", socket.nickname);

			socket.join(socket.nickname);
			socket.join(data.account_type);

			console.log("ROOMS ", io.sockets.adapter.rooms);
			console.log("data: "+ JSON.stringify(data));	

			console.log("LOG END SETUP CONNECTION ==========================================================", socket.id);
			deleteRoom(io, socket.id);
			socket.emit('test',{"data": "testing..."});
			
			if(data.account_type === "ADMIN"){
				
				socket.join("BARSTAFF");
				socket.join("ORGANISER");
			}
		});
	  
		socket.on('disconnect', function(){
			console.log('user disconnected: ', socket.id);
			deleteRoom(io, socket.id);
			deleteRoom(io, socket.nickname);
		});
	});
}


module.exports = function(io){
	
	socketService(io);
	
	config.socket = io;
	
	router.get('/message/:userid([0-9]+)', function(req,res){
		
		let user_ID = req.params.userid;
		
		console.log(('Getting info message from chat logs where user_ID = '+user_ID));
		
		db.getMissedChatMessages(user_ID)
		.then(function(succ){
			
			console.log("\n\nsucc: ",succ);
			res.json({ "data" : succ });
			
		})
		.catch(function(err){
			
			console.log(err);
			res.send("err: " + err);
		})
	});
	
	router.post('/create/:eventid([0-9]+)', function(req,res){

		
		let room = setRoomName(req.params.eventid);
		
		var exist = io.sockets.adapter.rooms[room];
		
		if(exist != undefined){
			
			res.status(403).json({'error-message' : 'room already exist'});
			
		}else{
			
			io.sockets.adapter.rooms[room] = { length: 0 };
			
			console.log('created event chat ', req.params.eventid);
			
			res.send('room created sucessfully');
		}
	});
	
	router.post('/message/:eventid([0-9]+)/:userid([0-9]+)/', function(req, res){
		
		if(helper.asserters.assertChatMessageObject(req.body)){
			
			console.log('socket' + config.socket);
			
			console.log("message: "+ req.body[db.constants.CHAT_MESSAGE]);
			

			console.log("MESSAGE =============================================================================");
			console.log("req param eventid ", req.params.eventid);
			console.log("req body u ", req.body[db.constants.USER_ID]);
			console.log("req body u ", req.body);
			console.log("req param userid ", req.params.userid);
			console.log("meassage ", req.body[db.constants.CHAT_MESSAGE]);
			console.log("MESSAGE =============================================================================");
			console.log('body: ', req.body);
			JobChatBroadcast(config.socket, req.body, req.params.eventid, req.body[db.constants.USER_ID], req.params.userid,  'chatMessageReceived',
			req.body[db.constants.CHAT_MESSAGE] )
			.then(function(succ){
				
				return res.json(helper.res.goodRes(succ));
			})
			.catch(function(err){
				
				console.log(err);
				return res.json(helper.res.badRes(err.message));
			});
			
		}else{
			
			return res.json(helper.res.badRes(400, "Invalid input response required fields missing"));
		}
	});

	router.post('/offer/:eventid([0-9]+)/:userid([0-9]+)/', function(req, res){
		
		if(helper.asserters.assertChatMessageObject(req.body)){
			
			console.log("offer: " + req.body);
		
			JobChatBroadcast(config.socket, req.body, req.params.eventid, req.body[db.constants.USER_ID], req.params.userid, 'jobPayRateOfferReceived',
				{ [db.constants.CHAT_MESSAGE] : ( req.body[db.constants.USER_ID] + " made offer of " + req.body[db.constants.CHAT_OFFER] + " isHourly " +  req.body["isHourly"] ) } )
		
			.then(function(succ){
			
				let obj = { [db.constants.USER_ID] : req.body[db.constants.USER_ID] , [db.constants.CHAT_OFFER] : req.body[db.constants.CHAT_OFFER] ,
				 [db.constants.CHAT_MESSAGE] : ( req.body[db.constants.USER_ID] + " made offer of " + req.body[db.constants.CHAT_OFFER] ) }
				
				res.json(helper.res.goodRes(succ));
			
			})
			
			.catch(function(err){
			
				res.json(helper.res.badRes(err.code, err.message));
			
			});
		
		}else{
			
			return res.json(helper.res.badRes(400, "Invalid input response required fields missing"));
		}
	});

	router.post('/acceptoffer/:eventid([0-9]+)/:userid([0-9]+)/', function(req, res){
			
		if(helper.asserters.assertChatMessageObject(req.body)){
			
			var offer = req.body.offer;
			var commission = ( offer * (12/100))
		
			db.getCustomerByID(req.body[db.constants.ORG_ID])
			.then(function(id){
				
				console.log('stripe: ' + config.stripe.object);
				console.log('id: ' + id);
			
				if(id === null){
					
					return res.json(helper.res.badRes(401, "Customer " + req.body[db.constants.ORG_ID] + " does not excist"));
				}
			
				config.stripe.object.charges.create({
			
					amount : (offer + commission) * 100,
					currency: 'gbp',
					description : ('For job creation between ' + req.body[db.constants.ORG_ID] + ' and ' + req.body[db.constants.STAFF_ID]),
					customer : id
			
					}, function(err,charge){
					
						if(err){
							
							console.log('err: ',err);
							JobChatBroadcast(config.socket, req.body, req.params.eventid, "server", req.body[db.constants.STAFF_ID], 'acceptOffer',
			
							{ [db.constants.CHAT_MESSAGE] : "Error something went wrong " + (offer + commission) + "payment could not be made" })
							.then(function(succ){
								
								return res.json(helper.res.goodRes("offer accepted {internal checks ongoing}"));
							})
							.catch(function(err){
								
								return res.json(helper.res.goodRes("offer accepted {internal checks ongoing}"));
							});
							
						}else{
						
							console.log('payment made');
							
							JobChatBroadcast(config.socket, req.body, req.params.eventid, "server", req.body[db.constants.STAFF_ID], 'acceptOffer',
			
							{ "success" : "payment off " + (offer + commission) + "has been charge for job creation", res })
							.then(function(succ){
								
								return res.json(helper.res.goodRes("contract made"));
							
							}).catch(function(err){
								
								helper.utils.LOG(err);
								
								console.log(err);
								
								return res.json(helper.res.goodRes("offer accepted {internal checks ongoing}"));
							});
						}
				});
			})
			.catch(function(err){
				
				console.log("1:",err);
				
				helper.utils.LOG(err);
				return res.json(helper.res.badRes(500, "Internal error"));
			});
			
		}else{
			
			return res.json(helper.res.badRes(400, "Invalid input response required fields missing"));
		}
	});

	router.post('/rejectoffer/:eventid([0-9]+)/:userid([0-9]+)/', function(req, res){
		
		if(helper.asserters.assertChatMessageObject(req.body)){
			
			console.log("offer: " + req.body);
		
			JobChatBroadcast(config.socket, req.body, req.params.eventid, req.body[db.constants.USER_ID], req.params.userid, 'jobPayRateOfferRejected',
				{ [db.constants.CHAT_MESSAGE] : ( req.body[db.constants.USER_ID] + " rejected offer of " + req.body[db.constants.CHAT_OFFER])})
		
			.then(function(succ){
			
				res.json(helper.res.goodRes(succ));
			
			})
			
			.catch(function(err){
			
				res.json(helper.res.badRes(err.code, err.message));
			
			});
		
		}else{
			
			return res.json(helper.res.badRes(400, "Invalid input response required fields missing"));
		}
		
	})

	router.post('/jobcompleted/:eventid([0-9]+)/:userid([0-9]+)/', function(req, res){
			
		if(helper.asserters.assertChatMessageObject(req.body)){
		
			db.getJobOfferAndToken(req.body[db.constants.JOB_ID])
			.then(function(row){
				
				console.log('row: ' + row);
				
				let pay = row[db.constants.PAY] * 100;
				
				let payed = Math.floor(pay -  (pay * (8/100)));
			
				config.stripe.object.charges.tranfer({
			
					amount : payed,
					currency: 'gbp',
					description : ('Payment for job ' + row[db.constants.TITLE]),
					source : row[db.constants.CARD_TOKEN]
			
					}, function(err,charge){
					
						if(err){
							
							console.log('err: ',err);
							JobChatBroadcast(config.socket, req.body, "server" , req.body[db.constants.STAFF_ID], 'jobCompleted',
			
							{ [db.constants.CHAT_MESSAGE] : "payment off " + (offer + commission) + "has been made", "ammount" : payed })
							.then(function(succ){
								
								return res.json(helper.res.goodRes("message sent"));
							})
							.catch(function(err){
								
								return res.json(helper.res.goodRes("message sent {internal problem}"));
							});
					}
					
					else{
						
						console.log('payment made');
						
						JobChatBroadcast(config.socket, req.body, "server" , req.body[db.constants.JOB_ID], 'acceptOffer',
		
						{ "success" : "payment off " + (offer + commission) + "has been charge for job creation", res })
						.then(function(succ){
							
							return res.json(helper.res.goodRes("contract made"));
						
						}).catch(function(err){
							
							helper.utils.LOG(err);
							
							console.log(err);
							
							return res.json(helper.res.badRes(err));
						});
					}
				});
			})
			.catch(function(err){
				
				console.log("1:",err);
				
				helper.utils.LOG(err);
				return res.json(helper.res.badRes(500, err));
			});
			
		}else{
			
			return res.json(helper.res.badRes(400, "Invalid input response required fields missing"));
		}
	});
	
	return router;
}


function setRoomName(roomName){
	return "/"+roomName;
}

function deleteRoom(io, roomName){
	delete io.sockets.adapter.rooms[roomName];
}

function JobChatBroadcast(io, data, event_id, userid_from, userid_to, event_name, message){
	
	console.log(event_id, " ", userid_from, " ", userid_to, " ", event_name, " ", message);

	let getRoom = generateMLBRoom(data);
	console.log('ROOMS : ', (io.sockets.adapter.rooms));
	let isRoom = io.sockets.adapter.rooms[getRoom];
	let userExists = io.sockets.adapter.rooms[setRoomName(userid_to)];
	
	return new Promise(function(resolve, reject){
		
		console.log("LOGSSSSSS===================================================================");
		console.log("getRomm ", getRoom);
		console.log("isRoom ", isRoom);
		console.log("userExists ", userExists);
		console.log("eventid ", event_id);
		console.log("useridfrom ", userid_from);
		console.log("userid to ", userid_to);
		console.log("setRoomnname func ", setRoomName(userid_to));
		console.log("adapter obj ", io.sockets.adapter);
		console.log("message ", message);
		console.log("LOGSSSSSS===================================================================");
				
			if(!userExists || !isRoom){ 
			
				console.log('db: ' + db);
				console.log('to: ', userid_to);
			
				db.addToChatLog(userid_to, userid_from, message[db.constants.CHAT_MESSAGE])
				.then(function(success){
					
					if(success){
						
						console.log('message added succesfully');
						resolve( "added to server database" );
					
					}else{
						
						console.log('message could not be added succefully');
						reject( { "code" : 500 , "message" : 'message could not be added to server' });
					}
				
				}).catch(function(err){
					
					console.log("error obj to print, ", err);
					helper.utils.LOG('err: '+ err);
					reject( { "code" : 500 , "message" : "internal error" });
				});
				
			}else{
				
				io.sockets.in(getRoom).emit( event_name, { "from": userid_from, [db.constants.CHAT_MESSAGE]: message});
				resolve( "sent to user" );
			}
	})
}

function broadcastNewJobCreated(io, socket, data, message){
	
	let isRoom = io.sockets.adapter.rooms["BARSTAFF"];
	console.log("new job created by: ", data.user_ID, "job_ID: ", data.job_ID);
		
	if(isRoom){
			
		console.log("broadcasting message");
		io.sockets.in("BARSTAFF").emit("newJobAvailable", { "org_ID": data.org_ID, "job_ID" : data.job_ID, "message": message});
	
	}else{
		//socket.emit('info', { "message": data.to + " disconnected"});console.log("no room with name %s", getRoom);
		console.log("broardcast failed: ",data.user_ID);
	}
}

function barStaffBroadcastTo(io, socket, data, eventToSend, message){
	
	let getRoom = setRoomName(data.org_ID);
	let isRoom = io.sockets.adapter.rooms[getRoom];
		
	if(isRoom){
		console.log("notify ",data.org_ID, "user ", data.user_ID, " applied");
		io.sockets.in(getRoom).emit(eventToSend, { "message": message});
	}else{
		//socket.emit('info', { "message": data.to + " disconnected"});console.log("no room with name %s", getRoom);
		console.log("user ", data.org_ID, " user does not excist");
	}
}

function organiserBroadcastTo(io, socket, data, eventToSend, message){
	
	let getRoom = setRoomName(data.user_ID);
	let isRoom = io.sockets.adapter.rooms[getRoom];
		
	if(isRoom){
		let name = socket.nickname.substring(1);
		io.sockets.in(getRoom).emit(eventToSend, { "org_ID": data.org_ID, "job_ID" : data.job_ID, "message": message});
	}else{
		//socket.emit('info', { "message": data.to + " disconnected"});console.log("no room with name %s", getRoom);
		console.log("user ", data.org_ID, " user does not excist");
	}
}
