package ajbc.doodle.calendar.manager;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Date;

import com.auth0.jwt.JWT;

import ajbc.doodle.calendar.Application;
import ajbc.doodle.calendar.CalendarException;
import ajbc.doodle.calendar.PushProp;
import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.entities.Notification;
import ajbc.doodle.calendar.entities.UserLoginInfo;
import ajbc.doodle.calendar.entities.webpush.PushMessage;
import ajbc.doodle.calendar.services.NotificationService;

/**
 * Sends an encrypted push message to the user via thread
 * @author amirs
 *
 */
public class PushNotificationThread implements Runnable {
	
	private NotificationService notificationService;

	private UserLoginInfo loginInfo;

	private PushProp pushProp;
	
	private Notification notification;

	public PushNotificationThread(UserLoginInfo loginInfo, PushProp pushProp, Notification notification, NotificationService notificationService) {
		this.loginInfo = loginInfo;
		this.notification = notification;
		this.pushProp = pushProp;
		this.notificationService = notificationService;

	}
	
	@Override
	public void run() {
		try {
			sendPushNotification();
			deleteNotification();
			
		} catch (CalendarException e) {
			e.printStackTrace();
		} catch (DaoException e) {
			e.printStackTrace();
		}
	}
	
	private void sendPushNotification() throws CalendarException {
		String msgTitle = "PushMessage: ";
		String msgString = notification.getTitle() + " " + notification.getMessage();
		
		System.out.println("<push thread> sending notification: "+ msgString);
		
		
		sendPushMessageToUser(loginInfo, new PushMessage(msgTitle, msgString));
	}
	
	/**
	 * Encrypts message into byte array using user's encryption keys in userlogininfo
	 * @param message
	 * @param loginInfo
	 * @return
	 * @throws CalendarException
	 */
	private byte[] encryptMessage(Object message, UserLoginInfo loginInfo) throws CalendarException {
		byte[] encryptedMessage = null;
		try {
			encryptedMessage = pushProp.getCryptoService().encrypt(pushProp.getObjectMapper().writeValueAsString(message),
			loginInfo.getP256dhKey(), loginInfo.getAuth(), 0);
		} catch (Exception e) {
			throw new CalendarException("Can't encrypt message " + e.getMessage());
		}
		
		return encryptedMessage;
	}
	
	/**
	 * sends push message to user already logged in using their logininfo
	 * @param loginInfo
	 * @param message
	 * @throws CalendarException
	 */
	public void sendPushMessageToUser(UserLoginInfo loginInfo, Object message) throws CalendarException  {
		
		byte[] encryptedMessage = encryptMessage(message, loginInfo);
		boolean success = sendPushMessage(loginInfo, encryptedMessage);
		
		if (!success)
			throw new CalendarException("Couldn't send push message to user " + loginInfo);
	}
	
	/**
	 * Sends already encrypted message via channel (UserLoginInfo class)
	 * 
	 * @param loginInfo
	 * @param body
	 * @return true if succeeded to send, false if failed to send
	 */
	private boolean sendPushMessage(UserLoginInfo loginInfo, byte[] body) {
		String origin = null;
		try {
			URL url = new URL(loginInfo.getEndPoint());
			origin = url.getProtocol() + "://" + url.getHost();
		} catch (MalformedURLException e) {
			Application.logger.error("create origin", e);
			return true;
		}

		Date today = new Date();
		Date expires = new Date(today.getTime() + 12 * 60 * 60 * 1000);

		String token = JWT.create().withAudience(origin).withExpiresAt(expires)
				.withSubject("mailto:example@example.com").sign(pushProp.getJwtAlgorithm());

		URI endpointURI = URI.create(loginInfo.getEndPoint());

		Builder httpRequestBuilder = HttpRequest.newBuilder();
		if (body != null) {
			httpRequestBuilder.POST(BodyPublishers.ofByteArray(body)).header("Content-Type", "application/octet-stream")
					.header("Content-Encoding", "aes128gcm");
		} else {
			httpRequestBuilder.POST(BodyPublishers.ofString(""));
		}

		HttpRequest request = httpRequestBuilder.uri(endpointURI).header("TTL", "180")
				.header("Authorization", "vapid t=" + token + ", k=" + pushProp.getServerKeys().getPublicKeyBase64()).build();
		try {
			HttpResponse<Void> response = pushProp.getHttpClient().send(request, BodyHandlers.discarding());

			switch (response.statusCode()) {
			case 201:
				Application.logger.info("Push message successfully sent: {}", loginInfo.getEndPoint());
				break;
			case 404:
			case 410:
				Application.logger.warn("Subscription not found or gone: {}", loginInfo.getEndPoint());
				// remove subscription from our collection of subscriptions
				return false;
			case 429:
				Application.logger.error("Too many requests: {}", request);
				break;
			case 400:
				Application.logger.error("Invalid request: {}", request);
				break;
			case 413:
				Application.logger.error("Payload size too large: {}", request);
				break;
			default:
				Application.logger.error("Unhandled status code: {} / {}", response.statusCode(), request);
			}
		} catch (IOException | InterruptedException e) {
			Application.logger.error("send push message", e);
		}

		return true;
	}
	

	private void deleteNotification() throws DaoException {
		notificationService.deleteNotification(notification.getNotificationId());
	}
	
}
