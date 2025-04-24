package com.bionet;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class InsertarEnBDRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        // 🔧 Manejo de errores debe ir al principio del configure()
        onException(Exception.class)
            .handled(true)
            .log("❌ Error al procesar archivo: ${exception.message}")
            .to("file:error");

        // 🔁 Ruta principal
        from("file:processed?noop=true")
            .routeId("archivo-a-sqlserver")
            .split(body().tokenize("\n"))
            .filter(body().isNotNull())
            .process(exchange -> {
                String linea = exchange.getIn().getBody(String.class);
                String[] campos = linea.split(",");

                if (campos.length != 6) {
                    throw new IllegalArgumentException("❌ Línea inválida: " + linea);
                }

                String query = String.format(
                    "INSERT INTO resultados_examenes (laboratorio_id, paciente_id, tipo_examen, resultado, fecha_examen) " +
                    "VALUES (%s, '%s', '%s', '%s', '%s')",
                    campos[1], campos[2], campos[3], campos[4], campos[5]
                );

                exchange.getIn().setBody(query);
            })
            .to("jdbc:dataSource")
            .log("✅ Insertado en BD: ${body}")
            .end()
            .to("file:processed");
    }
}
