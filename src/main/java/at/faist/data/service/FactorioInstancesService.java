package at.faist.data.service;

import at.faist.data.model.docker.DockerCompose;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class FactorioInstancesService {
    private final YmlCreationService ymlCreationService;
    private final Logger logger = LoggerFactory.getLogger(FactorioInstancesService.class);

    public FactorioInstancesService(YmlCreationService ymlCreationService) {
        this.ymlCreationService = ymlCreationService;
    }

    public YmlCreationService getYmlCreationService() {
        return ymlCreationService;
    }

    public String createInstance(String user, String instanceName) {
        var file = getInstance(user, instanceName);
        file.mkdirs();
        return file.getPath();
    }

    public String getSettingsJson(String user, String instanceName) {
        var settingJsonFile = new File("/opt/factorio/" + user + "/" + instanceName + "/config/server-settings.json");
        if (settingJsonFile.exists()) {
            try {
                return FileUtils.readFileToString(settingJsonFile, StandardCharsets.UTF_8);
            } catch (IOException e) {
                logger.error("Error reading settings json");
            }
        }
        return "";
    }

    public String getPathToServerInstance(String user, String instanceName) {
        return new File("/opt/factorio/" + user + "/" + instanceName).getPath();
    }

    public boolean instanceExists(String instanceName) {
        if (instanceName != null) {
            try {
                return Files.walk(Paths.get("/opt/factorio/"), 2)
                        .filter(Files::isDirectory)
                        .filter(f -> f.endsWith(instanceName)).findAny().isPresent();
            } catch (IOException e) {
                logger.error("Error when reading instanceExists()", e);
            }
        }
        return true;
    }


    public DockerCompose getDockerCompose(String user, String instanceName) {
        try {
            return getYmlCreationService().readYaml(getDockerComposeFile(user, instanceName));
        } catch (IOException e) {
            logger.error("Error user {} instancename {} read yml", user, instanceName, e);
        }
        return null;
    }

    public List<String> getAllInstances(String user) {
        return Optional.ofNullable(new File("/opt/factorio/" + user).listFiles(File::isDirectory)).map(Arrays::stream).orElse(Stream.empty())
                .map(f -> f.getName()).collect(Collectors.toList());
    }

    public void saveYamlToInstance(DockerCompose compose, String user, String instanceName) throws IOException {
        ymlCreationService.writeYamlToFile(compose, getDockerComposeFile(user, instanceName));
    }

    public boolean isYmlInInstance(String user, String instanceName) {
        return getDockerComposeFile(user, instanceName).exists();
    }

    public void deleteInstance(String user, String instanceName) throws IOException {
        var instance = getInstance(user, instanceName);
        if (instance.exists()) {
            FileUtils.deleteDirectory(instance);
        }
    }

    public boolean userInstanceExists(String user, String instanceName) {
        return getInstance(user, instanceName).exists();
    }

    public void saveModFileToInstance(String user, String instanceName, InputStream inputStream, String fileName) {
        if (userInstanceExists(user, instanceName)) {
            var modFolder = getModFolder(user, instanceName);
            modFolder.mkdirs();
            try {
                FileUtils.copyInputStreamToFile(inputStream, new File(modFolder + File.separator + fileName));
            } catch (IOException e) {
                logger.error("Error user {} save mod {}", user, fileName, e);
            }
        }
    }

    public void saveGameSaveStateFileToInstance(String user, String instanceName, InputStream inputStream, String fileName) {
        if (userInstanceExists(user, instanceName)) {
            var modFolder = getGamesSaveFolder(user, instanceName);
            modFolder.mkdirs();
            try {
                FileUtils.copyInputStreamToFile(inputStream, new File(modFolder + File.separator + fileName));
            } catch (IOException e) {
                logger.error("Error user {} save game {}", user, fileName, e);
            }
        }
    }

    public List<String> listMods(String user, String instanceName) {
        return Optional.ofNullable(getModFolder(user, instanceName).listFiles(File::isFile)).map(Arrays::stream).orElse(Stream.empty())
                .map(f -> f.getName()).collect(Collectors.toList());
    }

    public String getGameSaveName(String user, String instanceName) {
        return Optional.ofNullable(getGamesSaveFolder(user, instanceName).listFiles(File::isFile)).map(Arrays::stream).orElse(Stream.empty())
                .map(f -> f.getName()).findFirst().orElse("");
    }

    public void deleteSaveGame(String user, String instanceName) {
        Optional.ofNullable(getGamesSaveFolder(user, instanceName).listFiles(File::isFile)).map(Arrays::stream).orElse(Stream.empty()).forEach(f -> FileUtils.deleteQuietly(f));
    }

    public File getDockerComposeFile(String user, String instanceName) {
        return new File(getInstance(user, instanceName).getPath() + "/docker-compose.yml");
    }

    private File getInstance(String user, String instanceName) {
        return new File("/opt/factorio/" + user + "/" + instanceName);
    }


    private File getModFolder(String user, String instanceName) {
        var dirToUpload = getInstance(user, instanceName);
        return new File(dirToUpload.getPath() + File.separator + "mods");
    }

    private File getGamesSaveFolder(String user, String instanceName) {
        var dirToUpload = getInstance(user, instanceName);
        return new File(dirToUpload.getPath() + File.separator + "saves");
    }

    public boolean loescheMod(String user, String instanceName, String mod) {
        return Optional.ofNullable(getModFolder(user, instanceName).listFiles(File::isFile)).map(Arrays::stream).orElse(Stream.empty())
                .filter(f -> f.getName().equals(mod))
                .map(f -> f.delete()).findAny().orElse(false);
    }


    public void saveCompose(String user, String instanceName, DockerCompose compose) {
        try {
            ymlCreationService.writeYamlToFile(compose, getDockerComposeFile(user, instanceName));
        } catch (IOException e) {
            logger.error("Error writing compose", e);
        }
    }

    public void saveServerSettingsFile(String value, String currentUser, String instanceName) {
        var settingJsonFile = new File("/opt/factorio/" + currentUser + "/" + instanceName + "/config/server-settings.json");
        try {
            FileUtils.writeStringToFile(settingJsonFile, value, StandardCharsets.UTF_8);
        } catch (IOException e) {
            logger.error("Error writing Json value to settings file {} {}", currentUser, instanceName, e);
        }
    }
}