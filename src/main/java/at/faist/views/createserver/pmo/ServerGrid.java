package at.faist.views.createserver.pmo;

import at.faist.data.model.factoria.FactoriaServerModel;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.renderer.ComponentRenderer;

import java.util.function.Consumer;

public class ServerGrid extends Grid<FactoriaServerModel> {
    public ServerGrid(Consumer<FactoriaServerModel> startStopAction, Consumer<FactoriaServerModel> editAction, Consumer<FactoriaServerModel> deleteAction) {
        addColumn(FactoriaServerModel::getName).setHeader("Server Name");
        addColumn(FactoriaServerModel::getPorts).setHeader("Ports").setWidth("320px");
        addColumn(this::isRunning).setHeader("Status");
        addColumn(new ComponentRenderer<>(server -> {
            if (server.isRunning()) {
                return new Button("Stop", e -> startStopAction.accept(server));
            } else {
                return new Button("Start", e -> startStopAction.accept(server));
            }
        })).setHeader("Start/Stop");
        addColumn(new ComponentRenderer<>(server ->
                new Button("Edit", e -> editAction.accept(server)))
        ).setHeader("Edit");
        addColumn(new ComponentRenderer<>(server ->
                new Button("Löschen", e -> deleteAction.accept(server)))
        ).setHeader("Löschen");
    }

    private String isRunning(FactoriaServerModel model) {
        return model.isRunning() ? "running" : "stop";
    }
}
