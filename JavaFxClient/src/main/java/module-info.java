module com.sainnt {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;

    opens com.sainnt to javafx.fxml;
    exports com.sainnt;
}
