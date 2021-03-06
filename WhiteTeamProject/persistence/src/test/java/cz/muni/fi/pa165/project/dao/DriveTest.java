package cz.muni.fi.pa165.project.dao;

import cz.muni.fi.pa165.project.PersistenceLayerContext;

import cz.muni.fi.pa165.project.entity.Drive;
import cz.muni.fi.pa165.project.entity.Employee;
import cz.muni.fi.pa165.project.entity.Vehicle;
import cz.muni.fi.pa165.project.enums.Category;
import cz.muni.fi.pa165.project.enums.DriveStatus;
import cz.muni.fi.pa165.project.util.DataAccessExceptionImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import org.testng.annotations.Test;

/**
 * Unit tests for Drive entity
 *
 * @author Tomas Borcin | tborcin@redhat.com | created: 10/30/15.
 */
@ContextConfiguration(classes =PersistenceLayerContext.class)
@TestExecutionListeners(TransactionalTestExecutionListener.class)
@Transactional
public class DriveTest extends AbstractTestNGSpringContextTests{

	@Autowired
	private DriveDao driveDao;

	@Autowired
	private EmployeeDao employeeDao;

	@Autowired
	private VehicleDao vehicleDao;

        @PersistenceContext
        private EntityManager em;
        
	private final String vin1 = "KDJG454SD4DS";
	private final String vin2 = "REWGF69849SADF";
        private final String vin3 = "HJUS28FHS13KK2";
        private final String vin4 = "USH24GWRA12SL1";
        private final String vin5 = "FJAO836FE093G";

	@Test
	public void createDrive() throws DataAccessExceptionImpl {
            try{    
		List<Drive> drives = driveDao.getAll();
		assertEquals(0, drives.size());

		Drive drive1 = prepareDrive1();
		Drive drive2 = prepareDrive2();

		driveDao.create(drive1);
		driveDao.create(drive2);

		drives = driveDao.getAll();

		assertEquals(2, drives.size());

            }catch(Exception e) {
                e.printStackTrace();
            }
	}

	@Test
	public void getAll() throws DataAccessExceptionImpl {

		List<Drive> drives = driveDao.getAll();

		assertEquals(drives.size(), 0);

		Drive drive1 = prepareDrive1();
		Drive drive2 = prepareDrive2();

		driveDao.create(drive1);

		drives = driveDao.getAll();

		assertEquals(drives.size(), 1);
		assertTrue(drive1.equals(drives.get(0)));

		driveDao.create(drive2);

		drives = driveDao.getAll();

		assertEquals(drives.size(), 2);
		assertTrue(drive1.equals(drives.get(0)));
		assertTrue(drive2.equals(drives.get(1)));
	}

	@Test
	public void getById() throws DataAccessExceptionImpl {

		Drive drive1 = prepareDrive1();
		Drive drive2 = prepareDrive2();

		driveDao.create(drive1);

		Drive drive = driveDao.get(drive1.getId());

		assertTrue(drive1.equals(drive));
		
		driveDao.create(drive2);

		assertTrue(drive1.equals(driveDao.get(drive1.getId())));
		assertTrue(drive2.equals(driveDao.get(drive2.getId())));
	}

        @Test(expectedExceptions=IllegalArgumentException.class)
        public void getByIdError() throws DataAccessExceptionImpl {
            driveDao.get(null);
        }
        
        @Test(expectedExceptions=IllegalArgumentException.class)
        public void getByEmployeeError() throws DataAccessExceptionImpl {
            driveDao.getAllByEmployee(null);
        }
        
        @Test(expectedExceptions=IllegalArgumentException.class)
        public void getByVehicleError() throws DataAccessExceptionImpl {
            driveDao.getAllByVehicle(null);
        }
        
        @Test(expectedExceptions=IllegalArgumentException.class)
        public void updateError() throws DataAccessExceptionImpl {
            driveDao.update(null);
        }
        
        @Test(expectedExceptions=IllegalArgumentException.class)
        public void createError() throws DataAccessExceptionImpl {
            driveDao.create(null);
        }
        
        @Test(expectedExceptions=IllegalArgumentException.class)
        public void deleteError() throws DataAccessExceptionImpl {
            driveDao.delete(null);
        }
        
	@Test
	public void getByEmployee() throws DataAccessExceptionImpl {

		Drive drive1 = prepareDrive1();
		Drive drive2 = prepareDrive2();
		Employee employee = drive1.getEmployee();

		driveDao.create(drive1);

		List<Drive> drives = driveDao.getAllByEmployee(employee);

		assertEquals(1, drives.size());
		assertTrue(drive1.equals(drives.get(0)));

		employee = drive2.getEmployee();

		driveDao.create(drive2);

		drives = driveDao.getAllByEmployee(employee);

		assertEquals(1, drives.size());
		assertTrue(drive2.equals(drives.get(0)));
	}

