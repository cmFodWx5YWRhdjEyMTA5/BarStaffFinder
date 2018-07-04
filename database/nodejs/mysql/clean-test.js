//TEST JOBS

var Database = require('./database.js');
var readline = require('readline');
var Data = require('./clean-test-data.js');

var database = new Database();
var data = new Data();

var password = "00000000";


//setup readline
var rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout
});

//set up data for users;

var setup = function(x, _callback) {
	switch(x) {
		case 1:
			database.register(data.barstaff0);
			database.register(data.barstaff1);
			database.register(data.barstaff2);
			database.register(data.barstaff3);
			database.register(data.barstaff4);
			database.register(data.barstaff5);
			database.register(data.barstaff6);
			database.register(data.barstaff7);
			database.register(data.barstaff8);
			database.register(data.barstaff9);

			database.register(data.organiser0);
			database.register(data.organiser1);
			database.register(data.organiser2);
			database.register(data.organiser3);
			database.register(data.organiser4);
			database.register(data.organiser5);
			database.register(data.organiser6);
			database.register(data.organiser7);
			database.register(data.organiser8);
			break;
		case 2:
			database.confirmUser(data.barstaff0.token);
			database.confirmUser(data.barstaff1.token);
			database.confirmUser(data.barstaff2.token);
			database.confirmUser(data.barstaff3.token);
			database.confirmUser(data.barstaff4.token);
			database.confirmUser(data.barstaff5.token);
			database.confirmUser(data.barstaff6.token);
			database.confirmUser(data.barstaff7.token);
			database.confirmUser(data.barstaff8.token);
			database.confirmUser(data.barstaff9.token);


			database.confirmUser(data.organiser0.token);
			database.confirmUser(data.organiser1.token);
			database.confirmUser(data.organiser2.token);
			database.confirmUser(data.organiser3.token);
			database.confirmUser(data.organiser4.token);
			database.confirmUser(data.organiser5.token);
			database.confirmUser(data.organiser6.token);
			database.confirmUser(data.organiser7.token);
			database.confirmUser(data.organiser8.token);
			break;
		case 3:
			database.completeRegistration(1, data.barstaffdetails0);
			database.completeRegistration(2, data.barstaffdetails1);
			database.completeRegistration(3, data.barstaffdetails2);
			database.completeRegistration(4, data.barstaffdetails3);
			database.completeRegistration(5, data.barstaffdetails4);
			database.completeRegistration(6, data.barstaffdetails5);
			database.completeRegistration(7, data.barstaffdetails6);
			database.completeRegistration(8, data.barstaffdetails7);
			database.completeRegistration(9, data.barstaffdetails8);
			database.completeRegistration(10, data.barstaffdetails9);

			database.completeRegistration(11, data.organiserdetails0);
			database.completeRegistration(12, data.organiserdetails1);
			database.completeRegistration(13, data.organiserdetails2);
			database.completeRegistration(14, data.organiserdetails3);
			database.completeRegistration(15, data.organiserdetails4);
			database.completeRegistration(16, data.organiserdetails5);
			database.completeRegistration(17, data.organiserdetails6);
			database.completeRegistration(18, data.organiserdetails7);
			database.completeRegistration(19, data.organiserdetails8);
			break;
		case 4:
			database.createPublicJob(data.jobdetails0);
			database.createPublicJob(data.jobdetails1);
			database.createPublicJob(data.jobdetails2);
			database.createPublicJob(data.jobdetails3);
			database.createPublicJob(data.jobdetails4);
			break;
		// case 5:
		// 	database.applyJob(1, 1, "Staff_id1 applying to job_id1");
		// 	database.applyJob(2, 1, "Staff_id2 applying to job_id1");
		// 	database.applyJob(3, 1, "Staff_id3 applying to job_id1");
		// 	database.applyJob(1, 4, "Staff_id1 applying to job_id4");
		// 	database.applyJob(2, 4, "Staff_id2 applying to job_id4");
		// 	database.applyJob(5, 4, "Staff_id5 applying to job_id4");
		// 	break;
		default: 
			break;
	}
	_callback();
}

var createMenu = function() {
	console.log("---------JOBS TESTING---------");
	console.log("\nSelect an option:");
	console.log("1. Connect to database");
	console.log("2. Setup");
	console.log("3. Update User");
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

				setup(4, function() {
				setTimeout(function(){

				setup(5, function() {
				setTimeout(function(){

					console.log("SETUP DONE");

				}, 250);
				});						
				
				}, 250);
				});						
				
				}, 250);
				});						
				
				}, 250);
				});

				}, 250);
				});

				break;
			case "3":
					database.getOrganiserByJob(1);
				break;
			default:
				test();
				break;
		}
		console.log("\n\n\n\n\n");
		test();
	});
}

test();