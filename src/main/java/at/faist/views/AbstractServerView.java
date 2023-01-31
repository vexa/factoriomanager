package at.faist.views;

import at.faist.data.service.CurrentUserService;
import at.faist.data.service.FactorioInstancesService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;

import java.util.Optional;

public abstract class AbstractServerView extends AbstractValidationView implements BeforeEnterObserver {
    protected final FactorioInstancesService factorioInstancesService;
    protected final CurrentUserService currentUserService;

    protected AbstractServerView(FactorioInstancesService factorioInstancesService, CurrentUserService currentUserService) {
        this.factorioInstancesService = factorioInstancesService;
        this.currentUserService = currentUserService;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        beforeEnterEvent.getRouteParameters().get("instancename")
                .flatMap(name -> {
                    if (factorioInstancesService.userInstanceExists(currentUserService.getCurrentUser(), name)) {
                        return Optional.of(name);
                    } else {
                        return Optional.empty();
                    }
                }).ifPresentOrElse(data -> loadSite(data), () -> UI.getCurrent().navigate(ErrorVIew.class));
    }


    protected abstract void loadSite(String instanceName);

}
