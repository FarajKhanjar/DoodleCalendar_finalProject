package ajbc.doodle.calendar.entities;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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
	private String title;
	private Category category;
	private Address address;
	private boolean isAllDay;
	
	private LocalDate startDate;
	private LocalDate endDate;
	private LocalTime startTime;
	private LocalTime endTime;
	
	private String description;
	private List<Integer> guests;
	private List<Integer> notifications;
	private RepeatingType repeatingType;
	
	@ManyToOne(cascade = {CascadeType.MERGE})
	@JoinColumn(name="userId")
	private User owner;
	
	private Integer inActive; // inActive=1, active=0

}