package cz.muni.fi.pa165.project.dao;

import cz.muni.fi.pa165.project.entity.Employee;
import cz.muni.fi.pa165.project.util.DataAccessExceptionImpl;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link EmployeeDao} interface implementation.
 *
 * @author Tomas Borcin | tborcin@redhat.com | created: 10/25/15.
 */
@Repository
public class EmployeeDaoImpl implements EmployeeDao {

	@PersistenceContext
	private EntityManager em;

	@Override
	public void create(Employee employee) throws DataAccessExceptionImpl {
		try {
			em.persist(employee);
		} catch (Exception e) {
			throw new DataAccessExceptionImpl(e.getMessage());
		}
	}

	@Override
	public List getAll() throws DataAccessExceptionImpl {
		try {
			List<Employee> result = new ArrayList<>();
			CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
			cq.select(cq.from(Employee.class));
			Query q = em.createQuery(cq);
			result = q.getResultList();
			return result;
		} catch (Exception e) {
			throw new DataAccessExceptionImpl(e.getMessage());
		}
	}

	@Override
	public Employee get(Long id) throws DataAccessExceptionImpl {
		try {
			Employee result = null;
			result = em.find(Employee.class, id);
			return result;
		} catch (Exception e) {
			throw new DataAccessExceptionImpl(e.getMessage());
		}
	}

	@Override
	public Employee getByEmail(String email) throws DataAccessExceptionImpl {
		try {
			return em.createQuery("SELECT e FROM Employee e WHERE e.email = :email", Employee.class).setParameter("email", email).getSingleResult();
		} catch (Exception e) {
			throw new DataAccessExceptionImpl(e.getMessage());
		}
	}

	@Override
	public List<Employee> getAllByLastName(String lastName) throws DataAccessExceptionImpl {
		try {

			TypedQuery<Employee> result = em.createQuery("select u from Employee u where lastname=:lastName",
							Employee.class).setParameter("lastName", lastName);
			return result.getResultList();
		} catch (Exception e) {
			throw new DataAccessExceptionImpl(e.getMessage());
		}
	}

	@Override
	public void update(Employee employee) throws DataAccessExceptionImpl {
		try {
			em.merge(employee);
		} catch (Exception e) {
			throw new DataAccessExceptionImpl(e.getMessage());
		}
	}

	@Override
	public void delete(Employee employee) throws DataAccessExceptionImpl {
		try {
			Employee remove = em.getReference(Employee.class, employee.getId());
			em.remove(remove);
		} catch (Exception e) {
			throw new DataAccessExceptionImpl(e.getMessage());
		}
	}
}
