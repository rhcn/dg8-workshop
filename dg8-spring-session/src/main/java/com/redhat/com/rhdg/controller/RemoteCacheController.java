package com.redhat.com.rhdg.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.Configuration;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.commons.util.CloseableIteratorSet;
import org.infinispan.spring.remote.provider.SpringRemoteCacheManager;
import org.infinispan.spring.remote.session.configuration.EnableInfinispanRemoteHttpSession;
import org.infinispan.spring.starter.remote.InfinispanRemoteConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.session.MapSession;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableCaching
@EnableInfinispanRemoteHttpSession
public class RemoteCacheController {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass()); 
    
    private final SpringRemoteCacheManager cacheManager;
    
    // @Autowired
    // private RemoteCacheManager cacheManager;


    @Autowired
    public RemoteCacheController(SpringRemoteCacheManager cacheManager) {
        
        logger.info("====RemoteCacheController Constructor==");
        this.cacheManager = cacheManager;
        cacheManager.getNativeCacheManager().administration().getOrCreateCache("sessions", "org.infinispan.REPL_SYNC");
    }

    @GetMapping(value = "/get-data", produces = MediaType.TEXT_PLAIN_VALUE)
    public String getData() {
        // Using RemoteCacheManager
        // cacheManager = cacheManager.administration().getOrCreateCache("sessions", "org.infinispan.REPL_SYNC");
        // RemoteCache<String, String> cache = cacheManager.getCache("sessions");
        // return "Saved data: " + cache.get("latest");

        // Using SpringRemoteCacheManager
        Set entries = cacheManager.getCache("sessions").getNativeCache().keySet();
        // Set entries = cacheManager.getCache("sessions").getNativeCache().keySet();
        Cache.ValueWrapper data = null;
        // for (Object entry : entries) {
        for (Object entry : entries) {
            data = cacheManager.getCache("sessions").get(entry);
        }
        return "Saved data: " + data.toString();
    }

    @GetMapping("/greeting")
    public String greeting(@RequestParam(name = "name", required = false, defaultValue = "World") String name,
                           Model model, HttpSession session) {
        model.addAttribute("name", name);
        model.addAttribute("latest", session.getAttribute("latest"));
        session.setAttribute("latest", name);
        return "greeting";
    }

}