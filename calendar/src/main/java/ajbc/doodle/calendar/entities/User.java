package ajbc.doodle.calendar.entities;

import java.time.LocalDate;
import java.util.Set;
import java.util.HashSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

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
	@Column(updatable = false, nullable = false)
	private Integer userId;
	private String firstName;
	private String lastName;
	
	@Column(unique = true)
	private String email;
	private LocalDate birthDate;
	private LocalDate joinDate;
	private Integer inActive; // inActive=1, active=0(DEFAULT)
	
	@JsonProperty(access = Access.WRITE_ONLY)
	@ManyToMany(mappedBy="eventGuests")
	private Set<Event> events = new HashSet<Event>();

	@JsonIgnore
    @JoinColumn(name = "subDataId")	
	@OneToOne(cascade = CascadeType.ALL)
	private SubscriptionInfo subscriptionInfo;
	
	private Integer userOnline; // loggedIn=1, loggedOut=0(DEFAULT)
	
	public User(String firstName, String lastName, String email, LocalDate birthDate, LocalDate joinDate) {
		
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.birthDate = birthDate;
		this.joinDate = joinDate;
		this.inActive = 0;
	}

}