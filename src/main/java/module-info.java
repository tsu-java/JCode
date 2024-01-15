module ge.tsu.jcode {
    requires java.desktop;
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.apache.tika.core;

    exports ge.tsu.jcode;
    exports ge.tsu.jcode.fxml;

    opens ge.tsu.jcode to javafx.fxml;
    opens ge.tsu.jcode.fxml to javafx.fxml;
}