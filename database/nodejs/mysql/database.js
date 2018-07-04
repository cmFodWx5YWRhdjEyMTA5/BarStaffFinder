var mysql = require('mysql');

var connection;

// //Example of how to query insert into, no need to worry about sql injection :) 

// var query = connection.query('insert into BarStaff set ?', barStaff, function(err, res) {
// 	console.log(query);
// 	if(err) {
// 		console.error(err);
// 	} else {
// 		console.error(res);
// 	}
// });


// From(user_id)
// To(user_id)
// Messag(text)
// DatTime(DateTime)

module.exports = function() {

	//constants to be used in sql statements and for the user using
	//this so that they do not need to know what each attributes is stored as in the database
	//and that changes can be easily made in the future
 	const constants = {
		USER_ID: "user_ID",
		STAFF_ID: "staff_ID",
		ORG_ID: "org_ID",
		FIRSTNAME : "first_name",
		SURNAME : "last_name",
		GENDER : "gender",
		DOB : "DOB",
		POSTCODE: "postcode",
		CITY: "city",
		TOWN: "town",
		MOBILE : "mobile_number",
		EMAIL : "email_address",
		HASH : "hash",
		ACTIVE : "active",
		TYPE : "type",
		SPECIAL : "speciality",
		DESC : "description",
		PROX : "proximity",
		HRATE : "hour_rate",
		NRATE : "night_rate",
		RATE: "rate",
		IMAGE : "image_path",
		ACCOUNT_TYPE: "account_type",
		DURATION: "duration",
		MESSAGE: "message",

		ORG_NAME : "org_name",
		PROF_POS : "prof_pos",

		CAL_ID : "cal_ID",
		DAY : "day",
		STARTTIME : "startTime",
		ENDTIME : "endTime",

		JOB_ID : "job_ID",
		JOB_TITLE : "job_title",
		JOBSTART : "jobStart",
		JOBEND : "jobEnd",
		LOCATION : "location",
		JOBRATE : "rate",
		STATUS : "status",
		AVAILABLE : "availability",
		PRIVATE: "private",
		TOKEN : "token"
	}
	this.constants = constants;

//---------OTHER

	//private helper functoin to check if user is active (complete the profile)
	function isActiveQuery(user_ID){
		var isActive = `SELECT * FROM USER WHERE ` +
		constants.USER_ID + ` = ` + connection.escape(user_ID) + ` AND `+ 
		constants.ACTIVE + ` = ` + connection.escape(0) + `;`;
		return isActive;
	}

	function isUserNotActive(query, timesToCheck){
		for(var i = 0; i < timesToCheck; ++i){
			 if(query[i].length >= 1) return true
		}
		return false;
	}

	//connect to the database using the details given in the parameters
	this.connect = function(host, user, password) {
		return new Promise(function (resolve, reject){
			connection = mysql.createConnection({
					host: host,
					user: user,
					password: password,
					database: 'MyLocalBartender',
					//this allows multiple statements in one connection.query() function
					multipleStatements: true
			});
			connection.connect(function(err) {
				if(err) {
					console.log('Something went wrong - Could not connect');
					console.error(err);
					reject(err);
				} else {
					console.log('Connection established');
					resolve("connection to DB successful");
				}
			});
		});
	}

// --------------------------> Registration and login functions

	// get all jobs for bartaff at sign in
	this.getJobs = function(staff_ID){
		return new Promise(function (resolve, reject){
			var isActive = isActiveQuery(staff_ID);
			//if the user is a bartender then select from bartender table else try the organiser table
			var jobSql = `SELECT * FROM JOBS WHERE ` +  constants.PRIVATE + ` = ` + connection.escape(0);
			
			var sql = isActive + jobSql;

			connection.query(sql, function(err, rows) {
				if(err) {
					console.log(err);
					reject("An error has occured during log in");
				}
				else if(isUserNotActive(rows, 1)){
					resolve([]);
					//reject("Cannot use MLB service until account is complete");
				}
				 else {
					console.log(rows);
					resolve(rows[1]);
				}
			});
		});
	}

	// get all barstaff for organiser at sign in
	this.getAllBarstaff = function(org_ID){
		return new Promise(function (resolve, reject){
			var isActive = isActiveQuery(org_ID);

			var barstaffSql = `SELECT user_ID, CONCAT(` + constants.FIRSTNAME + `, LEFT(` + constants.SURNAME + 
			`, 1)) AS username, image_path, hour_rate, night_rate, speciality FROM USER INNER JOIN BARSTAFF ON BARSTAFF.staff_id = USER.user_id WHERE USER.active = 1;`;

			var sql = isActive + barstaffSql;

			connection.query(sql, function(err, rows) {
				if(err){
					console.error(err);
					reject("An error has occured during log in");
				}
				else if(isUserNotActive(rows, 1)){
					resolve([]);
					//reject("Cannot use MLB service until account is complete");
				}else{
					console.log(rows);
					resolve(rows[1]);
				}
			});
		});
	}


	// Function is called register, details is a parameter
	this.register = function(details) {
		// connection.query -> lets you write any sql query 
		// ? is a placeholder, second parameter replaces question mark
		// Third parameter function(err,res) is called at the end of the query
		return new Promise(function (resolve, reject) {

			var sql = `
				INSERT INTO TEMPUSER SET ` + connection.escape(details) + `;

				INSERT INTO REGISTRATION (` + constants.EMAIL + `, ` + constants.DOB + `, ` + constants.ACCOUNT_TYPE + `, ` + constants.HASH + `, ` + constants.TOKEN + `)
					SELECT TEMPUSER.` + constants.EMAIL + `, TEMPUSER.` + constants.DOB + `, TEMPUSER.` + constants.ACCOUNT_TYPE + `, TEMPUSER.` + constants.HASH + `, TEMPUSER.` + constants.TOKEN + ` 
					FROM TEMPUSER
					WHERE NOT EXISTS (SELECT * FROM USER
										WHERE ` + constants.EMAIL + ` = ` + connection.escape(details.email_address) + `)
					AND NOT EXISTS (SELECT * FROM REGISTRATION
										WHERE ` + constants.EMAIL + ` = ` + connection.escape(details.email_address) + `);

				SELECT * FROM TEMPUSER WHERE ` + constants.EMAIL + ` = ` + connection.escape(details.email_address) + `;

				DELETE FROM TEMPUSER WHERE ` + constants.EMAIL + ` = ` + connection.escape(details.email_address) + `;

			`;

			connection.query(sql, function(err, res) {
				if(err) {
					console.error(err);
					reject(err);
				}
				else {
					console.error(res);
					if(res[1].affectedRows > 0){
						resolve(res[1]);					
					} else {
						reject("User with " + details.email_address + " email already exists");
					}

				}
			});
		});
	}

	//moves the data of the user from the registration table to the one that the user belongs to
	this.confirmUser = function(token) {
		return new Promise(function(resolve, reject){
			var sql = `
				INSERT INTO USER (` + constants.ACCOUNT_TYPE + `,` + constants.DOB + `, `+ constants.EMAIL + `, ` + constants.HASH + `)  
	        		SELECT ` + constants.ACCOUNT_TYPE + `,` + constants.DOB + `, ` + constants.EMAIL + `, ` + constants.HASH + `  
	        		FROM REGISTRATION WHERE ` + constants.TOKEN + ` = ` + connection.escape(token) + `;  


				INSERT INTO BARSTAFF (` + constants.STAFF_ID + `)  
					SELECT ` + constants.USER_ID + `  
					FROM USER, REGISTRATION  
					WHERE EXISTS ( SELECT * FROM REGISTRATION WHERE ` + constants.ACCOUNT_TYPE + ` = 'BARSTAFF' AND ` + constants.TOKEN + ` = ` + connection.escape(token) + `)  
					AND REGISTRATION.` + constants.TOKEN + ` = ` + connection.escape(token) + ` 
					AND REGISTRATION.` + constants.EMAIL + ` = USER.` + constants.EMAIL +`;
 
 				INSERT INTO ORGANISER (` + constants.ORG_ID + `) 
					SELECT ` + constants.USER_ID + ` 
					FROM USER, REGISTRATION  
					WHERE EXISTS ( SELECT ` + constants.ACCOUNT_TYPE + ` FROM REGISTRATION WHERE ` + constants.ACCOUNT_TYPE + ` = 'ORGANISER' AND ` + constants.TOKEN + ` = ` + connection.escape(token) + `) 
					AND REGISTRATION.` + constants.TOKEN + ` = ` + connection.escape(token) + ` 
					AND REGISTRATION.` + constants.EMAIL + ` = USER.` + constants.EMAIL + `; 
 
				DELETE FROM REGISTRATION where ` + constants.TOKEN + ` = ` + connection.escape(token) + `;
			`;
			connection.query(sql, function(err, res){
				if(err) {
					console.error(err);
					reject(err);
				} else {
					console.log(res);
					// Chceck rows affacted in the first query to see if the confirmation has happend or not
					if(res[0].affectedRows == 0){
						reject("Registration unsuccessful");
					}else{
						resolve(res);
					}
				}
			});
		});
	}

	// Add the data not included in Registration user data to the user entries
	this.completeRegistration = function(user_ID, details) {
		return new Promise(function(resolve, reject){
			var sqlUser = `UPDATE USER SET ` +
				constants.FIRSTNAME +  ` = ` + connection.escape(details.first_name) +  `, ` +
				constants.SURNAME + ` = ` + connection.escape(details.last_name) +  `, ` + 
				constants.GENDER + ` = ` + connection.escape(details.gender) +  `, ` +  
				constants.MOBILE + ` = ` + connection.escape(details.mobile_number) +  `, ` +  
				constants.POSTCODE + ` = ` + connection.escape(details.postcode) + 
				` WHERE `+ constants.USER_ID + ` = ` + connection.escape(user_ID) + ` 
				AND ` + constants.ACTIVE + ` = ` + connection.escape(0) + ';';

			var sqlActive = `UPDATE USER SET ` +  constants.ACTIVE + ` = ` + connection.escape(1) +
					` WHERE ` + constants.USER_ID + ` = ` + connection.escape(user_ID) + 
					` AND ` + constants.ACTIVE + ` = ` + connection.escape(0) + ';'; 

			// If details has more than 2 fields, it is a bar staff object
			if(Object.keys(details).length > 6){

				var sqlBarStaff = `UPDATE BARSTAFF INNER JOIN USER ON BARSTAFF.staff_ID = USER.user_ID SET ` + 
					`BARSTAFF.` + constants.TYPE +  ` = ` + connection.escape(details.type) +  `, ` +
					`BARSTAFF.` + constants.SPECIAL + ` = ` + connection.escape(details.speciality) +  `, ` + 
					`BARSTAFF.` + constants.DESC + ` = ` + connection.escape(details.description) +  `, ` +  
					`BARSTAFF.` + constants.PROX + ` = ` + connection.escape(details.proximity) +  `, ` +  
					`BARSTAFF.` + constants.HRATE + ` = ` + connection.escape(details.hour_rate) +  `, ` +  
					`BARSTAFF.` + constants.NRATE + ` = ` + connection.escape(details.night_rate) +  `, `  + 
					`BARSTAFF.` + constants.IMAGE + ` = ` + connection.escape(details.image_path) +  
					` WHERE BARSTAFF.staff_ID = ` + connection.escape(user_ID) + ` AND USER.active = ` + 
						+ connection.escape(0) + ';';

				var sql = sqlUser + sqlBarStaff + sqlActive;
				connection.query(sql, function(err,res){
					if(err) {
						console.log("\n\n\n\nERROR---------------------------------\n\n\n\n\n\n");
						console.log(err);
						
					}else {
						console.log("resolve");
						console.log(res);
						resolve(res); //console.error(res);
					}
				});
			} // otherwise it is an organiser
			else{
				var sqlOrganiser = `
					UPDATE ORGANISER INNER JOIN USER ON ORGANISER.org_ID = USER.user_ID SET 
					ORGANISER.` + constants.PROF_POS +  ` = ` + connection.escape(details.prof_pos) + `, 
					ORGANISER.` + constants.ORG_NAME + ` = ` + connection.escape(details.org_name) + ` 
					WHERE ORGANISER.org_ID = ` + connection.escape(user_ID) + ` AND USER.active = ` + connection.escape("0") + ';';

				// var sqlOrganiser = `
				// UPDATE ORGANISER SET ` +
				// `ORGANISER.` + constants.PROF_POS +  ` = ` + connection.escape(details.prof_pos) + 
				// `FROM ORGANISER INNER JOIN USER ON ORGANISER.org_ID = USER.user_ID WHERE ORGANISER.org_ID = ` +
				// connection.escape(user_ID) + ` AND USER.active = 0`+ ';';

				var sql = sqlUser + sqlOrganiser + sqlActive;

				connection.query(sql, details, function(err,res){
						if(err) reject(err); //console.error(err);
						else resolve(res); //console.error(res);
				});
			}
		});
	}

	this.addAdmin = function(id, details) {
		return new Promise(function(resolve, reject){
			var sqlUser = `INSERT INTO USER SET ` +
				constants.USER_ID + ` = ` + connection.escape(id) + `, ` + 
				constants.ACCOUNT_TYPE + ` = 'ADMIN', ` +  
				constants.FIRSTNAME +  ` = ` + connection.escape(details.first_name) +  `, ` +
				constants.SURNAME + ` = ` + connection.escape(details.last_name) +  `, ` + 
				constants.GENDER + ` = ` + connection.escape(details.gender) +  `, ` +  
				constants.DOB + ` = ` + connection.escape(details.DOB) + `, ` + 
				constants.EMAIL + ` = ` + connection.escape(details.email_address) + `, ` + 
				constants.MOBILE + ` = ` + connection.escape(details.mobile_number) +  `, ` +  
				constants.POSTCODE + ` = ` + connection.escape(details.postcode) + `, ` + 
				constants.CITY + ` = ` + connection.escape(details.city) + `, ` + 
				constants.TOWN + ` = ` + connection.escape(details.town) + `, ` + 
				constants.HASH + ` = ` + connection.escape(details.hash) + `, ` + 
				constants.ACTIVE + ` = 1;`;

			var sqlBarStaff = `INSERT INTO BARSTAFF SET ` + 
				constants.STAFF_ID + ` = ` + connection.escape(id) + `, ` +
				constants.TYPE +  ` = ` + connection.escape(details.type) +  `, ` +
				constants.SPECIAL + ` = ` + connection.escape(details.speciality) +  `, ` + 
				constants.DESC + ` = ` + connection.escape(details.description) +  `, ` +  
				constants.PROX + ` = ` + connection.escape(details.proximity) +  `, ` +  
				constants.HRATE + ` = ` + connection.escape(details.hour_rate) +  `, ` +  
				constants.NRATE + ` = ` + connection.escape(details.night_rate) +  `, `  + 
				constants.IMAGE + ` = ` + connection.escape(details.image_path) + `;`;
			var sqlOrganiser = `INSERT INTO ORGANISER SET ` + 
					constants.ORG_ID + ` = ` + connection.escape(id) + `, ` + 
					constants.PROF_POS +  ` = ` + connection.escape(details.prof_pos) + `, ` + 
					constants.ORG_NAME + ` = ` + connection.escape(details.org_name) + `;`;
			var sql = sqlUser + sqlBarStaff + sqlOrganiser;

			connection.query(sql, function(err, res){
				if(err) {
					console.error(err);
					reject(err);
				} else {
					console.error(res);
					resolve(res);
				}
			});

		});
	}



	this.logIn = function(email, hash){
		var sql = 'SELECT ' + constants.EMAIL + `, ` + constants.ACCOUNT_TYPE + `,` + constants.USER_ID + `, ` +
				constants.ACTIVE + ` ` + `FROM USER ` + 
				`WHERE ` + constants.EMAIL + ` = ` + connection.escape(email) + 
				`AND ` + constants.HASH + ` = ` + connection.escape(hash) + `;`;

		return new Promise(function (resolve, reject){
			connection.query(sql, function(err, rows) {
				if(err){
					console.error(err);
					reject(err);
				}else{
					if(rows.length == 0){
						console.log("db db : Login failed");
						reject("No user found");
					}else{
						console.log("Login success!");
						console.log(rows);
						resolve(rows[0]);
					}
				}
			});
		});
	}

// --------------------------> Bartender functions

	// add a staffmember to the applicants table linked to a job
	this.applyJob = function(staffID, jobID, message) {
		return new Promise(function(resolve, reject) {
			var isActive = isActiveQuery(staffID);

			var sql = "";
			if(message.trim().length == 0){
				sql = "INSERT INTO APPLICANTS (" + constants.JOB_ID + ", "+ constants.STAFF_ID + ") VALUES (" + connection.escape(jobID) + ", " + connection.escape(staffID) + ");";
			}else{
				sql = "INSERT INTO APPLICANTS VALUES (" + connection.escape(jobID) + ", " + connection.escape(staffID) + ", " + connection.escape(message) + ");";
			}

			var query = isActive + sql;	
			connection.query(query,
				function(err, res){
					if(err) {
						if(err.code == "ER_DUP_ENTRY") reject("Already applied to the job");
						else reject("Application could not be sent");
					}
					else if(isUserNotActive(res, 1)){
						reject("Cannot use MLB service until profile is complete");
					}
					else {
						resolve("Application successfully sent");
					}
			});
		});
	}


	//Join APPLICATIONS and JOBS tables and check where staff_ID = barstaffid and private = 0 (false) and status = available
	this.getApplications = function(staff_ID){
		return new Promise(function(resolve,reject){
			var isActive = isActiveQuery(staff_ID);

			var sql = `
			SELECT * FROM JOBS INNER JOIN APPLICANTS 
			ON JOBS.`+ constants.JOB_ID + ` = APPLICANTS.` + constants.JOB_ID + ` 
			AND APPLICANTS.` + constants.STAFF_ID + ` = ` + connection.escape(staff_ID) + ` 
			AND JOBS.` + constants.PRIVATE + ` = ` + connection.escape(0) + ` AND JOBS.` +
			constants.STATUS + ` = ` + connection.escape("available");

			var query = isActive + sql;
			connection.query(query, function(err, rows){
				if(err){
					reject("Retrival of applications unsuccessful...");
				}else if(isUserNotActive(rows, 1)){
					reject("Cannot use MLB service until profile is completed")
				}
				else {
					resolve(rows[1]);
				}
			});
		});
	}

	// delete the a job that a barstaff member has applied to given their ID and the job ID
	this.deleteApplication = function(staff_ID, job_ID){
		return new Promise(function(resolve,reject){
			var isActive = isActiveQuery(staff_ID);

			var sql = `DELETE FROM APPLICANTS WHERE ` + constants.STAFF_ID + ` = ` + connection.escape(staff_ID) +
			` AND ` + constants.JOB_ID + ` = (SELECT ` + constants.JOB_ID + ` FROM JOBS WHERE ` +
			constants.JOB_ID + ` = ` + connection.escape(job_ID) + ` AND ` + constants.STATUS + ` = ` + connection.escape("available") + 
			` AND ` + constants.PRIVATE + ` = `+ connection.escape(0) + `)`;

			var query = isActive + sql;
			connection.query(query, function(err,entries){
				console.log(entries);
				if(err) {
					console.error(err);
					reject("Job deletion unsuccessful");
				}
				else if(isUserNotActive(entries, 1)){
					reject("Cannot use MLB service until profile is complete");
				}
				else {
					console.log(entries);
					if(entries[1].affectedRows == 0) reject("No jobs of this description exist or already deleted");
					else resolve("Job successfully deleted");	
				}
			});
		});
	}

	//get the private jobs
	this.getPrivateJobsForBarstaff = function(staff_ID) {
		return new Promise(function(resolve, reject){	
			var isActive = isActiveQuery(staff_ID);

			var sql = `
				SELECT * FROM JOBS INNER JOIN APPLICANTS 
				ON JOBS.`+ constants.JOB_ID + ` = APPLICANTS.` + constants.JOB_ID + ` 
				AND APPLICANTS.` + constants.STAFF_ID + ` = ` + connection.escape(staff_ID) + ` 
				AND JOBS.` + constants.PRIVATE + ` = ` + connection.escape(1);

			var query = isActive + sql;
			connection.query(sql, function(err,res){
				if(err) {
					reject("Retrieval of applications unsuccessful");
				}
				else if(isUserNotActive(res, 1)){
					reject("Cannot use MLB service until profile is complete");	
				}
				else {
					resolve(res[1]);
				}
			});
		});
	}

	this.acceptPrivateJob = function(staff_ID, job_ID){
		return new Promise(function(resolve, reject){
			var isActive = isActiveQuery(staff_ID);

			var sql = `UPDATE JOBS SET ` + constants.STATUS + ` = ` + connection.escape("accepted") + `, ` + constants.STAFF_ID + 
			` = ` + connection.escape(staff_ID) + ` WHERE `+ constants.PRIVATE + ` = ` + connection.escape(1) + ` AND ` + 
			constants.JOB_ID + ` = (SELECT ` + constants.JOB_ID + ` FROM APPLICANTS A INNER JOIN USER U ON A.` + constants.STAFF_ID + ` = 
			U.` + constants.USER_ID + ` WHERE A.` + constants.STAFF_ID + ` = ` + connection.escape(staff_ID) +
			` AND A.` + constants.JOB_ID + ` = ` + connection.escape(job_ID) +` AND U.` + constants.ACTIVE + ` = `+ connection.escape(1) +`)`;

			var query = isActive + sql;
			connection.query(query, function(err, row){
				if(err){
					reject("No job to accept");	
				}
				else if(isUserNotActive(row, 1)){
					reject("Ccanot user MLB service until profile is complete");
				}
				else{
					if(row[1].changedRows == 0){
						reject("No job to be accepted");
					}else{
						resolve("Job accepted");
					}
				}
			});	
		});
	}

	//remove job that has been rejected
	this.rejectPrivateJob = function(staff_ID, job_ID) {
		return new Promise(function(resolve, reject){	
			var isActive = isActiveQuery(staff_ID);

			var sql = `DELETE FROM APPLICANTS WHERE ` + constants.STAFF_ID + ` = ` + connection.escape(staff_ID) +
			` AND ` + constants.JOB_ID + ` = (SELECT ` + constants.JOB_ID + ` FROM JOBS WHERE ` +
			constants.JOB_ID + ` = ` + connection.escape(job_ID) + ` AND ` + constants.STATUS + ` = ` + connection.escape("available") + 
			` AND ` + constants.PRIVATE + ` = `+ connection.escape(1) + `)`;

			var query = isActive + sql;
			connection.query(query, function(err,res) {
				if(err) {
					reject("Could not delete job");
				}
				else if(isUserNotActive(res, 1)){
					reject("Ccanot user MLB service until profile is complete");
				}
				else {
					if(res[1].affectedRows == 0) reject("No job to be rejected");
					else resolve("Job rejected");
				}
			});
		});
	}


// --------------------------> Organiser functions

	// get all applicants and job information of jobs that the organiser has made, are not private.
	this.getAllApplicants = function(org_ID) {
		return new Promise(function(resolve, reject){	
			var isActive = isActiveQuery(org_ID);
		
			var applicantsSql = `SELECT J.*, U.` + constants.USER_ID + `, CONCAT(U.` + constants.FIRSTNAME + `, LEFT(U.` + constants.SURNAME + `,1)) AS username, B.`+
			constants.IMAGE + `, B.` + constants.HRATE + `, B.` + constants.NRATE + `, B.` + constants.SPECIAL + ` FROM JOBS J INNER JOIN APPLICANTS A ON J.`+
			constants.JOB_ID + ` = A.` + constants.JOB_ID + ` INNER JOIN USER U ON A.` + constants.STAFF_ID + ` = U.` + constants.USER_ID + ` INNER JOIN BARSTAFF B ON B.`+
			constants.STAFF_ID + ` = U.` + constants.USER_ID + ` WHERE J.` + constants.ORG_ID + ` = ` + connection.escape(org_ID) + ` AND J.` + constants.PRIVATE + ` = ` + 
			connection.escape(0) + ` AND J.` + constants.STATUS + ` = ` + connection.escape("available");  

			var sql = isActive + applicantsSql;

			connection.query(sql, function(err,res){
				if(err) {
					console.error(err);
					reject("Could not get applicants of this organiser's jobs");
				}
				else if(isUserNotActive(res, 1)){
					reject("Cannot user MLB service until profile is complete");
				}
				else{
					resolve(res[1]);
				}
			});
		});
	}
	// Get all organiser public job he/she has created
	this.getAllOrganiserPublicJobs = function(org_ID) {
		return new Promise(function(resolve, reject){	
			var isActive = isActiveQuery(org_ID);
		
			var pubJobsSql = `SELECT * FROM JOBS WHERE ` + constants.ORG_ID + ` = ` + connection.escape(org_ID) + ` AND ` + constants.PRIVATE + ` = ` + connection.escape(0);

			var sql = isActive + pubJobsSql ;

			connection.query(sql, function(err,res){
				if(err) {
					console.error(err);
					reject("Could not get jobs");
				}
				else if(isUserNotActive(res, 1)){
					reject("Cannot user MLB service until profile is complete");
				}
				else{
					resolve(res[1]);
				}
			});
		});
	}

	this.getOrganiserByJob = function(job_id) {
		return new Promise(function(resolve, reject) {
			sql = `
				SELECT ORGANISER.` + constants.ORG_ID + ` 
				FROM ORGANISER, JOBS
				WHERE ORGANISER.` + constants.ORG_ID + ` = JOBS.` + constants.ORG_ID + ` 
				AND JOBS.` + constants.JOB_ID + ` = ` + connection.escape(job_id) + `;
			`;

			connection.query(sql, function(err, res) {
				if (err) {
					console.error(err);
					reject(err);
				} else {
					console.error(res[0].org_ID);
					resolve(res[0].org_ID);
				}
			});
		});
	}

	this.rejectApplicant = function(org_ID, staff_ID, job_ID){
		return new Promise(function(resxolve, reject){
			var orgIsActive = isActiveQuery(org_ID);
			var staffIsActive = isActiveQuery(staff_ID);
			
			var rejectSql = `DELETE FROM APPLICANTS WHERE ` + constants.STAFF_ID + ` = ` + connection.escape(staff_ID) +
			` AND ` + constants.JOB_ID + ` = (SELECT ` + constants.JOB_ID + ` FROM JOBS WHERE ` + constants.JOB_ID + ` = `+ 
			connection.escape(job_ID) + ` AND ` + constants.ORG_ID + ` = ` + connection.escape(org_ID) + ` AND ` + constants.PRIVATE + ` = ` + connection.escape(0) + `)`;
			
			var sql = orgIsActive + staffIsActive + rejectSql;
			connection.query(sql, function(err,rows){
				if (err){
					 reject ("An error has occured in rejecting the applicants");				
				}
				else if(isUserNotActive(rows, 2)){
					reject("Cannot use MLB service until account is complete");
				}
				else{
					if(rows[2].affectedRows == 1) resolve ("Successfully rejected the applicant");
					else reject("No applicants to be rejected found");
				}
			});
		});
	}

	this.acceptApplicant = function(org_ID, staff_ID, job_ID){
		return new Promise(function(resolve, reject){
			var orgIsActive = isActiveQuery(org_ID);
			var staffIsActive = isActiveQuery(staff_ID);
			
			var acceptSql = `UPDATE JOBS SET ` + constants.STATUS + ` = ` + connection.escape("accepted") + `, ` + constants.STAFF_ID + 
			` = ` + connection.escape(staff_ID) + ` WHERE `+ constants.PRIVATE + ` = ` + connection.escape(0) + ` AND ` + 
			constants.JOB_ID + ` = (SELECT ` + constants.JOB_ID + ` FROM APPLICANTS A INNER JOIN USER U ON A.` + constants.STAFF_ID + ` = 
			U.` + constants.USER_ID + ` WHERE A.` + constants.STAFF_ID + ` = ` + connection.escape(staff_ID) +
			` AND A.` + constants.JOB_ID + ` = ` + connection.escape(job_ID) +` AND U.` + constants.ACTIVE + ` = `+ connection.escape(1) +`)`;
			
			var sql = orgIsActive + staffIsActive + acceptSql;
			connection.query(sql, function(err,rows){
				if (err){
					 reject ("An error has occured in accepting the applicants");				
				}
				else if(isUserNotActive(rows, 2)){
					reject("Cannot use MLB service until account is complete");
				}
				else{
					if(rows[2].changedRows == 1) resolve ("Successfully accepted the applicant");
					else reject("No applicants to be accepted found");
				}
			});
		});
	}

	this.getAllPrivateJobsSent = function(org_ID){
		return new Promise(function(resolve,reject){
			var isActive = isActiveQuery(org_ID);
		
			var jobsSql = `SELECT J.*, U.` + constants.USER_ID + `, CONCAT(U.` + constants.FIRSTNAME + `, LEFT(U.` + constants.SURNAME + `,1)) AS username, B.`+
			constants.IMAGE + `, B.` + constants.HRATE + `, B.` + constants.NRATE + `, B.` + constants.SPECIAL + ` FROM JOBS J INNER JOIN APPLICANTS A ON J.`+
			constants.JOB_ID + ` = A.` + constants.JOB_ID + ` INNER JOIN USER U ON A.` + constants.STAFF_ID + ` = U.` + constants.USER_ID + ` INNER JOIN BARSTAFF B ON B.`+
			constants.STAFF_ID + ` = U.` + constants.USER_ID + ` WHERE J.` + constants.ORG_ID + ` = ` + connection.escape(org_ID) + ` AND J.` + constants.PRIVATE + ` = ` + 
			connection.escape(1) + ` AND J.` + constants.STATUS + ` = ` + connection.escape("available");  

			var sql = isActive + jobsSql;
			connection.query(sql , function(err,res){
				if(err) {
					console.error(err);
					reject("Could not get list of jobs");
				}
				else if(isUserNotActive(res, 1)){
					reject("Cannot use MLB service until account is complete");
				}
				else{
					resolve(res[1]);
				}
			});
		});
	}
		
	this.assignPrivateJob = function(org_ID, staff_ID, job_ID){
		return new Promise(function(resolve,reject){
			var orgIsActive = isActiveQuery(org_ID);
			var staffIsActive = isActiveQuery(staff_ID);

			var isPrivate = `SELECT * FROM JOBS WHERE ` + constants.JOB_ID + ` = ` + connection.escape(job_ID) +
			` AND ` + constants.PRIVATE + ` = ` + connection.escape(1) + ` AND ` + constants.ORG_ID + ` = ` +
			connection.escape(org_ID) + `;`;

			function assignJob(job_ID, staff_ID){
				var insertSql =`INSERT INTO APPLICANTS(`+ constants.JOB_ID + `, `+ constants.STAFF_ID + `) `+
				`VALUES (` + connection.escape(job_ID) + `, `+ connection.escape(staff_ID) + `)`;
				return insertSql;
			}
	
			var sql = orgIsActive + staffIsActive + isPrivate; 
			connection.query(sql , function(err,res){
				if(err) {
					console.error(err);
					reject("Job assignment was unsuccessful");
				}
				else if(isUserNotActive(res, 2)){
					reject("Cannot use MLB service until account is complete");
				}
				else{
					if(res[2].length == 1){
						connection.query(assignJob(job_ID, staff_ID), (err, res) => {
							if(err) reject("Could not sent job to user");
							else resolve("Job successfuly sent");
						});
					}else{
						reject("No job found to be sent");
					}
				}
			});

		});
	} 

	this.deletePrivateJobSent = function(job_ID, org_ID){
		return new Promise(function(resolve,reject){
			var isActive = isActiveQuery(org_ID);
		
			var deleteSql = `DELETE FROM JOBS WHERE ` + constants.JOB_ID + ` = ` + connection.escape(job_ID) + ` AND ` +
			constants.ORG_ID + ` = ` + connection.escape(org_ID) + ` AND ` + constants.PRIVATE + ` = ` + connection.escape(1) +
			` AND ` + constants.STATUS + ` = ` + connection.escape("available");
			
			var sql = isActive + deleteSql;
			connection.query(sql , function(err,res){
				if(err) {
					console.error(err);
					reject("Job deletion unsuccessful");
				}
				else if(isUserNotActive(res, 1)){
					reject("Cannot use MLB service until account is complete");
				}
				else{
					console.log(res);
					if(res[1].affectedRows == 1) resolve("Job successfuly deleted");
					reject("No job found to be deleted");
				}
			});
		});
	}
// --------------------------> Job/Event functions
	
	// Create a new job using details
	this.createPublicJob = function(details) {
		console.log(details);
		return new Promise(function(resolve, reject) {
			var isActive = isActiveQuery(details[constants.USER_ID]);

			var sql = `INSERT INTO JOBS (` +
			constants.ORG_ID + `, ` +
			constants.JOBSTART + `, ` +
			constants.JOBEND + `, ` +
			constants.DURATION + `, ` +
			constants.LOCATION + `, ` +
			constants.RATE + `, ` +
			constants.SPECIAL + `, ` +
			constants.PRIVATE + `, ` +
			constants.STATUS + `) VALUES (` +
			connection.escape(details.org_ID) + `, ` +
			connection.escape(details.jobStart) + `, ` +
			connection.escape(details.jobEnd) + `, ` +
			connection.escape(details.duration) + `, ` +
			connection.escape(details.location) + `, ` +
			connection.escape(details.rate) + `, ` +
			connection.escape(details.speciality) + `, ` +
			connection.escape(details.private) + `, ` +
			connection.escape(details.status) + `);`;

			var query = isActive + sql;
			connection.query(query, function(err, res) {
				if(err) {
					console.error(err);
					reject("Unsuccessful job creation");
				}
				else if(isUserNotActive(res, 1)){
					reject("Cannot use MLB service until account is complete");
				}
				else {
					console.error(res[1]);
					resolve("Job create successfully");
				}
			});		
		});
	}

	// Create a new job using details
	this.createPrivateJob = function(staff_ID, details) {
		return new Promise(function(resolve, reject) {
		
			// check if user the job has been sent has the accout set to active = true
			var organiserIsActive = isActiveQuery(details[constants.USER_ID]);
			var barstaffIsActive = isActiveQuery(staff_ID);

			var sql = `INSERT INTO JOBS (` +
			constants.ORG_ID + `, ` +
			constants.JOBSTART + `, ` +
			constants.JOBEND + `, ` +
			constants.DURATION + `, ` +
			constants.LOCATION + `, ` +
			constants.RATE + `, ` +
			constants.SPECIAL + `, ` +
			constants.PRIVATE + `, ` +
			constants.STATUS + `) VALUES (` +
			connection.escape(details.org_ID) + `, ` +
			connection.escape(details.jobStart) + `, ` +
			connection.escape(details.jobEnd) + `, ` +
			connection.escape(details.duration) + `, ` +
			connection.escape(details.location) + `, ` +
			connection.escape(details.rate) + `, ` +
			connection.escape(details.speciality) + `, ` +
			connection.escape(details.private) + `, ` +
			connection.escape(details.status) + `);`;e
		
			function applicantInsert(job_ID, staff_ID){
				return `INSERT INTO APPLICANTS (` +
				constants.JOB_ID + `, `+
				constants.STAFF_ID + `) VALUES (` +
				connection.escape(job_ID) +`, `+
				connection.escape(staff_ID) + `);`;
			}		

			function jobDelete(job_ID, cb){
				var del = `DELETE FROM JOBS WHERE `+
				constants.JOB_ID + ` = ` +
				connection.escape(job_ID);

				connection.query(del, cb);
			}

			var sql = organiserIsActive + barstaffIsActive + insert;
			console.log(sql);
			connection.query(sql, function(err, jobInserterRes) {
				console.log(jobInserterRes);
				if(err) {
					console.error(err);
					reject(err);
				}
				else if(isUserNotActive(jobInserterRes, 2)){ 
					jobDelete(jobInserterRes[2].insertId, (err, res) => { 
						if(err) reject("Job create but could not be sent to the barstaff");
						else reject("Private job could not be created. Profile needs to be completed before unabling this feature"); 
					});
					//reject("Cannot send private job to this user, until profile is complete");
				}else {
					console.error(jobInserterRes);
					connection.query(applicantInsert(jobInserterRes[2].insertId, staff_ID), function (err, res){
						if(err){
							jobDelete(jobInserterRes[2].insertId, (err, res) => { 
								if(err) reject("Job create but could not be sent to the barstaff");
								else reject("Private job could not be created"); 
							});
						}else{
							resolve("Private job successfully created");
						}
					});
				}
			});		
		});
	}

	// Delete a job from the jobs table permanently
	this.deletePublicJob = function(job_ID, org_ID){
		
		return new Promise(function(resolve,reject){
			var isActive = isActiveQuery(org_ID);
		
			var deleteSql = `DELETE FROM JOBS WHERE ` + constants.JOB_ID + ` = ` + connection.escape(job_ID) + ` AND ` +
			constants.ORG_ID + ` = ` + connection.escape(org_ID) + ` AND ` + constants.PRIVATE + ` = ` + connection.escape(0) + 
			` AND ` + constants.STATUS + ` = ` + connection.escape("available");
			
			var sql = isActive + deleteSql;
			connection.query(sql , function(err,res){
				if(err) {
					console.error(err);
					reject("Job deletion unsuccessful");
				}
				else if(isUserNotActive(res, 1)){
					reject("Cannot use MLB service until account is complete");
				}
				else{
					console.log(res);
					if(res[1].affectedRows == 1) resolve("Job successfuly deleted");
					reject("No job found to be deleted");
				}
			});
		});
	}


	// Accept an applicant for a specific job, move the staffID value into the Job field
	this.acceptJob = function(staffID, jobID) {
		return new Promise(function(resolve, reject) {
			connection.query(
				"DELETE FROM APPLICANTS WHERE job_ID = " + connection.escape(jobID)  + ";" +
				"UPDATE JOBS SET staff_ID = " + connection.escape(staffID) + ', status = "accepted" WHERE job_ID =' + 
				connection.escape(jobID) + ";", 
				function(err,res){
					if(err) {
						console.error(err);
					}
					else console.error(res);
			});
		});
	}

	// Remove a staff ID from the applicants table for a given Job
	this.rejectRequest = function(staffID, jobID) {
		return new Promise(function(resolve, reject) {
			connection.query(
				"DELETE FROM APPLICANTS WHERE staff_ID = " + connection.escape(staffID)  + 
				" AND job_ID = " + connection.escape(jobID) + ";", 
				function(err,res){
					if(err) {
						console.error(err);
						reject(err);
					}
					else {
						console.error(res);
						resolve(true);
					}
			});
		});
	}


	this.cycleJobStatus = function(job_ID){
		return new Promise(function(resolve,reject){
			connection.query('SELECT (status) FROM JOBS WHERE JOB_ID = ?', connection.escape(job_ID), function(err,rows){
				if(err) 
				{
					console.error(err);
					reject("Job status not gettable...");
				}
				else 
				{
					if(rows[0].status == 'accepted'){
						console.log("Job is accepted. Setting to paid");
						connection.query('UPDATE JOBS SET status = "paid" WHERE job_ID = ?', job_ID ,function(err,rows){
							if(err) {
								console.error(err);
								reject("Job status set to completed FAILED...");
							}
							else {
								console.error(rows);
								resolve(true);
							}
						});
					}

					if(rows[0].status == "paid"){
						console.log("Job is paid. Setting to ongoing");
						connection.query('UPDATE JOBS SET status = "ongoing" WHERE job_ID = ?', job_ID ,function(err,rows){
							if(err) {
								console.error(err);
								reject("Job status set to ongoing FAILED...");
							}
							else {
								console.error(rows);
								resolve(true);
							}
						});
					}

					if(rows[0].status == "ongoing"){
						console.log("Job is ongoing. Setting to completed");
						connection.query('UPDATE JOBS SET status = "completed" WHERE job_ID = ?', job_ID ,function(err,rows){
							if(err) {
								console.error(err);
								reject("Job status set to completed FAILED...");
							}
							else {
								console.error(rows);
								resolve(true);
							}
						});
					}

					if(rows[0].status == "completed"){
						console.log("Job is already completed");
					}
					else{
						resolve("Unsuccessful");
					}
				}
			});
		});
	}


	//Get the private jobs only
	this.getPrivateJobs = function() {
		return new Promise(function(resolve, reject) {
			sql = `
				SELECT * FROM JOBS
				WHERE ` + constants.PRIVATE + ` = 1;
			`;

			connection.query(sql, function(err,rows) {
				if(err) {
					console.error(err);
					reject(err);
				} else {
					console.error(rows);
					resolve(rows);
				}
			});
		});
	}

	// Get jobs that have applicants but are not yet accepted 

	this.getJobsWithApplicants = function() {
		return new Promise(function(resolve, reject) {
			sql = `SELECT * FROM JOBS INNER JOIN APPLICANTS ON APPLICANTS.job_ID = JOBS.job_ID WHERE JOBS.`+constants.STATUS+` = 'available';`;

			connection.query(sql, function(err,rows) {
				if(err) {
					console.error(err);
					reject(err);
				} else {
					console.error(rows);
					resolve(rows);
				}
			});
		});
	}

	//function that returns all private jobs for a particular user
	this.getJobRequests = function(staff_Id) {
		return new Promise(function(resolve, reject) {
			sql = `
				SELECT * FROM JOBS
				WHERE ` + constants.STAFF_ID + ` = ` + connection.escape(staff_Id) + ` 
				AND ` + constants.PRIVATE + ` = 1;
			`;
			connection.query(sql, function(err, rows){
				if(err) {
					console.error(err);
					reject(err);
				} else {
					console.error(rows);
					resolve(rows);
				}
			});
		});
	}

	//function that returns all the jobs posted by a particular organiser
	this.getOrganiserJobs = function(org_Id) {
		return new Promise(function(resolve, reject) {
			sql = `
				SELECT * FROM JOBS
				WHERE ` + constants.ORG_ID + ` = ` + connection.escape(org_Id) + `;
			`;
			connection.query(sql, function(err, rows){
				if(err) {
					console.error(err);
					reject(err);
				} else {
					console.error(rows);
					resolve(rows);
				}
			});
		});
	}




	this.updatejobdetails = function(job_id, key, value) {
		return new promise(function(resolve,reject){
			sql=`update jobs set `+key+` = `+connection.escape(value)+` where job_id = `+connection.escape(job_id)+`;`;
			connection.query(sql, function(err,rows){
				if (err) reject (err);				
				else resolve (rows);
			});
		});
	}




	this.getAllPrivateJobSent = function(organiser) {
		return new Promise(function(resolve, reject){	
			sql = `
				SELECT * FROM JOBS, USER where JOBS.org_ID = ` + connection.escape(organiser) + `
				and JOBS.private = 1 
				and JOBS.status = 'available'
				and JOBS.staff_ID = USER.user_ID;`;
			connection.query(sql, function(err,res) {
				if(err) {
					console.error(err);
					reject("Could not get private jobs by this organiser");
				}
				else {
					console.error(res);
					resolve(true);
				}
			});
		});
	}


// --------------------------> GET/REMOVE/UPDATE USER FUNCTIONS

	this.removeUser = function(user_ID) {
		return new Promise(function(resolve,reject){
			connection.query('DELETE FROM USER WHERE USER_ID = ?', user_ID, function(err,res){
				if(err) {
					console.error(err);
					reject("Account deletion unsuccessful...");
				}
				else {
					if(res.affectedRows == 0) {
						reject("No user of that id exists")
					} else {
						console.error(res);
						resolve(true);
					}
				}
			});
		});
	}


	this.getUser = function(user_ID) {
		return new Promise(function (resolve, reject){
			//if the user is a bartender then select from bartender table else try the organiser table
			sql = `
				select * from USER, BARSTAFF WHERE EXISTS (SELECT account_type from USER, BARSTAFF where user.user_ID = ` + connection.escape(user_ID) + ` and user.account_type = 'BARSTAFF') and user.user_id = barstaff.staff_id and user.user_id = ` + connection.escape(user_ID) + `;
				select * from USER, ORGANISER WHERE EXISTS (SELECT account_type from USER, ORGANISER where user.user_ID = ` + connection.escape(user_ID) + ` and user.account_type = 'ORGANISER') and user.user_id = ORGANISER.org_id and user.user_id = ` + connection.escape(user_ID) + `;
			`;

			connection.query(sql, function(err, rows) {
				if(err) {
					console.log(err);
					reject(err);
				} else {
					if(rows[0].length == 1){
						console.log(rows[0][0]);
						resolve(rows[0][0]);
					} else {
						console.log(rows[1][0]);
						resolve(rows[1][0]);
					}
				}
			});
		});

	}

	//returns object with user details
	this.getUserByEmail = function(email) {
		return new Promise(function (resolve, reject){

			sql = `
				SELECT * FROM USER, BARSTAFF
				WHERE EXISTS (SELECT ` + constants.ACCOUNT_TYPE + ` 
								FROM USER 
								WHERE ` + constants.EMAIL + ` = ` + connection.escape(email) + ` 
								AND  ` + constants.ACCOUNT_TYPE + ` = 'BARSTAFF')
				AND USER.user_ID = BARSTAFF.staff_ID
				AND  ` + constants.EMAIL + ` = ` + connection.escape(email) + `;

				SELECT * FROM USER, ORGANISER
				WHERE EXISTS (SELECT ` + constants.ACCOUNT_TYPE + ` 
								FROM USER 
								WHERE ` + constants.EMAIL + ` = ` + connection.escape(email) + ` 
								AND  ` + constants.ACCOUNT_TYPE + ` = 'ORGANISER')
				AND USER.user_ID = ORGANISER.org_ID
				AND  ` + constants.EMAIL + ` = ` + connection.escape(email) + `;
			`;

			connection.query(sql, function(err, rows){
				if(err) {
					console.error(err);
					reject(err);
				} else {
					if(rows[0].length == 1){
						console.log(rows[0][0]);
						resolve(rows[0][0]);
					} else {
						console.log(rows[1][0]);
						resolve(rows[1][0]);
					}
				}
			});
		});

	}


	this.getBarstaffProfile = function(staff_Id) {
		return new Promise(function(resolve, reject) {
			sql = `
				SELECT USER.` + constants.FIRSTNAME + `, USER.` + constants.SURNAME + `, BARSTAFF.` + constants.IMAGE + `, BARSTAFF.` + constants.SPECIAL + `, BARSTAFF.` + constants.HRATE + `, BARSTAFF.` + constants.NRATE + ` 
				FROM USER, BARSTAFF
				WHERE BARSTAFF.` + constants.STAFF_ID + ` = USER.` + constants.USER_ID + ` 
				AND USER.` + constants.USER_ID + ` = ` +  connection.escape(staff_Id) + `;
			`;
			connection.query(sql, function(err, rows){
				if(err) {
					console.error(err);
					reject(err);
				} else {
					console.error({
						username: rows[0].first_name.substr(0,1) + ". " + rows[0].last_name,
						image_path: rows[0].image_path, speciality: rows[0].speciality,
						hour_rate: rows[0].hour_rate,
						night_rate: rows[0].night_rate
					});
					resolve({
						username: rows[0].first_name.substr(0,1) + ". " + rows[0].last_name,
						image_path: rows[0].image_path,
						speciality: rows[0].speciality,
						hour_rate: rows[0].hour_rate,
						night_rate: rows[0].night_rate
					});
				}
			});
		});
	}

	this.getAllOrganisers = function(){
		sql = `SELECT * FROM USER INNER JOIN ORGANISER on ORGANISER.org_id = USER.user_id WHERE USER.active = 1;`;
		return new Promise(function (resolve, reject){
			connection.query(sql, function(err, rows) {
				if(err){
					console.error(err);
					reject(err);
				}else{
					console.log(rows);
					resolve(rows);
				}
			});
		});
	}


	//FUNCTION TO UPDATE BARSTAFF
	this.updateBarstaff = function(user_Id, user_details, barstaff_details) {
		return new Promise(function(resolve, reject){
			connection.query(`
				UPDATE USER SET ? WHERE ` + 
				constants.USER_ID + ' = ' + connection.escape(user_Id) + `;
				UPDATE BARSTAFF SET ? WHERE ` + 
				constants.STAFF_ID + ' = ' + connection.escape(user_Id) + `;
				`, [user_details, barstaff_details], function(err,res){
				if(err) {
					console.error(err);
					reject(err);
				} else {
					console.error(res);
					resolve(true);
				}
			})
		});
	}

	//FUNCTION TO UPDATE ORGANISER
	this.updateOrganiser = function(user_Id, user_details, organiser_details) {
		return new Promise(function(resolve, reject){
			connection.query(`
				UPDATE USER SET ? WHERE ` + 
				constants.USER_ID + ' = ' + connection.escape(user_Id) + `;
				UPDATE ORGANISER SET ? WHERE ` + 
				constants.ORG_ID + ' = ' + connection.escape(user_Id) + `;
				`, [user_details, organiser_details], function(err,res){
				if(err) {
					console.error(err);
					reject(err);
				} else {
					console.error(res);
					resolve(true);
				}
			});
		});
	}

	//FUNCTION TO UPDATE JOB DETAILS
	this.updateJob = function(job_id, job_details) {
		return new Promise(function(resolve, reject){
			connection.query(`
				UPDATE USER SET ? WHERE ` + 
				constants.JOB_ID + ' = ' + connection.escape(job_id) + `;
				`, job_details, function(err,res){
				if(err) {
					console.error(err);
					reject(err);
				} else {
					console.error(res);
					resolve(true);
				}
			});
		});
	}

	this.updatePicture = function(user_ID, newPicturePath) {
		return new Promise(function(resolve,reject){
			sql =
			`SELECT `+constants.IMAGE+` FROM BARSTAFF WHERE ` + constants.STAFF_ID + ` = `+connection.escape(user_ID)+`;` +
			`UPDATE BARSTAFF SET `+constants.IMAGE+` = `+connection.escape(newPicturePath)+` WHERE ` + constants.STAFF_ID + ` = `+connection.escape(user_ID)+`;`;

			connection.query(sql, function(err,rows){
				if (err) {
					console.error(err);
					reject (err);
				}			
				else {
					console.error(rows[0][0].image_path);
					resolve (rows[0][0].image_path);
				}
			});
		});
	}


// -------------------------->  Chat functionality


	this.createChat = function(details){
		return new Promise(function(resolve, reject) {
			connection.query('INSERT INTO CHATS SET ?', details , function(err, res) {
				if(err) {
					console.error(err);
					reject(err);
				}
				else {
					console.error(res);
					resolve(true);
				}
			});		
		});
	}

	this.sendMessage = function(details){
		return new Promise(function(resolve, reject) {
			connection.query('INSERT INTO CHATLOGS SET ?', details , function(err, res) {
				if(err) {
					console.error(err);
					reject(err);
				}
				else {
					console.error(res);
					resolve(true);
				}
			});		
		});
	}

// --------------------------> FAVOURITES

	this.addFavourite = function(org_ID, staff_ID) {
		return new Promise(function(resolve, reject) {
			connection.query('INSERT INTO FAVOURITES SET ' + constants.ORG_ID + ' = ?, ' + constants.STAFF_ID + ' = ?;', [org_ID, staff_ID], function(err, res) {
				if(err) {
					console.error(err);
					reject(err);
				}
				else {
					console.error(res);
					resolve(true);
				}
			});		
		});
	}

	this.removeFavourite = function(org_ID, staff_ID) {
		return new Promise(function(resolve, reject) {
			connection.query('DELETE FROM FAVOURITES WHERE ' + constants.ORG_ID + ' = ? AND ' + constants.STAFF_ID + ' = ?;', [org_ID, staff_ID], function(err, res) {
				if(err) {
					console.error(err);
					reject(err);
				}
				else {
					console.error(res);
					resolve(true);
				}
			});		
		});
	}

	this.getFavourites = function(org_ID) {
		return new Promise(function(resolve, reject) {
			connection.query('SELECT * FROM FAVOURITES WHERE ' + constants.ORG_ID + ' = ?;', [org_ID], function(err, res) {
				if(err) {
					console.error(err);
					reject(err);
				}
				else {
					console.error(res);
					resolve(true);
				}
			});		
		});
	}

// --------------------------> AVAILABILITY

	this.addAvailability = function(details) {
		return new Promise(function(resolve, reject) {
			connection.query('INSERT INTO FAVOURITES SET ?', details, function(err, res) {
				if(err) {
					console.error(err);
					reject(err);
				}
				else {
					console.error(res);
					resolve(true);
				}
			});		
		});
	}

	this.removeAvailibility = function(cal_ID) {
		return new Promise(function(resolve, reject) {
			connection.query('DELETE FROM AVAILABILITY WHERE ' + constants.CAL_ID + ' = ?;', [cal_ID], function(err, res) {
				if(err) {
					console.error(err);
					reject(err);
				}
				else {
					console.error(res);
					resolve(true);
				}
			});		
		});
	}

	this.getAvailibility = function(staff_ID) {
		return new Promise(function(resolve, reject) {
			connection.query('SELECT * FROM AVAILABILITY WHERE ' + constants.STAFF_ID + ' = ?;', [staff_ID], function(err, res) {
				if(err) {
					console.error(err);
					reject(err);
				}
				else {
					console.error(res);
					resolve(true);
				}
			});		
		});
	}

}
