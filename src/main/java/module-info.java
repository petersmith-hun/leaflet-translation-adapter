module leaflet.translation.adapter {
    requires java.annotation;
    requires java.compiler;
    requires leaflet.component.bridge.api;
    requires leaflet.component.rest.tms.api;
    requires leaflet.component.rest.tms.client;
    requires slf4j.api;
    requires spring.beans;
    requires spring.context;
    requires spring.core;
}