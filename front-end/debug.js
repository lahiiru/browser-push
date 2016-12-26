/*
 * 2016 (c) lahiiru@gmail.com
 * This script retreives existing web push subscription objects. Can be used as debugging script
 * when configuring push notifications.
 *
 * https://github.com/lahiiru/browser-push
 *
 * Copy and paste the body of injectScript function in the broswer developer console.
 *
 */

console.log('%c WebPush debug script injected.', 'background: green; color: white; display: block; font-size:20px');

 /*
  * dummy function which contains the code you need to paste in developer console in browser.
  */
function injectScript(){
       (
           function (a,b,c,d) {
             b.type= c;
             b.src= d;
             a.appendChild(b);
       })
       (
	       document.getElementsByTagName('head')[0], 
	       document.createElement('script'), 
	       'text/javascript', 
	       'https://cdn.rawgit.com/lahiiru/browser-push/master/front-end/debug.js'
       );
}

function safariCheck(){
	var domain = prompt("Enter website id to check (e.g. web.com.example)","");
	var perm = window.safari.pushNotification.permission(domain);
	if (perm.permission == 'granted'){
        var token = perm.deviceToken;
        // Show subscription for debug
        window.prompt('Subscription details:',token);
	} else {
		alert("Permission state is: " + perm.permission)
	}
}

function nonSafariCheck(){
	if(Notification.permission == 'granted'){
		navigator.serviceWorker.getRegistration().then(function(s){
			s.pushManager.getSubscription().then(function(ss){
				window.prompt('Subscription details:',JSON.stringify(ss));
			});
		});
	} else {
		alert("Permission state is: " + Notification.permission);
	}
}

/*
 * Call relevant methods.
 */
if(chrome ==-1 && safariTxt > 0) {
    if(parseInt(version, 10) >=7){
        console.log("Safari browser detected.");
        safariCheck();
    } else {
        alert("Safari unsupported version detected.");
    }
}
else {
	console.log("Non Safari browser detected.");
	nonSafariCheck();
}
