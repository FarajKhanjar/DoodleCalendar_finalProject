package ajbc.doodle.calendar.entities;

import java.util.concurrent.TimeUnit;

import javax.persistence.CascadeType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
@Table(name = "notifications")
public class Notification {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer notificationId;
	private String title;	
	private TimeUnit unit;
	private Integer period;
	private String message;
	
	
	@ManyToOne(cascade = {CascadeType.MERGE})
	@JoinColumn(name="userId")
	private User user;
	
	@ManyToOne(cascade = {CascadeType.MERGE})
	@JoinColumn(name="eventId")
	private Event event;

}