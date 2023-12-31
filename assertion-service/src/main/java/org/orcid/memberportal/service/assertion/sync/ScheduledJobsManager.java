package org.orcid.memberportal.service.assertion.sync;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.orcid.memberportal.service.assertion.services.AssertionService;
import org.orcid.memberportal.service.assertion.services.CsvReportService;
import org.orcid.memberportal.service.assertion.services.NotificationService;
import org.orcid.memberportal.service.assertion.services.StoredFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

@Component
@EnableScheduling
public class ScheduledJobsManager {

    private static final Logger LOG = LoggerFactory.getLogger(ScheduledJobsManager.class);

    @Autowired
    private AssertionService assertionsService;
    
    @Autowired
    private StoredFileService storedFileService;
    
    @Autowired
    private CsvReportService csvReportService;
    
    @Autowired
    private NotificationService notificationService;

    @Scheduled(initialDelay = 90000, fixedDelayString = "${application.syncAffiliationsDelay}")
    @SchedulerLock(name = "syncAffiliations", lockAtMostFor = "20m", lockAtLeastFor = "2m")
    public void syncAffiliations() throws JAXBException {
        LOG.info("Running cron to sync assertions with registry");
        assertionsService.postAssertionsToOrcid();
        assertionsService.putAssertionsInOrcid();
        LOG.info("Sync complete");
    }
    
    @Scheduled(cron = "${application.generateMemberAssertionStatsCron}")
    @SchedulerLock(name = "generateMemberAssertionStats", lockAtMostFor = "60m", lockAtLeastFor = "10m")
    public void generateMemberAssertionStats() throws IOException  {
        LOG.info("Running cron to generate member assertion stats");
        assertionsService.generateAndSendMemberAssertionStats();
        LOG.info("Stats generation complete");
    }
    
    @Scheduled(initialDelay = 90000, fixedDelayString = "${application.processAssertionUploadsDelay}")
    @SchedulerLock(name = "processAssertionUploads", lockAtMostFor = "60m", lockAtLeastFor = "2m")
    public void processAssertionUploads() throws IOException  {
        LOG.info("Running cron to process assertion uploads");
        assertionsService.processAssertionUploads();
        LOG.info("Assertion uploads processed");
    }
    
    @Scheduled(initialDelay = 90000, fixedDelayString = "${application.removeStoredFilesDelay}")
    @SchedulerLock(name = "removeStoredFiles", lockAtMostFor = "60m", lockAtLeastFor = "2m")
    public void removeStoredFiles() throws IOException  {
        LOG.info("Running cron to remove old files");
        storedFileService.removeStoredFiles();
        LOG.info("Old files removed");
    }
    
    @Scheduled(initialDelay = 90000, fixedDelayString = "${application.processCsvReportsDelay}")
    @SchedulerLock(name = "processCsvReports", lockAtMostFor = "60m", lockAtLeastFor = "2m")
    public void sendCSVReports() throws IOException  {
        LOG.info("Running cron to process CSV reports");
        csvReportService.processCsvReports();
        LOG.info("CSV reports processed");
    }
    
    @Scheduled(initialDelay = 90000, fixedDelayString = "${application.sendPermissionLinkNotificationsDelay}")
    @SchedulerLock(name = "sendPermissionLinkNotifications", lockAtMostFor = "60m", lockAtLeastFor = "2m")
    public void sendPermissionLinkNotifications() throws IOException  {
        LOG.info("Running cron to send permission link notifications");
        notificationService.sendPermissionLinkNotifications();
        LOG.info("Permission link notifications sent");
    }
    
    @Scheduled(cron = "${application.resendNotificationsCron}")
    @SchedulerLock(name = "resendNotifications", lockAtMostFor = "60m", lockAtLeastFor = "10m")
    public void resendNotifications() throws IOException  {
        LOG.info("Running cron to resend notifications");
        notificationService.resendNotifications();
        LOG.info("Notifications resent");
    }
    
    
}
