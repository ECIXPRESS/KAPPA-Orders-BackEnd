package edu.dosw.KAPPA_Orders_BackEnd.Config;

import edu.dosw.KAPPA_Orders_BackEnd.Application.ports.OrderRepositoryPort;
import edu.dosw.KAPPA_Orders_BackEnd.Infrastructure.Persistence.OrdersMongoRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class Config {

    @Bean
    public OrderRepositoryPort orderRepositoryPort(MongoTemplate mongoTemplate) {
        return new OrdersMongoRepository(mongoTemplate);
    }
}