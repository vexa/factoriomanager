package at.faist.data.service;

import at.faist.data.model.docker.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Service
public class YmlCreationService {
    private final ObjectMapper mapper = new ObjectMapper(new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER));
    Logger logger = LoggerFactory.getLogger(YmlCreationService.class);

    public YmlCreationService() {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Config.class, new ConfigJsonDeserializer());
        mapper.registerModule(module);
    }

    public String createYml(DockerCompose dockerCompose) throws JsonProcessingException {
        return mapper.writeValueAsString(dockerCompose);
    }

    public DockerCompose readYaml(File yaml) throws IOException {
        return mapper.readValue(FileUtils.readFileToByteArray(yaml), DockerCompose.class);
    }

    public DockerCompose readYaml(String yaml) throws IOException {
        return mapper.readValue(yaml.getBytes(StandardCharsets.UTF_8), DockerCompose.class);
    }

    public void writeYamlToFile(DockerCompose dockerCompose, File file) throws IOException {
        mapper.writeValue(file, dockerCompose);
    }

    public void createDockerCompose(String instanceName, String username) {
        var pathToInstance = "/opt/factorio/" + username + "/" + instanceName;
        var compose = getDefaultFacotriaCompose();
        compose.getServices().getFactorio().setContainer_name(instanceName);
        compose.getServices().getFactorio().setVolumes(List.of(pathToInstance + ":/factorio"));
        compose.getServices().getFactorio().setSaveName("autogenerated_savegame");
        compose.getServices().getFactorio().setGENERATE_NEW_SAVE(true);
        try {
            writeYamlToFile(compose, new File(pathToInstance + File.separator + "docker-compose.yml"));
        } catch (Exception e) {
            logger.error("Error writing docker-componse.yml", e);
        }
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public DockerCompose getDefaultFacotriaCompose() {
        return DockerCompose.builder()
                .version("3.5")
                .services(
                        ComposeServices.builder()
                                .factorio(
                                        ComposeService.builder()
                                                .image("factoriotools/factorio:stable")
                                                .restart("unless-stopped")
                                                .container_name("factorio-default")
                                                .environment(Map.of("LOAD_LATEST_SAVE", "true", "SAVE_NAME", "replace", "UPDATE_MODS_ON_START", "false"))
                                                .ports(List.of("34197-35197:34197/udp", "27015-27115:27015/tcp"))
                                                .volumes(List.of("/opt/factorio:/factorio"))
                                                .networks(List.of("factorioserver"))
                                                .build())
                                .build())
                .networks(ComposeNetworks.builder().def(
                                ComposeNetwork.builder().driver("bridge").name("factorioserver")
                                        .ipam(NetworkIpam.builder()
                                                .driver("default")
                                                .config(List.of(SubnetConfig.builder().subnet("172.28.0.0/16").build()))
                                                .build())
                                        .build())
                        .build())
                .build();
    }
}
