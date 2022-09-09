module com.example.mailclient {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;

    opens com.example.mailclient to javafx.fxml;
    exports com.example.mailclient;
    exports middleware;
    opens middleware to javafx.fxml;
}