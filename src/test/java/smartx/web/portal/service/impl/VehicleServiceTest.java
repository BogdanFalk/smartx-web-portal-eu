package smartx.web.portal.service.impl;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import smartx.data.vehicle.Vehicle;
import smartx.data.vehicle.VehicleService;
import smartx.utils.Status;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@ExtendWith(SpringExtension.class)
class VehicleServiceTest {

    @Autowired
    private VehicleService vehicleService;

    private static List<Vehicle> vehicleList;

    @BeforeAll
    public static void beforeAll() {
        System.out.println("@BeforeAll - oneTimeSetUp");
        vehicleList = new ArrayList<>();
        Vehicle vehicle;
        for (int i = 0; i < 5; i++) {
            vehicle = new Vehicle();
            //vehicle.setUserId(1);
            vehicle.setStatus(Status.AVAILABLE.getValue());
//            vehicle.setCreateDate(LocalDateTime.now());
//            vehicle.setVehicleDate(LocalDate.now());
            vehicle.setVehicleNumber("Test Vehicle" + i);
            vehicle.setDeviceSerialNumber("Test Desc" + i);
//            vehicle.setContent("Test Vehicle Content " + i);
            vehicleList.add(vehicle);
        }
    }

    @AfterAll
    public static void afterAll() {
        // one-time cleanup code
        System.out.println("@AfterAll - oneTimeTearDown");
    }

    @BeforeEach
    public void beforeEach() throws Exception {
        System.out.println("@BeforeEach - VehicleServiceTest setUp");
    }

    @AfterEach
    public void afterEach() throws Exception {
        System.out.println("@AfterEach - VehicleServiceTest tearDown");
    }

    @Test
    void testSave() {
        List<Vehicle> beforeInsert = vehicleService.getAllVehicles();
        vehicleList.forEach(vehicle -> {
            vehicleService.addVehicle(vehicle);
        });
        List<Vehicle> afterInsert = vehicleService.getAllVehicles();
        assertEquals((afterInsert.size() - beforeInsert.size()), vehicleList.size());

    }

    void delete(List<Vehicle> vehicles) {
        vehicles.forEach(vehicle -> {
            assertNotNull(vehicleService.getVehicleById(vehicle.getId()));
            vehicleService.deleteVehicle(vehicle.getId());
            try {
                vehicleService.getVehicleById(vehicle.getId());
                fail("No value present");
            } catch (NoSuchElementException e) {

            }

        });

    }

}