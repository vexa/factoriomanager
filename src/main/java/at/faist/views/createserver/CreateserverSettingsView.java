package at.faist.views.createserver;

import at.faist.data.model.factoria.FactoriaSettingsModel;
import at.faist.data.service.CurrentUserService;
import at.faist.data.service.FactorioInstancesService;
import at.faist.views.AbstractServerView;
import at.faist.views.MainLayout;
import at.faist.views.createserver.pmo.ModPmo;
import at.faist.views.createserver.pmo.SaveGamePmo;
import at.faist.views.createserver.pmo.ServerSettingsJsonEditor;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import javax.annotation.security.PermitAll;

@PermitAll
@PageTitle("Configure server")
@Route(value = "server/:instancename/settings", layout = MainLayout.class)
@Uses(Icon.class)
public class CreateserverSettingsView extends AbstractServerView {


    private FactoriaSettingsModel store;

    public CreateserverSettingsView(FactorioInstancesService factorioInstancesService, CurrentUserService currentUserService) {

        super(factorioInstancesService, currentUserService);
        addClassName("serversettings-view");
    }


    @Override
    protected void loadSite(String instanceName) {
        removeAll();
        add(new H1("Server Settings für " + instanceName));
        add(new H2("Save Game"));
        var saveGamePmo = new SaveGamePmo(factorioInstancesService, currentUserService, () -> getInstanceModel());
        add(saveGamePmo);
        saveGamePmo.render(instanceName);
        add(new H1("Mods"));
        var modPmo = new ModPmo(factorioInstancesService, currentUserService);
        add(modPmo);
        modPmo.renderMod(instanceName);
        modPmo.renderUpload(instanceName);
        add(new H1("Server Settings Json "));
        add(new ServerSettingsJsonEditor(factorioInstancesService, currentUserService.getCurrentUser(), instanceName));
    }


    private FactoriaSettingsModel getInstanceModel() {
        if (store == null) {
            return store = new FactoriaSettingsModel();
        }
        return store;
    }

}
