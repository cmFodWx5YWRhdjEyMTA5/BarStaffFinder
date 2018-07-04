var express = require("express"); 
var router = express.Router();

// sets up base file path to get static files for this route
router.use(express.static("controllers/chat/lib"));

module.exports = function(io){
	socketService(io);
	router.get("/", function(req, res){
		res.sendFile("index.html");
	});

	return router;
}


function socketService(io){
	io.on('connection', function(socket){

		console.log("socket id: "+ socket.id);		
		
		console.log("user connected: %s", socket.id);
	  
		socket.on("list_clients", function(data){
			let rooms = Object.keys(io.sockets.adapter.rooms).filter(function(value){
				return value.substring(0,1) == "/";	
			});
			socket.emit("clients_list", {"sock_rooms": rooms});
			
		});

		// nickname
		socket.on("set_nickname", function(data){
			console.log("socket nickname: ", data);
			socket.nickname = setRoomName(data.name);
			socket.join(socket.nickname); // join the user to a room with all their devises
			deleteRoom(io, data.old_nickname);
			deleteRoom(io, socket.id);
			console.log(io.sockets.adapter.rooms);
		});
		
		// general chat 
		socket.on('chat', function(data){
			console.log('new message recieved: ', data);
			let getRoom = setRoomName(data.to);
			let isRoom = io.sockets.adapter.rooms[getRoom];
			if(isRoom){
				let name = socket.nickname.substring(1);
				io.sockets.in(getRoom).emit('chat', { "from": name, "message": data.message});
				socket.broadcast.to(socket.nickname).emit('chat', { "from": name, "message": data.message});
			}else{
				socket.emit('info', { "message": data.to + " disconnected"});
				console.log("no room with name %s", getRoom);
			}
		});
	  
		socket.on('disconnect', function(){
			console.log('user disconnected: ', socket.id);
			deleteRoom(io, socket.id);
			deleteRoom(io, socket.nickname);
		});
	});
}

function setRoomName(roomName){
	return "/"+roomName;
}

function deleteRoom(io, roomName){
	delete io.sockets.adapter.rooms[roomName];
}
