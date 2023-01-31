package at.faist.views.createserver.pmo;

import at.faist.data.model.factoria.FactoriaSettingsModel;
import at.faist.data.service.CurrentUserService;
import at.faist.data.service.FactorioInstancesService;
import at.faist.views.FileUtil;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.upload.receivers.FileBuffer;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Supplier;

public class SaveGamePmo extends Div {
    private final FileBuffer saveGameReceiver = new FileBuffer();
    private final FactorioInstancesService factorioInstancesService;
    private final CurrentUserService currentUserService;
    private final Supplier<FactoriaSettingsModel> model;

    public SaveGamePmo(FactorioInstancesService service, CurrentUserService userService, Supplier<FactoriaSettingsModel> model) {
        this.factorioInstancesService = service;
        this.currentUserService = userService;
        this.model = model;
    }

    public void render(String instanceName) {
        removeAll();
        var savegameName = factorioInstancesService.getGameSaveName(currentUserService.getCurrentUser(), instanceName);
        if (StringUtils.isBlank(savegameName)) {
            add(new SaveGameUploadPmo(saveGameReceiver, e -> {
                String fileName = e.getFileName();
                factorioInstancesService.saveGameSaveStateFileToInstance(currentUserService.getCurrentUser(), instanceName, saveGameReceiver.getInputStream(), fileName);
                var compose = factorioInstancesService.getDockerCompose(currentUserService.getCurrentUser(), instanceName);
                compose.getServices().getFactorio().setSaveName(FileUtil.removeExtension(fileName));
                factorioInstancesService.saveCompose(currentUserService.getCurrentUser(), instanceName, compose);
                render(instanceName);
            }));
        } else {
            add(new H3("Gespeicherte Spielstand"));
            add(new Button("Lösche " + savegameName, e -> {
                factorioInstancesService.deleteSaveGame(currentUserService.getCurrentUser(), instanceName);
                render(instanceName);
            }));
        }
    }
}
