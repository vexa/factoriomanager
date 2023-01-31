package at.faist.views;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.linkki.core.binding.BindingContext;
import org.linkki.core.binding.manager.DefaultBindingManager;
import org.linkki.core.binding.validation.ValidationService;

public abstract class AbstractValidationView extends VerticalLayout {
    private DefaultBindingManager defaultBindingManager;
    private ValidationService validationService;

    protected DefaultBindingManager getDefaultBindingManager(ValidationService validationService) {
        this.validationService = validationService;
        if (defaultBindingManager == null) {
            return defaultBindingManager = new DefaultBindingManager(validationService);
        }
        return defaultBindingManager;
    }

    protected BindingContext getBindingContext() {
        return defaultBindingManager.getContext(this.getClass());
    }

    protected ValidationService getValidationService() {
        return validationService;
    }
}
