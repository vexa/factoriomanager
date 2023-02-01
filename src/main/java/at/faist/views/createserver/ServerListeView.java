package at.faist.views.createserver;

import at.faist.data.model.factoria.FactoriaServerModel;
import at.faist.data.service.CurrentUserService;
import at.faist.data.service.DockerComposeService;
import at.faist.data.service.FactorioInstancesService;
import at.faist.views.MainLayout;
import at.faist.views.createserver.pmo.ServerGrid;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;

import javax.annotation.security.PermitAll;
import java.util.Collection;
import java.util.stream.Collectors;

@PermitAll
@PageTitle("Configure server")
@Route(value = "server", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@Uses(Icon.class)
public class ServerListeView extends VerticalLayout implements BeforeEnterObserver {

    private final FactorioInstancesService factorioInstancesService;
    private final CurrentUserService currentUserService;
    private final ServerGrid serverGrid;
    private final DockerComposeService dockerComposeService;

    public ServerListeView(FactorioInstancesService factorioInstancesService, CurrentUserService currentUserService, DockerComposeService dockerComposeService) {
        this.factorioInstancesService = factorioInstancesService;
        this.currentUserService = currentUserService;
        this.dockerComposeService = dockerComposeService;
        serverGrid = new ServerGrid(this::startStop, this::edit, this::delete);
    }


    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        add(serverGrid);
        serverGrid.setItems(getItems());
    }

    private void startStop(FactoriaServerModel model) {
        if (model.isRunning()) {
            dockerComposeService.down(factorioInstancesService.getPathToServerInstance(model.getUser(), model.getName()));
        } else {
            dockerComposeService.up(factorioInstancesService.getPathToServerInstance(model.getUser(), model.getName()));
        }
        serverGrid.setItems(getItems());
    }

    private void edit(FactoriaServerModel model) {
        UI.getCurrent().navigate(CreateserverSettingsView.class, new RouteParameters(new RouteParam("instancename", model.getName())));
    }

    private void delete(FactoriaServerModel model) {
        if (dockerComposeService.isRunning(model.getName())) {
            try {
                dockerComposeService.down(factorioInstancesService.getPathToServerInstance(model.getUser(), model.getName()));
            } catch (Exception e) {
            }
            try {
                dockerComposeService.delete(factorioInstancesService.getPathToServerInstance(model.getUser(), model.getName()));
            } catch (Exception e) {
            }
        }
        try {
            factorioInstancesService.deleteInstance(model.getUser(), model.getName());
            serverGrid.setItems(getItems());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Collection<FactoriaServerModel> getItems() {
        return factorioInstancesService.getAllInstances(currentUserService.getCurrentUser()).stream()
                .map(inst ->
                        FactoriaServerModel.builder().name(inst)
                                .ports(dockerComposeService.getPorts(inst))
                                .running(dockerComposeService.isRunning(inst))
                                .user(currentUserService.getCurrentUser())
                                .build()
                ).collect(Collectors.toList());
    }
}
