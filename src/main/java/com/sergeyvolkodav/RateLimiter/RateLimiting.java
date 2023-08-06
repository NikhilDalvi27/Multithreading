package com.sergeyvolkodav.RateLimiter;

import java.time.Instant;


//todo for imagining, assume a queue of length -> 5
// and assume current sec we are at last element of the queue
// all the past added tokens will be added at the index (0,4)


//Todo IMP
// this is that type of Rate-Limiter which
// delays the processing of request, if that request is Rate Limited
// instead of Dropping the request.

public class RateLimiting {

    private int maxTokens;
    private long lastRequestTime = System.currentTimeMillis();

     private long currentTokens = 0;

    public RateLimiting(int maxTokens) {
        // 1 token per second is a minimum
        this.maxTokens = Math.max(maxTokens, 1);

        //todo comment/uncomment to initialize currentTokens variable to 0 or MaxTokens as per the need.
        this.currentTokens = maxTokens;
    }

    //todo Note, here the return type of getToken() is void,
    // Since the algorithm suggests that, the request will be granted token EVENTUALLY (sometimes with delay)
    // EVEN IF the request is Rate Limited.
    public synchronized void getToken() throws InterruptedException {

        //todo the 2nd part shows the new tokens which were added, since the lastRequest
        currentTokens = currentTokens + (System.currentTimeMillis() - lastRequestTime) / 1000;

        if (currentTokens > maxTokens) {
            currentTokens = maxTokens;
        }

        if (currentTokens == 0) {
            //todo note Thread.sleep() DOESN'T give up the acquired lock,
            // So if the current thread is blocked here,
            // it will also block ALL OTHER threads from acquiring the Lock.
            Thread.sleep(1000);

            //todo here we are NOT incrementing/decrementing the currentTokens
            // bcoz when we sleep for 1 sec, 1 token will be added.
            // and the same added token will be consumed after blocked request's sleep is completed
        } else {
            currentTokens--;
        }

        //todo note, don't forget to update this.
        lastRequestTime = System.currentTimeMillis();

        System.out.println("Granting " + Thread.currentThread().getName() + " token at " + (Instant.now()));

    }
}
