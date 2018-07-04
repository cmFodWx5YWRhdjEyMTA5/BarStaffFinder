var emailRegex = new RegExp('^([\\w-\\.]+[_]?)@(\\1)?[-\\w\\.]+(\\1)?\\.[a-zA-Z0-9]{2,}$');

function assertEmail(){
	
	if(emailRegex.test(document.getElementById('email_address').value)){
		
		return true;
	}
	
	return false;
}

function signInForum(){

	if(true){
		
		var data = 	generateJSON(['email_address','password']);
		console.log(document.getElementById('email_address').value)
		data['hash'] = data['password'];
		delete data['password'];
		sendToServer('POST', '/api/admin/', data, function(data){

			document.write(data);
			
		});

	}else{

		alert("error");
	}
}

function generateJSON(arrayOfIds){

	var toReturn = {};

	for(i = 0 ; i < arrayOfIds.length ; ++i){

		var id = arrayOfIds[i];
		
		if(document.getElementById(id) != null){
			
			toReturn[id] = document.getElementById(id).value;
		}
	}

	return toReturn;
}

function sendToServer(method, path, data, onResponse) {

  	var xhr = new XMLHttpRequest();
  	xhr.open(method, path, true);
  	xhr.setRequestHeader('Content-Type', 'application/json; charset=UTF-8');

  	// send the collected data as JSON
  	xhr.send(JSON.stringify(data));

  	xhr.onreadystatechange = function() {

        if (xhr.readyState == XMLHttpRequest.DONE) {
        	
        	onResponse(xhr.responseText);
    	
    	}else{

    		return null;
    	}
    };
}


