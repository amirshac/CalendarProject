package ajbc.doodle.calendar.entities;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter

@Entity
@Table(name = "users")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer userId;
	
	@NonNull
	private String firstName;
	@NonNull
	private String lastName;
	@Column(unique = true)
	@NonNull
	private String email; 
	@NonNull
	private LocalDate birthDate;
	@NonNull
	private LocalDate joinDate;
	
	private boolean loggedIn;
	private boolean deleted;
	
	@LazyCollection(LazyCollectionOption.FALSE)
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(
			name = "UsersEvents", 
			joinColumns = @JoinColumn(name = "userId"),
			inverseJoinColumns = @JoinColumn(name = "eventId") )	
	private List<Event> events;
	
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "loginId")
	private UserLoginInfo loginInfo;
	
	
	public void addEvent(Event event) {
		
		if (events == null) {
			events = new ArrayList<Event>();
		}
		
		events.add(event);	
	}
	
	public boolean isLoggedIn() {
		return loggedIn;
	}
}
