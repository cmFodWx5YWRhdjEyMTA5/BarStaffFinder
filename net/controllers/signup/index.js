var express = require("express");
var router = express.Router();
var transporter = require("./mail");
var helper = require("../../helpers");
var config = require("../../config");
var db = config.database.object;

router.post("/", function(req, res){
	// Check firstname, lastname, email, password and type must be provided
	
	console.log(req.body);

	if(helper.asserters.assertSignup(req.body)){
		
		//send email
		var signup = req.body;

		console.log("email: "+ signup[db.constants.EMAIL])
		
		let hash = helper.utils.createCrypto(signup);
		
		let email = signup[db.constants.EMAIL];
		let account_type = signup[db.constants.ACCOUNT_TYPE];

		helper.utils.LOG("User sign-up [ email: " + email + " , hash: " + hash + " , account_type: " + account_type + " , dob: " + signup[db.constants.DOB] + " ]");
		
		db.register(
			helper.utils.createProfileObject(
				email,
				signup[db.constants.HASH], 
				account_type,
				signup[db.constants.DOB],
				hash
			)
		)
		.then(function(entry){
			
		
			//Login to generate validation link
			//possibly tolk to DB for varification
			let mailOptions = {
				from: 'noreply@mylocalbartendertest <' + config.email.address + '>', // sender address
				to: email, // list of receivers
				subject: 'MLB Account Activation', // Subject line
				html: validationPage(account_type, hash)
			};

			// send mail with defined transport object
			transporter.sendMail(mailOptions, function(error, info){
				if (error) {
					console.log(error);
					return res.status(500).json(helper.res.badRes(500, "Email could not be sent"));
				}
				console.log('Message %s sent: %s', info.messageId, info.response);
				// Message to be seen by the user after validation 
				return res.json(helper.res.goodRes("Account successfully created. Validiaton link sent to " + email));
			});
		})
		.catch(function(errMsg){
			return res.status(400).json(helper.res.badRes(400, errMsg));
		});
	}else{
		return res.status(400).json(helper.res.badRes(400, "Invalid registraton, one or more of email, password and dob were not provided"));
	}

});
	


function validationPage(account_type, hash){

	var validationLink = config.host + config.controller.validate.path + "/" + hash;
	return `<html>
		<head>
			<title>My Local Bartender Registration</title>
		</head>
		<style>
			.top{
				background-color:#000000;
				padding: 10px;
			}
			.bottom{
				background-color:#E9E9E9;
				padding: 10px;
			}
			.subText{
				font-size: 22px;
				margin-left:20px;
			}
			.link{
				margin-left:20px;
				background-color:#222222;
				padding: 8px;
				border: 2px solid white;
				font-size: 24px;
				color:white;
			}
			h1{
				font-style: bold;
				color:#FCFCFC;
				text-shadow: -1px 0 #989898, 0 1px #989898, 1px 0 #989898, 0 -1px #989898;
				font-size: 50px;
				margin-left:20px;
			}
			p{
				margin-left:20px;
				font-size: 34px;
				font-family: arial;
			}
		</style>
		<body>
			<div class="top">
				<h1>Welcome To My Local Bartender<h1>
			</div>
			<div class="bottom">
				<p>Dear ` + account_type + `</p>
				<p>Thank you registering to My Local Bartender app to continue using our wonderfull service and connect with other bartender and organiser please validate your account using the following link</p>
				<a class="link" href=` + validationLink + ` style="text-decoration:none"> validate account </a>
				<p class="subText">Or you may use the link bellow</p>
				<a class="subText" href=`+ validationLink +`>`+ validationLink+ `</a>
			</div>
			<p class="subText">If you did not register for this application please ignore this email</p>
		</body>
	</html>`;
}

module.exports = router;
