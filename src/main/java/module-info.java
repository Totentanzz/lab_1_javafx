module com.example.fishapp.lab_1 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.fishapp.lab_1 to javafx.fxml;
    exports com.example.fishapp.lab_1;
}