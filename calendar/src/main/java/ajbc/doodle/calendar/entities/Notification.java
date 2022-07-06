package ajbc.doodle.calendar.entities;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString

@Entity
@Table(name = "Notifications")
public class Notification {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int notificationId;
	
	private int eventId; // which event the notification belongs to
	
	private String title;
	private String message;
	
	private LocalDateTime eventTime;
	
	@Enumerated(EnumType.STRING)
	private ReminderUnit reminderUnit;
	
	private int reminderQuantity;
	
	private boolean deleted;
}
