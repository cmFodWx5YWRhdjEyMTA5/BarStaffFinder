var express = require('express');
var app = express();
var bodyParser = require("body-parser"); // used the data post to the server in JSON format 
var config = require("./config");
var fs = require("fs");
var stripe = require("stripe")("sk_test_qf7sE7bEPj1YyX9QidUQrW43");
config.stripe.object = stripe;

/*var option = {
	key : fs.readFileSync("ssl/server.key"),
	cert : fs.readFileSync("ssl/server.crt")
}*/

var server = require('http').createServer(app);
//var server = require('https').createServer(option, app);
var Database = require(config.database.path);
config.database.object = new Database(); 

config.database.object.connect(
	config.database.host, 
	config.database.username,
	config.database.password
)
.then(function(data){

	/*stripe.tokens.create({
		card: {
			"number": "371449635398431",
			"exp_month": 11,
			"exp_year": 2021,
			"cvc": "245"
			}
		}, function(err, token){
			if(err) console.log(err);
			config.stripe.token = token;
			
			console.log('updating car details');
			
			stripe.customers.updateCard(
				"cus_AIPRrzvXr7A9we",
				token.card.id,
				{},function(err2, card){
				
				if(err2) console.log("err2: " + err);
				
				console.log('eddited succesfully');
			}
			
			)
		});*/
		
		config.stripe.object = stripe;
		
		/*stripe.charges.create({
			amount: 5000,
			currency: 'gbp',
			customer: "cus_ALEuOdsoME4ZUy", // obtained with Stripe.js
			description: "Test charge for Organiser account",
			
		}, function(err, charge){
			
			if(err) console.log(err);
			
			console.log('charge organiser')
		});*/
		
		/*stripe.transfers.create({
			amount: 5000,
			currency: "gbp",
			customer: "cus_AIPNHaZuIp7h5N", // obtained with Stripe.js
			description: "Test transfer for Barstaff account",
			
		}, function(err, charge){
			
			if(err) console.log(err);
			
			else console.log('transfer to barstaff')
		});*/
		
		
		
	app.disable("x-powered-by"); // security to hide server implementation to the public
	app.use(bodyParser.json());

	app.set("port", process.env.PORT || config.port); // set the port the server will be listening to

	app.use("/api", require("./controllers")(require("socket.io")(server)));

	server.listen(app.get("port"), function(){
		config.port = app.get("port");
		config.host += ":"+ config.port + "/api";
		console.log("Server running on "+ config.host + " ...Press Ctrl-C to stop");
	});
})
.catch(function(err){
	console.log(err);
	console.log("Database is offline");
});

