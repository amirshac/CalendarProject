package ajbc.doodle.calendar.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import ajbc.doodle.calendar.enums.RepeatingOptions;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter

@Entity
@Table(name = "events")
public class Event {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int eventId;
	
	@NonNull
	private Integer ownerId;
	@NonNull
	private String title;
	@NonNull
	private LocalDateTime starting;
	
	private LocalDateTime ending;
	
	@NonNull
	private Boolean allDay;
	@NonNull
	private String address;
	@NonNull
	private String description;

	@Enumerated(EnumType.STRING)
	@NonNull
	private RepeatingOptions repeatingOptions;
	
	private boolean deleted;
	
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(mappedBy = "eventId", cascade = { CascadeType.MERGE })
	private List<Notification> notifications;
	
	@ToString.Exclude
	@JsonIgnore
	@ManyToOne(cascade = {CascadeType.MERGE})
	@JoinColumn(name="OwnerId", insertable=false, updatable=false )
	private User owner;
	
	public void addNotification(Notification notification) {
		
		if (notifications == null) {
			notifications = new ArrayList<Notification>();
		}
		
		notifications.add(notification);
	}
	
}
