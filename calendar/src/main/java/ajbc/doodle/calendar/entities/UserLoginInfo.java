package ajbc.doodle.calendar.entities;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@RequiredArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter

/**
 * Saves user login information for communication with the server (encrypted push messages)
 * @author amirs
 *
 */
@Entity
@Table(name = "UserLoginInfo")
public class UserLoginInfo {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	
	private Integer loginId;
	
	private Integer userId;
	
	@NonNull
	private String email;
	@NonNull
	private String endPoint;
	@NonNull
	private String p256dhKey;
	@NonNull
	private String auth;	
	 	
	private boolean deleted;
}
