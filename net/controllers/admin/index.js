let express = require("express");
let router = express.Router();
let jwt = require("jsonwebtoken");
let config = require("../../config");
let helper = require("../../helpers");
let cookieCheck = new RegExp('session=.*');
let htmlFiles = new RegExp('\.html');
var generator = require("./HTMLGenerator.js");
var db = config.database.object;
let fs = require('fs');

router.use(express.static("files/admin/public"), function(req, res, next){

	let ishtml = htmlFiles.test(req.url);

	if(req.headers.hasOwnProperty('cookie')){

		var token = req.headers.cookie.split(';')[0].split('session=')[1];

		if(req.method === 'POST' && req.url == '/' && token === 'finished') { next(); } 

		jwt.verify(token, config.appSecret, function(err, decoded){
		
			if(err){
				
				return res.sendFile('index.html', {root: __dirname+'/../../files/admin/' });
			
			}else{
				
				var user = decoded.user;
				req.decoded = user;
				var profile = decoded.profile
				req.mlbProfile = profile;

				if(user[db.constants.ACCOUNT_TYPE] === 'ADMIN'){

					if(user[db.constants.ACTIVE] === 1){

						if(req.method === 'GET' && req.url === '/'){

							res.send(generator.generateHTML( (''+profile[db.constants.FIRSTNAME]), 'Dashboard' , '', 'Welcome '+ profile[db.constants.FIRSTNAME] + ' ' + profile[db.constants.SURNAME]));
			
						}else if(req.method === 'GET' && req.url === '/index'){

							res.sendFile('index.html', {root: __dirname+'/../../files/admin/' });
						
						}else{

							next();
						}
					
					}else{

						console.log('you need to activate');
						
						fs.readFile(__dirname+'/../../files/admin/complete_profile.html', function(err, data) {
	    
						    if(err) { console.log(err); }
						   	
						   	else res.send(data.toString().split('$name').join(req.profile[db.constants.FIRSTNAME]));
						});
					}
				
				}else{

					helper.utils.LOG("User accessed admin site: " + req.decoded[db.constants.USER_ID] + " without authorization");
				
					return res.status(401).json(helper.res.badRes(401, "You do not have access to this page"));
				}
			}
			
		});

	}else if (req.method === 'POST' && req.url === '/'){

		next();

	}else{

		res.sendFile('index.html', {root: __dirname+'/../../files/admin/' });
	}
});

router.get("/logout", function(req, res){

	var token = req.headers.cookie.split(';')[0].split('session=')[1];
	res.cookie('session', "finished", { expires: new Date(Date.now() + 60*1000 * 60), path: '/api/admin', httpOnly: true });
	res.redirect('/api/admin/');
	res.end();
	helper.utils.LOG("Admin ID: " + req.decoded[db.constants.USER_ID] + " quit session");
});

router.get("/add%20admin", function(req, res){

	fs.readFile(__dirname+'/../../files/admin/admin.html', function(err, data) {
	    
	    if(err) { console.log(err); }
	   	
	   	else res.send(data.toString().split('$name').join(req.mlbProfile[db.constants.FIRSTNAME]));
	});
});

router.get("/profile", function(req, res){

	fs.readFile(__dirname+'/../../files/admin/profile.html', function(err, data) {
	    
	    if(err) { console.log(err); }
	   	
	   	else res.send(data.toString().split('$name').join(req.mlbProfile[db.constants.FIRSTNAME]));
	});

});

