package com.yuedongyun.api.config;

import com.yuedongyun.api.client.workout.fallback.WorkoutClientFallback;
import com.yuedongyun.api.client.campaign.fallback.CampaignClientFallback;
import com.yuedongyun.api.client.remark.fallback.RemarkClientFallback;
import com.yuedongyun.api.client.trade.fallback.TradeClientFallback;
import com.yuedongyun.api.client.user.fallback.UserClientFallback;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FallbackConfig {
    @Bean
    public WorkoutClientFallback workoutClientFallback(){
        return new WorkoutClientFallback();
    }

    @Bean
    public TradeClientFallback tradeClientFallback(){
        return new TradeClientFallback();
    }

    @Bean
    public UserClientFallback userClientFallback(){
        return new UserClientFallback();
    }

    @Bean
    public RemarkClientFallback remarkClientFallback(){
        return new RemarkClientFallback();
    }

    @Bean
    public CampaignClientFallback campaignClientFallback(){
        return new CampaignClientFallback();
    }
}

