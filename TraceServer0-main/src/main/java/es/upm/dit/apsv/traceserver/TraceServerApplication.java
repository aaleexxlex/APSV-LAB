/*package es.upm.dit.apsv.traceserver;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import es.upm.dit.apsv.traceserver.model.Trace;
//import es.upm.dit.apsv.traceserver.model.TransportationOrder;
import es.upm.dit.apsv.traceserver.repository.TraceRepository;

@SpringBootApplication
public class TraceServerApplication {

	@Autowired
	private Environment env;
	
	public static final Logger log = LoggerFactory.getLogger(TraceServerApplication.class);

	private final TraceRepository tr;

	public TraceServerApplication(TraceRepository tr) {
		this.tr = tr;
	}

	public static void main(String[] args) {
		SpringApplication.run(TraceServerApplication.class, args);
		log.info("Prueba consumer arrancando...");
	}

	@Bean("consumer")
	public Consumer<Trace> checkTrace() {
		return t -> {
                          log.info("Order: "+ t);
			}
		};
	}
}*/
 /*package es.upm.dit.apsv.traceserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TraceServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TraceServerApplication.class, args);
    }

}*/
package es.upm.dit.apsv.traceserver;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import es.upm.dit.apsv.traceserver.model.Trace;
import es.upm.dit.apsv.traceserver.model.TransportationOrder;
import es.upm.dit.apsv.traceserver.repository.TraceRepository;

@SpringBootApplication
public class TraceServerApplication {

    @Autowired
    private Environment env;

    public static final Logger log = LoggerFactory.getLogger(TraceServerApplication.class);

    private final TraceRepository tr;

    public TraceServerApplication(TraceRepository tr) {
        this.tr = tr;
    }

    public static void main(String[] args) {
        SpringApplication.run(TraceServerApplication.class, args);
        log.info("TraceServer (consumer) arrancando...");
    }

    @Bean("consumer")
    public Consumer<Trace> checkTrace() {
        return t -> {
            // 1) generar un id único para la traza y guardarla
            t.setTraceId(t.getTruck() + "-" + t.getLastSeen());
            tr.save(t);
            log.info("Trace almacenada: {}", t);

            // 2) llamar al microservicio de órdenes para actualizarla
            //    leemos la URL base de application.properties (orders.port)
            String baseUri = env.getProperty("orders.port", "http://localhost:8081/orders/");

            RestTemplate restTemplate = new RestTemplate();
            TransportationOrder result = null;

            try {
                // en el enunciado se busca la orden por el id del camión
                result = restTemplate.getForObject(
                        baseUri + t.getTruck(),
                        TransportationOrder.class
                );
            } catch (HttpClientErrorException.NotFound ex) {
                log.info("No se ha encontrado orden para el camión {}", t.getTruck());
                result = null;
            }

            // 3) si hay orden y está en estado 0, actualizamos posición y estado
            if (result != null && result.getSt() == 0) {
                result.setLastDate(t.getLastSeen());
                result.setLastLat(t.getLat());
                result.setLastLong(t.getLng());

                if (result.distanceToDestination() < 10) {
                    result.setSt(1);
                }

                // PUT para actualizar la orden
                // (si tu endpoint usa otra ruta, ajusta aquí)
                restTemplate.put(baseUri + result.getToid(), result);

                log.info("Order updated: {}", result);
            }
        };
    }
}
