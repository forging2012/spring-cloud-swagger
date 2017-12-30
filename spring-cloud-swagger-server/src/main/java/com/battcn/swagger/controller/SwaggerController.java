package com.battcn.swagger.controller;

import com.battcn.swagger.model.CloudSwaggerResource;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.swagger.web.SwaggerResource;

import java.util.List;

/**
 * @author Levin
 * @create 2017/12/30 0030
 */
@RestController
public class SwaggerController {

    private final RestTemplate restTemplate;
    private final DiscoveryClient discoveryClient;

    @Autowired
    public SwaggerController(DiscoveryClient discoveryClient, RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.discoveryClient = discoveryClient;
    }

    @GetMapping(value = "/swagger-resources", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<CloudSwaggerResource> test() {
        List<CloudSwaggerResource> list = Lists.newArrayList();
        List<String> services = discoveryClient.getServices();
        if (CollectionUtils.isEmpty(services)) {
            return null;
        }
        for (String serviceId : services) {
            CloudSwaggerResource resource = new CloudSwaggerResource();
            List<ServiceInstance> instances = discoveryClient.getInstances(serviceId);
            List<SwaggerResource> swaggerResources = this.restTemplate.getForObject("http://" + serviceId + "/swagger-resources", List.class);
            resource.setServiceInstances(instances);
            resource.setSwaggerResources(swaggerResources);
            list.add(resource);
        }
        return list;
    }


}
