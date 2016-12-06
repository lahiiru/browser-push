package info.wearetrying;

import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;
import info.wearetrying.model.PushPayload;
import info.wearetrying.model.PushSubscription;
import nl.martijndwars.webpush.*;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.security.Security;
/*
 * Visit Github page[Browser-push](https://lahiiru.github.io/browser-push) for guide lines.
 */
public class Sender {

    private static void sendToNonSafari(PushSubscription subsObj, PushPayload payloadObj) throws Exception {
        Security.addProvider(new BouncyCastleProvider());

        String endpoint = subsObj.getEndpoint();
        PublicKey publicKey = Utils.loadPublicKey(subsObj.getP256dh());
        byte[] auth = Utils.base64Decode(subsObj.getAuth());
        byte[] payload = payloadObj.toString().getBytes();
        // Construct notification
        Notification notification = new Notification(endpoint,publicKey , auth, payload);
        // Construct push service
        PushService pushService = new PushService();
        pushService.setSubject("mailto:adm.trine@gmail.com");
        pushService.setPublicKey(Utils.loadPublicKey(PushServerVars.VAPID_PUBLIC_KEY));
        pushService.setPrivateKey(Utils.loadPrivateKey(PushServerVars.VAPID_PRIVATE_KEY));
        // Send notification!
        HttpResponse httpResponse = pushService.send(notification);

        System.out.println(httpResponse.getStatusLine().getStatusCode());
        System.out.println(IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8));
    }

    private static void sendToSafari(String token, String payloadJSON) throws Exception {
        ApnsService service = APNS.newService()
                .withCert("apn_developer_identity.p12", "123456")
                .withAppleDestination(true)
                .build();
        service.push(token, payloadJSON);
    }

    public static void main(String[] args) {
        // Sample payloads
        PushPayload
                nonSufariPayload = new PushPayload("Hi","Test payload");
        String
                safariPayload = "{\"aps\":{\"alert\":{\"title\":\"Flight A998 Now Boarding\",\"body\":\"Boarding has begun for Flight A998.\",\"action\":\"View\"},\"url-args\":[\"https://lahiru.tech/\"]}}";
        // Replace these subscriptions with your valid ones
        PushSubscription
                nonSuffariSubscription = new PushSubscription( "{\"endpoint\":\"https://fcm.googleapis.com/fcm/send/e0pv48gmIzA:APA91bFZTijLzg0fXwQqBC4Eq7XqObpLzypht-C8pHSiewDVpflZkdymdr66XXMJ2h_Nwj6eNSUCks5sl4j0j-dXobItoGkcRt3OL5c4FjKGcE826ntQVGov9qXOetI_VuIYhd3yAAiC\",\"keys\":{\"p256dh\":\"BKOHT5XpWDj-qla-5Z30HBqHDUODZoN6udMjRWZTg1habcm_ZRDqpMMD3CSyuR3yhy5E5Ui-ijxkPaJdwtdRp7g=\",\"auth\":\"n4rq-LKPv9mR8ELhrOpl5g==\"}}");
        String
                safariSubscription = "D1F415F5FEF4B42879E4AA0AEFD5B0E34D3C1FBA3E73BFE546E4C65E1F552C0B";

        try {
            /*
             * Send push notifications to relevant service endpoints. Messages will be delivered when
             * the subscribed receiver opens browser
             */
            sendToNonSafari(nonSuffariSubscription, nonSufariPayload);
            //sendToSafari(safariSubscription, safariPayload);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
