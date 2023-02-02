package at.faist.data.model.docker;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComposeService {

    private String image = "factoriotools/factorio:stable";
    private String container_name;
    private Map<String, String> environment;
    private List<String> ports;
    private List<String> volumes;
    private String restart = "unless-stopped";
    private List<String> networks;

    public void setContainer_name(String container_name) {
        this.container_name = container_name;
        volumes = List.of("/opt/" + container_name + ":/factorio");
    }

    public void setSaveName(String saveName) {
        var mod = new HashMap<>(environment);
        mod.put("SAVE_NAME", saveName);
        mod.put("LOAD_LATEST_SAVE", "false");
        environment = Collections.unmodifiableMap(mod);
    }

    public void setGENERATE_NEW_SAVE(boolean newSave) {
        var mod = new HashMap<>(environment);
        mod.put("GENERATE_NEW_SAVE", Boolean.toString(newSave));
        environment = Collections.unmodifiableMap(mod);
    }
}
