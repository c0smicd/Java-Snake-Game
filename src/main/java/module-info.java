module com.cosmic.snakegamecraft {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires atlantafx.base;
    requires java.desktop;
    requires annotations;
    requires java.compiler;

    opens com.cosmic.snakegamecraft to javafx.fxml;
    exports com.cosmic.snakegamecraft;
    exports com.cosmic.snakegamecraft.controller;
    opens com.cosmic.snakegamecraft.controller to javafx.fxml;
}