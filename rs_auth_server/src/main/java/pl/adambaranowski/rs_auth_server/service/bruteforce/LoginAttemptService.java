package pl.adambaranowski.rs_auth_server.service.bruteforce;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Blocks the ip of user which is trying to login to many times
 */
@Service
public class LoginAttemptService {
    private final int MAX_USER_LOGIN_ATTEMPT;
    private final int USER_LOGIN_TIME_WINDOW_MINUTES;
    private static final int ATTEMPT_INCREMENT = 1;
    private LoadingCache<String, Integer> loginAttemptsCache;

    public LoginAttemptService(@Value("${bruteForce.prevention.maxLoginAttempts:5}") int maxAttempts,
                               @Value("${bruteForce.prevention.timeWindowMinutes:10}") int timeWindow
    ) {
        super();
        this.USER_LOGIN_TIME_WINDOW_MINUTES = timeWindow;
        this.MAX_USER_LOGIN_ATTEMPT = maxAttempts;
        loginAttemptsCache = CacheBuilder.newBuilder()
                .expireAfterAccess(USER_LOGIN_TIME_WINDOW_MINUTES, TimeUnit.MINUTES)
                .maximumSize(100)
                .build(new CacheLoader<String, Integer>() {
                    @Override
                    public Integer load(String key) throws Exception {
                        return 0;
                    }
                });
    }

    public void evictUserFromLoginAttempt(String ip){
        loginAttemptsCache.invalidate(ip);
    }

    public void addUserToLoginAttempt(String ip){
        int attempts = 0;
        try {
            attempts = ATTEMPT_INCREMENT + loginAttemptsCache.get(ip);
            loginAttemptsCache.put(ip, attempts);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public boolean isMaxAttemptsExceeded(String ip){
        try {
            return loginAttemptsCache.get(ip) > MAX_USER_LOGIN_ATTEMPT;
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }
}
