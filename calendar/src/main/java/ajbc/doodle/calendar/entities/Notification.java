package ajbc.doodle.calendar.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

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
	
	@Column(insertable = false, updatable = false)
	private Integer eventId;
	@ManyToOne
    @JoinColumn(name="eventId")
	@JsonProperty(access = Access.WRITE_ONLY)
	private Event eventToNotify;
	
	@Column(insertable = false, updatable = false)
	private Integer userId;
	@ManyToOne
    @JoinColumn(name="userId")
	@JsonProperty(access = Access.WRITE_ONLY)
	private User userToNotify;
	
	public Notification(String title, String message, Unit unit, Integer quantity, Event eventToNotify, User userToNotify) {
		this.title = title;
		this.message = message;
		this.unit = unit;
		this.quantity = quantity;
		this.eventToNotify = eventToNotify;
		this.userToNotify = userToNotify;
		this.inActive=0;
		this.isSent=0;

	}

}