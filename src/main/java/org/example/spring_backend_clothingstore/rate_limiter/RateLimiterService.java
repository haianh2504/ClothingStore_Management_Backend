package org.example.spring_backend_clothingstore.rate_limiter;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.time.Duration;

// chỉ điều phối vô, không quan tâm các request bị xử lý ra sao
// sử dụng thuật toán: Fixed Window Counter
//Key: Định danh của user (Ví dụ: rate_limit:192.168.1.10)
//Value: Số đếm request (Ví dụ: "3", "4", "5")

@Service
@RequiredArgsConstructor
public class RateLimiterService {
    // Object kế thừa từ RedisTemplate<object, object> ( key - value )
    // cấu hình sẵn bộ chuyển đổi StringRedisSerializer để hiển thị String dễ đọc
    // là công cụ do Spring design sẵn để giao tiếp với Redis
    private final StringRedisTemplate stringRedisTemplate; // <String, String>

    // trong 60s chỉ nhận tối đa 5 requests vào servlet container để xử lý...
    private static final int GLOBAL_MAX_REQUESTS = 100;
    private static final Duration WINDOW_TIME_SECONDS = Duration.ofSeconds(60);

    public boolean tryAcquire1stLayer(String clientKey)
    {
        String redisKey = "rate-limit:ip:" + clientKey;

        Long requestCount = stringRedisTemplate
                .opsForValue()
                .increment(redisKey);

        if(requestCount == null || requestCount > GLOBAL_MAX_REQUESTS)
        {
            return false;
        }
        // Bắt đầu thiết lập thời gian hết hạn chỉ cho request đầu tiên
        if(requestCount == 1L)
        {
            stringRedisTemplate.expire(redisKey, WINDOW_TIME_SECONDS);
        }
        return true;
    }
    // method key
    public boolean tryAccquire2ndLayer(String methodkey, String clientKey)
    {
        String redisKey = "rate-limit:method:" + methodkey + ":" + clientKey;
        int maxRequests = switch(methodkey)
        {
            case "GET" -> 15;
            case "POST" -> 5;
            default -> 10;
        };

        Long requestCount = stringRedisTemplate
                .opsForValue()
                .increment(redisKey);
        if(requestCount == null || requestCount > maxRequests)
        {
            return false;
        }
        if(requestCount == 1L)
        {
            stringRedisTemplate.expire(redisKey, WINDOW_TIME_SECONDS);
        }
        return true;
    }

}

// stringRedisTemplate.opsForValue() sẽ trả về ValueOperations<String,String>
// opsForValue() là hành động lấy "bộ dụng cụ"
// Value Operation: đóng vai trò như một "túi đựng bộ công cụ" chứa toàn bộ các câu lệnh chuyên dùng để thao tác với kiểu dữ liệu String trên Redis.
// .set(key, value) (Tương đương lệnh SET của Redis)
//.get(key) (Tương đương lệnh GET của Redis)
//.increment(key) (Tương đương lệnh INCR của Redis)
//.decrement(key) (Tương đương lệnh DECR của Redis)