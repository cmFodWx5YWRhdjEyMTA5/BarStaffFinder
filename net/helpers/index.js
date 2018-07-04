const crypto = require('crypto');
const fs = require('fs');
var config = require("../config");
var db = config.database.object;

var helper = (function(){

	var asserters = (function(){
		function assertUsername(obj){
			return regexAssert(obj, db.constants.USERNAME, /[\w\b]+?/);
		}

		function assertFirstname(obj){
			return regexAssert(obj, db.constants.FIRSTNAME, /[\w\b]+?/);
		}

		function assertLastname(obj){
			return regexAssert(obj, db.constants.SURNAME, /[\w\b]+?/);
		}

		function assertEmail(obj){
			return regexAssert(obj, db.constants.EMAIL,/^([\w-\.]+[_]?)@(\1)?[-\w\.]+(\1)?\.[\w]{2,}$/i); 
			//something@something.co..
		}

		function assertHash(obj){
			return obj.hasOwnProperty(db.constants.HASH);
		}

		function assertDOB(obj){
			return regexAssert(obj, db.constants.DOB, /^[\d]{4,4}-[\d]{2,2}-[\d]{2,2}$/); // YYYY-MM-DD
		}

		// Check which type the user has registered with (bartender or organiser)
		function assertAccountType(obj){
			console.log(obj.account_type);
			return regexAssert(obj,db.constants.ACCOUNT_TYPE, /^(ORGANISER|BARSTAFF|ADMIN)$/i);
		}

		function assertBarstaffType(obj){
			return regexAssert(obj,db.constants.TYPE, /^(Bartender|Mixologist|Barback)$/i);
		}

		//valide Gender
		
		function assertGender(obj){
			var r =regexAssert(obj, db.constants.GENDER, /^(M|F)$/);
			console.log(r);
			return r;
		}
			

		function assertMobile(obj){
			return regexAssert(obj, db.constants.MOBILE, /^[\d]{11,11}$/);
			// 00000 000 000 without the spaces
		}

		function assertPostcode(obj){
			return regexAssert(obj, db.constants.POSTCODE, /^[\w\d _]{3,}$/);
		}

		function assertLocation(obj){
			return obj.hasOwnProperty(db.constants.LOCATION);
		}

		function assertProximity(obj){
			return obj.hasOwnProperty(db.constants.PROX); // Interger value 
		}

		function assertSpeciality(obj){
			console.log('fails here: '+ obj.hasOwnProperty(db.constants.SPECIAL))
			return obj.hasOwnProperty(db.constants.SPECIAL);
		}
			
		function assertHRate(obj){
			return regexAssert(obj, db.constants.HRATE, /^[\d]+([.][0-9]+)?$/);
			// 2.45, .23, 1834.90
		}

		function assertNRate(obj){
			return regexAssert(obj, db.constants.NRATE, /^[\d]+([.][0-9]+)?$/);
			// 2.45, .23, 1834.90
		}

		function assertProfilePicture(obj){
			return obj.hasOwnProperty(db.constants.IMAGE); //image_path
		}

		function assertDescription(obj){
			return obj.hasOwnProperty(db.constants.DESC); //description
		}

		function assertAccountNumber(obj){
			// TODO define a better regex for this field
			regexAssert(obj, db.constants.ACC_NO, /^[\d]*$/);
		} 

		function assertProfPos(obj){
			return regexAssert(obj, db.constants.PROF_POS, /^[\w]*$/);
		}

		function assertJobid(obj){
			return regexAssert(obj, db.constants.JOB_ID, /^[\d]+$/);
		}

		function assertStaffid(obj){
			return regexAssert(obj, db.constants.STAFF_ID, /^[\d]+$/);
		}

		function assertUserid(obj){
			
			return regexAssert(obj, db.constants.USER_ID, /^[\d]+$/);
		}

		function assertStreetName(obj){
			return regexAssert(obj, db.constants.STREET_NAME, /^[\w\b]+$/);
		}

		function assertPostcode(obj){
			return regexAssert(obj, db.constants.POSTCODE, /^[\w\d\b]+$/);
		}

		function assertBuildingNumber(obj){
			return regexAssert(obj, db.constants.NO, /^[\d]+$/);

		}

		function assertTitle(obj){
			return regexAssert(obj, db.constants.TITLE, /^[\w\s]+$/);
		}

		function assertCity(obj){
			return regexAssert(obj, db.constants.CITY, /^[\w]+$/);
		}

		function assertStartDateTime(obj){
			return regexAssert(obj, db.constants.JOB_START, 
				/^[\d]{4,4}-[\d]{2,2}-[\d]{2,2} [\d]{2,2}:[\d]{2,2}$/);
		}

		function assertEndDateTime(obj){
			return regexAssert(obj, db.constants.JOB_END, 
				/^[\d]{4,4}-[\d]{2,2}-[\d]{2,2} [\d]{2,2}:[\d]{2,2}$/);
		}
		
		function assertJobSpeciality(obj){
			return regexAssert(obj, db.constants.SPECIAL, /^(Bartender|Mixologist|Barback)$/);
		}

		function assertDuration(obj){
			// TODO regex
			return obj.hasOwnProperty(db.constants.DURATION);
		}

		function assertRate(obj){
			//TODO regex
			return obj.hasOwnProperty(db.constants.RATE);
		}

		// Validate signup request
		function assertSignup(obj){
			return assertHash(obj) &&
				assertEmail(obj) &&
				assertDOB(obj) &&
				assertAccountType(obj);
		}

		// Validate signin(login) request
		function assertSignin(obj){
			return assertEmail(obj) &&
				assertHash(obj);
		}

		//Asserter for general attributes shared between user types
		function assertCompleteProfile(obj){
			
			console.log("firstn ", assertFirstname(obj))
			console.log("lastn ", assertLastname(obj));
			console.log("genger ", assertGender(obj));
			console.log("st ", assertStripeToken(obj));
			console.log("pos ", assertPostcode(obj));

			return assertFirstname(obj) &&
				assertLastname(obj) && //assertUsername(obj) &&
				assertGender(obj) &&
				//assertMobile(obj) &&
				//assertLocation(obj) &&
				assertStripeToken(obj) &&
				assertPostcode(obj);
		}

		function assertEventType(obj){
			return obj.hasOwnProperty(db.constants.EVENT_TYPE);
		}
		
		//Organiser profile completion 
		function assertOrganiserProfile(obj){
			// TODO
			//  add fav events
			//  position is optional
			return assertCompleteProfile(obj) &&
				assertEventType(obj) &&
				assertProfPos(obj);
		}

		function assertExperience(obj){
			return obj.hasOwnProperty(db.constants.EXP);
		}

		function assertStripeToken(obj){
			return obj.hasOwnProperty(db.constants.STRIPE_TOKEN);
		}
		
		function assertOrgID(obj){
			
			return obj.hasOwnProperty(db.constants.ORG_ID);
		}

		//Bar staff profile completion
		function assertBarStaffProfile(obj){
			console.log("complete ",assertCompleteProfile(obj));
			console.log("exp ",assertExperience(obj));
			console.log("hr ",assertHRate(obj));
			console.log("nr ",assertNRate(obj));
			console.log("spe ",assertSpeciality(obj));
			console.log("d ",assertProfilePicture(obj));

			return assertCompleteProfile(obj) &&
				//assertProximity(obj) &&
				assertExperience(obj) &&
				assertHRate(obj) &&
				assertNRate(obj) &&
				assertSpeciality(obj);
				//assertProfilePicture(obj);
		}

		function assertJobApplication(obj){
			return obj.hasOwnProperty(db.constants.MESSAGE);
		}

		function assertApplicantResponse(obj){
			return assertJobid(obj) &&
				assertStaffid(obj);
		}


		function assertJobCreation(obj){
			console.log("id ", assertUserid(obj));
			console.log("title ", assertTitle(obj));
			console.log("descir ", assertDescription(obj));
			console.log("city ", assertCity(obj));
			console.log("start ", assertStartDateTime(obj));
			console.log("end ", assertEndDateTime(obj));
			console.log("dur ", assertDuration(obj));
			console.log("stree ", assertStreetName(obj));
			console.log("build ", assertBuildingNumber(obj));
			console.log("post ", assertPostcode(obj));
			console.log("rate ", assertRate(obj));
			console.log("spec ", assertJobSpeciality(obj));

			return assertUserid(obj) &&
				assertTitle(obj) &&
				assertDescription(obj) &&
				assertStartDateTime(obj) &&
				assertEndDateTime(obj) &&
				assertDuration(obj) &&
				assertStreetName(obj) &&
				assertBuildingNumber(obj) &&
				assertPostcode(obj) &&
				assertCity(obj) &&
				assertRate(obj) &&
				assertJobSpeciality(obj);
		}
		
		function assertChatOffer(obj){
			
			return ( regexAssert(obj, (db.constants.CHAT_OFFER), /^[\d]+([.][0-9]+)?$/ ));
		}
		
		function assertChatMessage(obj){
			
			return obj.hasOwnProperty(db.constants.CHAT_MESSAGE);
		}
		
		function assertChatMessageObject(obj){
			
			return assertOrgID(obj) &&
					assertStaffid(obj)
					assertUserid(obj) &&
					assertJobid(obj) &&
					( assertChatMessage(obj) || assertChatOffer(obj) );
		}

		function regexAssert(obj, property, regex){
			if(!obj.hasOwnProperty(property)) return false;
			return regex.test(obj[property]);
		}
		
		let api = {
			"assertSignup": assertSignup,
			"assertSignin": assertSignin,
			"isOrganiserProfileComplete": assertOrganiserProfile,
			"isBarstaffProfileComplete": assertBarStaffProfile,
			"assertJobApplication": assertJobApplication,
			"assertApplicantResponse": assertApplicantResponse,
			"assertJobCreation": assertJobCreation,
			"assertChatMessageObject": assertChatMessageObject
		}

		return api;
	})();

	var utils = (function(){

		function createProfileObject(email, hash, account_type, dob, token){
			var emailKey = db.constants.EMAIL;
			var hashKey = db.constants.HASH;
			var accoutTypeKey = db.constants.ACCOUNT_TYPE;
			var dobKey = db.constants.DOB;
			var tokenKey = db.constants.TOKEN;
			var signUpObj = {};	
			signUpObj[emailKey]= email,
			signUpObj[hashKey]= hash,
			signUpObj[accoutTypeKey]= account_type,
			signUpObj[dobKey]= dob,
			signUpObj[tokenKey]= token
			return signUpObj;
		}

		function filterObj(obj){
			var keys = Object.keys(obj);

			keys.forEach(function(key){
				if(obj[key] == "" || obj[key] == undefined){
					delete obj[key];
				}
			});
			return obj;
		}

		function setOrganiserObj(obj){
			var userObj = {};
			userObj[db.constants.FIRSTNAME] = obj[db.constants.FIRSTNAME];
			userObj[db.constants.SURNAME] = obj[db.constants.SURNAME];
			userObj[db.constants.GENDER] = obj[db.constants.GENDER];
			userObj[db.constants.POSTCODE] = obj[db.constants.POSTCODE];
			
			console.log(userObj);

			var organiserObj = {};

			organiserObj[db.constants.PROF_POS] = obj[db.constants.PROF_POS];
			organiserObj[db.constants.EVENT_TYPE] = obj[db.constants.EVENT_TYPE];
			
			console.log(organiserObj);
			return [userObj, filterObj(organiserObj)];
		}
		
		function setBarstaffObj(obj){
			var userObj = {};
			userObj[db.constants.FIRSTNAME] = obj[db.constants.FIRSTNAME];
			userObj[db.constants.SURNAME] = obj[db.constants.SURNAME];
			userObj[db.constants.GENDER] = obj[db.constants.GENDER];
			userObj[db.constants.POSTCODE] = obj[db.constants.POSTCODE];
			userObj[db.constants.IMAGE] = obj[db.constants.IMAGE];
			
			console.log(userObj);

			var barstaffObj = {};

			barstaffObj[db.constants.HRATE] = obj[db.constants.HRATE];
			barstaffObj[db.constants.NRATE] = obj[db.constants.NRATE];
			barstaffObj[db.constants.EXP] = obj[db.constants.EXP];
			barstaffObj[db.constants.SPECIAL] = obj[db.constants.SPECIAL];
			barstaffObj[db.constants.DESC] = obj[db.constants.DESC];
			
			console.log(barstaffObj);
			return [userObj, filterObj(barstaffObj)];
		}

		// Crypto to generate random string based on user email on validation email 
		function createCrypto(obj){
			let sample = obj[db.constants.EMAIL];
			return crypto.createHash('sha256').update(sample).digest('hex');
		}

		function setPictureName(userid){
			return ""+ userid + new Date().getTime() + ".jpeg";
		}

		function createPictureURL(pictureName){
			return config.host + "/upload/" + pictureName;
		}

		function getPicturePath(pictureName){
			// Dirname realtive to this file
			return __dirname + "/../files/images/" + pictureName;
		}

		function deletePicture(fs, picturePath){
			fs.unlinkSync(picturePath);	
		}

		function getPictureName(imageURL){
			var toRemove = config.host + "/upload/";
			return imageURL.substring(toRemove.length);
		}

		function sqlSAFE(string){
			var stage1 = string.split("\\").join('\\\\');
			var stage2 = stage1.split("'").join("\\'");
			var stage3 = stage2.split(";").join("\\;");
			return stage3;
		}
		
		function LOG(message){
			
			let date = new Date().toISOString().replace(/T/, ' ').replace(/\..+/, '');
			
			let toAppend = '[' + date + ']: ' + JSON.stringify(message).split('"').join('') + '\n';
			
			fs.appendFile('log/log_'+date.split(' ')[0]+'.txt', toAppend, function (err) {
				
				console.log(err);
			});
		}

		function LOG(message){
 			
 			let date = new Date().toISOString().replace(/T/, ' ').replace(/\..+/, '');
 			
 			let toAppend = '[' + date + ']: ' + JSON.stringify(message) + '\n';
 			
 			fs.appendFile('log/log_'+date.split(' ')[0]+'.txt', toAppend, function (err) {
 				
 				console.log(err);
 			});
 		}

		let api = {
			"createProfileObject" : createProfileObject,
			"createCrypto" : createCrypto,
			"setPictureName": setPictureName,
			"createPictureURL": createPictureURL,
			"getPicturePath": getPicturePath,
			"deletePicture": deletePicture,
			"getPictureName": getPictureName,
			"setOrganiserObj" : setOrganiserObj,
			"setBarstaffObj" : setBarstaffObj,
			"sqlSAFE": sqlSAFE,
			"LOG": LOG
		}
		return api;
	})();

	// sockets notification	
	var notification = (function(){

		function notifyUserOfEvent(from_user_id, to_user_id, event_ID, eventToSend, additional_data){
			
			let io = config.socket;
			let room = getRoom(to_user_id);
			let isRoom = io.sockets.adapter.rooms[room];
			if(isRoom){
				
				console.log("notify ",to_user_id, " user ", from_user_id, " data: ", additional_data);
				io.sockets.in(room).emit(eventToSend, { [db.constants.USER_ID] : from_user_id, [db.constants.JOB_ID] : event_ID, "data" : additional_data} ) ;
			}else{
				
				console.log("user ", to_user_id, " user does not excist", " for event: ", eventToSend, " from: ", from_user_id);
				io.sockets.in(getRoom(from_user_id)).emit(eventToSend, { "message": "could not notify of event, " + eventToSend } );
			}
		}

		function getRoom(id){
			return "/"+id;
		}

		let resApi = {
			"notifyUserOfEvent": notifyUserOfEvent
		}
		
		return resApi;
	})();

	// JSON responce API
	var res = (function(){

		function badRes(errCode, errMsg){
			return {
				"error_code": errCode,
				"error_message": errMsg
			}
		}

		function goodRes(value, key){
			key = key || "message";
			resObj = {};
			resObj[key] = value;
			return resObj;
		}

		let resApi = {
			"badRes": badRes,
			"goodRes": goodRes
		}

		return resApi;
	})();

	let api = {
		"asserters": asserters,
		"utils": utils,
		"notification": notification,
		"res": res
	}

	return api
})();

module.exports = helper;
