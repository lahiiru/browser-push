'use strict';
/*
 * Visit Github page[Browser-push](https://lahiiru.github.io/browser-push) for guide lines.
 */
const applicationServerPublicKey = 'BHJbjjDO-6F-eXTqtVVu_aHZ_yssYATwe-3bU8pqXwe8Xik0lkq6LI9rOojUbVqh46GQrxoObj4A4zv_T9hBpZ0';

var domain = "web.tech.lahiru";
var ua = window.navigator.userAgent,
safariTxt = ua.indexOf ( "Safari" ),
chrome = ua.indexOf ( "Chrome" ),
version = ua.substring(0,safariTxt).substring(ua.substring(0,safariTxt).lastIndexOf("/")+1);
var actionButton = document.querySelector('.action-button');
let isSubscribed = false;
let swRegistration = null;

console.log('%c WebPush script injected.', 'background: green; color: white; display: block; font-size:20px');

function urlB64ToUint8Array(base64String) {
    const padding = '='.repeat((4 - base64String.length % 4) % 4);
    const base64 = (base64String + padding)
        .replace(/\-/g, '+')
        .replace(/_/g, '/');

    const rawData = window.atob(base64);
    const outputArray = new Uint8Array(rawData.length);

    for (let i = 0; i < rawData.length; ++i) {
        outputArray[i] = rawData.charCodeAt(i);
    }
    return outputArray;
}

function updateBtn() {
    if (Notification.permission === 'denied') {
        alert('Push Messaging Blocked.');
        updateSubscriptionOnServer(null);
        return;
    }
    if (isSubscribed) {
        actionButton.innerText='Disable Push Messaging';
    } else {
        actionButton.innerText='Enable Push Messaging';
    }
}

function updateSubscriptionOnServer(subscription) {
    /* TODO: Send the subscription object to application server.
     *       notifications are sent from the server using this object.
     */
    if (subscription) {
        console.log(JSON.stringify(subscription));
    }
}

function subscribeUser() {
    const applicationServerKey = urlB64ToUint8Array(applicationServerPublicKey);
    swRegistration.pushManager.subscribe({
        userVisibleOnly: true,
        applicationServerKey: applicationServerKey
    }).then(function(subscription) {
        console.log('User is subscribed:', subscription);
        updateSubscriptionOnServer(subscription);
        isSubscribed = true;
        updateBtn();
        // Show subscription for debug
        window.prompt('Subscription details:',JSON.stringify(subscription));
    })
    .catch(function(err) {
    console.log('Failed to subscribe the user: ', err);
    updateBtn();
    });
}

function unsubscribeUser() {
    swRegistration.pushManager.getSubscription().then(function(subscription) {
        if (subscription) {
            alert("You are not subscribed now.")
            return subscription.unsubscribe();
        }
    }).catch(function(error) {
        console.log('Error unsubscribing', error);
    }).then(function() {
        isSubscribed = false;
        updateSubscriptionOnServer(null);
        console.log('User is unsubscribed.');
        updateBtn();
    });
}

function subscribe() {
    if (isSubscribed) {
        unsubscribeUser();
    } else {
        subscribeUser();
    }
    // Set the initial subscription value
    swRegistration.pushManager.getSubscription().then(function(subscription) {
        isSubscribed = !(subscription === null);
        updateSubscriptionOnServer(subscription);
    });
}

// For safari
function requestPermissions() {
    window.safari.pushNotification.requestPermission('https://apps.wearetrying.info/push-api', domain, {}, function(subscription) {
        console.log(subscription);
        if(c.permission === 'granted') {
            updateSubscriptionOnServer(subscription);
        }
        else if(c.permission === 'denied') {
            // TODO:
        }
    });
}

function nonSafariInit(){
    if ('serviceWorker' in navigator && 'PushManager' in window) {
        console.log('Service Worker and Push is supported');
        actionButton.addEventListener('click',function(){
            subscribe();
        });
        navigator.serviceWorker.register('sw.js').then(function(swReg) {
            console.log('Service Worker is registered', swReg);
            actionButton.innerText='Enable Push Messaging';
            swRegistration = swReg;
        }).catch(function(error) {
            console.error('Service Worker Error', error);
        });
    } else {
        console.warn('Push messaging is not supported');
        alert('Push Not Supported');
    }
}

// For safari
function safariIniti() {
    var pResult = window.safari.pushNotification.permission(domain);
    
    if(pResult.permission === 'default') {
        //request permission
        requestPermissions();
    } else if (pResult.permission === 'granted') {
        console.log("Permission for " + domain + " is " + pResult.permission);
        var token = pResult.deviceToken;
        // Show subscription for debug
        window.prompt('Subscription details:',token);
    } else if(pResult.permission === 'denied') {
        alert("Permission for " + domain + " is " + pResult.permission);
    }
}

/*
 * Call relevant methods.
 */
if(chrome ==-1 && safariTxt > 0) {
    if(parseInt(version, 10) >=7){
        console.log("Safari browser detected.");
        safariIniti();
    } else {
        console.log("Safari unsupported version detected.");
    }
}
else {
	console.log("Non Safari browser detected.");
	nonSafariInit();
}