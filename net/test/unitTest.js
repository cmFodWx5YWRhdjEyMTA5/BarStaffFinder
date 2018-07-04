const should = require('should');
const request = require('request');
const assert = require('chai').assert;

let config = require('../config');
let helper = require('../helpers');
let db = config.database.object


describe('test module', function(){
	
	it('db constant USER_ID test', function(){
		
		assert.equal((db.constants.USER_ID),'user_ID');
	});
	
});

describe('signUp assertion module', function(){
	
	const assertSignup = helper.asserters.assertSignup;
	
	it('missing required fields \'hash\', \'account_type\', \'DOB\' ', function(){
		
		
		let obj = {};
		
		obj[db.constants.USER_ID] = '3';
		obj[db.constants.EMAIL] = 'test@gmail.com';
		
		assert.equal(assertSignup(obj), false);
	});
	
	it('invalid \'user_ID\'', function(){
		
		
		let obj = {};
		
		obj[db.constants.USER_ID] = '+3';
		obj[db.constants.EMAIL] = '_@gmail.com';
		
		assert.equal(assertSignup(obj), false);
	});
	
	it('include all required data with valid input', function(){
		
		let obj = {};
		
		obj[db.constants.EMAIL] = '_@gmail.com';
		
		assert.equal(assertSignup(obj), false);
		
		obj[db.constants.EMAIL] = 'test@gmail.com';
		
		obj[db.constants.ACCOUNT_TYPE] = 'BARSTAFF';
		
		obj[db.constants.DOB] = '2016-05-20';
		
		obj[db.constants.HASH] = 'ashg\asjh+as';
		
		assert.equal(assertSignup(obj), true);
	});
});

describe('signIn assertion module', function(){
	
	const assertSignin = helper.asserters.assertSignin;
	
	it('email starts with \'@\'', function(){
		
		let obj = {};

		obj[db.constants.EMAIL] = '@test@gmail.com';
		obj[db.constants.HASH] = 'YWBXSC+/';
		assert.equal(assertSignin(obj), false);
	});
	
	it('email contains an underscore after one letter', function(){
		
		let obj = {};

		obj[db.constants.EMAIL] = 'a_test@gmail.com';
		obj[db.constants.HASH] = 'YWBXSC+/';
		assert.equal(assertSignin(obj), true);
	});
	
	it('email contains \'\\\'', function(){
		
		let obj = {};

		obj[db.constants.EMAIL] = 'a_\test@gmail.com';
		obj[db.constants.HASH] = 'YWBXSC+/';
		assert.equal(assertSignin(obj), false);
	});
	
	it('hash contains an valid key that stres hash', function(){
		
		let obj = {};

		obj[db.constants.EMAIL] = 'a_test@gmail.com';
		obj['HASH'] = 'YW_BXSC+\'/';
		assert.equal(assertSignin(obj), false);
	});
});

