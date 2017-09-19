# Push notifications for your webapp!

## Support

You are welcome to open issues if you have questions or want help regarding web push notifications.

## Introduction

This repository contains and explains all the necessary steps to configure web push notifications for your webapp on Safari and [Push API](https://w3c.github.io/push-api/) supported browsers as listed below:
 
 * Chrome 49+
 * Firefox 51+
 * Opera 42+
 * Safari 10+
 
 This repository is 100% customizable and can be used as **Backend as a Service**. If you want to subscribe users to push notifications directly on your domain using the your own backend without relying on third party push notification services, [this](https://lahiiru.github.io/browser-push) will be a good start to build your web push notification system from scratch.
 
## Tutorial

Visit Github page [Browser-push](https://lahiiru.github.io/browser-push) for guide lines.

*(This tutorial is for technical people. **Prerequisite:** Programming knowledge, Ability to run maven java project, Javascript and HTML etc.)*

## Presentation

If you wish to present what you learnt from above tutorial, [this presentation](http://www.slideshare.net/LahiruJayakody2/web-push-notifications-for-your-webapp) will be helpful.

## Debug your app

Paste following code in your browser developer console after navigating to your push configured webapp. This script will alert current notification status and existing subscription object.

```javascript
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
    'https://rawgit.com/lahiiru/browser-push/master/front-end/debug.js'
 );
```
---
lahiiru (at) gmail.com
