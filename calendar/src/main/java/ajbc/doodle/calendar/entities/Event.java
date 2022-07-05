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

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@NoArgsConstructor
@Getter
@Setter

@Entity
@Table(name = "events")
public class Event {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer eventId;
	private String title;
	private LocalDateTime start;
	private LocalDateTime end;
	private boolean allDay;
	private String address;
	private String descripton;
	
	@JsonIgnore
	private List<Integer> guests;
	@JsonIgnore
	private List<Integer> notifications;
	
	@Enumerated(EnumType.STRING)
	private RepeatingOptions repeatingOptions;
	
	private boolean deleted;
}
