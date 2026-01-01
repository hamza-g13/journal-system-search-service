package kth.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.Map;

@Path("/")
public class HealthResource {

    @GET
    @Path("/healthz")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, String> healthz() {
        return Map.of(
                "status", "UP",
                "service", "search-service");
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, String> root() {
        return Map.of(
                "status", "UP",
                "service", "search-service");
    }
}
