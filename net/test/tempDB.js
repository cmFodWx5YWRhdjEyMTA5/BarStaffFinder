const should = require('should');
const request = require('request');
const assert = require('chai').assert;

let config = require('../config');

function DBl (){
	
	this.constants = {
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
		STATUS : "status",
		AVAILABLE : "availability",
		PRIVATE: "private",
		TOKEN : "token"
	}
}

var db = new DBl();

module.exports = db;