	@Test
	public void getByVehicle() throws DataAccessExceptionImpl {

		Drive drive1 = prepareDrive1();
		Drive drive2 = prepareDrive2();

		Vehicle vehicle1 = drive1.getVehicle();
		Vehicle vehicle2 = drive2.getVehicle();

		driveDao.create(drive1);

		List<Drive> drives = driveDao.getAllByVehicle(vehicle1);

		assertEquals(1, drives.size());
		assertTrue(drive1.equals(drives.get(0)));

		driveDao.create(drive2);

		drives = driveDao.getAllByVehicle(vehicle1);

		assertEquals(1, drives.size());
		assertTrue(drive1.equals(drives.get(0)));

		drives = driveDao.getAllByVehicle(vehicle2);

		assertEquals(1, drives.size());
		assertTrue(drive2.equals(drives.get(0)));
	}

        @Test
        public void getByTimeInterval() throws DataAccessExceptionImpl {
            Drive drive1 = prepareDrive1(); //1.1.2015 - 10.1.2015
            Drive drive2 = prepareDrive2_APP(); //5.1.2015 - 15.1.2015
            Drive drive3 = prepareDrive3_APP(); //10.1.2015 - 20.1.2015
            Drive drive4 = prepareDrive2(); //5.1.2015 - 15.1.2015 - NOT APPROVED STATE
            Drive drive5 = prepareDrive3(); //10.1.2015 - 20.1.2015 - NOT APPROVED STATE
            
            driveDao.create(drive1);
            driveDao.create(drive2);
            driveDao.create(drive3);
            driveDao.create(drive4);
            driveDao.create(drive5);
            
            Date startDate;
            Date endDate;
            
            Calendar calendar = new GregorianCalendar();
            calendar.set(2014, 12, 20);
            startDate = calendar.getTime();
            
            calendar.set(2014, 12, 31);
            endDate = calendar.getTime();
            assertEquals(0, driveDao.getAllDrivesByTimeInterval(startDate, endDate).size());
            
            calendar.set(2015, 1, 1);
            endDate = calendar.getTime();
            assertEquals(1, driveDao.getAllDrivesByTimeInterval(startDate, endDate).size());
            
            calendar.set(2015, 1, 5);
            endDate = calendar.getTime();
            assertEquals(2, driveDao.getAllDrivesByTimeInterval(startDate, endDate).size());
            
            calendar.set(2015, 1, 10);
            endDate = calendar.getTime();
            assertEquals(3, driveDao.getAllDrivesByTimeInterval(startDate, endDate).size());
            
            calendar.set(2015, 1, 30);
            endDate = calendar.getTime();
            assertEquals(3, driveDao.getAllDrivesByTimeInterval(startDate, endDate).size());
            
            calendar.set(2015, 1, 11);
            startDate = calendar.getTime();
            assertEquals(2, driveDao.getAllDrivesByTimeInterval(startDate, endDate).size());
            
            calendar.set(2015, 1, 16);
            startDate = calendar.getTime();
            assertEquals(1, driveDao.getAllDrivesByTimeInterval(startDate, endDate).size());
            
            calendar.set(2015, 1, 21);
            startDate = calendar.getTime();
            assertEquals(0, driveDao.getAllDrivesByTimeInterval(startDate, endDate).size());
        }
        
	@Test
	public void update() throws DataAccessExceptionImpl {

		Drive drive1 = prepareDrive1();
		Drive drive2 = prepareDrive2();

		driveDao.create(drive1);
		driveDao.create(drive2);

		drive1.setKm(new BigDecimal(13216));
		driveDao.update(drive1);

		assertTrue(new BigDecimal(13216).compareTo(driveDao.get(drive1.getId()).getKm()) == 0);

		drive1.setDriveStatus(DriveStatus.CANCELLED);
		driveDao.update(drive1);

		assertEquals(DriveStatus.CANCELLED, driveDao.get(drive1.getId()).getDriveStatus());

		Calendar calendar = new GregorianCalendar();
		calendar.set(2015, 1, 1);
		drive1.setStartDate(calendar.getTime());
		driveDao.update(drive1);

		assertTrue(calendar.getTime().equals(driveDao.get(drive1.getId()).getStartDate()));

		calendar = new GregorianCalendar();
		calendar.set(2015, 2, 2);
		drive1.setEndDate(calendar.getTime());
		driveDao.update(drive1);

		assertTrue(calendar.getTime().equals(driveDao.get(drive1.getId()).getEndDate()));

		Employee employee = createEmployee("NewFirstname", "NewLastname");
		drive1.setEmployee(employee);

		driveDao.update(drive1);

		assertTrue(employee.equals(driveDao.get(drive1.getId()).getEmployee()));

		assertTrue(drive2.equals(driveDao.get(drive2.getId())));
	}

