package pl.adambaranowski.rs_auth_server.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ExceptionsLogger {
    private static final String MESSAGE = "\n\n Exception occurred: %s \n By User: \n %s \n";
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionsLogger.class);

    @Around("execution(* pl.adambaranowski.rs_auth_server.exception.handler..*.*(..))")
    public Object logControllerActivity(ProceedingJoinPoint joinPoint) throws Throwable {
        String user = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();

        Class<? extends ProceedingJoinPoint> aClass = joinPoint.getClass();
        Object[] exceptions = joinPoint.getArgs();

        StringBuilder argsBuilder = new StringBuilder();

        for (Object arg: exceptions
        ) {
            argsBuilder.append(arg.toString()).append("\n");
        }

        LOGGER.info(String.format(MESSAGE, argsBuilder.toString(), user));

        return joinPoint.proceed();
    }
}
