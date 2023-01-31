package at.faist.data.model.factoria;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FactoriaServerModel {
    private String name;
    private String ports;
    private boolean running;
    private String user;
}
