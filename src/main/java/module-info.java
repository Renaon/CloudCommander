module ru.cloud.cloudcommander {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires io.netty.all;
    requires org.apache.logging.log4j;

    opens ru.cloud.cloudcommander to javafx.fxml;
    exports ru.cloud.cloudcommander;
}