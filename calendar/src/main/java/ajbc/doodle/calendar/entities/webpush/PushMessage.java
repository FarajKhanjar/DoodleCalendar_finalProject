package ajbc.doodle.calendar.entities.webpush;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PushMessage {

	private final String title;
	private final String body;

	public PushMessage(String title, String body) {
		this.title = title;
		this.body = body;
	}

}