# Push notifications for your webapp!

## Introduction
 This repository contains and explains necessary steps to configure web push notifications for your webapp on Safari and [Push API](https://w3c.github.io/push-api/) supported browsers as listed below:
 
 * Chrome 49+
 * Firefox 51+
 * Opera 42+
 * Safari 10+
 
 This repository contains two sections, *backend* and *frontend*.
 
 * Backend
    * Universal push notification sender
        > This project helps to send push notifications for subscribed users
    * Safari push package creator
        > Safari needs to create an archive called push package for your website. This project creates push package for your website.
    * Safari web service backend
        > Safari push notification service needs a RESTful web service for your website to serve push packages and to log errors.
 * Frontend
    * sw.js
        > This file implements a service worker to receive push notifications. This is needed for web browsers which have implemented [Push API](https://w3c.github.io/push-api/)
    * main.js
        > This script helps to subscribe or un-subscribe users to receive push notifications by asking permissions.
    * index.html
        > Demo html website which can be used to test web push notification.
   
    
 
    