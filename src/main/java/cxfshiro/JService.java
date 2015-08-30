package cxfshiro;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;
import org.apache.cxf.rs.security.cors.LocalPreflight;

import com.sun.jersey.multipart.FormDataParam;


@CrossOriginResourceSharing(allowAllOrigins = true, maxAge = 100000,  
allowHeaders = {"X-custom-1", "X-custom-2"}, exposeHeaders = {"X-custom-3", "X-custom-4"})
@Path("/service")
public abstract class JService {


	@GET
	@Path("/status/")
	public abstract Response status();


}

