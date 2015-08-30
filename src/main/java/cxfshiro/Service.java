package cxfshiro;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;


public class Service extends JService {
	
	@Override
	public Response status()
	{
		return Response.status(Status.OK).entity("Service up and running!").build();
	}


}
