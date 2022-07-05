package ajbc.doodle.calendar.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
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
	@ManyToOne(cascade = {CascadeType.MERGE})
	@JoinColumn(name="userId")
	private User owner;
	
	private String title;
	private Category category;
	private Address address;
	private Integer isAllDay; // isAllDay=1, not=0(DEFAULT)
	
	private LocalDateTime startDateTime;
	private LocalDateTime endDateTime;
	
	private String description;
	private RepeatingType repeatingType;
	private Integer inActive; // inActive=1, active=0(DEFAULT)
	
//	@JsonIgnore
//	@ManyToMany(mappedBy = "usersEvents")
//	private List<User> guestsList;
	


}