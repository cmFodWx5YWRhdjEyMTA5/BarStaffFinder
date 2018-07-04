let express = require("express");
let router = express.Router();
let jwt = require("jsonwebtoken");
let config = require("../../config");
let helper = require("../../helpers");
let qs = require('querystring');

router.use(express.static("files/login"));

router.post("/", function(req, res){

	let body = '';
	req.on('data', function (data) {
            body += data;

            // Too much POST data, kill the connection!
            // 1e6 === 1 * Math.pow(10, 6) === 1 * 1000000 ~~~ 1MB
            if (body.length > 1e6)
                request.connection.destroy();
        });

        req.on('end', function () {
            var post = qs.parse(body);
            console.log(post);

            if(post.email === config.account.email &&  post.password === config.account.hash ){

            	let token = jwt.sign({"user": 0 }, config.appSecret, {
					expiresIn: config.tokenExpires  // expires in 1 hours
				});
	
				if (!token){
		
					return res.status(500).json(helper.res.badRes(500, "Internal server error"));
	
				}else{

					res.redirect('/api/admin/homepage.html?name=root&id='+config.account.id+'&token='+token);
				}
            
            }else{

            	res.status(401).send("Invalid login");
            }
        });
});

module.exports = router;