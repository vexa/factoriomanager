package at.faist.views.createserver.pmo;

import at.faist.data.model.factoria.FactoriaInstanceModel;
import org.linkki.core.pmo.ModelObject;
import org.linkki.core.ui.element.annotation.UIButton;
import org.linkki.core.ui.element.annotation.UITextField;
import org.linkki.core.ui.layout.annotation.UIFormLayout;

import java.util.function.Consumer;
import java.util.function.Supplier;

@UIFormLayout()
public class CreateInstancePmo {
    Supplier<FactoriaInstanceModel> factoriaInstance;
    Consumer<FactoriaInstanceModel> erstellenConsumer;

    public CreateInstancePmo(Supplier<FactoriaInstanceModel> factoriaInstance, Consumer<FactoriaInstanceModel> erstellenConsumer) {
        this.factoriaInstance = factoriaInstance;
        this.erstellenConsumer = erstellenConsumer;
    }

    @ModelObject
    public FactoriaInstanceModel getModel() {
        return factoriaInstance.get();
    }

    @UITextField(position = 10, label = "Server Name", modelAttribute = "instanceName")
    public String getInstanceName() {
        return getModel().getName();
    }

    public void setInstanceName(String name) {
        getModel().setName(name);
    }

    @UIButton(position = 20, caption = "Erstellen")
    public void erstelleServer() {
        this.erstellenConsumer.accept(getModel());
    }
}
