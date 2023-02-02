package at.faist.data.service;

import org.apache.commons.lang3.StringUtils;
import org.buildobjects.process.ProcBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class DockerComposeService {
    Logger logger = LoggerFactory.getLogger(DockerComposeService.class);

    public void pull(String pathToDockerCompose) {
        try {
            File f = new File(pathToDockerCompose + File.separator + "docker-compose.yml");
            if (f.exists()) {
                var procResult = new ProcBuilder("docker-compose").withArgs("-f", f.getAbsolutePath(), "pull").withTimeoutMillis(220000).run();
                var errorByte = procResult.getErrorBytes();
                System.out.println(errorByte);
                System.out.println("-----");
                System.out.println(procResult.getCommandLine());
            }
        } catch (Exception e) {
            logger.error("Error during pull", e);
        }
    }

    public void up(String pathToDockerCompose) {
        try {
            File f = new File(pathToDockerCompose + File.separator + "docker-compose.yml");
            if (f.exists()) {
                var procResult = new ProcBuilder("docker-compose").withArgs("-f", f.getAbsolutePath(), "up", "-d").withTimeoutMillis(120000).run();
                var errorByte = procResult.getErrorBytes();
                System.out.println(errorByte);
                System.out.println("-----");
                System.out.println(procResult.getCommandLine());
            }
        } catch (Exception e) {
            logger.error("Error during up", e);
        }
    }

    public void down(String pathToDockerCompose) {
        try {
            File f = new File(pathToDockerCompose + File.separator + "docker-compose.yml");
            var procResult = new ProcBuilder("docker-compose").withArgs("-f", f.getAbsolutePath(), "down").withTimeoutMillis(60000).run();
            var errorByte = procResult.getErrorBytes();
            System.out.println(errorByte);
            System.out.println("-----");
            System.out.println(procResult.getCommandLine());
        } catch (Exception e) {
            logger.error("Error during down", e);
        }
    }

    public void delete(String pathToDockerCompose) {
        try {
            File f = new File(pathToDockerCompose + File.separator + "docker-compose.yml");
            var procResult = new ProcBuilder("docker-compose").withArgs("rm", f.getAbsolutePath()).run();
            var errorByte = procResult.getErrorBytes();
            System.out.println(errorByte);
            System.out.println("-----");
            System.out.println(procResult.getCommandLine());
        } catch (Exception e) {
            logger.error("Error during delete", e);
        }
    }

    public boolean isRunning(String instanceName) {
        try {
            var result = new ProcBuilder("docker").withArgs("container", "inspect", instanceName, "-f '{{.State.Status}}'").run();
            return StringUtils.contains(result.getOutputString(), "running");
        } catch (Exception e) {
            logger.trace("Docker Error ", e);
        }
        return false;
    }

    public String getPorts(String instanceName) {
        try {
            var result = new ProcBuilder("docker").withArgs("container", "inspect", instanceName, "-f '{{.NetworkSettings.Ports}}'").run();
            var res = result.getOutputString();
            res = StringUtils.replace(res, "0.0.0.0", "");
            res = StringUtils.replace(res, "map", "");
            res = StringUtils.replace(res, "{", "");
            res = StringUtils.replace(res, "}", "");
            res = StringUtils.replace(res, "/tcp:[", ":");
            res = StringUtils.replace(res, "/udp:[", ":");
            res = StringUtils.replace(res, "[", "");
            res = StringUtils.replace(res, "]", "");
            return res;
        } catch (Exception e) {
            logger.trace("Docker Error ", e);
        }
        return "no port";
    }

}
