package in.ac.amu.zhcet.service.security.ratelimit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RateLimitService {

    private static final int EXPIRE_TIME = 30;
    private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;

    private final Cache<String, Integer> attemptsCache;

    public RateLimitService() {
        attemptsCache = Caffeine
                .newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(EXPIRE_TIME, TIME_UNIT)
                .build();
    }

    public int incrementAttempts(String key) {
        int attempts = getAttempts(key);
        attemptsCache.put(key, ++attempts);

        return attempts;
    }

    private int getAttempts(String key) {
        Integer attempts = attemptsCache.getIfPresent(key);
        return attempts != null ? attempts : 0;
    }
}

