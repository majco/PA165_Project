package cz.muni.fi.pa165.project.dao;

import cz.muni.fi.pa165.project.entity.Vehicle;
import cz.muni.fi.pa165.project.util.HibernateUtil;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mario Kudolani | mariok@mail.muni.cz | created: 27.10.2015
 */
public class VehicleDaoImpl implements VehicleDao {

	public List<Vehicle> findAll() {
		List<Vehicle> vehicles;
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.getTransaction().begin();

		vehicles = session.createCriteria(Vehicle.class).list();

		session.getTransaction().commit();

		return vehicles;
	}

	public Vehicle findByVin(String vin) {
		if(vin == null){
			throw new IllegalArgumentException("Vin is null!");
		}

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.getTransaction().begin();

		Vehicle vehicle = session.get(Vehicle.class, vin);

		session.getTransaction().commit();

		return vehicle;
	}

	public void update(Vehicle vehicle) {
		if(vehicle == null){
			throw new IllegalArgumentException("Vehicle is null!");
		}

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.getTransaction().begin();

		session.update(vehicle);

		session.getTransaction().commit();
	}

	public void delete(Vehicle vehicle) {
		if(vehicle == null){
			throw new IllegalArgumentException("Vehicle is null!");
		}

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.getTransaction().begin();

		session.delete(vehicle);

		session.getTransaction().commit();
	}

	public void insert(Vehicle vehicle) {
		if(vehicle == null){
			throw new IllegalArgumentException("Vehicle is null!");
		}

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.getTransaction().begin();

		session.save(vehicle);

		session.getTransaction().commit();
	}
}
