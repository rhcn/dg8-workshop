package com.redhat.com.rhdg;

import java.lang.invoke.MethodHandles;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.spring.remote.provider.SpringRemoteCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;

@SpringBootApplication
public class ExtRhdgApplication {
		
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
   
	@Autowired
	// private SpringRemoteCacheManager cacheManager;
	private RemoteCacheManager cacheManager;


	@Order(1)
	@Bean
	public CommandLineRunner createCache(ApplicationContext ctx) {
		return args -> {
			cacheManager.administration().getOrCreateCache("sessions", "org.infinispan.REPL_SYNC");
			// cacheManager.administration().getOrCreateCache("sessions", DefaultTemplate.DIST_SYNC.getTemplateName());
			// cacheManager.getNativeCacheManager().administration().getOrCreateCache("sessions", "org.infinispan.REPL_SYNC");
			logger.info(String.format("sessions cache has been created"));
		};
	}
	
    public static void main(String... args) {
        new SpringApplicationBuilder().sources(ExtRhdgApplication.class).run(args);
    }

}
