package at.faist.views;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.router.Route;

@Route("error")
public class ErrorVIew extends Div {
    public ErrorVIew() {
        add(new H2("Die Angeforderte Seite Existiert leider nicht"));
    }
}
