var table_headers = [];

window.onload = function(){
	
	var loadTableHeaders = function(){
		
		console.log('deffining checks');
		
		if(document.getElementById('table') != undefined){
		
			console.log('undefined');
		
			var table_head = document.getElementById('table').rows[0].cells;
			
			for(i = 0 ; i < table_head.length ; ++i){
				
				table_headers.push(table_head[i].innerHTML);
			}
		}
	}
	
	loadTableHeaders();
}

var emailRegex = new RegExp('^([\\w-\\.]+[_]?)@(\\1)?[-\\w\\.]+(\\1)?\\.[a-zA-Z0-9]{2,}$');
var dOBRegex = new RegExp('[\\d]{4}-[\\d]{2}-[\\d]{2}$');
var wordRegex = new RegExp('[a-zA-Z][a-zA-Z ]+');
var idRegex = new RegExp('\\d+');

function getSetName(){
	var nameRegex = new RegExp('name=+([a-zA-Z])+');
	var idRegex = new RegExp('id=+([0-9])+');
	var tokenRegex = new RegExp('token=+([a-zA-Z])+');
	var locationBar = window.location.search.substring(0).split('&');
	if(locationBar.length>1){
		
		var URLparam = window.location.search.substring(0).split('&');
		
		if(URLparam.length > 1 && nameRegex.test(URLparam[0]) && idRegex.test(URLparam[1]) && tokenRegex.test(URLparam[2])){
			
			var userName = URLparam[0].substring(6);
			
			if(document.getElementById('user_name') != null) { document.getElementById('user_name').innerHTML = "Welcome " + userName; }
			document.getElementById('login_user').innerHTML = " " + userName;

			updateLinks(window.location.search);
		
		}else{
			redirect(3);
		}
	
	}else{
		redirect(2);
	}
}

function redirect(code){
	window.location = "/api/login?error="+code;
}

function updateLinks(link){
	var array = document.getElementsByClassName('page-link');
	for(i = 0 ; i < array.length ; ++i){
		
		var element = array[i];
		element.setAttribute("href", element.getAttribute("href") + link);
	}
}

function assertSurnameName(){
	
	if(wordRegex.test(document.getElementById('first_name').value)){
		
		$('#first_name_div').removeClass('has-error');
		$('#first_name_error').addClass('hidden');
		return true;
	}
	

	$('#first_name_div').addClass('has-error');
	$('#first_name_error').removeClass('hidden');
	document.getElementById('first_name_error').innerHTML = "Error, invalid email";
	return false;
}

function assertFirstName(){
	
	if(wordRegex.test(document.getElementById('first_name').value)){
		
		$('#first_name_div').removeClass('has-error');
		$('#first_name_error').addClass('hidden');
		return true;
	}
	

	$('#first_name_div').addClass('has-error');
	$('#first_name_error').removeClass('hidden');
	document.getElementById('first_name_error').innerHTML = "Error, invalid email";
	return false;
}

function assertEmail(){
	
	if(emailRegex.test(document.getElementById('email_address').value)){
		
		$('#email_div').removeClass('has-error');
		$('#email_error').addClass('hidden');
		return true;
	}
	

	$('#email_div').addClass('has-error');
	$('#email_error').removeClass('hidden');
	document.getElementById('email_error').innerHTML = "Error, invalid email";
	return false;
}

function assertPasswords(){
	
	if(document.getElementById('password').value != "" && (document.getElementById('password').value === document.getElementById('confirm_password').value)){
		
		$('#passwords_div').removeClass('has-error');
		$('#passwords_error').addClass('hidden');
		return true;
	}
	

	$('#passwords_div').addClass('has-error');
	$('#passwords_error').removeClass('hidden');
	document.getElementById('passwords_error').innerHTML = "Error, passwords do not match or is blank";
	return false;
}

function assertDOB(){
	
	if(dOBRegex.test(document.getElementById('DOB').value)){
		
		$('#DOB_div').removeClass('has-error');
		$('#DOB_error').addClass('hidden');
		return true;
	}
	

	$('#DOB_div').addClass('has-error');
	$('#DOB_error').removeClass('hidden');
	document.getElementById('DOB_error').innerHTML = "Error, incorrect date format format must be YYYY-MM-DD";
	return false;
}