router.post("/", function(req, res){

	db.logIn(req.body[db.constants.EMAIL], req.body[db.constants.HASH])
	.then(function(entry){

		if(entry[db.constants.ACCOUNT_TYPE] === "ADMIN"){
			
			db.getUserProfile(entry[db.constants.USER_ID])
			.then(function(profileEntry){

				var token = jwt.sign({"user": entry, "profile" : profileEntry }, config.appSecret, {
					expiresIn: config.tokenExpires  // expires in 1 hours
				});

				res.cookie('session', token, { expires: new Date(Date.now() + 60*1000 * 60), path: '/api/admin', httpOnly: true });

				res.redirect("./");
				res.end();
				
				helper.utils.LOG("Admin ID: " + entry[db.constants.USER_ID] + " joined session");

			});

		}else{
			
			helper.utils.LOG("User accessed admin site: " + entry[db.constants.USER_ID] + " without authorization");
			
			return res.status(401).json(helper.res.badRes(401, "You do not have access to this page"));
		}
	
	}).catch(function(err){
				
			helper.utils.LOG('error accored: ' + err);
			res.status(401).json(helper.res.badRes(401, "Email and/or password are incorret"));
	});
});

router.get(/^\/(tables%20)(user|job|online)/, function(req, res){

	var name = req.url.split('tables%20')[1];
	console.log('name: '+name);

	var constants = db.constants;

	if(name === 'user'){
		
		db.getTable('SELECT * FROM USER')
		.then(function(entry){

			var fields = Object.keys(entry[0]);

			var body = generator.generateTable('User', fields, entry);

			res.send(
				
				generator.generateHTML(''+req.mlbProfile[db.constants.FIRSTNAME], 'Users Table' , body, 'Users Tables')
			);
		});
	
	}else if(name === 'job'){

		db.getTable('SELECT * FROM JOBS')
		.then(function(entry){

			var fields = Object.keys(entry[0]);

			var body = generator.generateTable('Job', fields, entry);

			res.send(
				
				generator.generateHTML(''+req.mlbProfile[db.constants.FIRSTNAME], 'Jobs Table' , body, 'Jobs Table')
			);
		});

	}else{

		var fields = [ [db.constants.USER_ID], 'socket_id', [db.constants.ACCOUNT_TYPE] ];
	
		var data = [];
		
		function checkIsUser(room){
			
			return room.substring(0,1) === '/';
		}
		
		var socket_rooms = config.socket.sockets.adapter.rooms;
		var rooms = Object.keys(config.socket.sockets.adapter.rooms).filter(checkIsUser);
		console.log("rooms: ", rooms);
		
		if(rooms.length > 0){
			
			
			for(let i = 0 ; i < rooms.length ; ++i){
					(function(i){
						var id = rooms[i].substring(1);
				
						db.getUserProfile(parseInt(id))
						.then(function(entry){
				
				
							console.log("HKJDFDJFHASFKDFHAFDASDF");
							console.log(socket_rooms);
							console.log(rooms);
							console.log("i value ", i)
							console.log("FHJKFHJFSDFHSJHSJKHSHAKS");
					
							var sockets = Object.keys( socket_rooms[ (rooms[i]) ].sockets );
							
							for(j = 0; j < sockets.length ; ++j){
								
								var socket_data = {};
								
								socket_data[db.constants.USER_ID] = id;
								socket_data.socket_id = sockets[j];
								socket_data[db.constants.ACCOUNT_TYPE] = entry[db.constants.ACCOUNT_TYPE];
								
								data.push(socket_data);
							}
							
							console.log('data value: ', data);
								
							var body = generator.generateTable('Online Users', fields, data);
								
							res.send(
								generator.generateHTML(''+req.mlbProfile[db.constants.FIRSTNAME], 'Online Users' , body, 'Online Users Table')
							);
					});
					})(i);
				
			}
		}else{
			
			var body = generator.generateTable('Online Users', fields, data);
								
			res.send(
				
				generator.generateHTML(''+req.mlbProfile[db.constants.FIRSTNAME], 'Online Users' , body, 'Online Users Table')
			);
		}
	}

});

router.get(/^\/(delete%20)(job|account)/, function(req, res){

	var name = req.url.split('delete%20')[1];

	if(name === 'job'){
		
		var body = generator.deleteEntryFragment('Job ID:', db.constants.JOB_ID);
		res.send(
			generator.generateHTML(''+req.mlbProfile[db.constants.FIRSTNAME], 'Delete Job' , body, 'Delete Job')
		);
	
	}else{

		var body = generator.deleteEntryFragment('User ID:', db.constants.USER_ID);
		res.send(
			generator.generateHTML(''+req.mlbProfile[db.constants.FIRSTNAME], 'Delete User' , body, 'Delete User')
		);
	}

}).post( /^\/(delete%20)(job|account)/, function(req, res){

	var name = req.url.split('delete%20')[1];
	res.send('delete/'+name);
});

