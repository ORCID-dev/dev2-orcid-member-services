package io.github.jhipster.registry.web.rest;

import static java.util.stream.Collectors.toMap;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.config.ConfigurationManager;
import com.netflix.discovery.shared.Pair;
import com.netflix.eureka.registry.PeerAwareInstanceRegistry;
import com.netflix.eureka.registry.PeerAwareInstanceRegistryImpl;
import com.netflix.eureka.resources.StatusResource;
import com.netflix.eureka.util.StatusInfo;

import io.github.jhipster.registry.service.EurekaService;
import io.github.jhipster.registry.web.rest.vm.EurekaVM;

/**
 * Controller for viewing Eureka data.
 */
@RestController
@RequestMapping("/api")
public class EurekaResource {

    private final Logger log = LoggerFactory.getLogger(EurekaResource.class);
    
    @Autowired
    private EurekaService eurekaService;

    /**
     * GET  /eureka/applications : get Eureka applications information
     */
    @GetMapping("/eureka/applications")
    public ResponseEntity<EurekaVM> eureka() {
        EurekaVM eurekaVM = new EurekaVM();
        eurekaVM.setApplications(eurekaService.getApplications());
        return new ResponseEntity<>(eurekaVM, HttpStatus.OK);
    }

    

    /**
     * GET  /eureka/lastn : get Eureka registrations
     */
    @GetMapping("/eureka/lastn")
    public ResponseEntity<Map<String, Map<Long, String>>> lastn() {
        Map<String, Map<Long, String>> lastn = new HashMap<>();
        PeerAwareInstanceRegistryImpl registry = (PeerAwareInstanceRegistryImpl) eurekaService.getRegistry();
        Map<Long, String> canceledMap = registry.getLastNCanceledInstances()
            .stream().collect(toMap(Pair::first, Pair::second));
        lastn.put("canceled", canceledMap);
        Map<Long, String> registeredMap = registry.getLastNRegisteredInstances()
            .stream().collect(toMap(Pair::first, Pair::second));
        lastn.put("registered", registeredMap);
        return new ResponseEntity<>(lastn, HttpStatus.OK);
    }

    /**
     * GET  /eureka/replicas : get Eureka replicas
     */
    @GetMapping("/eureka/replicas")
    public ResponseEntity<List<String>> replicas() {
        List<String> replicas = new ArrayList<>();
        eurekaService.getServerContext().getPeerEurekaNodes().getPeerNodesView().forEach(
            node -> {
                try {
                    // The URL is parsed in order to remove login/password information
                    URI uri = new URI(node.getServiceUrl());
                    replicas.add(uri.getHost() + ":" + uri.getPort());
                } catch (URISyntaxException e) {
                    log.warn("Could not parse peer Eureka node URL: {}", e.getMessage());
                }
            }
        );

        return new ResponseEntity<>(replicas, HttpStatus.OK);
    }

    /**
     * GET  /eureka/status : get Eureka status
     */
    @GetMapping("/eureka/status")
    public ResponseEntity<EurekaVM> eurekaStatus() {

        EurekaVM eurekaVM = new EurekaVM();
        eurekaVM.setStatus(getEurekaStatus());
        return new ResponseEntity<>(eurekaVM, HttpStatus.OK);
    }

    private Map<String, Object> getEurekaStatus() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("time", new Date());
        stats.put("currentTime", StatusResource.getCurrentTimeAsString());
        stats.put("upTime", StatusInfo.getUpTime());
        stats.put("environment", ConfigurationManager.getDeploymentContext()
            .getDeploymentEnvironment());
        stats.put("datacenter", ConfigurationManager.getDeploymentContext()
            .getDeploymentDatacenter());

        PeerAwareInstanceRegistry registry = eurekaService.getRegistry();

        stats.put("isBelowRenewThreshold", registry.isBelowRenewThresold() == 1);

        populateInstanceInfo(stats);

        return stats;
    }

    private void populateInstanceInfo(Map<String, Object> model) {

        StatusInfo statusInfo;
        try {
            statusInfo = new StatusResource().getStatusInfo();
        } catch (Exception e) {
            log.error(e.getMessage());
            statusInfo = StatusInfo.Builder.newBuilder().isHealthy(false).build();
        }
        if (statusInfo != null && statusInfo.getGeneralStats() != null) {
            model.put("generalStats", statusInfo.getGeneralStats());
        }
        if (statusInfo != null && statusInfo.getInstanceInfo() != null) {
            InstanceInfo instanceInfo = statusInfo.getInstanceInfo();
            Map<String, String> instanceMap = new HashMap<>();
            instanceMap.put("ipAddr", instanceInfo.getIPAddr());
            instanceMap.put("status", instanceInfo.getStatus().toString());
            model.put("instanceInfo", instanceMap);
        }
    }

}
