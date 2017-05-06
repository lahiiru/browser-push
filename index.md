# Push notifications for your webapp!

## Introduction
 This repository contains and explains necessary steps to configure web push notifications for your webapp on Safari and [Push API](https://w3c.github.io/push-api/) supported browsers as listed below: 
 
 * Chrome 49+
 * Firefox 52+
 * Opera 42+
 * Safari 10+

---

This tutorial uses Java in Back-end and JavaScript in front-end. IntelliJ IDEA configured for Java, Maven and PHP is recommended to use for this tutorial. Source code can be downloaded from top of this site. The code is written for demonstration purpose and you have to learn and modify the code to use in your production environment.  Follow the steps below to configure web push notifications.

## What is a web push notification?

![Push notification](https://github.com/lahiiru/browser-push/raw/gh-pages/pushondesk.png)

**Mobile push notifications** can be sent from your back-end to some device owner's notification service such as Google's [FCM](https://firebase.google.com/docs/cloud-messaging) or Apple's [APNS](https://developer.apple.com/notifications). Whenever the recipient device has the internet connectivity, pending messages are fetched from device owner's notification service and displayed to the user. To do this, your message recipient should have installed a push notification enabled mobile application given by you. **Web push notifications** or browser push notifications are somewhat different. Those are delivered to a web browser. Message recipient should have allowed notifications for your webapp or website by visiting the url from a push notification supported browser. It's the time for sending push notifications to your webapp visitors.
    
## Let's start

You can download the source code related to this tutorial from below or clicking the link at the top of this page. Feel free to contribute to the repository.

> Source code: [Browser-push](https://github.com/lahiiru/browser-push)

If you are not willing to send notifications to Safari users, you can skip almost all the steps. Still Safari doesn't support [Push API](https://w3c.github.io/push-api/) which is the W3C standard for web push notification protocol. But Chrome, Firefox and Opera latest versions has nicely done that. Therefore, we have to handle Safari separately while other all browsers can be considered as one. Let's say Safari and non-Safari.

---
## 1. Preparing client-side scripts

Open the project in IntelliJ IDEA.

### Guide lines

1. First you need to generate an **elliptic curve public–private key pair** (a public-key cryptosystems like old RSA). This key pair is for identification of your back-end service which will be used later to send notifications. This identification process is called [VAPID](https://tools.ietf.org/html/draft-ietf-webpush-vapid-01). You can use `front-end/keygen.html` to generate a key pair. Generate and note down the VAPID keys.

2. Put the generated public key in following files as below. **N.B.** File path and name is commented at the top of the code block.

    ```javascript
    // front-end/sw.js
    const applicationServerPublicKey = "<your-generated-public-key>";
    ```

    ```javascript
    // front-end/main.js
    const applicationServerPublicKey = "<your-generated-public-key>";
    ```


3. Now your client-side scripts are ready to test on non-safari browsers. You should add **main.js** to your web page as a script. It will register **sw.js** as a [service worker](https://www.w3.org/TR/service-workers).

4. Open `front-end/index.html` in browser **using any HTTP Server**. You should be able to access to index.html from similar url as following.    
`http://localhost:65124/browser-push/front-end/index.html`    
DNS should be **localhost**. Otherwise, it should be **https://**    
Press **Load Script** button to inject **main.js** script. (instead, you can simply add **main.js** to the head section of the web page). After press **Enable Push Messaging** to subscribe for push notificatons. An input box will appear. Note down the JSON string.

    > This JSON object is called user subscription. In production environment, you should send this subscription to your backen-end and it should be persisted against some user identification. Later your back-end will use that user subscription objects to send push notifications to subscribed users. For demonstration purpose, you can copy it now and hard code in server code.

**Following steps are needed for Safari only. If you wish to send notifications for a non-Safari browser, move to [next section](#2-sending-push-notifications-to-subscribed-users)**

1. Create certificates (.p12 and .cer) and a website push id for your domain from Apple developer site. Refer *Registering with Apple* section in [Configuring Safari Push Notifications](https://developer.apple.com/library/content/documentation/NetworkingInternet/Conceptual/NotificationProgrammingGuideForWebsites/PushNotifications/PushNotifications.html#//apple_ref/doc/uid/TP40013225-CH3-SW33) article.

2. Add your website push id which will look like `web.com.example` to the code as below.

    ```javascript
    // front-end/main.js
    var domain = "web.com.example";
    ```

    Then you need to setup a RESTful web service which will be called every time some Safari user is trying to subscribe for your webapp. Therefore, Safari push notification protocol needs 3 components to work together as following figure.

    ![Safari-handshake](https://github.com/lahiiru/browser-push/raw/gh-pages/handshake_2x.png)

    In above diagram, pink color box *Returns push package* needs to be done by our RESTful web service. **Push package** is a zip file which contains some files and it's hashes. Although Apple developer page instructs to build push packages dynamically, in our implementation we will create it once and RESTful service will serve it as a static resource.

    > If you plan to follow Apple developer instructions strictly, that is fine and it follows good security practices. But, I suggest you to try this first. After you can change the way push packages are created by modifying `private void getZip()` method in my RESTful service implementation.

3. Navigate to `back-end/safari_push_package_creator/pushPackage.raw/`.    
Modify the files as you needed. Please refer the *Building the Push Package* section in [Configuring Safari Push Notifications](https://developer.apple.com/library/content/documentation/NetworkingInternet/Conceptual/NotificationProgrammingGuideForWebsites/PushNotifications/PushNotifications.html#//apple_ref/doc/uid/TP40013225-CH3-SW7) article.

    > If you are planing to add Safari push notification support to multiple domain names, specify those as **"allowedDomains"**    
in `back-end/safari_push_package_creator/pushPackage.raw/website.json`.    
Parameter **"webServiceURL"** specifies the path where the Safari RESTful service will be deployed at. **authenticationToken** will be not using to identify users in our implementation. So keep it as it is. Further, you can have your own iconset.

4. Next, a push package should be created using files in **pushPackage.raw** directory. This happens in following manner.
    1. [SHA1](https://www.w3.org/PICS/DSig/SHA1_1_0.html) hash values of the files are calculated.
    2. Those hash values are put in a file, named **manifest.json**.
    3. This manifest.json file is signed with the private key in .p12 archive and signature is written as a file named **signature**.
    4. Finally a zip archive is created with all those files.

    This process ensures the integrity of the push package (changing the content of files by a third party is avoided).
    Apple developer site has given a [php script](https://developer.apple.com/library/content/documentation/NetworkingInternet/Conceptual/NotificationProgrammingGuideForWebsites/CompanionFile.zip) to do that process. I have sightly changed it to cater our need easily. Do following changes first.

    ```php
    // back-end/safari_push_package_creator/index.php
    $certificate_path = "path/to/file/pkey.p12";         // Change this to the path where your certificate is located.
    $certificate_password = "your-certificate-password"; // Change this to the certificate's import password.
    ```
   
5. Run that index.php file.

    ```shell
    > php index.php
    ```

6. Then a push package for your webapp will be created at following path    
`back-end/safari_webservice_backend/binaries/push-package.zip`

7. Now make the **push-api.war** by packaging project **safari_webservice_backend**.
    ```shell
    > mvn package
    ```

8. Deploy the **push-api.war** file in Tomcat or other container. After the deployment, **"webServiceURL"** specified in **website.json** should matches the context path of deployed web application.    
If everything works fine, 
    * `http://webServiceURL/v1/` should response with **BAD REQUEST**
    * `http://pushPackages/v1/web.com.example` should download zip file **push-package.bin**.

9. Now you can test Safari push notifications using `front-end/index.html`.

10. A confirmation box should be displayed when **Load Script** button is clicked. Otherwise, check developer console outputs. If output is printed with `deviceToken=null`, this is a problem of your safari RESTful service or created push package. See push-api.war logs for troubleshooting.

11. If everything works fine, input box with a token should be appeared. Note it down as Safari subscription. It is needed when sending notifications.

    > In production environment, you should send this subscription to your back-end and it should be persisted against some user identification. Later your back-end will use that user subscription objects to send push notifications to subscribed users. For demonstration purpose, you can copy it now and hard code in server code.

### References

**If you encounter any problem with above guide lines, following resources from the original authors will be helpful.**

- Architecture of the Push Framework is described in [W3C draft](https://w3c.github.io/push-api)
- Architecture of the [Safari push notification framework](https://github.com/lahiiru/browser-push/raw/gh-pages/safari_notifs_rev_2x.png)
- Original [createPushPackage.php](https://developer.apple.com/library/content/documentation/NetworkingInternet/Conceptual/NotificationProgrammingGuideForWebsites/CompanionFile.zip) from developer.apple.com
- Nice tutorial from Google, [Push Codelab](https://developers.google.com/web/fundamentals/getting-started/codelabs/push-notifications/)

---

## 2. Sending push notifications to subscribed users


### Guide lines

1. Navigate to **universal_web_notification_sender** project. Set previously generated ([in Step 1.1](#guide-lines)) server keys as below.

    ```java
    // universal_web_notification_sender/src/main/java/info/wearetrying/PushServerVars.java
    static final String VAPID_PUBLIC_KEY = "put-the-public-key-here";
    static final String VAPID_PRIVATE_KEY = "put-the-private-key-here";
    ```

2. Add your subscriptions in `Sender.java` as below. (If you don't remember what is called subscription, those are the strings obtained in above steps 1.4 and 1.11)

    ```java
    PushSubscription nonSuffariSubscription = new PushSubscription( "subscriptionJson" )
    String suffariSubscription = "subscriptionToken";
    ```

3. Sample notification should be delivered to your browser when you run `Sender.java` class. (If you couldn't run the java class please refer [Google](https://www.google.lk/search?q=how+to+run+maven+java+project+intellij)) on how to configure and run maven java project.

**If you need to send notifications to Safari browser, following additional steps are needed.**

1. Place your **.cer file** (i.e apn_developer_identity.cer), **.p12 file** (i.e. private_dev_key.p12) and **.certSigningRequest** (i.e CertificateSigningRequest.certSigningRequest) in same directory and execute following commands to create **apn_developer_identity.p12**.

    ```dos
    openssl x509 -in apn_developer_identity.cer -inform DER -out apn_developer_identity.pem -outform PEM}
    openssl rsa -out private_key_noenc.pem -in private_dev_key.pem
    openssl pkcs12 -export -in apn_developer_identity.pem -inkey private_key_noenc.pem -certfile CertificateSigningRequest.certSigningRequest -name "apn_developer_identity" -out apn_developer_identity.p12
    ```

2. Place your **apn_developer_identity.p12** in **universal_web_notification_sender** project root.

3. Change your certificate passkey in `private static void sendToSafari()` method in `Sender.java` class.

4. Uncomment `sendToSafari(safariSubscription, safariPayload);` line    
in `public static void main(String[] args)` method in `Sender.java` class.

3. Sample notification should be delivered to your Safari when you run `Sender.java` class.

### References

**If you encounter any problem with above guide lines, following resources from the original authors will be helpful.**

- [web-push-libs](https://github.com/web-push-libs), [MartijnDwars/web-push](https://github.com/MartijnDwars/web-push) for sending push notifications to non-Safari browsers.
- [jpoz/APNS](https://github.com/jpoz/APNS) and [notnoop/java-apns](https://github.com/notnoop/java-apns) for sending notifications to Safari.
- Some online demo sites, [simple-push-demo](https://gauntface.github.io/simple-push-demo/), [simple-push-demo-repo](https://github.com/gauntface/simple-push-demo)
- A free third party service does all of above things, [OneSignal](https://onesignal.com)
