package ajbc.doodle.calendar.entities;

import java.time.LocalDateTime;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class Notification {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	private String title;
	private String message;
	private Integer eventId;
	private LocalDateTime eventTime;
	
	@Enumerated(EnumType.STRING)
	private ReminderUnit reminderUnit;
	
	private Integer reminderQuantity;
	
	private boolean deleted;
}
