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



//Create menu for the test application
var createMenu = function(){
	console.log("---------DATABASE TESTING---------");
	console.log("\nSelect an option:");
	console.log("1. Connect to database");
	console.log("2. Register a dummy user");
	console.log("3. Authenticate dummy user");
	console.log("4. Complete registration for one of the users");
	console.log("5. Return a user");
	console.log("6. Log in a user");
	console.log("7. Get all Barstaff");
	console.log("8. Get all Organisers");
	console.log("9. Get user by email");
	console.log("10. Create 2 Jobs");
	console.log("11. Get a list of all jobs");
	console.log("12. Apply for a job");
	console.log("13. Accept one of the jobs");
	console.log("14. Cycle one of the jobs' status");
	console.log("15. Reject the other job");
	console.log("16. Remove user");
	console.log("17. Create a chat");
	console.log("18. Send messages in a chat");
	console.log("19. Update the first name of account ID 1");
	console.log("20. Get jobs that are not yet accepted but have applicants")
	console.log("quit. Exit application\n");
}

var test = function() {
	createMenu();
	rl.question("Enter your option: ", function(userInput) {
		switch(userInput) {
			case "1":
				//database.connect('localhost', 'root', "0000000");
				database.connect('localhost', 'root', "00000000");
				break;
			case "2":
				database.register(data.barstaff0);
				database.register(data.barstaff1);
				database.register(data.barstaff2);
				database.register(data.barstaff3);
				database.register(data.organiser0);
				break;
			case "3":
				database.confirmUser(data.barstaff0.token);
				database.confirmUser(data.barstaff1.token);
				database.confirmUser(data.barstaff2.token);
				database.confirmUser(data.barstaff3.token);
				database.confirmUser(data.organiser0.token);
				break;
			case "4":
				database.completeRegistration(1, data.barstaffdetails0);
				database.completeRegistration(5, data.organiserdetails0);
				database.completeRegistration(2, data.barstaffdetails1);
				break;
			case "5":
				database.getUser(5);
				break;
			case "6":
				database.logIn("organiser@test.com", "000001");
				break;
			case "7":
				database.getAllBarstaff();
				break;
			case "8":
				database.getAllOrganisers();
				break;
			case "9":
				database.getUserByEmail("staff1@test.com");
				break;
			case "10":
				database.createPublicJob(data.jobdetails0);
				database.createPublicJob(data.jobdetails1);
				break;
			case "11":
				database.getJobs();
				break;
			case "12":
				database.applyJob(1, 1, "The ideal siege weapon to launch more than 95kg of material over 300m is what?");
				database.applyJob(2, 2, "I'm the ultimate siege weapon, a trebuchet");
				break;
			case "13":
				database.acceptJob(1, 1);
				break;
			case "14":
				database.cycleJobStatus(1);
				break;
			case "15":
				database.rejectRequest(2,2);
				break;
			case "16":
				database.removeUser(3);
				break;
			case "17":
				database.createChat(data.chat0);
				break;
			case "18":
				database.sendMessage(data.message0);
				database.sendMessage(data.message1);
				break;
			case "19":
				database.updateGeneralDetails(1, "first_name", "AlphaCharlieEcho");
				break;
			case "20":
				database.getJobsWithApplicants();
				break;
			case "quit":
				process.exit();
			default:
				test();
				break;
		}
		console.log("\n\n\n");
		test();
	});
}


console.log(data.barstaff0);

test();



