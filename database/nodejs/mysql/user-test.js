//TEST USERS

var Database = require('./database.js');
var readline = require('readline');
var Data = require('./test-data.js');

var database = new Database();
var data = new Data();

var password = "00000000";


//setup readline
var rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout
});


//set up data for users;

//set up data for users;

var setup = function(x, _callback) {
	switch(x) {
		case 1:
			database.register(data.barstaff0);
			database.register(data.barstaff1);
			database.register(data.barstaff2);
			database.register(data.barstaff3);
			database.register(data.organiser0);
			break;
		case 2:
			database.confirmUser(data.barstaff0.token);
			database.confirmUser(data.barstaff1.token);
			database.confirmUser(data.barstaff2.token);
			database.confirmUser(data.barstaff3.token);
			database.confirmUser(data.organiser0.token);
			break;
		case 3:
			database.completeRegistration(1, data.barstaffdetails0);
			database.completeRegistration(5, data.organiserdetails0);
			database.completeRegistration(2, data.barstaffdetails1);
			break;
		default: 
			break;
	}
	_callback();
}

var createMenu = function() {
	console.log("---------REGISTRATION TESTING---------");
	console.log("\nSelect an option:");
	console.log("1. Connect to database");
	console.log("2. Setup");
	console.log("3. Get barstaff profile");
}

var test = function() {
	createMenu();
	rl.question("Enter your option: ", function(option){
		switch(option) {
			case "1":
				database.connect('localhost', 'root', "0000000");
				// database.connect('localhost', 'root', "00000000");
				break;
			case "2":
				setup(1, function() {
					setTimeout(function(){
						setup(2, function() {
							setTimeout(function(){
								setup(3, function() {
									setTimeout(function(){
										console.log("\n\n\nSETUP DONE");
									}, 250);
								});									
							}, 250);
						});						
					}, 250);
				});
				break;
			case "3":
				database.getBarstaffProfile(1);
				break;
			default:
				console.log("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
				test();
				break;
		}
		console.log("\n\n\n\n\n");
		test();
	});
}

test();