router.get(/^\/(filter%20)(job|account|online)/, function(req, res){
	
	var name = req.url.split('filter%20')[1];
	
	
});

function getCookieParam(cookie, i){
	
	return cookie.split(';')[i];
}

function generateFilters(keys){

	var toReturn = '';

	for(i = 0 ; i < keys.length ; ++i){
	
	toReturn += `<label>filter `+keys[i]+`:</label>
	<input class="form-control">
	<br>
	<label>filter `+keys[i]+` usng "SQL LIKE":</label>
	<input class="form-control">
	</br>`
	
	}
	
	return toReturn;
}

// Delete usr account
router.put(/^\/(delete%20)(job|account)/, function(req, res){
	
	let name = req.url.split('delete%20')[1];
	
	console.log("name :", name);
	
	let body = req.body;
	
	if(name === 'job'){
		
		body[db.constants.JOB_ID] = body.id;
		
		helper.utils.LOG('asserJobId: ' + helper.asserters.assertJobid(body) + ' assertReason: ' +  (body.reason != '') );
		
		if(helper.asserters.assertJobid(body) && body.reason != ''){
		
			let SQL = "DELETE FROM JOBS WHERE " + (db.constants.JOB_ID) + " = '"+req.body.id+"';"
			
			helper.utils.LOG("SQL: " + SQL + "userID: " + req.decoded[db.constants.USER_ID] + " reason: " + req.body.reason);
		
			console.log(SQL);
			
			db.rawSql(SQL)
			.then(function(response){
				
				res.json( "Successfully deleted job" );
				
			}).catch(function(err){
				
				helper.utils.LOG(err);
				
				return res.status(401).json(helper.res.badRes(401, "Invalid input"));
			});
		
		}else{
			
			helper.utils.LOG("invalid request by: " + req.decoded[db.constants.USER_ID] + ", id " + body.id + " reason: " + body.reason + " [DELETE JOB ASSERT fAILS ("
			
			+ (helper.asserters.assertJobid(body.id)) + "," + (body.reason != '') + ")]");
			
			return res.status(401).json(helper.res.badRes(401, "Invalid input"));
		}
	
	}else {
		
		body[db.constants.USER_ID] = body.id;
		
		if(helper.asserters.assertUserid(body) && body.reason != ''){
		
			let SQL = "DELETE FROM USER WHERE " + (db.constants.USER_ID) + " = '"+req.body.id+"';"
			
			helper.utils.LOG("SQL: " + SQL + " userID: " + req.decoded[db.constants.USER_ID] + " reason: " + req.body.reason);
			
			if(req.body.id != '1'){
				
				db.rawSql(SQL)
				.then(function(response){
					
					res.send( "Successfully deleted user" );
					
				}).catch(function(err){
					
					helper.utils.LOG(err);
					
					return res.status(401).json(helper.res.badRes(401, "Invalid input"));
				});
			
			}else{
				
				helper.utils.LOG("invalid request by: " + req.decoded[db.constants.USER_ID] + ", id " + body.id + " reason: " + body.reason + " [DELETE ROOT USER]");
				
				return res.status(401).json(helper.res.badRes(401, "This account can not be delted through this site"));
			}
		
		}else{
			
			helper.utils.LOG("invalid request by: " + req.decoded[db.constants.USER_ID] + ", id " + body.id + " reason: " + body.reason + " [DELETE USER - ASSERT fAILS ("
			
			+ (helper.asserters.assertUserid(body)) + "," + body.reason != ")]");
			
			return res.status(401).json(helper.res.badRes(401, "Invalid input"));
		}
	}

});

module.exports = router;
