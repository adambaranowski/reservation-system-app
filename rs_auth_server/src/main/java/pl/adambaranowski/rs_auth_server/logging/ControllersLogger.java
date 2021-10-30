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
public class ControllersLogger {
    private static final String MESSAGE = "\n Invoked: %s \n With params: \n %s \n By User: \n %s \n Returned: %s";
    private static final Logger LOGGER = LoggerFactory.getLogger(ControllersLogger.class);

    @Around("execution(* pl.adambaranowski.rs_auth_server.controller..*.*(..))")
    public Object logControllerActivity(ProceedingJoinPoint joinPoint) throws Throwable {
        String user = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();

        String signature = joinPoint.getSignature().toLongString();
        Object[] args = joinPoint.getArgs();

        StringBuilder argsBuilder = new StringBuilder();

        for (Object arg: args
        ) {
            argsBuilder.append(arg.toString()).append("\n");
        }

        Object proceeded = joinPoint.proceed();
        System.out.println(proceeded);

        LOGGER.info(String.format(MESSAGE, signature, argsBuilder.toString(), user, proceeded));


        return proceeded;
    }
}

