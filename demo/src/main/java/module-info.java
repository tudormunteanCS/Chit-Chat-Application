module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.postgresql.jdbc;


    opens com.example.demo to javafx.fxml, javafx.base;
    opens domain to javafx.base;
    exports com.example.demo;


}
