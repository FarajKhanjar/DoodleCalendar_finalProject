package ajbc.doodle.calendar.hibernates;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Component;

import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.daos.UserDao;
import ajbc.doodle.calendar.entities.User;

@Component(value = "HNT_user")
@SuppressWarnings("unchecked")
public class HibernateTemplateUserDao implements UserDao {

	@Autowired
	private HibernateTemplate template;

	// CRUD
	@Override
	public List<User> getAllUsers() throws DaoException {
		DetachedCriteria criteria = DetachedCriteria.forClass(User.class);
		return (List<User>) template.findByCriteria(criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY));
	
	}
	
	@Override
	public void addUser(User user) throws DaoException {
		if(checkIfEmailExist(user)==true)
			throw new DaoException("This email is exist in 'users' DB.");
		template.persist(user);
	}
	
	@Override
	public void updateUser(User user) throws DaoException {
		if(checkIfUserExist(user.getUserId())==false)
			throw new DaoException("This user is exist in 'users' DB.");
		
		if(checkIfEmailExist(user))
			throw new DaoException("This email is exist in 'users' DB.");
		
		template.merge(user);
	}


	// Queries
	@Override
	public User getUserById(int userId) throws DaoException {
		User user = template.get(User.class, userId);
		if (user == null)
			throw new DaoException("There is no such user in 'users' DB with id: "+userId);
		return user;
	}

	@Override
	public User getUserByEmail(String email) throws DaoException  {
		DetachedCriteria criteria = DetachedCriteria.forClass(User.class);
		Criterion criterion = Restrictions.eq("email", email);
		criteria.add(criterion);
		List<User> users = (List<User>)template.findByCriteria(criteria);
		if(users.size() == 0 || users == null)
			throw new DaoException("There is no such user in 'users' DB with email: "+email);
		return users.get(0);
	}
	
	private boolean checkIfEmailExist(User user) throws DaoException {
		List<User> allUsers = getAllUsers();
		for(User oneUser : allUsers) {
			if(oneUser.getUserId() != user.getUserId() 
					&& oneUser.getEmail().equals(user.getEmail()))
				return true;
		}
		return false;
	}
	
	private boolean checkIfUserExist(Integer userId) {
		User user = template.get(User.class, userId);
		if (user != null)
			return true;
		return false;
	}
	
	@Override
	public void deleteUserSoftly(User user) throws DaoException {
		user.setInActive(1);
		updateUser(user);	
	}
	
	@Override
	public void deleteUserHardly(User user) throws DaoException {
		template.delete(user);
	}
	
	@Override
	public List<User> getAllUsersInRangeDateEvent(LocalDateTime stateDate, LocalDateTime endDate) throws DaoException {
		DetachedCriteria criteria = DetachedCriteria.forClass(User.class).createCriteria("events");
		criteria.add(Restrictions.ge("startDateTime", stateDate));
		criteria.add(Restrictions.le("endDateTime", endDate));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		return (List<User>) template.findByCriteria(criteria);
	}

}