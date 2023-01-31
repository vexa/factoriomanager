package at.faist.data.service;

import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FactorioInstancesServiceTest {
    private final YmlCreationService ymlService = new YmlCreationService();
    private final FactorioInstancesService service = new FactorioInstancesService(ymlService);

    @Test
    @Order(1)
    void createInstance() {
        var instancePath = service.createInstance("demouser", "demoInstance");
        Assertions.assertEquals(new File("/opt/factorio/demouser/demoInstance").getPath(), instancePath);
        Assertions.assertTrue(service.userInstanceExists("demouser", "demoInstance"));
    }

    @Test
    @Order(2)
    void listInstance() {
        var instances = service.getAllInstances("demouser");
        Assertions.assertTrue(instances.contains("demoInstance"));
    }

    @Test
    @Order(3)
    void saveYmlToInstance() throws IOException {
        var defaultyml = ymlService.getDefaultFacotriaCompose();
        service.saveYamlToInstance(defaultyml, "demouser", "demoInstance");
        Assertions.assertTrue(service.isYmlInInstance("demouser", "demoInstance"));
    }

    @Test
    @Order(4)
    void saveMod() throws IOException {
        var demoFile = this.getClass().getResourceAsStream("/demofiles/modFile.zip");
        service.saveModFileToInstance("demouser", "demoInstance", demoFile, "modFile.zip");
        Assertions.assertTrue(service.listMods("demouser", "demoInstance").contains("modFile.zip"));
    }

    @Test
    @Order(4)
    void saveGameSaveState() throws IOException {
        var demoFile = this.getClass().getResourceAsStream("/demofiles/saveFile.zip");
        service.saveGameSaveStateFileToInstance("demouser", "demoInstance", demoFile, "saveFile.zip");
        Assertions.assertEquals("saveFile.zip", service.getGameSaveName("demouser", "demoInstance"));
    }

    @Test
    @Order(5)
    void checkInstnaceExists() throws IOException {
        service.createInstance("huhu", "demoInstance3");
        service.createInstance("sdsd", "demoInstance2");
        service.createInstance("dfgdfgdf", "sdfsdf");
        var oneExists = service.instanceExists("demoInstance3");
        var notxists = service.instanceExists("444");
        var twoExtis = service.instanceExists("demoInstance2");
        var threeExists = service.instanceExists("sdfsdf");
        Assertions.assertTrue(oneExists);
        Assertions.assertTrue(twoExtis);
        Assertions.assertTrue(threeExists);
        Assertions.assertFalse(notxists);
        service.deleteInstance("huhu", "demoInstance3");
        service.deleteInstance("sdsd", "demoInstance2");
        service.deleteInstance("dfgdfgdf", "sdfsdf");
    }

    @Test
    @Order(10)
    void deleteInstance() throws IOException {
        service.deleteInstance("demouser", "demoInstance");
        Assertions.assertFalse(service.userInstanceExists("demouser", "demoInstance"));
    }
}