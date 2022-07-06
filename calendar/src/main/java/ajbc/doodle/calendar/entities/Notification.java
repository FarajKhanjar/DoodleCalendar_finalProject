package ajbc.doodle.calendar.entities;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import ajbc.doodle.calendar.enums.Unit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "notifications")
public class Notification {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer notificationId;
	private String title;
	private String message;
	@Enumerated(EnumType.STRING)
	private Unit unit;
	private Integer quantity;
	
	private Integer inActive; // active=0(DEFAULT), inActive=1
	private Integer isSent;   // isSent=0(DEFAULT), unSent=1
	
	private Integer eventId;
//	@ManyToOne
//    @JoinColumn(name="eventId")
//	private Event eventToNotify;
	
	public Notification(String title, String message, Unit unit, Integer quantity, Integer eventId) {
		this.title = title;
		this.message = message;
		this.unit = unit;
		this.quantity = quantity;
		//this.eventToNotify = eventToNotify;
		this.eventId = eventId;
		this.inActive=0;
		this.isSent=0;

	}
	

}