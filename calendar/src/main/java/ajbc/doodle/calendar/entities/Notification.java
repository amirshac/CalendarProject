package ajbc.doodle.calendar.entities;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ajbc.doodle.calendar.enums.ReminderUnit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@ToString

@Entity
@Table(name = "Notifications")
public class Notification implements Comparable<Notification>{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer notificationId;
	
	@NonNull
	@Column(insertable=false, updatable=false)
	private Integer eventId; // which event the notification belongs to
	@NonNull
	private String title;
	@NonNull
	private String message;
	@NonNull
	private LocalDateTime eventTime;
	
	@Enumerated(EnumType.STRING)
	@NonNull
	private ReminderUnit reminderUnit;
	
	@NonNull
	private Integer reminderQuantity;
	
	// calculated value by refresh method
	private LocalDateTime alertTime;
	
	private Boolean deleted;
	
	@JsonIgnore
	@ManyToOne(cascade = {CascadeType.MERGE})
	@JoinColumn(name="eventId")
	private Event event;
	
	@Override
	public int compareTo(Notification otherNotification) {
		// refresh to make sure alert time is calculated properly
		this.refresh();
		otherNotification.refresh();
		return this.getAlertTime().isBefore(otherNotification.getAlertTime()) ? -1 : 1;
	}

	
	// calculates proper alerttime and updates and other values
	public void refresh() {
		this.alertTime = calculateAlertTime();
		
		if (this.deleted == null) this.deleted = false;
	}
	
	public LocalDateTime calculateAlertTime() {
		ChronoUnit chronoUnit = ChronoUnit.MINUTES;
		
		if (reminderUnit == ReminderUnit.HOURS) chronoUnit = ChronoUnit.HOURS;
		
		return eventTime.minus(reminderQuantity, chronoUnit);	
	}
}
