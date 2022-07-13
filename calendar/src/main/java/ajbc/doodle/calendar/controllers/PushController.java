package ajbc.doodle.calendar.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ajbc.doodle.calendar.CalendarException;
import ajbc.doodle.calendar.PushProp;
import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.entities.UserLoginInfo;
import ajbc.doodle.calendar.entities.webpush.Subscription;
import ajbc.doodle.calendar.entities.webpush.SubscriptionEndpoint;
import ajbc.doodle.calendar.services.PushService;
import ajbc.doodle.calendar.services.UserService;

@RestController
@RequestMapping("/push")
public class PushController {

	@Autowired
	private UserService userService;

	@Autowired
	private PushService pushService;
	
	@Autowired 
	private PushProp pushProp;
	

	@GetMapping(path = "publicSigningKey", produces = "application/octet-stream")
	public byte[] publicSigningKey() {
		return pushProp.getServerKeys().getPublicKeyUncompressed();
	}

	@GetMapping(path = "/publicSigningKeyBase64")
	public String publicSigningKeyBase64() {
		return pushProp.getServerKeys().getPublicKeyBase64();
	}

	@PostMapping("subscribe/{email}")
	@ResponseStatus(HttpStatus.CREATED)
	public void subscribe(@RequestBody Subscription subscription, @PathVariable(required = false) String email) throws CalendarException {

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

	@PostMapping("unsubscribe/{email}")
	public void unsubscribe(@RequestBody SubscriptionEndpoint subscription,
			@PathVariable(required = false) String email) throws CalendarException {
		try {
			System.out.println(subscription.getEndpoint());
			userService.attemptLogout(email, subscription.getEndpoint());

			pushService.removeUserFromMap(email);

			System.out.println("Subscription with email " + email + " got removed!");
		} catch (DaoException e) {
			System.out.println(e);
		}

	}

}