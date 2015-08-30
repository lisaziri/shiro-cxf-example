package cxfshiro;

import java.util.Arrays;
import java.util.EnumSet;

import javax.servlet.DispatcherType;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.bus.spring.SpringBusFactory;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;
import org.apache.cxf.jaxrs.servlet.CXFNonSpringJaxrsServlet;
import org.apache.cxf.rs.security.cors.CrossOriginResourceSharingFilter;
import org.apache.cxf.transport.Destination;
import org.apache.cxf.transport.http_jetty.JettyHTTPDestination;
import org.apache.cxf.transport.http_jetty.JettyHTTPServerEngine;
import org.apache.cxf.transport.http_jetty.ServerEngine;
import org.apache.shiro.web.env.EnvironmentLoaderListener;
import org.apache.shiro.web.servlet.ShiroFilter;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

public class Main {

	
	public static void main(String args[]) {
		
			try  {
				SpringBusFactory factory = new SpringBusFactory();
				Bus bus = factory.createBus("ServerConfig.xml");
				BusFactory.setDefaultBus(bus);

				JAXRSServerFactoryBean sf = new JAXRSServerFactoryBean();

				JacksonJaxbJsonProvider jackson = new JacksonJaxbJsonProvider();
				ObjectMapper m = new ObjectMapper();
				m.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);

				String address = "https://localhost:9000";
				jackson.setMapper(m);
				CrossOriginResourceSharingFilter cors = new CrossOriginResourceSharingFilter();
				sf.setProviders( Arrays.< Object >asList(cors, jackson) ); 
				sf.setResourceClasses(JService.class );
				sf.setResourceProvider(JService.class, new SingletonResourceProvider(new Service()));
				System.out.println("webservice published on "+address);
				sf.setAddress(address);

				CXFNonSpringJaxrsServlet jaxrsServlet = new CXFNonSpringJaxrsServlet();
				final ServletHolder servletHolder = new ServletHolder(jaxrsServlet);

				ServletContextHandler context=new ServletContextHandler(ServletContextHandler.SECURITY);
				context.addServlet(servletHolder, "/*");

				context.setContextPath("/");
				context.setInitParameter("shiroConfigLocations","classpath:shiro.ini");

				context.addEventListener(new EnvironmentLoaderListener());
				FilterHolder filterHolder = new FilterHolder();
				filterHolder.setFilter(new ShiroFilter());

				EnumSet<DispatcherType> types = EnumSet.allOf(DispatcherType.class);
				context.addFilter(filterHolder, "/*", types);

				Server cxfServer = sf.create();
				Destination dest = cxfServer.getDestination(); 
				JettyHTTPDestination jettyDestination = JettyHTTPDestination.class.cast(dest); 
				ServerEngine engine = jettyDestination.getEngine(); 
				JettyHTTPServerEngine serverEngine = JettyHTTPServerEngine.class.cast(engine); 
				org.eclipse.jetty.server.Server httpServer = serverEngine.getServer(); 

				// Had to start the server to get the Jetty Main instance. 
				// Have to stop it to add the custom Jetty handler. 
				httpServer.stop(); 
				httpServer.join();

	//			httpServer.setHandler(context);

				httpServer.start();
				httpServer.join();
				
				System.out.println("webservice active on "+cxfServer.getEndpoint().getEndpointInfo().getAddress());
				System.out.println("Main ready...");
			} catch (Exception e) {
				System.out.println(e.getMessage());
				System.out.println(e.getCause().getMessage());
			}

			//Thread.sleep(5 * 6000 * 1000);
			//System.out.println("Main exiting");
			//System.exit(0);
		}


}
