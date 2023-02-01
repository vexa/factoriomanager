package at.faist.views.createserver.pmo;


import at.faist.data.service.FactorioInstancesService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;

public class ServerSettingsJsonEditor extends VerticalLayout {
    private final FactorioInstancesService service;
    private final Button save;
    private final TextArea textArea = new TextArea();
    private final String currentUser;
    private final String instanceName;

    public ServerSettingsJsonEditor(FactorioInstancesService service, String currentUser, String instanceName) {
        this.service = service;
        this.currentUser = currentUser;
        this.instanceName = instanceName;
        textArea.setWidthFull();
        textArea.setHeight("500px");
        textArea.setMaxHeight("500px");
        textArea.setValue(getSettingsOrDefault());
        save = new Button("Speichern", e -> {
            service.saveServerSettingsFile(textArea.getValue(), currentUser, instanceName);
        });
        add(textArea);
        add(save);
    }

    private String getSettingsOrDefault() {
        var res = service.getSettingsJson(currentUser, instanceName);
        if (StringUtils.isBlank(res)) {
            res = loadDefaultSettings();
        }
        return res;
    }

    private String loadDefaultSettings() {
        try {
            var res = getClass().getClassLoader().getResourceAsStream("default-server-settings.json");
            String text = new String(res.readAllBytes(), StandardCharsets.UTF_8);
            return text;
        } catch (Exception e) {

        }
        return "";
    }
}
