const mysql = require('mysql');

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
		NO: "no",
		STREET_NAME: "street_name",
		EVENT_TYPE: "event_type",
		EXP: "experience",
		STRIPE_TOKEN: "stripe_token",
		CARD_TOKEN : "card_token",
		ACCEPTED : "accepted",
		
		ORG_NAME : "org_name",
		PROF_POS : "prof_pos",

		CAL_ID : "cal_ID",
		DAY : "day",

		START_TIME: "start_time",
		END_TIME: "end_time",
		START_MERIDIEM: "start_meridiem",
		END_MERIDIEM: "end_meridiem",

		JOB_ID : "job_ID",
		TITLE : "title",
		JOB_START : "job_start",
		JOB_END : "job_end",
		LOCATION : "location",
		PAY : "pay",
		STATUS : "status",
		AVAILABLE : "availability",
		PRIVATE: "private",
		TOKEN : "token",
		CHAT_OFFER : "offer",
		CHAT_MESSAGE : "chat_message"
	}
	this.constants = constants;


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


	function getUsername(){
		return ` CONCAT(USER.first_name, LEFT(USER.last_name,1)) AS username, `;
	}

	function formatDate(field){
		return `DATE_FORMAT(`+ field + `, "%d-%m%-%Y") `;

	}

	function formatJOBSDateTime(){
		return `DATE_FORMAT(JOBS.job_creation, "%d-%m-%Y %r") AS job_creation,
			DATE_FORMAT(JOBS.job_start, "%d-%m-%Y %r") AS job_start,
			DATE_FORMAT(JOBS.job_end, "%d-%m-%Y %r") AS job_end, `
	}

	function formatUSERDOB(){
		return `DATE_FORMAT(USER.DOB, "%d-%m-%y") AS DOB, `;
	}

