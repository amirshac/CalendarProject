package ajbc.doodle.calendar.services;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ajbc.doodle.calendar.Application;
import ajbc.doodle.calendar.CalendarException;
import ajbc.doodle.calendar.ServerKeys;
import ajbc.doodle.calendar.entities.UserLoginInfo;

@Component
public class PushService {

	private final CryptoService cryptoService;
	private final ObjectMapper objectMapper;
	private final HttpClient httpClient;
	private final ServerKeys serverKeys;
	private final Algorithm jwtAlgorithm;

	private final Map<String, UserLoginInfo> userMap = new ConcurrentHashMap<>();

	public PushService(ServerKeys serverKeys, CryptoService cryptoService, ObjectMapper objectMapper) {
		this.serverKeys = serverKeys;
		this.cryptoService = cryptoService;
		this.httpClient = HttpClient.newHttpClient();
		this.objectMapper = objectMapper;

		this.jwtAlgorithm = Algorithm.ECDSA256(this.serverKeys.getPublicKey(), this.serverKeys.getPrivateKey());
	}
	
	
	public void addUserToMap(String email, UserLoginInfo info) {
		this.userMap.put(email, info);
	}
	
	public void removeUserFromMap(String email) {
		this.userMap.remove(email);
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
			encryptedMessage = this.cryptoService.encrypt(this.objectMapper.writeValueAsString(message),
			loginInfo.getP256dhKey(), loginInfo.getAuth(), 0);
		} catch (Exception e) {
			throw new CalendarException("Can't encrypt message " + e.getMessage());
		}
		
		return encryptedMessage;
	}
	
	public void sendPushMessageToAllUsersInMap(Object message) {
		if (this.userMap.isEmpty())
			return;

		for (String key : userMap.keySet()) {
			try {
				sendPushMessageToUserMap(key, message);
			} catch (CalendarException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * Encrypts and sends push message to registered email
	 * 
	 * @param email
	 * @param message
	 * @throws JsonProcessingException
	 * @throws CalendarException 
	 */
	public void sendPushMessageToUserMap(String email, Object message) throws CalendarException {

		UserLoginInfo loginInfo = userMap.get(email);

		try {
			// message encryption
			byte[] encryptedMessage = encryptMessage(message, loginInfo);
			
			boolean success = sendPushMessage(loginInfo, encryptedMessage);

			if (!success) {
				this.userMap.remove(email);
			}
		} catch (CalendarException e) {
			Application.logger.error(e.getMessage());
		}
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
				.withSubject("mailto:example@example.com").sign(this.jwtAlgorithm);

		URI endpointURI = URI.create(loginInfo.getEndPoint());

		Builder httpRequestBuilder = HttpRequest.newBuilder();
		if (body != null) {
			httpRequestBuilder.POST(BodyPublishers.ofByteArray(body)).header("Content-Type", "application/octet-stream")
					.header("Content-Encoding", "aes128gcm");
		} else {
			httpRequestBuilder.POST(BodyPublishers.ofString(""));
		}

		HttpRequest request = httpRequestBuilder.uri(endpointURI).header("TTL", "180")
				.header("Authorization", "vapid t=" + token + ", k=" + this.serverKeys.getPublicKeyBase64()).build();
		try {
			HttpResponse<Void> response = this.httpClient.send(request, BodyHandlers.discarding());

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
}
