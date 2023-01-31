package at.faist.views.createserver.pmo;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.upload.Receiver;
import com.vaadin.flow.component.upload.SucceededEvent;
import com.vaadin.flow.component.upload.Upload;


public class SaveGameUploadPmo extends Div {


    public SaveGameUploadPmo(Receiver receiver, ComponentEventListener<SucceededEvent> listener) {
        Label label = new Label("Lade ein SaveGames hoch");
        Upload upload = new Upload(receiver);
        upload.setAcceptedFileTypes("application/zip", ".zip");
        add(label);
        add(upload);
        upload.addSucceededListener(listener);
    }

}