//---------OTHER
	
	function sqlAcceptApplicant(staff_ID, job_ID){
		return `UPDATE APPLICANTS SET accepted = 1 WHERE staff_ID = ` + connection.escape(staff_ID) + ` AND job_ID = ` + connection.escape(job_ID) + `;`;
	}


	function sqlWithdraw(staff_ID, job_ID){
		return `DELETE FROM APPLICANTS WHERE staff_ID = `+ connection.escape(staff_ID) + 
		` AND job_ID = ` + connection.escape(job_ID) +`;`;
	}

	function sqlAcceptPrivateOffer(staff_ID, job_ID){
		var sql = `UPDATE JOBS SET ` + constants.STATUS + ` = ` + connection.escape("accepted") + `, ` + constants.STAFF_ID + 
		` = ` + connection.escape(staff_ID) + ` WHERE `+ constants.PRIVATE + ` = ` + connection.escape(1) + ` AND ` + 
		constants.JOB_ID + ` = (SELECT ` + constants.JOB_ID + ` FROM APPLICANTS A INNER JOIN USER U ON A.` + constants.STAFF_ID + ` = 
		U.` + constants.USER_ID + ` WHERE A.` + constants.STAFF_ID + ` = ` + connection.escape(staff_ID) +
		` AND A.` + constants.JOB_ID + ` = ` + connection.escape(job_ID) +` AND U.` + constants.ACTIVE + ` = `+ connection.escape(1) +`);`;
		return sql;
	}


	function sqlAcceptStaffApplication(staff_ID, job_ID){
		var sql = `UPDATE JOBS SET ` + constants.STATUS + ` = ` + connection.escape("accepted") + `, ` + constants.STAFF_ID + 
		` = ` + connection.escape(staff_ID) + ` WHERE `+ constants.PRIVATE + ` = ` + connection.escape(0) + ` AND ` + 
		constants.JOB_ID + ` = (SELECT ` + constants.JOB_ID + ` FROM APPLICANTS A INNER JOIN USER U ON A.` + constants.STAFF_ID + ` = 
		U.` + constants.USER_ID + ` WHERE A.` + constants.STAFF_ID + ` = ` + connection.escape(staff_ID) +
		` AND A.` + constants.JOB_ID + ` = ` + connection.escape(job_ID) +` AND U.` + constants.ACTIVE + ` = `+ connection.escape(1) +`);`;
		return sql;
	}
	
	function sqlGetAllBarStaffPublicProfile(){
		var barstaffSqlPublicProfile= `SELECT user_ID, CONCAT(` + constants.FIRSTNAME + `, LEFT(` + constants.SURNAME + 
		`, 1)) AS username, image_path, experience, hour_rate, night_rate, speciality FROM USER INNER JOIN BARSTAFF ON BARSTAFF.staff_ID = USER.user_ID;`;
		return barstaffSqlPublicProfile;
	}

	function sqlGetOrganiserPublicJobs(org_ID){
		var pubJobsSql = `SELECT JOBS.job_ID, JOBS.org_ID, JOBS.staff_ID, JOBS.title,`
		+ formatJOBSDateTime() + 
		`JOBS.duration, JOBS.postcode, JOBS.street_name, JOBS.no, JOBS.city,
		JOBS.rate, JOBS.description, JOBS.speciality, JOBS.type, JOBS.private, JOBS.status FROM JOBS WHERE JOBS.org_ID =`+ connection.escape(org_ID) +` AND 
		JOBS.staff_ID IS NULL AND JOBS.private = 0;`;
		return pubJobsSql;
	}

	function sqlGetPrivateJobs(org_ID){
		var priJobsSql = `SELECT JOBS.job_ID, JOBS.org_ID, JOBS.staff_ID, JOBS.title,`
		+ formatJOBSDateTime() + 
		`JOBS.duration, JOBS.postcode, JOBS.street_name, JOBS.no, JOBS.city,
		JOBS.rate, JOBS.description, JOBS.speciality, JOBS.type, JOBS.private, JOBS.status FROM JOBS WHERE JOBS.org_ID =`+ connection.escape(org_ID) +` AND 
		JOBS.staff_ID IS NULL AND JOBS.private = 1;`;
		return priJobsSql;
	}

	function sqlGetApplicants(org_ID){
		var applicantsSql = `
		SELECT JOBS.job_ID, JOBS.org_ID, A.staff_ID, JOBS.title, JOBS.duration, JOBS.postcode, JOBS.street_name, JOBS.no, JOBS.city, JOBS.rate, JOBS.description, 
		JOBS.speciality, JOBS.type, JOBS.private, JOBS.status, ` + formatJOBSDateTime() + ` A.message, ` 
		+ ` U.` + constants.USER_ID + `, CONCAT(U.` + constants.FIRSTNAME + `, LEFT(U.` + constants.SURNAME + `,1)) AS username, B.`+
		constants.IMAGE + `, B.` + constants.HRATE + `, B.` + constants.NRATE + `, B.` + constants.SPECIAL + ` FROM JOBS INNER JOIN APPLICANTS A ON JOBS.`+
		constants.JOB_ID + ` = A.` + constants.JOB_ID + ` INNER JOIN USER U ON A.` + constants.STAFF_ID + ` = U.` + constants.USER_ID + ` INNER JOIN BARSTAFF B ON B.`+
		constants.STAFF_ID + ` = U.` + constants.USER_ID + ` WHERE JOBS.` + constants.ORG_ID + ` = ` + connection.escape(org_ID) + ` AND JOBS.` + constants.PRIVATE + ` = ` + 
		connection.escape(0) + ` AND JOBS.` + constants.STATUS + ` = ` + connection.escape("available") + ` AND A.accepted = 0;`;

		return applicantsSql;
	}

	function sqlGetPrivatejobsSent(org_ID){
		var privateOffersSent = `SELECT JOBS.job_ID, JOBS.org_ID, A.staff_ID, JOBS.title, `
		+ formatJOBSDateTime() + 
		` JOBS.duration, JOBS.postcode, JOBS.street_name,
		JOBS.no, JOBS.city, JOBS.rate, JOBS.description, JOBS.speciality, JOBS.type, JOBS.private, JOBS.status,
		U.` + constants.USER_ID + `, CONCAT(U.` + constants.FIRSTNAME + `, LEFT(U.` + constants.SURNAME + `,1)) AS username, B.`+
		constants.IMAGE + `, B.` + constants.HRATE + `, B.` + constants.NRATE + `, B.` + constants.SPECIAL + ` FROM JOBS INNER JOIN APPLICANTS A ON JOBS.`+
		constants.JOB_ID + ` = A.` + constants.JOB_ID + ` INNER JOIN USER U ON A.` + constants.STAFF_ID + ` = U.` + constants.USER_ID + ` INNER JOIN BARSTAFF B ON B.`+
		constants.STAFF_ID + ` = U.` + constants.USER_ID + ` WHERE JOBS.` + constants.ORG_ID + ` = ` + connection.escape(org_ID) + ` AND JOBS.` + constants.PRIVATE + ` = ` + 
		connection.escape(1) + ` AND JOBS.` + constants.STATUS + ` = ` + connection.escape("available")+ ` AND A.accepted =0;`;
		return privateOffersSent;
	}

	function getOrgProfile(org_ID){
		var orgProfile = `SELECT USER.user_ID, ` + formatUSERDOB() + getUsername() + `
		USER.account_type, USER.first_name, USER.last_name, USER.gender, USER.email_address,
		USER.mobile_number, USER.postcode, USER.city, USER.town, USER.active,
		ORGANISER.prof_pos, ORGANISER.event_type FROM USER INNER JOIN ORGANISER ON USER.user_ID = ORGANISER.org_ID WHERE USER.user_ID = `+ 
		connection.escape(org_ID)+`;`;
		return orgProfile;
	}

	function getStaffAvailability(){
		var av = `SELECT staff_ID, day, ` + formatAvailability() + ` FROM AVAILABILITY;`;
		return av;
	}

	function sqlChatJobs(org_ID){
		return `SELECT APPLICANTS.job_ID, JOBS.title, `+ getUsername() + ` APPLICANTS.staff_ID FROM APPLICANTS INNER JOIN JOBS ON APPLICANTS.job_ID = JOBS.job_ID 
		INNER JOIN USER ON APPLICANTS.staff_ID = USER.user_ID WHERE APPLICANTS.accepted = 1 AND JOBS.org_ID = ` +
		connection.escape(org_ID) + `;`;
	}

	this.organiserSignin = function(org_ID){
		return new Promise(function(resolve, reject){
			var isActive = isActiveQuery(org_ID);

			var staff_availability = getStaffAvailability();
			var barStaffProfiles = sqlGetAllBarStaffPublicProfile();
			var pubJobs = sqlGetOrganiserPublicJobs(org_ID);
			var privateJobs = sqlGetPrivateJobs(org_ID);
			var applicants = sqlGetApplicants(org_ID);
			var privateJobSent = sqlGetPrivatejobsSent(org_ID);
			var profile = getOrgProfile(org_ID);
			var chat = sqlChatJobs(org_ID);	
		
			console.log(pubJobs);
			console.log("+++++++==========");
			var sql = isActive + barStaffProfiles + staff_availability + pubJobs + privateJobs + applicants + privateJobSent + profile + chat;

			connection.query(sql, function(err, rows) {
				if(err) {
					console.error(err);
					reject(err);
				} else if(isUserNotActive(rows, 1)){
					resolve({
						"profile": rows[5][0]
					});
					//reject("Cannot use MLB service until account is complete");
				}else {
					console.log(rows);
					resolve( { 
					        "adverts": rows[1], 
						"staff_availability": rows[2],
						"public_jobs": rows[3], "private_jobs": rows[4], 
						"answers": rows[6],  
						"requests": rows[5], //applicants
						"profile": rows[7][0],
						"chat_jobs": rows[8]
					});
				}
			});
		});
	}

	function formatAvailability(){
		return ` TIME_FORMAT(AVAILABILITY.start_time, "%r") AS start, TIME_FORMAT(AVAILABILITY.end_time, "%r") AS end `; 	
	}


	function sqlGetJobs(){
		var jobSql = `SELECT JOBS.job_ID, JOBS.org_ID, JOBS.staff_ID, JOBS.title, CONCAT(U.first_name, LEFT(U.last_name,1)) as username, ` 
		+ formatJOBSDateTime() +
		` JOBS.duration, JOBS.postcode, JOBS.street_name, JOBS.no, JOBS.city,
		JOBS.rate, JOBS.description, JOBS.speciality, JOBS.type, JOBS.private, JOBS.status
		FROM JOBS INNER JOIN USER U ON U.user_ID = JOBS.org_ID WHERE ` +  constants.PRIVATE + ` = ` + connection.escape(0)+`;`; 
		return jobSql;
	}

	function sqlGetBarStaffApplications(staff_ID){
		var sqlBarStaffApplications = `SELECT JOBS.job_ID, JOBS.org_ID, JOBS.staff_ID, JOBS.title, `+ formatJOBSDateTime() +`
		JOBS.duration, JOBS.postcode, JOBS.rate, JOBS.city, JOBS.no, JOBS.description, JOBS.street_name, JOBS.speciality, JOBS.type,
		CONCAT(U.first_name, LEFT(U.last_name,1)) AS username, JOBS.speciality, JOBS.type, JOBS.private, JOBS.status FROM JOBS INNER JOIN USER
		U ON U.user_ID = JOBS.org_ID INNER JOIN APPLICANTS 
		ON JOBS.`+ constants.JOB_ID + ` = APPLICANTS.` + constants.JOB_ID + ` 
		AND APPLICANTS.` + constants.STAFF_ID + ` = ` + connection.escape(staff_ID) + ` 
		AND JOBS.` + constants.PRIVATE + ` = ` + connection.escape(0) + ` AND JOBS.` +
		constants.STATUS + ` = ` + connection.escape("available")+` AND APPLICANTS.accepted =0;`;

		return sqlBarStaffApplications;
	}

	function sqlGetPrivateJobsOffered(staff_ID){
		var privateJobOffered = `SELECT JOBS.job_ID, JOBS.org_ID, APPLICANTS.staff_ID, JOBS.title, ` + getUsername() +  formatJOBSDateTime() + 
			` JOBS.description, JOBS.postcode, JOBS.street_name, JOBS.no, JOBS.city, JOBS.rate, JOBS.speciality, JOBS.type, JOBS.duration, JOBS.status FROM JOBS 
			INNER JOIN USER ON USER.user_ID = JOBS.org_ID INNER JOIN APPLICANTS 
			ON JOBS.`+ constants.JOB_ID + ` = APPLICANTS.` + constants.JOB_ID + ` 
			AND APPLICANTS.` + constants.STAFF_ID + ` = ` + connection.escape(staff_ID) + ` 
			AND JOBS.` + constants.PRIVATE + ` = ` + connection.escape(1)+` AND APPLICANTS.accepted = 0;`;
		return privateJobOffered;
	}

	function sqlGetBarStaffProfile(staff_ID){
		var barStaffProfile = `SELECT USER.user_ID, ` + formatUSERDOB() + getUsername() + `
		USER.account_type, USER.first_name, USER.last_name, USER.gender, USER.email_address,
		USER.mobile_number, USER.postcode, USER.city, USER.town, USER.active,
		BARSTAFF.speciality, BARSTAFF.description, BARSTAFF.proximity, BARSTAFF.experience,
		BARSTAFF.hour_rate, BARSTAFF.night_rate, BARSTAFF.image_path
		FROM USER INNER JOIN BARSTAFF ON USER.user_ID = BARSTAFF.staff_ID WHERE USER.user_ID = `+ 
		connection.escape(staff_ID)+`;`;
		return barStaffProfile;
	}

	function sqlGetAvailability(staff_ID){
		var sqlAvailability = `SELECT AVAILABILITY.day, `+ formatAvailability() + `FROM AVAILABILITY WHERE ` + constants.STAFF_ID + ` = `+ connection.escape(staff_ID) + `;`;
		return sqlAvailability;
	}


	function sqlChat(staff_ID){
		return ` SELECT APPLICANTS.job_ID,`+ getUsername() +` JOBS.org_ID, JOBS.title FROM APPLICANTS INNER JOIN JOBS ON JOBS.job_ID = APPLICANTS.job_ID 
		INNER JOIN USER ON USER.user_ID = JOBS.org_ID WHERE APPLICANTS.accepted = 1 AND APPLICANTS.staff_ID = ` + connection.escape(staff_ID) +`;`;
	}

	this.barStaffSignin = function(staff_ID){
		return new Promise(function(resolve, reject){
			var isActive = isActiveQuery(staff_ID);

			var jobSql = sqlGetJobs();	
			var barStaffApplications = sqlGetBarStaffApplications(staff_ID);
			var privateJobsOffered = sqlGetPrivateJobsOffered(staff_ID);
			var barStaffProfile = sqlGetBarStaffProfile(staff_ID);
			var availability = sqlGetAvailability(staff_ID);
			var chat = sqlChat(staff_ID);
		
			var sql = isActive + jobSql + barStaffApplications + privateJobsOffered + barStaffProfile + availability + chat;
			console.log("SQL: "+sql);

			connection.query(sql, function(err, rows) {
				if(err) {
					console.error(err);
					reject(err);
				}else if(isUserNotActive(rows, 1)){
					resolve({
						"profile": rows[4][0]
					});
					//reject("Cannot use MLB service until account is complete");
				
				} else {
					console.log('Connection established');
					var profile = rows[4][0];
					profile["availability"] = rows[5];
					resolve( { 
						"adverts": rows[1], 
						"answers": rows[2], 
						"requests": rows[3],
						"profile": profile,
						"chat_jobs": rows[6]
					});
				}
			});
		});
	}

	function getOrganiserIdByJobId(job_ID){
		return `SELECT org_ID FROM JOBS WHERE job_ID = `+ connection.escape(job_ID) + `;`;
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
			
			var jobSql = `SELECT JOBS.job_ID, JOBS.org_ID, JOBS.staff_ID, JOBS.title, CONCAT(U.first_name, LEFT(U.last_name,1)) as username, ` 
			+ formatJOBSDateTime() +
		  	` JOBS.duration, JOBS.postcode, JOBS.street_name, JOBS.no, JOBS.city,
			JOBS.rate, JOBS.description, JOBS.speciality, JOBS.type, JOBS.private, JOBS.status
			FROM JOBS INNER JOIN USER U ON U.user_ID = JOBS.org_ID WHERE ` +  constants.PRIVATE + ` = ` + connection.escape(0);
			
			var sql = isActive + jobSql;

			connection.query(sql, function(err, rows) {
				if(err) {
					console.log(err);
					reject("An error has occured during log in");
				}
				/*else if(isUserNotActive(rows, 1)){
					resolve(rows[1]);
					//reject("Cannot use MLB service until account is complete");
				}*/
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
			`, 1)) AS username, image_path, hour_rate, night_rate, speciality FROM USER INNER JOIN BARSTAFF ON BARSTAFF.staff_ID = USER.user_ID;`;

			var sql = isActive + barstaffSql;

			connection.query(sql, function(err, rows) {
				if(err){
					console.error(err);
					reject("An error has occured during log in");
				}
				/*else if(isUserNotActive(rows, 1)){
					resolve(rows[1]);
					//reject("Cannot use MLB service until account is complete");
				}*/
				else{
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
					reject(err);
				} else {
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

	//FUNCTION TO UPDATE BARSTAFF
	this.completeOrganiserProfile = function(user_Id, user_details, organiser_details) {
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
					resolve("Profile successfully completed");
				}
			});
		});
	}

	function completeUserProfile(user_ID, userDetails){
		var sqlUser = `UPDATE USER SET ` +
			constants.FIRSTNAME +  ` = ` + connection.escape(userDetails.first_name) +  `, ` +
			constants.SURNAME + ` = ` + connection.escape(userDetails.last_name) +  `, ` + 
			constants.GENDER + ` = ` + connection.escape(userDetails.gender) +  `, ` +  
			constants.POSTCODE + ` = ` + connection.escape(userDetails.postcode) + `, `+ 
			constants.STRIPE_TOKEN + ` = ` + connection.escape(userDetails.stripe_token) + `
			WHERE `+ constants.USER_ID + ` = ` + connection.escape(user_ID) + ` 
			AND ` + constants.ACTIVE + ` = ` + connection.escape(0) + ';';
		return sqlUser;
	}


	// TODO REMEBER TO CHANGE VALUE TO 1
	function completeUserRegistration(user_ID){
		var sqlActivate = `UPDATE USER SET ` +  constants.ACTIVE + ` = ` + connection.escape(1) + 
				` WHERE ` + constants.USER_ID + ` = ` + connection.escape(user_ID) + 
				` AND ` + constants.ACTIVE + ` = ` + connection.escape(0) + ';'; 
		return sqlActivate;
	}
	
	// Update user card details when they register to the app
	this.updateUserCardDetails = function(user_ID, stripe_token, card_id, last_4){
		return new Promise(function(resolve, reject){
			var updateCardDetails = `UPDATE USER SET stripe_token = ` +
						connection.escape(stripe_token) + `,
						card_id = `+ connection.escape(card_id) + `,
						last_4 =`+ connection.escape(last_4) + `
						WHERE user_ID =`+ connection.escape(user_ID)+`;`; 
						
			var sqlActivate = completeUserRegistration(user_ID);

			var sql = updateCardDetails + sqlActivate;

			connection.query(sql, function(err, res){
				if(err){
					console.log(err)
					reject(err);
				}else{
					console.log(res);
					resolve(res);
				}
			});
		});
	}

	this.completeBarstaffRegistration = function(user_ID, userDetails, barDetails){
		return new Promise(function(resolve, reject){

			var sqlUser = completeUserProfile(user_ID, userDetails);

			var sqlBarstaff = `
					UPDATE BARSTAFF INNER JOIN USER ON BARSTAFF.staff_ID = USER.user_ID SET 
					BARSTAFF.` + constants.HRATE +  ` = ` + connection.escape(barDetails.hour_rate) + `, 
					BARSTAFF.` + constants.NRATE + ` = ` + connection.escape(barDetails.night_rate) + `,
					BARSTAFF.` + constants.SPECIAL + ` = ` + connection.escape(barDetails.speciality) + `,
					BARSTAFF.` + constants.EXP + ` = ` + connection.escape(barDetails.experience) + ` 
					WHERE BARSTAFF.staff_ID = ` + connection.escape(user_ID) + ` AND USER.active = ` + connection.escape(0) + ';';

			//var sqlActivate = completeUserRegistration(user_ID);

			var sql = sqlUser +  sqlBarstaff; //+ sqlActivate;

			connection.query(sql, function(err,res){
					if(err){
						console.log(err);
					       	reject("Updates could not be completed"); 
					}else{
						console.log(res);
						resolve("Updates successfully completed"); 
					}
			});
		});
	}	

	this.completeOrganiserRegistration = function(user_ID, userDetails, orgDetails) {
		return new Promise(function(resolve, reject){

			var sqlUser = completeUserProfile(user_ID, userDetails);

			var sqlOrganiser = `
					UPDATE ORGANISER INNER JOIN USER ON ORGANISER.org_ID = USER.user_ID SET 
					ORGANISER.` + constants.PROF_POS +  ` = ` + connection.escape(orgDetails.prof_pos) + `, 
					ORGANISER.` + constants.EVENT_TYPE + ` = ` + connection.escape(orgDetails.event_type) + ` 
					WHERE ORGANISER.org_ID = ` + connection.escape(user_ID) + ` AND USER.active = ` + connection.escape(0) + ';';

			//var sqlActivate = completeUserRegistration(user_ID);

			var sql = sqlUser +  sqlOrganiser; //+ sqlActivate;

			connection.query(sql, function(err,res){
					if(err){
						console.log(err);
					       	reject("Updates could not be completed"); 
					}else{
						console.log(res);
						resolve("Updates successfully completed"); 
					}
			});
		});
	}

	this.completeBarstaffRegistration = function(user_ID, userDetails, barstaffDetails) {
		return new Promise(function(resolve, reject){

			var sqlUser = completeUserProfile(user_ID, userDetails);

			var sqlBarstaff = `
					UPDATE BARSTAFF INNER JOIN USER ON BARSTAFF.staff_ID = USER.user_ID SET 
					BARSTAFF.` + constants.HRATE + ` = ` + connection.escape(barstaffDetails.hour_rate) + `,
					BARSTAFF.` + constants.NRATE + ` = ` + connection.escape(barstaffDetails.night_rate) + `,
					BARSTAFF.` + constants.IMAGE + ` = ` + connection.escape(barstaffDetails.image_path) + `,
					BARSTAFF.` + constants.EXP + ` = ` + connection.escape(barstaffDetails.experience) + `
					 WHERE BARSTAFF.staff_ID = ` + connection.escape(user_ID) + ` AND USER.active = ` + connection.escape(0) + ';';

			//var sqlActivate = completeUserRegistration(user_ID);

			var sql = sqlUser +  sqlBarstaff; //+ sqlActivate;

			connection.query(sql, function(err,res){
					if(err){
						console.log("Updates could not be completed" + err);
					       	reject("Updates could not be completed"); 
					}else{
						console.log(res);
						resolve("Updates successfully completed"); //console.error(res);
					}
			});
		});
	}

	// Add the data not included in Registration user data to the user entries
	this.completeRegistration = function(user_ID, details) {
		return new Promise(function(resolve, reject){

			// If details has more than 2 fields, it is a bar staff object
			if(Object.keys(details).length > 6){

				var sqlBarStaff = `UPDATE BARSTAFF INNER JOIN USER ON BARSTAFF.staff_ID = USER.user_ID SET ` + 
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
		var sql = 'SELECT ' + constants.EMAIL + `, ` + constants.ACCOUNT_TYPE + `,` 
				+ constants.USER_ID + `, ` + formatDate(constants.DOB) + ` AS DOB, ` +
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
				sql = "INSERT INTO APPLICANTS (" + constants.JOB_ID + ", " + constants.STAFF_ID + ") VALUES (" + connection.escape(jobID) + ", " + connection.escape(staffID) + ");";
			}else{
				sql = "INSERT INTO APPLICANTS VALUES (" + connection.escape(jobID) + ", " + connection.escape(staffID) + ", " + connection.escape(message) + ", "+ connection.escape(0)+ ");";
			}
			var org_ID_sql = getOrganiserIdByJobId(jobID);

			var query = isActive + sql + org_ID_sql;	
			connection.query(query,
				function(err, res){
					if(err) {
						console.log(err);
						if(err.code == "ER_DUP_ENTRY") reject("Already applied to the job");
						else reject("Application could not be sent");
					}
					else if(isUserNotActive(res, 1)){
						reject("Cannot use MLB service until profile is complete");
					}
					else {
						resolve(["Application successfully sent", res[2][0]]);
					}
			});
		});
	}

	this.getAvailability = function(staff_ID){
		return new Promise(function(resolve, reject){
			var isActive = isActiveQuery(staff_ID);
			var sql = sqlGetAvailability();
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

	//Join APPLICATIONS and JOBS tables and check where staff_ID = barstaffid and private = 0 (false) and status = available
	this.getApplications = function(staff_ID){
		return new Promise(function(resolve,reject){
			var isActive = isActiveQuery(staff_ID);

			var sql = `SELECT JOBS.job_ID, JOBS.org_ID, JOBS.staff_ID, JOBS.title, `+ formatJOBSDateTime() +`
			JOBS.duration, JOBS.postcode, JOBS.rate, JOBS.city, JOBS.no, JOBS.description, JOBS.street_name, JOBS.speciality, JOBS.type,
			CONCAT(U.first_name, LEFT(U.last_name,1)) AS username, JOBS.speciality, JOBS.type, JOBS.private, JOBS.status FROM JOBS INNER JOIN USER
			U ON U.user_ID = JOBS.org_ID INNER JOIN APPLICANTS 
			ON JOBS.`+ constants.JOB_ID + ` = APPLICANTS.` + constants.JOB_ID + ` 
			AND APPLICANTS.` + constants.STAFF_ID + ` = ` + connection.escape(staff_ID) + ` 
			AND JOBS.` + constants.PRIVATE + ` = ` + connection.escape(0) + ` AND JOBS.` +
			constants.STATUS + ` = ` + connection.escape("available") + ` AND APPLICANTS.accepted = 0;`;

			var query = isActive + sql;
			connection.query(query, function(err, rows){
				if(err){
					console.log(err);
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
	this.deleteApplication = function(staff_ID, job_ID, org_ID){
		return new Promise(function(resolve,reject){
			var isActive = isActiveQuery(staff_ID);

			var sql = sqlWithdraw(staff_ID, job_ID);

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

	//get the private jobs offers from organiser to barstaff
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
			var sql = sqlAcceptApplicant(staff_ID, job_ID);
			var org_ID_sql = getOrganiserIdByJobId(job_ID);

			var query = isActive + sql + org_ID_sql;
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
						resolve(["Job accepted", row[2]]);
					}
				}
			});	
		});
	}

	//remove job that has been rejected
	this.rejectPrivateJob = function(staff_ID, job_ID) {
		return new Promise(function(resolve, reject){	
			var isActive = isActiveQuery(staff_ID);

			/*var sql = `DELETE FROM APPLICANTS WHERE ` + constants.STAFF_ID + ` = ` + connection.escape(staff_ID) +
			` AND ` + constants.JOB_ID + ` = (SELECT ` + constants.JOB_ID + ` FROM JOBS WHERE ` +
			constants.JOB_ID + ` = ` + connection.escape(job_ID) + ` AND ` + constants.STATUS + ` = ` + connection.escape("available") + 
			` AND ` + constants.PRIVATE + ` = `+ connection.escape(1) + `)`;*/

			var sql = `DELETE FROM APPLICANTS WHERE ` + constants.STAFF_ID + ` = ` + connection.escape(staff_ID) +
			` AND ` + constants.JOB_ID + ` = `+ connection.escape(job_ID) + `;`;

			var org_ID_sql = getOrganiserIdByJobId(job_ID);

			var query = isActive + sql + org_ID_sql;
			connection.query(query, function(err,res) {
				if(err) {
					reject("Could not delete job");
				}
				else if(isUserNotActive(res, 1)){
					reject("Ccanot user MLB service until profile is complete");
				}
				else {
					if(res[1].affectedRows == 0) reject("No job to be rejected");
					else resolve(["Job rejected", res[2]]);
				}
			});
		});
	}


// --------------------------> Organiser functions

	function getOrgProfile(org_ID){
		return `SELECT U.user_ID, U.first_name, U.last_name, DATE_FORMAT(U.DOB, "%d-%m-%Y") AS DOB, U.postcode, U.gender, O.event_type, O.prof_pos FROM USER U INNER JOIN ORGANISER O ON O.org_ID = U.user_ID WHERE 
		O.org_ID = ` + connection.escape(org_ID)+ `;`;
	}

	this.getOrganiserProfile = function(org_ID){
		return new Promise(function(resolve, reject){
			var isActive = isActiveQuery(org_ID);
			var sql = getOrgProfile(org_ID);
			var query = isActive + sql;	
			connection.query(query, function(err, res){
				if(err){
					console.log(err);
					reject("Could not get user profile");
				}else{
					resolve(res[1]);
				}	
			});
		});
	}

	// get all applicants and job information of jobs that the organiser has made, are not private.
	this.getAllApplicants = function(org_ID) {
		return new Promise(function(resolve, reject){	
			var isActive = isActiveQuery(org_ID);
		
			var applicantsSql = `
			SELECT JOBS.job_ID, JOBS.org_ID, JOBS.staff_ID, JOBS.title, JOBS.duration, JOBS.postcode, JOBS.street_name, JOBS.no, JOBS.city, JOBS.rate, JOBS.description, 
			JOBS.speciality, JOBS.type, JOBS.private, JOBS.status, ` + formatJOBSDateTime() 
			+ ` U.` + constants.USER_ID + `, CONCAT(U.` + constants.FIRSTNAME + `, LEFT(U.` + constants.SURNAME + `,1)) AS username, B.`+
			constants.IMAGE + `, B.` + constants.HRATE + `, B.` + constants.NRATE + `, B.` + constants.SPECIAL + ` FROM JOBS INNER JOIN APPLICANTS A ON JOBS.`+
			constants.JOB_ID + ` = A.` + constants.JOB_ID + ` INNER JOIN USER U ON A.` + constants.STAFF_ID + ` = U.` + constants.USER_ID + ` INNER JOIN BARSTAFF B ON B.`+
			constants.STAFF_ID + ` = U.` + constants.USER_ID + ` WHERE JOBS.` + constants.ORG_ID + ` = ` + connection.escape(org_ID) + ` AND JOBS.` + constants.PRIVATE + ` = ` + 
			connection.escape(0) + ` AND JOBS.` + constants.STATUS + ` = ` + connection.escape("available");  

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

	//FUNCTION TO UPDATE ORGANISER
	this.completeOrganiserProfile = function(user_Id, user_details, organiser_details) {
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
					resolve("Profile successfully completed");
				}
			});
		});
	}

	// Get all organiser public job he/she has created
	this.getAllOrganiserPublicJobs = function(org_ID) {
		return new Promise(function(resolve, reject){	
			var isActive = isActiveQuery(org_ID);
		
			var pubJobsSql = sqlGetOrganiserPublicJobs(org_ID);

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

	this.rejectApplicant = function(org_ID, staff_ID, job_ID){
		return new Promise(function(resolve, reject){
			var orgIsActive = isActiveQuery(org_ID);
			var staffIsActive = isActiveQuery(staff_ID);
			
			console.log("staff id ", staff_ID);
			console.log("job id ", job_ID);
			/*var rejectSql = `DELETE FROM APPLICANTS WHERE ` + constants.STAFF_ID + ` = ` + connection.escape(staff_ID) +
			` AND ` + constants.JOB_ID + ` = (SELECT ` + constants.JOB_ID + ` FROM JOBS WHERE ` + constants.JOB_ID + ` = `+ 
			connection.escape(job_ID) + ` AND ` + constants.ORG_ID + ` = ` + connection.escape(org_ID) + ` AND ` + constants.PRIVATE + ` = ` + connection.escape(0) + `)`;*/
			
			var rejectSql = `DELETE FROM APPLICANTS WHERE ` + constants.STAFF_ID + ` = ` + connection.escape(staff_ID) +
			` AND ` + constants.JOB_ID + ` = ` + connection.escape(job_ID) +`;`;
			
			var sql = orgIsActive + staffIsActive + rejectSql;
			connection.query(sql, function(err,rows){
				if (err){
					 reject ("An error has occured in rejecting the applicants");				
				}
				else if(isUserNotActive(rows, 2)){
					reject("Cannot use MLB service until account is complete");
				}
				else{
					console.log("-=================================== APPLICANT REJECTED =====================================");
					console.log(rows);
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
			var acceptSql = sqlAcceptApplicant(staff_ID, job_ID);

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
		
			var jobsSql = sqlGetPrivateJobs(org_ID);

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
		
			var deleteSql = sqlWithdraw(staff_ID, job_ID);
			
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
			constants.TITLE + `, ` +
			constants.JOB_START + `, ` +
			constants.JOB_END + `, ` +
			constants.DURATION + `, ` +
			constants.POSTCODE + `, ` +
			constants.STREET_NAME + `, ` +
			constants.CITY + `, ` +
			constants.NO + `, ` +
			constants.RATE + `, ` +
			constants.TYPE + `, ` +
			constants.DESC + `, ` +
			constants.SPECIAL + `, ` +
			constants.PRIVATE + `, ` +
			constants.STATUS + `) VALUES (` +
			connection.escape(details.user_ID) + `, ` +
			connection.escape(details.title) + `, ` +
			connection.escape(details.job_start) + `, ` +
			connection.escape(details.job_end) + `, ` +
			connection.escape(details.duration) + `, ` +
			connection.escape(details.postcode) + `, ` +
			connection.escape(details.street_name) + `, ` +
			connection.escape(details.city) + `, ` +
			connection.escape(details.no) + `, ` +
			connection.escape(details.rate) + `, ` +
			connection.escape(details.type) + `, ` +
			connection.escape(details.description) + `, ` +
			connection.escape(details.speciality) + `, ` +
			connection.escape(0) + `, ` +
			connection.escape("available") + `);`;

			var query = isActive + sql;
			connection.query(query, function(err, res) {
				if(err) {
					console.error(err);
					reject("Unsuccessful job creation");
				}
				else if(isUserNotActive(res, 1)){
					console.log("activate");
					reject("Cannot use MLB service until account is complete");
				}
				else {
					console.error(res[1]);
					console.error("wow");
					resolve(["Job create successfully", details]);
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
			constants.TITLE + `, ` +
			constants.JOB_START + `, ` +
			constants.JOB_END + `, ` +
			constants.DURATION + `, ` +
			constants.POSTCODE + `, ` +
			constants.STREET_NAME + `, ` +
			constants.NO + `, ` +
			constants.RATE + `, ` +
			constants.TYPE + `, ` +
			constants.SPECIAL + `, ` +
			constants.DESC + `, ` +
			constants.PRIVATE + `, ` +
			constants.STATUS + `) VALUES (` +
			connection.escape(details.user_ID) + `, ` +
			connection.escape(details.title) + `, ` +
			connection.escape(details.job_start) + `, ` +
			connection.escape(details.job_end) + `, ` +
			connection.escape(details.duration) + `, ` +
			connection.escape(details.postcode) + `, ` +
			connection.escape(details.street_name) + `, ` +
			connection.escape(details.no) + `, ` +
			connection.escape(details.rate) + `, ` +
			connection.escape(details.type) + `, ` +
			connection.escape(details.speciality) + `, ` +
			connection.escape(details.description) + `, ` +
			connection.escape(1) + `, ` +
			connection.escape("available") + `);`;e
		
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
					console.log(err);
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

	//Get the private jobs only
	this.getPublicJobs = function() {
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

	this.getUserProfile = function(user_ID){

		return new Promise(function(resolve, reject){

			connection.query('SELECT * FROM USER WHERE '+constants.USER_ID+' = '+user_ID, function(err, rows){

				if(err) {
					console.error(err);
					reject(err);
				} else {
					console.error(rows[0].fi);
					resolve(rows[0]);
				}
			});

		})
	}

	this.getTable = function(sql){

		return new Promise(function(resolve, reject){

			connection.query(sql, function(err, rows){

				if(err) {
					console.error(err);
					reject(err);
				} else {
					console.error(rows);
					resolve(rows);
				}
			});
		})
	}
	
	this.getCustomerByID = function(user_ID){
		
		return new Promise(function(resolve, reject){

			connection.query(`SELECT `+(constants.STRIPE_TOKEN)+` FROM USER WHERE `+(constants.USER_ID)+` = `+user_ID+`;`, function(err, rows){

				if(err) {
					console.error(err);
					reject(err);
				} else {
					
					resolve(rows[0][constants.STRIPE_TOKEN]);
				}
			});
		})
	}
	
	this.addToChatLog = function(user_ID, event_ID, message){
		
		return new Promise(function(resolve, reject){

			console.log("ADD TO CHAT LOG DATA:");
			console.log("userid ", user_ID);
			console.log("eventid ", event_ID);
			console.log("message ", message);
			console.log("END OF TO CHAT LOG DATA");
			connection.query(`INSERT INTO CHATS(`+(constants.USER_ID)+`,`+(constants.JOB_ID)+`,`+(constants.CHAT_MESSAGE)+`) 
			VALUES(`+user_ID+`,`+event_ID+`,'`+message+`');`, function(err, rows){

				if(err) {
					
					console.log('db err: ', err);
					reject(false);
				
				} else {
					console.error(rows);
					resolve(true);
				}
			});
		});
	}
	
	this.getJobOfferAndToken = function(event_ID){
		
		return new Promise(function(resolve, reject){

			connection.query(`SELECT USER.`+(constants.CARD_TOKEN)+`, JOBS.`+(constants.PAY)+`, JOBS.`+(constants.TITLE)+` FROM JOBS JOIN USER ON JOBS.`+(constants.STAFF_ID)+
			` = USER.`+(constants.USER_ID)+` WHERE JOBS.job_ID = `+(event_ID)+`;`, function(err, rows){

				console.log('rows: ',rows);
				
				if(rows.length === 0) return reject( ('no staff for job with id ' + event_ID) );
			
				if(err) {
					
					console.log('db err: ', err);
					return reject(err);
				
				} else {
					console.error(rows);
					return resolve(rows[0]);
				}
			});
		});
	}
	
	this.getMissedChatMessages = function(user_ID){
		
		return new Promise(function(resolve, reject){

			connection.query(`SELECT `+(constants.USER_ID)+`, `+(constants.JOB_ID)+`, `+(constants.CHAT_MESSAGE)+` FROM CHATS WHERE user_ID = `+(user_ID)+`;`, function(err, rows){

				if(err) {
					
					console.error(err);
					reject(err);
				
				} else {
					
					console.error(rows);
					let obj = [];
					
					for(i = 0 ; i < rows.length ; ++i){
						
						obj.push({ [constants.JOB_ID] : rows[i][constants.JOB_ID] , [constants.CHAT_MESSAGE] : rows[i][constants.CHAT_MESSAGE] });
					}
					
					return new Promise(function(resolve1, reject1){
					
						connection.query(`DELETE FROM CHATS WHERE user_ID = `+(user_ID)+`;`, function(err1, rows1){
							
							if(err1) {
								
								console.error(err1);
								reject1(err1);
							
							} else {
									
								resolve1(obj);
							}
						});
						
					})
					.then(function(succ){
						
						return resolve(obj);
					
					}).catch(function(err){
						
						return reject(err);
					})
				}
			});
		});
	}
	
	this.rawSql = function(sql){
		
		return new Promise(function(resolve, reject){

			connection.query(sql, function(err, rows){

				if(err) {
					console.error(err);
					reject(err);
				} else {
					console.error(rows);
					resolve(rows);
				}
			});
		})
	}
}
