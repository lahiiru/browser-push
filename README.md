# Push notifications for your webapp!

## Introduction

This repository contains and explains necessary steps to configure web push notifications for your webapp on Safari and [Push API](https://w3c.github.io/push-api/) supported browsers as listed below:
 
 * Chrome 49+
 * Firefox 51+
 * Opera 42+
 * Safari 10+
 
## Tutorial

Visit Github page [Browser-push](https://lahiiru.github.io/browser-push) for guide lines.

## Presentation

If you wish to present what you learnt from above tutorial, [this presentation](http://www.slideshare.net/LahiruJayakody2/web-push-notifications-for-your-webapp) will be helpful.

## Debug your app

Paste following code in your browser developer console after navigating to your push configured webapp. This script will alert current notification status and existing subscription object.

	    (
           function (a,b,c,d) {
             b.type= c;
             b.src= d;
             a.appendChild(b);
       })
       (document.getElementsByTagName('head')[0], document.createElement('script'), 'text/javascript', 'https://rawgit.com/lahiiru/browser-push/master/front-end/debug.js');
       
---
lahiiru@gmail.com
