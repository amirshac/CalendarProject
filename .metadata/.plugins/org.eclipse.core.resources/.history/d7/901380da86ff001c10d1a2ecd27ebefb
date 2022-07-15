package ajbc.doodle.calendar.entities;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

@Entity
@Table(name = "users")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int userId;
	private String firstName;
	private String lastName;
	
	@Column(unique = true)
	private String email; 
	
	private LocalDate birthDate;
	private LocalDate joinDate;
	
	private boolean deleted;
	
	@LazyCollection(LazyCollectionOption.FALSE)
	//@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(
			name = "UsersEvents", 
			joinColumns = @JoinColumn(name = "userId"),
			inverseJoinColumns = @JoinColumn(name = "eventId") )	
	private List<Event> events;
	
	
	public void addEvent(Event event) {
		
		if (events == null) {
			events = new ArrayList<Event>();
		}
		
		events.add(event);	
	}
}
