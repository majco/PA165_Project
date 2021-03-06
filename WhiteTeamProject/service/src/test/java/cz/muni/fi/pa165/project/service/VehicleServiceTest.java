package cz.muni.fi.pa165.project.service;

import cz.muni.fi.pa165.project.dao.DriveDao;
import cz.muni.fi.pa165.project.dao.VehicleDao;
import cz.muni.fi.pa165.project.entity.Drive;
import cz.muni.fi.pa165.project.entity.Vehicle;
import cz.muni.fi.pa165.project.enums.DriveStatus;
import cz.muni.fi.pa165.project.service.config.ServiceConfiguration;

import org.hibernate.service.spi.ServiceException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.inject.Inject;

import java.util.*;

import static org.mockito.Mockito.*;



/**
 * Test class for {@link VehicleService} interface
 * @author Mario Kudolani | mariok@mail.muni.cz | created: 26.11.2015
 */
@ContextConfiguration(classes = ServiceConfiguration.class)
public class VehicleServiceTest extends AbstractTestNGSpringContextTests{

	@Mock
	private VehicleDao vehicleDao;

	@Mock
	private DriveDao driveDao;

	@Inject
	@InjectMocks
	private VehicleService vehicleService;

	@BeforeMethod
	public void prepare() throws ServiceException{
		MockitoAnnotations.initMocks(this);
	}


	private String vin1 = "IPK204t4FG";
	private String vin2 = "DRH244yOKS";
	private String vin3 = "HJY83HJSA2";

	private Vehicle vehicle1;
	private Vehicle vehicle2;
	private Vehicle vehicle3;

	private Drive drive1;
	private Drive drive2;

	@BeforeMethod
	public void prepareVehicles(){
		this.vehicle1 = this.prepareVehicle1();
		this.vehicle2 = this.prepareVehicle2();
		this.vehicle3 = this.prepareVehicle3();

		this.drive1 = this.prepareDrive1();
		this.drive2 = this.prepareDrive2();
	}

