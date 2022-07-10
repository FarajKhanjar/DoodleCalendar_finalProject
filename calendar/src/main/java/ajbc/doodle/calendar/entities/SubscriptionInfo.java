package ajbc.doodle.calendar.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "subscriptions")
@ToString
public class SubscriptionInfo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer subId;
	
	//data:
	private String publicKey;
	private String authKey;
	private String endpoint;

	//private Integer userId;
	
	 @JsonIgnore
	 @OneToOne(mappedBy = "subscriptionInfo")
	 private User user;

		public SubscriptionInfo(String publicKey, String authKey, String endPoint) {
			this.publicKey = publicKey;
			this.authKey = authKey;
			this.endpoint = endPoint;
			
		}
}