package Model;


import java.io.InputStream;
import java.util.Properties;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import io.jaegertracing.Configuration;
import io.jaegertracing.internal.JaegerTracer;
import io.opentracing.util.GlobalTracer;

public class OpenTracingInitializer implements ServletContextListener {

    private static final Properties properties = new Properties();

    static {
        try {
            InputStream inputStream = OpenTracingInitializer.class.getClassLoader().getResourceAsStream("jaeger.properties");
            properties.load(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static JaegerTracer initJaegerTracer(String serviceName) {
        String jaegerEndpoint = "http://localhost:14268/api/traces";

        Configuration.SenderConfiguration senderConfiguration = new Configuration.SenderConfiguration()
                .withEndpoint(jaegerEndpoint);
        Configuration.ReporterConfiguration reporterConfiguration = new Configuration.ReporterConfiguration()
                .withSender(senderConfiguration);

        Configuration config = new Configuration(serviceName)
                .withReporter(reporterConfiguration);

        return config.getTracer();
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        initializeJaegerTracer("lllllllll");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Clean up resources if needed
    }

    public static void initializeJaegerTracer(String serviceName) {
        JaegerTracer jaegerTracer = initJaegerTracer(serviceName);
        // Register the tracer as the global tracer
        GlobalTracer.register(jaegerTracer);
        System.out.println("Jaeger Tracer initialized successfully.");
    }
}












// import java.io.InputStream;
// import java.util.Properties;
// import javax.servlet.ServletContextEvent;
// import javax.servlet.ServletContextListener;
// import io.jaegertracing.Configuration;
// import io.jaegertracing.internal.JaegerTracer;
// import io.opentracing.util.GlobalTracer;

// public class OpenTracingInitializer implements ServletContextListener{
	
// 	private static final Properties properties = new Properties();

//     static {
//         try {
//             InputStream inputStream = OpenTracingInitializer.class.getClassLoader().getResourceAsStream("jaeger.properties");
//             properties.load(inputStream);
//         } catch (Exception e) {
//             e.printStackTrace();
//         }
//     }

//     public static JaegerTracer initTracer(String serviceName) {
//         // String jaegerHost = properties.getProperty("jaeger.host");
//         // String jaegerPort = properties.getProperty("jaeger.port");

//         String jaegerEndpoint = "http://localhost:14268/api/traces";

//         Configuration.SenderConfiguration senderConfiguration = new Configuration.SenderConfiguration()
//                 .withEndpoint(jaegerEndpoint);
//         Configuration.ReporterConfiguration reporterConfiguration = new Configuration.ReporterConfiguration()
//                 .withSender(senderConfiguration);

//         Configuration config = new Configuration(serviceName)
//                 .withReporter(reporterConfiguration);

//         return config.getTracer();
//     }

//     public static void initializeGlobalTracer(String serviceName) {
//         JaegerTracer tracer = initTracer(serviceName);
//         GlobalTracer.register(tracer);
//         System.out.println("Jaeger Tracer initialized successfully.");
//     }

//     @Override
//     public void contextInitialized(ServletContextEvent sce) {
//         initializeGlobalTracer("FRONT1122");
//     }

//     @Override
//     public void contextDestroyed(ServletContextEvent sce) {
//         // Clean up resources if needed
//     }

// }
