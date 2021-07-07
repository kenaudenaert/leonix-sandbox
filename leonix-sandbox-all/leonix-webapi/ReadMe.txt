Spring-5.2.x
============
https://www.baeldung.com/spring-web-contexts
	=> spring-web context configuration in web.xml or programmically.

|jar:spring-web-5.2.x|
----------------------
package: org.springframework.web
	SpringServletContainerInitializer
		-> implements javax.servlet.ServletContainerInitializer (programmatic web.xml).
	WebApplicationInitializer
		-> delegation api for SpringServletContainerInitializer.
package: org.springframework.web.context
	WebApplicationContext
		-> interface for an ApplicationContext with a javax.servlet.ServletContext.
	ContextLoaderListener
		-> implements javax.servlet.ServletContextListener (start() & stop() webapp)
	ContextLoader
		-> implementation base-class of ContextLoaderListener.
	AbstractContextLoaderInitializer (base for API configuration)
		-> abstract WebApplicationInitializer implementation for programmatic web.xml.

|jar:spring-webmvc-5.2.x|
------------------------
package: org.springframework.web.servlet (web.xml)
	DispatcherServlet
		-> configuration of a seperate (child) spring-mvc web-application-context.
package: org.springframework.web.servlet.support
	AbstractDispatcherServletInitializer (base for API configuration)
		-> abstract WebApplicationInitializer implementation for programmatic web.xml.
