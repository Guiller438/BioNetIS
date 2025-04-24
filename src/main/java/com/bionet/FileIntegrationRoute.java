package com.bionet;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;
import java.io.File;

@Component
public class FileIntegrationRoute extends RouteBuilder {

    @Override
    public void configure() {

        from("file:input-labs?noop=true")
            .routeId("file-validator-route")
            .log("Archivo detectado: ${file:name}")
            .choice()
                .when(method(ArchivoValidator.class, "esValido"))
                    .log("Archivo válido: ${file:name}")
                    .to("file:processed")
                .otherwise()
                    .log("Archivo inválido: ${file:name}")
                    .to("file:error");
    }
}
