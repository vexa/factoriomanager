package at.faist.views.createserver;

import at.faist.data.model.factoria.FactoriaInstanceModel;
import at.faist.data.service.CurrentUserService;
import at.faist.data.service.FactorioInstancesService;
import at.faist.data.service.YmlCreationService;
import at.faist.views.AbstractValidationView;
import at.faist.views.MainLayout;
import at.faist.views.createserver.pmo.CreateInstancePmo;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParam;
import com.vaadin.flow.router.RouteParameters;
import org.linkki.core.binding.validation.ValidationService;
import org.linkki.core.binding.validation.message.Message;
import org.linkki.core.binding.validation.message.MessageList;
import org.linkki.core.binding.validation.message.Severity;
import org.linkki.core.ui.creation.VaadinUiCreator;

import javax.annotation.security.PermitAll;
import java.util.function.Supplier;

@PermitAll
@PageTitle("Create server")
@Route(value = "server/create", layout = MainLayout.class)
@Uses(Icon.class)
public class CreateserverView extends AbstractValidationView {
    private final FactorioInstancesService factorioInstancesService;
    private final CurrentUserService currentUserService;
    private final YmlCreationService ymlCreationService;
    private FactoriaInstanceModel store;


    public CreateserverView(FactorioInstancesService factorioInstancesService, CurrentUserService currentUserService, YmlCreationService ymlCreationService) {
        this.factorioInstancesService = factorioInstancesService;
        this.currentUserService = currentUserService;
        this.ymlCreationService = ymlCreationService;
        addClassName("createserver-view");
        getDefaultBindingManager(new CreateInstanceValidationService(() -> getInstanceModel(), factorioInstancesService)).addUiUpdateObserver(getBindingContext());
        Component section = VaadinUiCreator.createComponent(new CreateInstancePmo(() -> getInstanceModel(), this::saveInstance), getBindingContext());
        add(section);
    }

    private void saveInstance(FactoriaInstanceModel data) {
        if (getValidationService().getValidationMessages().isEmpty()) {
            factorioInstancesService.createInstance(currentUserService.getCurrentUser(), data.getName());
            ymlCreationService.createDockerCompose(data.getName(), currentUserService.getCurrentUser());
            UI.getCurrent().navigate(CreateserverSettingsView.class, new RouteParameters(new RouteParam("instancename", data.getName())));
            store = null;
        }
        if (factorioInstancesService.userInstanceExists(currentUserService.getCurrentUser(), data.getName())) {
            UI.getCurrent().navigate(CreateserverSettingsView.class, new RouteParameters(new RouteParam("instancename", data.getName())));
            store = null;
        }
    }

    private FactoriaInstanceModel getInstanceModel() {
        if (store == null) {
            return store = new FactoriaInstanceModel();
        }
        return store;
    }


    private static class CreateInstanceValidationService implements ValidationService {
        private final Supplier<FactoriaInstanceModel> modelSupplier;
        private final FactorioInstancesService factorioInstancesService;

        public CreateInstanceValidationService(Supplier<FactoriaInstanceModel> modelSupplier, FactorioInstancesService factorioInstancesService) {
            this.modelSupplier = modelSupplier;
            this.factorioInstancesService = factorioInstancesService;
        }

        @Override
        public MessageList getValidationMessages() {
            if (factorioInstancesService.instanceExists(modelSupplier.get().getName())) {
                return new MessageList(Message.builder("Der Name existiert bereits", Severity.ERROR).invalidObjectWithProperties(modelSupplier.get(), "instanceName").create());
            }
            return new MessageList();
        }
    }
}