describe('profile completed assertion test', function(){
	
	const isOrganiserProfileComplete = helper.asserters.isOrganiserProfileComplete;
	const isBarstaffProfileComplete = helper.asserters.isBarstaffProfileComplete;
	
	it('missing stripe_token for bartender profile completed test', function(){
			
		let obj = {};
		
		obj[db.constants.FIRSTNAME] = 'Test';
		obj[db.constants.SURNAME] = 'User';
		obj[db.constants.GENDER] = 'F';
		obj[db.constants.POSTCODE] = 'HA4 1WE';
		obj[db.constants.POSTCODE] = 'HA44LR';
		obj[db.constants.EVENT_TYPE] = 'Birthday';
		obj[db.constants.EXPERIENCE] = 'Barstaff';
		obj[db.constants.HRATE] = '12.99';
		obj[db.constants.NRATE] = '12.99';
		obj[db.constants.HRATE] = '12.99';
		obj[db.constants.SPECIAL] = 'n/a';
	
		assert.equal(isBarstaffProfileComplete(obj),false);
	});
	
	it('missing stripe_token for organiser profile completed test', function(){
			
		let obj = {};
		
		obj[db.constants.FIRSTNAME] = 'Test';
		obj[db.constants.SURNAME] = 'User';
		obj[db.constants.GENDER] = 'F';
		obj[db.constants.POSTCODE] = 'HA4 1WE';
		obj[db.constants.POSTCODE] = 'HA44LR';
		obj[db.constants.EVENT_TYPE] = 'Birthday';
		obj[db.constants.PROF_POS] = 'test';
	
		assert.equal(isOrganiserProfileComplete(obj),false);
	});
	
	it('Gender should not be defined as full word for barstaff', function(){
			
		let obj = {};
		
		obj[db.constants.FIRSTNAME] = 'Test';
		obj[db.constants.SURNAME] = 'User';
		obj[db.constants.GENDER] = 'Female';
		obj[db.constants.POSTCODE] = 'HA44LR';
		obj[db.constants.EVENT_TYPE] = 'Birthday';
		obj[db.constants.EXPERIENCE] = 'Barstaff'
		obj[db.constants.HRATE] = '12.99';
		obj[db.constants.NRATE] = '12.99';
		obj[db.constants.HRATE] = '12.99';
		obj[db.constants.SPECIAL] = 'n/a';
	
		assert.equal(isOrganiserProfileComplete(obj),false);
	});
	
	it('Gender should not be defined as full word for organiser', function(){
			
		let obj = {};
		
		obj[db.constants.FIRSTNAME] = 'Test';
		obj[db.constants.SURNAME] = 'User';
		obj[db.constants.GENDER] = 'Male';
		obj[db.constants.POSTCODE] = 'HA41WE';
		obj[db.constants.STRIPE_TOKEN] = 'AKKSD_SDKJ139XM92'
		obj[db.constants.EVENT_TYPE] = 'Birthday';
		obj[db.constants.PROF_POS] = 'test';
	
		assert.equal(isBarstaffProfileComplete(obj),false);
	});
	
	it('Organiser with everything defined but professional position has \'+\'', function(){
			
		let obj = {};
		
		obj[db.constants.FIRSTNAME] = 'Test';
		obj[db.constants.SURNAME] = 'User';
		obj[db.constants.GENDER] = 'M';
		obj[db.constants.POSTCODE] = 'HA4 1WE';
		obj[db.constants.STRIPE_TOKEN] = 'AKKSD_SDKJ139XM92'
		obj[db.constants.EVENT_TYPE] = 'Birthday';
		obj[db.constants.PROF_POS] = '+test';
	
		assert.equal(isOrganiserProfileComplete(obj),false);
	});
	
	it('Organiser with everything defined correctly ', function(){
			
		let obj = {};
		
		obj[db.constants.FIRSTNAME] = 'Test';
		obj[db.constants.SURNAME] = 'User';
		obj[db.constants.GENDER] = 'M';
		obj[db.constants.POSTCODE] = 'HA41WE';
		obj[db.constants.STRIPE_TOKEN] = 'AKKSD_SDKJ139XM92'
		obj[db.constants.EVENT_TYPE] = 'Birthday';
		obj[db.constants.PROF_POS] = 'test';
	
		assert.equal(isOrganiserProfileComplete(obj),true);
	});
	
	
	it('Experience key not spelled correctly for barstaff', function(){
			
		let obj = {};
		
		obj[db.constants.FIRSTNAME] = 'Test';
		obj[db.constants.SURNAME] = 'User';
		obj[db.constants.GENDER] = 'M';
		obj[db.constants.POSTCODE] = 'HA4 1WE';
		obj[db.constants.STRIPE_TOKEN] = 'AKKSD_SDKJ139XM92'
		obj[db.constants.EVENT_TYPE] = 'Birthday';
		obj[db.constants.PROF_POS] = '+test';
		obj[db.constants.EXPERIENCE] = 'Barstaff'
		obj[db.constants.HRATE] = '12.99';
		obj[db.constants.NRATE] = '12.99';
		obj[db.constants.HRATE] = '12.99';
		obj[db.constants.SPECIAL] = 'n/a';
	
		assert.equal(isBarstaffProfileComplete(obj),false);
	});
	
	it('Hour rate deffined incorrectly for barstaff', function(){
			
		let obj = {};
		
		obj[db.constants.FIRSTNAME] = 'Test';
		obj[db.constants.SURNAME] = 'User';
		obj[db.constants.GENDER] = 'M';
		obj[db.constants.POSTCODE] = 'HA41WE';
		obj[db.constants.STRIPE_TOKEN] = 'AKKSD_SDKJ139XM92'
		obj[db.constants.EXP] = 'Barstaff'
		obj[db.constants.HRATE] = '12.99.0';
		obj[db.constants.NRATE] = '14';
		obj[db.constants.SPECIAL] = 'test';
	
		assert.equal(isBarstaffProfileComplete(obj),false);
	});
	
	it('Everything is defined correctly', function(){
			
		let obj = {};
		
		obj[db.constants.FIRSTNAME] = 'Test';
		obj[db.constants.SURNAME] = 'User';
		obj[db.constants.GENDER] = 'M';
		obj[db.constants.POSTCODE] = 'HA41WE';
		obj[db.constants.STRIPE_TOKEN] = 'AKKSD_SDKJ139XM92'
		obj[db.constants.EXP] = 'Barstaff'
		obj[db.constants.HRATE] = '12.99';
		obj[db.constants.NRATE] = '14';
		obj[db.constants.SPECIAL] = 'test';
	
		assert.equal(isBarstaffProfileComplete(obj),true);
	});
});

describe(' completed assertion test', function(){
	
	
	
});