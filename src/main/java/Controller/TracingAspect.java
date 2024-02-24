package Controller;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.aspectj.lang.ProceedingJoinPoint;

import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import io.opentracing.Span;
import io.opentracing.Tracer;
@Component
@Aspect
public class TracingAspect {
    private final Tracer tracer;
    private static final Logger logger = Logger.getLogger(TracingAspect.class.getName());

    public TracingAspect(Tracer tracer) {
        this.tracer = tracer;
        System.out.println("alsjjjhhhhhhhhhhhhhhhhhhhhhhhhhhhh");

    }

    @Pointcut(value = "@within(Controller.Trace)")
    private void spanClass() {
        System.out.println("alsjjjhhhhhhhhhhhhhhhhhhhhhhhhhhhh");

    }

    @Pointcut(value = "execution(* *(..))")
    private void spanMethod() {
        System.out.println("alsjjjhhhhhhhhhhhhhhhhhhhhhhhhhhhh");

    }

    @Around(value = "spanClass() && spanMethod()")
    public Object addSpan(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        String methodName = signature.getName();
        String className = point.getTarget().getClass().getName();
        System.out.println("alsjjjhhhhhhhhhhhhhhhhhhhhhhhhhhhh");

        logger.log(Level.INFO, "Starting span for method: {0}.{1}", new Object[]{className, methodName});

        Span span = tracer.buildSpan(className + "." + methodName).start();
        System.out.println("alsjjjhhhhhhhhhhhhhhhhhhhhhhhhhhhh");

        try {
            Object result = point.proceed();
            return result;
        } catch (Throwable throwable) {
            // Handle exceptions
            throw throwable;
        } finally {
            span.finish();
            logger.log(Level.INFO, "Ending span for method: {0}.{1}", new Object[]{className, methodName});
            System.out.println("alsjjjhhhhhhhhhhhhhhhhhhhhhhhhhhhh");
        }
    }
}
