package at.faist.views.createserver.pmo;

import at.faist.data.service.CurrentUserService;
import at.faist.data.service.FactorioInstancesService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.receivers.MultiFileBuffer;

public class ModPmo extends Div {

    private final MultiFileBuffer modeUploadReceiver = new MultiFileBuffer();
    private final FactorioInstancesService factorioInstancesService;
    private final CurrentUserService currentUserService;

    private final Div modList = new Div();
    private final VerticalLayout uploadPanel = new VerticalLayout();

    public ModPmo(FactorioInstancesService service, CurrentUserService userService) {
        this.factorioInstancesService = service;
        this.currentUserService = userService;
        add(uploadPanel);
        add(new H3("Verfügbare Mods"));
        add(modList);
    }

    public void renderUpload(String instanceName) {
        uploadPanel.add(new ModUploadPmo(modeUploadReceiver, e -> {
            String fileName = e.getFileName();
            factorioInstancesService.saveModFileToInstance(currentUserService.getCurrentUser(), instanceName, modeUploadReceiver.getInputStream(fileName), fileName);
            renderMod(instanceName);
        }));
    }

    public void renderMod(String instanceName) {
        modList.removeAll();
        factorioInstancesService.listMods(currentUserService.getCurrentUser(), instanceName).forEach(mod -> {
            var delteBtn = new Button(mod + " Löschen", e -> {
                if (factorioInstancesService.loescheMod(currentUserService.getCurrentUser(), instanceName, mod)) {
                    renderMod(instanceName);
                }
            });
            delteBtn.getStyle().set("margin", "5px");
            modList.add(delteBtn);
        });
    }
}
