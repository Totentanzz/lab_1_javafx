module lab_1.fishApp {
    requires javafx.controls;
    requires javafx.fxml;
    requires typesafe.config;
    requires lombok;
    requires java.sql;

    exports lab_1.fishApp;
    opens lab_1.fishApp to javafx.graphics;
    exports lab_1.fishApp.controller;
    opens lab_1.fishApp.controller to javafx.fxml;
    exports lab_1.fishApp.model;
    opens lab_1.fishApp.model to javafx.fxml, msgpack.template.TemplateRegistry;
    exports lab_1.fishApp.utils;
    opens lab_1.fishApp.utils to javafx.graphics;
}