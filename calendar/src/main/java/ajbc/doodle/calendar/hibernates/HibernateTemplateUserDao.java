package ajbc.doodle.calendar.hibernates;

import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Component;

import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.daos.UserDao;
import ajbc.doodle.calendar.entities.User;

@Component(value = "HT_user")
@SuppressWarnings("unchecked")
public class HibernateTemplateUserDao implements UserDao {

	@Autowired
	private HibernateTemplate template;

	// CRUD
	@Override
	public List<User> getAllUsers() throws DaoException {
		DetachedCriteria criteria = DetachedCriteria.forClass(User.class);
		return (List<User>) template.findByCriteria(criteria);
	}
	
	@Override
	public void addUser(User user) throws DaoException {
		template.persist(user);
	}
	
	@Override
	public void updateUser(User user) throws DaoException {
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
		if(users == null || users.size() == 0)
			throw new DaoException("There is no such user in 'users' DB with email: "+email);
		return users.get(0);
	}

	@Override
	public void hardDeleteUser(User user) throws DaoException {
		// TODO 
		template.delete(user);
	}

	@Override
	public void softDeleteUser(User user) throws DaoException {
		user.setInActive(1);
		updateUser(user);	
	}

}