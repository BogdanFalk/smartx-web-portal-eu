function checkCookie() {
	var sheet= getCookie("StyleSheet");

	if (sheet =="") {
		//no cookie is set, aplying white theme
		swapStyleSheet('/static/css/theme.css');
	} else {
		console.log("checkCookie");
		swapStyleSheet(sheet); 
	}

	var sheet= getCookie("StyleSheet");
	if(!isDarkThemeEnable(sheet)){
		document.getElementById('themeSwitch').checked = false;
	}
	else {
		document.getElementById('themeSwitch').checked = true;
	}
}

function getCookie(cookieName) {
	var name = cookieName + "=";
	var ca = document.cookie.split(';');
	for(var i = 0; i < ca.length; i++) {
		var c = ca[i];
		while (c.charAt(0) == ' ') {
			c = c.substring(1);
		}
		if (c.indexOf(name) == 0) {
			console.log("get=" + document.cookie);
			return c.substring(name.length, c.length);
		}
	}

	return "";
}

function swapStyleSheet(sheet){
	document.getElementById('pagestyle').setAttribute('href', sheet);
	console.log("before set cookie");
	setCookie("StyleSheet", sheet);
	console.log("after set cookie");
	isDarkThemeEnable(sheet);
}

function setCookie(cookieName, cookieValue) {
	var currentDate = new Date();
	//set cookie expiration date to 1 year
	currentDate.setTime(currentDate.getTime() + (365 * 24 * 60 * 60 * 1000));
	var expires = "expires="+currentDate.toUTCString();
	document.cookie = cookieName + "=" + cookieValue + ";path=/";
}

function isDarkThemeEnable(sheet){

	if( sheet === "/static/css/theme_dark.css"){
		return true;
	}
	else {
		return false;
	}
}

