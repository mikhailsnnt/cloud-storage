module com.sainnt {
    requires javafx.controls;
    requires javafx.fxml;
    requires io.netty.common;
    requires io.netty.handler;
    requires io.netty.buffer;
    requires io.netty.resolver;
    requires io.netty.transport;
    requires io.netty.codec;
    requires org.slf4j;
    requires static lombok;

    opens com.sainnt to javafx.fxml;
    opens com.sainnt.controller to javafx.fxml;
    exports com.sainnt;


}