function assertId(id){

	var element = document.getElementById(id);
	
	console.log('id: ', id);

	if(element != null && idRegex.test(element.value) &&  element.value >= 0) {
		
		$('#'+id+'_div').removeClass('has-error');
		$('#'+id+'_error').addClass('hidden');
		return true;
	}
	
	$('#'+id+'_div').addClass('has-error');
	$('#'+id+'_error').removeClass('hidden');
	document.getElementById(id+'_error').innerHTML = "Error, ID can not be blank and must be a number";
	return false;
}

function assertIsEmpty(id){

	var element = document.getElementById(id);

	if(element != null && element.value != "") {
		
		$('#'+id+'_div').removeClass('has-error');
		$('#'+id+'_error').addClass('hidden');
		return true;
	}
	

	$('#'+id+'_div').addClass('has-error');
	$('#'+id+'_error').removeClass('hidden');
	document.getElementById(id+'_error').innerHTML = "Error, reason can not be blank";
	return false;
}

function assertGender(id){

	var element = document.getElementById(id+'_div');
	var genderSelector = document.getElementById(id);

	if(element != null && genderSelector.value != 'Gender'){

		console.log(element.value);
		$('#'+id+'_div').removeClass('has-error');
		$('#'+id+'_error').addClass('hidden');
		return true;
	}

	$('#'+id+'_div').addClass('has-error');
	$('#'+id+'_error').removeClass('hidden');
	document.getElementById(id+'_error').innerHTML = "Error, please select a gender";
}

function assertProfile(){

	let assertFirstName = assertFirstName();
	let assertSurname = assertSurname();
	let assertDOB = assertDOB();
	let passwrodMatchTest = assertPasswords();
	
	console.log('firstname: ' + assertFirstName);
	console.log('surname: ' + assertSurname);
	console.log('dob: ' + assertDOB);
	console.log('passwords: ' + passwrodMatchTest);


	if(assertFirstName){

		console.log('yo it is done');

	}else{

		$("#error_message_placeholder").removeClass('hidden');
	}
}

function deleteEntryFromTable(id, reason){

	var idCheck = assertId(id);
	var reasonCheck = assertIsEmpty(reason);
	
	let m_id = document.getElementById(id).value;
	let m_reason = document.getElementById(reason).value;

	if(idCheck && reasonCheck){

		let data = { "table" : document.getElementById('page_title').innerHTML.split(' ')[1], "id" : m_id, "reason" : m_reason };
		
		console.log("data: ", data);
		
		sendToServer('PUT', window.location.pathname.split('/api/admin/')[1], data, function(res){
			
			alert(res);
		});
		
	}else{
		
		$("#error_message_placeholder").removeClass('hidden');
	}
}

function signUpForum(){

	assertEmail();
	assertPasswords();
	assertDOB();

	if(assertEmail() && assertPasswords() && assertDOB()){
		
		var data = 	generateJSON(['email_address','confirm_password','DOB','account_type']);
		console.log(document.getElementById('email_address').value)
		data['hash'] = data['confirm_password'];
		delete data['confirm_password'];
		sendToServer('POST', '/api/signup/', data);

	}else{

		$("#error_message_placeholder").removeClass('hidden');
	}
}

function ApplyFilter(){
	
	let filter_options = [];
	
	for(i = 0 ; i < table_headers.length ; ++i){
		
		let option = {}
		let element = document.getElementById(""+table_headers[i]+"_operator");
		
		
		if(element.selectedIndex != 0){
			
			option['id'] = table_headers[i];
			option['operator'] = element.selectedIndex;
			option['value'] = document.getElementById(""+table_headers[i]+"_value").value;
			
			filter_options.push(option);
		}
	}
	
	if(filter_options.length > 0){
		
		console.log(window.location);
		
		sendToServer('POST',('filter ')+document.getElementById('page_title').innerHTML.split(' ')[0], {"fields" : table_headers , "data" : filter_options}, function(res){
			
			document.getElementById('table_data').innerHTML = res;
		});
	}
}

function resetTable(){
	
	sendToServer('GET', window.location.pathname, {"data" : null}, function(res){
			
		document.getElementById('table_data').innerHTML = res.split('<tbody id="table_data">')[1].split('</tbody>')[0];
	});
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
		}
    };
}


