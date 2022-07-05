package ajbc.doodle.calendar.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ajbc.doodle.calendar.enums.Category;
import ajbc.doodle.calendar.enums.RepeatingType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter 
@NoArgsConstructor
@ToString
@Entity
@Table(name = "events")
public class Event {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer eventId;
	@Column(updatable = false)
	@JoinColumn(name="userId")
	private Integer userId;
	
	private String title;
	@Enumerated(EnumType.STRING)
	private Category category;
	
	@JsonIgnore
	private Integer addressId;

	@OneToMany(cascade = {CascadeType.MERGE})
	@JoinColumn(name="addressId")
	private Address address;
	

	private Integer isAllDay; // isAllDay=1, not=0(DEFAULT)
	
	private LocalDateTime startDateTime;
	private LocalDateTime endDateTime;
	
	private String description;
	@Enumerated(EnumType.STRING)
	private RepeatingType repeatingType;
	private Integer inActive; // inActive=1, active=0(DEFAULT)
	
//	@ManyToMany(mappedBy="eventsList",cascade = {CascadeType.MERGE})
//	private List<User> guestsList;
	
	@OneToMany(cascade = {CascadeType.MERGE})
	@JoinColumn(name = "notificationId")
	private List<Notification> notifications;
	
//	public Event(Integer userId, String title, Category category, Integer addressId, Integer isAllDay, LocalDateTime startDateTime,
//			LocalDateTime endDateTime, String description, RepeatingType repeatingType,
//			List<User> guestsList) {
//		this.userId = userId;
//		this.title = title;
//		this.category= category;
//		this.addressId=addressId;
//		this.isAllDay = isAllDay;
//		this.startDateTime = startDateTime;
//		this.endDateTime = endDateTime;
//		this.description = description;
//		this.repeatingType = repeatingType;
//		this.inActive = 0;
//		this.guestsList = guestsList;
//	}
	


}