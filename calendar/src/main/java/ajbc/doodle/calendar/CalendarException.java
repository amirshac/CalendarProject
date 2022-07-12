package ajbc.doodle.calendar;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service

public class CalendarException extends Exception {

	private static final long serialVersionUID = 974447172254738763L;
	
	public CalendarException(String message) {
		super(message);
	}

	public CalendarException(Throwable cause) {
		super(cause);
	}

}