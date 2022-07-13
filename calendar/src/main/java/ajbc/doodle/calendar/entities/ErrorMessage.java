package ajbc.doodle.calendar.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class ErrorMessage extends Exception {

	private static final long serialVersionUID = -2395926777293017518L;
	
	private String message;
	private Object data;
}
