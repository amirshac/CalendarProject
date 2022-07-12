package ajbc.doodle.calendar.entities;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
	
	// calculated value
	private LocalDateTime alertTime;
	
	//TODO: fix - CRUD "update" deleted can be null if value not set
	private Boolean deleted;
	
	@Override
	public int compareTo(Notification otherNotification) {
		// refresh to make sure alert time is calculated properly
		this.refresh();
		otherNotification.refresh();
		return this.getAlertTime().isBefore(otherNotification.getAlertTime()) ? -1 : 1;
	}

	
	// calculates proper alerttime and updates
	public void refresh() {
		this.alertTime = calculateAlertTime();
	}
	
	public LocalDateTime calculateAlertTime() {
		ChronoUnit chronoUnit = ChronoUnit.MINUTES;
		
		if (reminderUnit == ReminderUnit.HOURS) chronoUnit = ChronoUnit.HOURS;
		
		return eventTime.minus(reminderQuantity, chronoUnit);
		
	}
}
