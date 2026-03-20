package com.test

import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType

@Path("/api/v1/hello")
class Hello {
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    fun hello(): String {
        return "HELLO!"
    }
}
