package ajbc.doodle.calendar.entities;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

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
public class Notification {
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
	
	//TODO: fix - CRUD "update" deleted can be null if value not set
	private Boolean deleted;
	
	//TODO: Add notification guest list
}
