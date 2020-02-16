open module leaflet.component.tms.adapter {
    requires java.annotation;
    requires java.compiler;
    requires leaflet.component.bridge.api;
    requires leaflet.component.rest.tms.api;
    requires leaflet.component.rest.tms.client;
    requires org.slf4j;
    requires spring.beans;
    requires spring.context;
    requires spring.core;
    requires spring.boot.autoconfigure;
}