	@Test
	public void getByIdTest() throws DataAccessException {
		when(vehicleDao.get(1L)).thenReturn(this.vehicle1);

		Vehicle v1 = this.vehicleService.getById(1L);
		verify(this.vehicleDao).get(1L);
		verifyNoMoreInteractions(this.vehicleDao);

		Assert.assertEquals(v1, this.vehicle1);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void getByNullIdTest(){
		this.vehicleService.getById(null);
	}

	@Test
	public void getByVinTest() throws DataAccessException {
		when(vehicleDao.getByVin(this.vin2)).thenReturn(this.vehicle2);

		Vehicle v1 = this.vehicleService.getByVin(this.vin2);
		verify(this.vehicleDao).getByVin(this.vin2);
		verifyNoMoreInteractions(this.vehicleDao);

		Assert.assertEquals(v1, this.vehicle2);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void getByNullVinTest(){
		this.vehicleService.getByVin(null);
	}

	@Test
	public void getAllTest() throws DataAccessException {
		List<Vehicle> vehicles = new ArrayList<>();
		vehicles.add(this.vehicle1);
		vehicles.add(this.vehicle2);
		vehicles.add(this.vehicle3);
		when(vehicleDao.getAll()).thenReturn(vehicles);

		List<Vehicle> foundVehicles = this.vehicleService.getAll();
		verify(this.vehicleDao).getAll();

		Assert.assertEquals(foundVehicles, vehicles);
	}

	@Test
	public void getAllByModelTest() throws DataAccessException {
		String model = "Astra";
		List<Vehicle> vehicles = new ArrayList<>();
		vehicles.add(this.vehicle1);
		vehicles.add(this.vehicle2);
		when(vehicleDao.getAllByModel(model)).thenReturn(vehicles);

		List<Vehicle> foundVehicles = this.vehicleService.getAllByModel(model);
		verify(this.vehicleDao).getAllByModel(model);
		verifyNoMoreInteractions(this.vehicleDao);

		Assert.assertEquals(foundVehicles, vehicles);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void getAllByNullModelTest(){
		this.vehicleService.getAllByModel(null);
	}

	@Test
	public void getAllByBrandTest() throws DataAccessException {
		String brand = "Mazda";
		List<Vehicle> vehicles = new ArrayList<>();
		vehicles.add(this.vehicle3);
		when(vehicleDao.getAllByBrand(brand)).thenReturn(vehicles);

		List<Vehicle> foundVehicles = this.vehicleService.getAllByBrand(brand);
		verify(this.vehicleDao).getAllByBrand(brand);
		verifyNoMoreInteractions(this.vehicleDao);

		Assert.assertEquals(foundVehicles, vehicles);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void getAllByNullBrandTest(){
		this.vehicleService.getAllByBrand(null);
	}

	@Test
	public void getAllByMileageTest() throws DataAccessException {

		List<Vehicle> vehicles = new ArrayList<>();
		vehicles.add(this.vehicle2);
		vehicles.add(this.vehicle3);
		when(vehicleDao.getAllByMileage(31000L)).thenReturn(vehicles);
		List<Vehicle> foundVehicles = this.vehicleService.getAllByMileage(31000L);

		verify(this.vehicleDao).getAllByMileage(31000L);
		verifyNoMoreInteractions(this.vehicleDao);
		Assert.assertEquals(foundVehicles, vehicles);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void getAllByNullMileageTest(){
		this.vehicleService.getAllByMileage(null);
	}


	@Test
	public void getAllFreeInDateTest() throws DataAccessException {
		List<Vehicle> vehicles = new ArrayList<>();
		vehicles.add(this.vehicle1); // 2 drives
		vehicles.add(this.vehicle2); // 0 drives
		vehicles.add(this.vehicle3); // 0 drives

		List<Drive> drives = new ArrayList<>();
		drives.add(this.drive1); // from 5.11.2015 to 10.11.2015
		drives.add(this.drive2); // from 20.11.2015 to 25.11.2015

		Calendar c = Calendar.getInstance();
		c.set(2015, Calendar.NOVEMBER, 15);
		Date startDate = c.getTime();
		c.set(2015, Calendar.NOVEMBER, 18);
		Date endDate = c.getTime();

		when(this.vehicleDao.getAll()).thenReturn(vehicles);
		when(this.driveDao.getAllDrivesByTimeInterval(startDate, endDate)).thenReturn(Collections.emptyList());

		List<Vehicle> foundVehicles = this.vehicleService.getAllFreeInDate(startDate, endDate);
		Assert.assertEquals(foundVehicles.size(), 3);

		c.set(2015, Calendar.NOVEMBER, 5);
		startDate = c.getTime();
		c.set(2015, Calendar.NOVEMBER, 8);
		endDate = c.getTime();
		when(this.driveDao.getAllDrivesByTimeInterval(startDate, endDate)).thenReturn(new ArrayList<>(drives));

		foundVehicles = this.vehicleService.getAllFreeInDate(startDate, endDate);
		Assert.assertEquals(foundVehicles.size(), 2);

		c.set(2015, Calendar.NOVEMBER, 15);
		startDate = c.getTime();
		c.set(2015, Calendar.NOVEMBER, 20);
		endDate = c.getTime();
		when(this.driveDao.getAllDrivesByTimeInterval(startDate, endDate)).thenReturn(new ArrayList<>(drives));

		foundVehicles = this.vehicleService.getAllFreeInDate(startDate, endDate);
		Assert.assertEquals(foundVehicles.size(), 2);

		c.set(2015, Calendar.NOVEMBER, 10);
		startDate = c.getTime();
		c.set(2015, Calendar.NOVEMBER, 18);
		endDate = c.getTime();
		when(this.driveDao.getAllDrivesByTimeInterval(startDate, endDate)).thenReturn(new ArrayList<>(drives));

		foundVehicles = this.vehicleService.getAllFreeInDate(startDate, endDate);
		Assert.assertEquals(foundVehicles.size(), 2);

		c.set(2015, Calendar.NOVEMBER, 30);
		startDate = c.getTime();
		c.set(2015, Calendar.DECEMBER, 20);
		endDate = c.getTime();
		when(this.driveDao.getAllDrivesByTimeInterval(startDate, endDate)).thenReturn(Collections.<Drive>emptyList());

		foundVehicles = this.vehicleService.getAllFreeInDate(startDate, endDate);
		Assert.assertEquals(foundVehicles.size(), 3);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void getAllFreeInDateNullStartDateTest(){
		this.vehicleService.getAllFreeInDate(null, new Date(115,10,5));
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void getAllFreeInDateNullEndDateTest(){
		this.vehicleService.getAllFreeInDate(new Date(115,10,5), null);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void getAllFreeInDateEndDateBeforeStartDateTest(){
		this.vehicleService.getAllFreeInDate(new Date(115,10,5), new Date(115,10,4));
	}

	@Test
	public void createVehicleTest() throws DataAccessException{
		this.vehicleService.createVehicle(this.vehicle1);
		verify(this.vehicleDao).create(this.vehicle1);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void createNullVehicleTest(){
		this.vehicleService.createVehicle(null);
	}


	@Test
	public void deleteVehicleTest() throws DataAccessException{
		this.vehicle1.setId(300L);
		this.vehicleService.deleteVehicle(this.vehicle1);
		verify(this.vehicleDao).delete(this.vehicle1);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void deleteNullVehicleTest(){
		this.vehicleService.deleteVehicle(null);
	}

	@Test
	public void updateServiceCheckIntervalTest() throws DataAccessException{
		this.vehicle1.setId(500L);
		this.vehicleService.updateServiceCheckInterval(this.vehicle1, 5);
		verify(this.vehicleDao).update(this.vehicle1);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void updateNullServiceCheckIntervalTest(){
		this.vehicleService.updateServiceCheckInterval(null, 0);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void updateInvalidServiceCheckIntervalTest(){
		this.vehicleService.updateServiceCheckInterval(this.vehicle1, -2);
	}

	@Test
	public void updateMaxMileageTest() throws DataAccessException{
		this.vehicle2.setId(600L);
		this.vehicleService.updateMaxMileage(this.vehicle2, 200000L);
		verify(this.vehicleDao).update(this.vehicle2);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void updateNullMaxMileageTest(){
		this.vehicleService.updateMaxMileage(null, 0L);
	}

	@Test
	public void updateMileageTest() throws DataAccessException {
		this.vehicle2.setId(700L);
		this.vehicleService.updateMileage(this.vehicle2, 1000L);
		verify(this.vehicleDao).update(this.vehicle2);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void updateNullMileageTest(){
		this.vehicleService.updateMileage(null, 0L);
	}

	private Vehicle prepareVehicle1(){
		Vehicle v = new Vehicle();
		v.setVin(vin1);
		v.setModel("Astra");
		v.setBrand("Opel");
		v.setType("type1");
		v.setYearOfProduction(2012);
		v.setEngineType("gasoline");
		v.setMaxMileage(400000L);
		v.setMileage(45000L);
		v.setServiceCheckInterval(365);
		return v;
	}

	private Vehicle prepareVehicle2(){
		Vehicle v = new Vehicle();
		v.setVin(vin2);
		v.setModel("Astra");
		v.setBrand("Opel");
		v.setType("type1");
		v.setYearOfProduction(2005);
		v.setEngineType("diesel");
		v.setMaxMileage(110000L);
		v.setMileage(10000L);
		v.setServiceCheckInterval(200);
		return v;
	}

	private Vehicle prepareVehicle3(){
		Vehicle v = new Vehicle();
		v.setVin(vin3);
		v.setModel("3");
		v.setBrand("Mazda");
		v.setType("type2");
		v.setYearOfProduction(2007);
		v.setEngineType("diesel");
		v.setMaxMileage(110000L);
		v.setMileage(30000L);
		v.setServiceCheckInterval(200);
		return v;
	}

	private Drive prepareDrive1(){
		Drive d = new Drive();
		d.setVehicle(this.vehicle1);
		d.setDriveStatus(DriveStatus.APPROVED);
		Calendar c = Calendar.getInstance();
		c.set(2015, Calendar.NOVEMBER, 5);
		d.setStartDate(c.getTime());
		c.set(2015, Calendar.NOVEMBER, 10);
		d.setEndDate(c.getTime());
		return d;
	}

	private Drive prepareDrive2(){
		Drive d = new Drive();
		d.setVehicle(this.vehicle1);
		d.setDriveStatus(DriveStatus.APPROVED);
		Calendar c = Calendar.getInstance();
		c.set(2015, Calendar.NOVEMBER, 20);
		d.setStartDate(c.getTime());
		c.set(2015, Calendar.NOVEMBER, 25);
		d.setEndDate(c.getTime());
		return d;
	}
}
