package at.faist.data.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class YmlCreationServiceTest {
    YmlCreationService service = new YmlCreationService();

    @Test
    void createYml() throws JsonProcessingException {
        var result = service.createYml(service.getDefaultFacotriaCompose());
        Assertions.assertNotNull(result);
    }

    @Test
    void createYmlAndModify() throws IOException {
        var defaultValues = service.getDefaultFacotriaCompose();
        defaultValues.getServices().getFactorio().setContainer_name("Factorio-Spiel1");
        defaultValues.getServices().getFactorio().setSaveName("Factorio-SaveGame01");
        var result = service.createYml(defaultValues);
        var modifiedValue = service.readYaml(result);
        Assertions.assertEquals(defaultValues.getServices().getFactorio().getContainer_name(), modifiedValue.getServices().getFactorio().getContainer_name());
    }

}