	@Test
	public void delete() throws DataAccessExceptionImpl {

		Drive drive1 = prepareDrive1();
		Drive drive2 = prepareDrive2();

		driveDao.create(drive1);
		driveDao.create(drive2);

		assertEquals(2, driveDao.getAll().size());

		driveDao.delete(drive1);

		assertEquals(1, driveDao.getAll().size());
		assertEquals(null, driveDao.get(drive1.getId()));
		assertTrue(drive2.equals(driveDao.get(drive2.getId())));

		drive1 = prepareDrive3();

		driveDao.create(drive1);

		driveDao.delete(drive2);

		assertEquals(1, driveDao.getAll().size());
		assertTrue(drive1.equals(driveDao.get(drive1.getId())));
		assertEquals(null, driveDao.get(drive2.getId()));

		driveDao.delete(drive1);

		assertEquals(0, driveDao.getAll().size());
		assertEquals(null, driveDao.get(drive1.getId()));
		assertEquals(null, driveDao.get(drive2.getId()));
	}

	private Employee createEmployee(String firstname, String lastname) throws DataAccessExceptionImpl {
		Employee employee = new Employee(firstname, lastname, "email@email.com", "0958421547", "role", new BigDecimal(5000), Category.SILVER, new ArrayList<>(), "SDO##NDF5:<>$DS$#46");
		employeeDao.create(employee);

		return employee;
	}

	private Vehicle createVehicle(String vin) throws DataAccessExceptionImpl {
		Vehicle vehicle = new Vehicle();
		vehicle.setBrand("brand");
		vehicle.setDrives(new ArrayList<>());
		vehicle.setEngineType("engine");
		vehicle.setMaxMileage(1000000L);
		vehicle.setMileage(10000L);
		vehicle.setModel("model");
		vehicle.setEngineType("diesel");
		vehicle.setType("type");
		vehicle.setVin(vin);
		vehicle.setServiceChecks(new ArrayList<>());
		vehicle.setServiceCheckInterval(365);
		vehicleDao.create(vehicle);

		return vehicle;
	}

	private Drive prepareDrive1() throws DataAccessExceptionImpl {
		Drive drive1 = new Drive();
		Calendar calendar = new GregorianCalendar();
		calendar.set(2015, 1, 1);
		drive1.setStartDate(calendar.getTime());
		calendar.set(2015, 1, 10);
		drive1.setEndDate(calendar.getTime());
		drive1.setDriveStatus(DriveStatus.APPROVED);
		drive1.setVehicle(createVehicle(vin1));
		drive1.setEmployee(createEmployee("Firstname1", "Lastname1"));
		drive1.setKm(new BigDecimal(10000));

		return drive1;
	}

	private Drive prepareDrive2() throws DataAccessExceptionImpl {

		Drive drive2 = new Drive();
		Calendar calendar = new GregorianCalendar();
		calendar.set(2015, 1, 5);
		drive2.setStartDate(calendar.getTime());
		calendar.set(2015, 1, 15);
		drive2.setEndDate(calendar.getTime());
		drive2.setDriveStatus(DriveStatus.COMPLETED);
		drive2.setVehicle(createVehicle(vin2));
		drive2.setEmployee(createEmployee("Firstname2", "Lastname2"));
		drive2.setKm(new BigDecimal(10000));

		return drive2;
	}
        
        private Drive prepareDrive2_APP() throws DataAccessExceptionImpl {

		Drive drive2 = new Drive();
		Calendar calendar = new GregorianCalendar();
		calendar.set(2015, 1, 5);
		drive2.setStartDate(calendar.getTime());
		calendar.set(2015, 1, 15);
		drive2.setEndDate(calendar.getTime());
		drive2.setDriveStatus(DriveStatus.APPROVED);
		drive2.setVehicle(createVehicle(vin3));
		drive2.setEmployee(createEmployee("Firsname2", "Lastname2"));
		drive2.setKm(new BigDecimal(10000));

		return drive2;
	}

	private Drive prepareDrive3() throws DataAccessExceptionImpl {
		Drive drive1 = new Drive();
		Calendar calendar = new GregorianCalendar();
		calendar.set(2015, 1, 10);
		drive1.setStartDate(calendar.getTime());
		calendar.set(2015, 1, 20);
		drive1.setEndDate(calendar.getTime());
		drive1.setDriveStatus(DriveStatus.CANCELLED);
		drive1.setVehicle(createVehicle(vin4));
		drive1.setEmployee(createEmployee("Firstname3", "Lastname3"));
		drive1.setKm(new BigDecimal(10000));

		return drive1;
	}
        
        private Drive prepareDrive3_APP() throws DataAccessExceptionImpl {
		Drive drive1 = new Drive();
		Calendar calendar = new GregorianCalendar();
		calendar.set(2015, 1, 10);
		drive1.setStartDate(calendar.getTime());
		calendar.set(2015, 1, 20);
		drive1.setEndDate(calendar.getTime());
		drive1.setDriveStatus(DriveStatus.APPROVED);
		drive1.setVehicle(createVehicle(vin5));
		drive1.setEmployee(createEmployee("Firstname3", "Lastname3"));
		drive1.setKm(new BigDecimal(10000));

		return drive1;
	}
}