package ajbc.doodle.calendar.entities;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
@Table(name = "users")
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer userId;
	private String firstName;
	private String lastName;
	
	@Column(unique = true)
	private String email;
	private LocalDate birthDate;
	private LocalDate joinDate;
	private Integer inActive; // inActive=1, active=0(DEFAULT)
	
	@JsonIgnore
	@ManyToMany(cascade = {CascadeType.MERGE}, fetch = FetchType.EAGER)
	@JoinTable(name = "usersEvents", joinColumns = @JoinColumn(name = "userId"), inverseJoinColumns = @JoinColumn(name = "eventId"))
	private List<Event> eventsList;
	
	public User(String firstName, String lastName, String email, LocalDate birthDate, LocalDate joinDate) {
		
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.birthDate = birthDate;
		this.joinDate = joinDate;
		this.inActive = 0;
	}

}