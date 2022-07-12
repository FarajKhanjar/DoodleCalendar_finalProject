package ajbc.doodle.calendar.entities.webpush;

public class PushMessage {

	private final String title;
	private final String body;

	public String getTitle() {
		return this.title;
	}

	public String getBody() {
		return this.body;
	}

	public PushMessage(String title, String body) {
		this.title = title;
		this.body = body;
	}

	@Override
	public String toString() {
		return "PushMessage [title=" + this.title + ", body=" + this.body + "]";
	}

}
