package ajbc.doodle.calendar.controllers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ajbc.doodle.calendar.Application;
import ajbc.doodle.calendar.ServerKeys;
import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.entities.Notification;
import ajbc.doodle.calendar.entities.UserLoginInfo;
import ajbc.doodle.calendar.entities.webpush.PushMessage;
import ajbc.doodle.calendar.entities.webpush.Subscription;
import ajbc.doodle.calendar.entities.webpush.SubscriptionEndpoint;
import ajbc.doodle.calendar.services.CryptoService;
import ajbc.doodle.calendar.services.PushService;
import ajbc.doodle.calendar.services.UserService;

@RestController
public class PushController {

	@Autowired
	private UserService userService;

	@Autowired
	private PushService pushService;
	
	private final ServerKeys serverKeys;

	private final CryptoService cryptoService;

	private final HttpClient httpClient;

	private final Algorithm jwtAlgorithm;

	private final ObjectMapper objectMapper;
	
	private Set<String> currentEndPoints;


	public PushController(ServerKeys serverKeys, CryptoService cryptoService, ObjectMapper objectMapper) {
		this.serverKeys = serverKeys;
		this.cryptoService = cryptoService;
		this.httpClient = HttpClient.newHttpClient();
		this.objectMapper = objectMapper;

		this.jwtAlgorithm = Algorithm.ECDSA256(this.serverKeys.getPublicKey(), this.serverKeys.getPrivateKey());
		
	}

	@GetMapping(path = "push/publicSigningKey", produces = "application/octet-stream")
	public byte[] publicSigningKey() {
		return this.serverKeys.getPublicKeyUncompressed();
	}

	@GetMapping(path = "/push/publicSigningKeyBase64")
	public String publicSigningKeyBase64() {
		return this.serverKeys.getPublicKeyBase64();
	}

	@PostMapping("push/subscribe/{email}")
	@ResponseStatus(HttpStatus.CREATED)
	public void subscribe(@RequestBody Subscription subscription, @PathVariable(required = false) String email) {

		try {
			// extract the communication info we need 
			UserLoginInfo loginInfo = new UserLoginInfo(email, subscription.getEndpoint(),
					subscription.getKeys().getP256dh(), subscription.getKeys().getAuth());
			
			// update login information for user
			userService.attemptLogIn(email, loginInfo);

			// add communication information to push service user map
			pushService.addUserToMap(email, loginInfo);

			System.out.println("Subscription added with email " + email);
		} catch (DaoException e) {
			System.out.println(e);
		}
	}

	@PostMapping("push/unsubscribe/{email}")
	public void unsubscribe(@RequestBody SubscriptionEndpoint subscription,
			@PathVariable(required = false) String email) {
		try {
			userService.attemptLogout(email, subscription.getEndpoint());

			// this.subscriptions.remove(subscription.getEndpoint(), subscription);
			pushService.removeUserFromMap(email);

			System.out.println("Subscription with email " + email + " got removed!");
		} catch (DaoException e) {
			System.out.println(e);
		}

	}

	// TODO: fix to contain email string
	@PostMapping("push/isSubscribed")
	public boolean isSubscribed(@RequestBody SubscriptionEndpoint subscription) {
		//return this.subscriptions.containsKey(subscription.getEndpoint());
		return false;
	}

	
	@Scheduled(fixedDelay = 3_000)
	public void testNotification() {
		pushService.sendPushMessageToAllUsers(new PushMessage("test", "testing push notification"));
	}




}