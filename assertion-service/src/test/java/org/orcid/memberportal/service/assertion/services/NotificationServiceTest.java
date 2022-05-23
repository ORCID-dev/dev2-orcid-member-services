package org.orcid.memberportal.service.assertion.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.xml.bind.JAXBException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.orcid.jaxb.model.v3.release.notification.NotificationType;
import org.orcid.jaxb.model.v3.release.notification.permission.Item;
import org.orcid.jaxb.model.v3.release.notification.permission.ItemType;
import org.orcid.jaxb.model.v3.release.notification.permission.NotificationPermission;
import org.orcid.memberportal.service.assertion.client.OrcidAPIClient;
import org.orcid.memberportal.service.assertion.domain.Assertion;
import org.orcid.memberportal.service.assertion.domain.SendNotificationsRequest;
import org.orcid.memberportal.service.assertion.domain.enumeration.AffiliationSection;
import org.orcid.memberportal.service.assertion.domain.enumeration.AssertionStatus;
import org.orcid.memberportal.service.assertion.repository.AssertionRepository;
import org.orcid.memberportal.service.assertion.repository.SendNotificationsRequestRepository;
import org.orcid.memberportal.service.assertion.web.rest.errors.ORCIDAPIException;
import org.springframework.context.MessageSource;

class NotificationServiceTest {
    
    @Mock
    private AssertionRepository assertionRepository;
    
    @Mock
    private OrcidRecordService orcidRecordService;
    
    @Mock
    private OrcidAPIClient orcidApiClient;
    
    @Mock
    private MessageSource messageSource;
    
    @Captor
    private ArgumentCaptor<Assertion> assertionCaptor;
    
    @Captor
    private ArgumentCaptor<NotificationPermission> notificationPermissionCaptor;
    
    @InjectMocks
    private NotificationService notificationService;
    
    @Mock
    private SendNotificationsRequestRepository sendNotificationsRequestRepository;

    @Captor
    private ArgumentCaptor<SendNotificationsRequest> requestCaptor;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testSendPermissionLinkNotifications() throws IOException, JAXBException {
        Mockito.when(sendNotificationsRequestRepository.findActiveRequests()).thenReturn(getListOfManyRequests());
        
        Mockito.when(assertionRepository.findDistinctEmailsWithNotificationRequested(Mockito.eq("salesforceId1"))).thenReturn(Arrays.asList("email1").iterator());
        Mockito.when(assertionRepository.findDistinctEmailsWithNotificationRequested(Mockito.eq("salesforceId2"))).thenReturn(Arrays.asList("email2").iterator());
        Mockito.when(assertionRepository.findDistinctEmailsWithNotificationRequested(Mockito.eq("salesforceId3"))).thenReturn(Arrays.asList("email3").iterator());
        Mockito.when(assertionRepository.findDistinctEmailsWithNotificationRequested(Mockito.eq("salesforceId4"))).thenReturn(Arrays.asList("email4", "email5").iterator());
        Mockito.when(assertionRepository.findDistinctEmailsWithNotificationRequested(Mockito.eq("salesforceId5"))).thenReturn(Arrays.asList("email5", "email6").iterator());
        
        Mockito.when(assertionRepository.findByEmailAndSalesforceIdAndStatus(Mockito.eq("email1"), Mockito.eq("salesforceId1"), Mockito.eq(AssertionStatus.NOTIFICATION_REQUESTED.name()))).thenReturn(getListOfAssertionsForNotification(1, "email1", "salesforceId1"));
        Mockito.when(assertionRepository.findByEmailAndSalesforceIdAndStatus(Mockito.eq("email2"), Mockito.eq("salesforceId2"), Mockito.eq(AssertionStatus.NOTIFICATION_REQUESTED.name()))).thenReturn(getListOfAssertionsForNotification(3, "email2", "salesforceId2"));
        Mockito.when(assertionRepository.findByEmailAndSalesforceIdAndStatus(Mockito.eq("email3"), Mockito.eq("salesforceId3"), Mockito.eq(AssertionStatus.NOTIFICATION_REQUESTED.name()))).thenReturn(getListOfAssertionsForNotification(5, "email3", "salesforceId3"));
        Mockito.when(assertionRepository.findByEmailAndSalesforceIdAndStatus(Mockito.eq("email4"), Mockito.eq("salesforceId4"), Mockito.eq(AssertionStatus.NOTIFICATION_REQUESTED.name()))).thenReturn(getListOfAssertionsForNotification(7, "email4", "salesforceId4"));
        Mockito.when(assertionRepository.findByEmailAndSalesforceIdAndStatus(Mockito.eq("email5"), Mockito.eq("salesforceId4"), Mockito.eq(AssertionStatus.NOTIFICATION_REQUESTED.name()))).thenReturn(getListOfAssertionsForNotification(2, "email5", "salesforceId4"));
        Mockito.when(assertionRepository.findByEmailAndSalesforceIdAndStatus(Mockito.eq("email5"), Mockito.eq("salesforceId5"), Mockito.eq(AssertionStatus.NOTIFICATION_REQUESTED.name()))).thenReturn(getListOfAssertionsForNotification(9, "email5", "salesforceId5"));
        Mockito.when(assertionRepository.findByEmailAndSalesforceIdAndStatus(Mockito.eq("email6"), Mockito.eq("salesforceId5"), Mockito.eq(AssertionStatus.NOTIFICATION_REQUESTED.name()))).thenReturn(getListOfAssertionsForNotification(4, "email6", "salesforceId5"));
        
        Mockito.when(orcidRecordService.generateLinkForEmailAndSalesforceId(Mockito.eq("email1"), Mockito.eq("salesforceId1"))).thenReturn("link1");
        Mockito.when(orcidRecordService.generateLinkForEmailAndSalesforceId(Mockito.eq("email2"), Mockito.eq("salesforceId2"))).thenReturn("link2");
        Mockito.when(orcidRecordService.generateLinkForEmailAndSalesforceId(Mockito.eq("email3"), Mockito.eq("salesforceId3"))).thenReturn("link3");
        Mockito.when(orcidRecordService.generateLinkForEmailAndSalesforceId(Mockito.eq("email4"), Mockito.eq("salesforceId4"))).thenReturn("link4");
        Mockito.when(orcidRecordService.generateLinkForEmailAndSalesforceId(Mockito.eq("email5"), Mockito.eq("salesforceId4"))).thenReturn("link4a");
        Mockito.when(orcidRecordService.generateLinkForEmailAndSalesforceId(Mockito.eq("email5"), Mockito.eq("salesforceId5"))).thenReturn("link5");
        Mockito.when(orcidRecordService.generateLinkForEmailAndSalesforceId(Mockito.eq("email6"), Mockito.eq("salesforceId5"))).thenReturn("link6");
        
        Mockito.when(messageSource.getMessage(Mockito.eq("assertion.notifications.intro"), Mockito.isNull(), Mockito.any(Locale.class))).thenReturn("intro");
        Mockito.when(messageSource.getMessage(Mockito.eq("assertion.notifications.subject"), Mockito.isNotNull(), Mockito.any(Locale.class))).thenReturn("subject");
        
        Mockito.when(orcidApiClient.getOrcidIdForEmail(Mockito.eq("email1"))).thenReturn("orcid1");
        Mockito.when(orcidApiClient.getOrcidIdForEmail(Mockito.eq("email2"))).thenReturn("orcid2");
        Mockito.when(orcidApiClient.getOrcidIdForEmail(Mockito.eq("email3"))).thenReturn("orcid3");
        Mockito.when(orcidApiClient.getOrcidIdForEmail(Mockito.eq("email4"))).thenReturn("orcid4");
        Mockito.when(orcidApiClient.getOrcidIdForEmail(Mockito.eq("email5"))).thenReturn(null);
        Mockito.when(orcidApiClient.getOrcidIdForEmail(Mockito.eq("email6"))).thenReturn("orcid6");
        
        notificationService.sendPermissionLinkNotifications();
        
        Mockito.verify(sendNotificationsRequestRepository).findActiveRequests();
        Mockito.verify(assertionRepository).findByEmailAndSalesforceIdAndStatus(Mockito.eq("email1"), Mockito.eq("salesforceId1"), Mockito.eq(AssertionStatus.NOTIFICATION_REQUESTED.name()));
        Mockito.verify(assertionRepository).findByEmailAndSalesforceIdAndStatus(Mockito.eq("email2"), Mockito.eq("salesforceId2"), Mockito.eq(AssertionStatus.NOTIFICATION_REQUESTED.name()));
        Mockito.verify(assertionRepository).findByEmailAndSalesforceIdAndStatus(Mockito.eq("email3"), Mockito.eq("salesforceId3"), Mockito.eq(AssertionStatus.NOTIFICATION_REQUESTED.name()));
        Mockito.verify(assertionRepository).findByEmailAndSalesforceIdAndStatus(Mockito.eq("email4"), Mockito.eq("salesforceId4"), Mockito.eq(AssertionStatus.NOTIFICATION_REQUESTED.name()));
        Mockito.verify(assertionRepository).findByEmailAndSalesforceIdAndStatus(Mockito.eq("email5"), Mockito.eq("salesforceId4"), Mockito.eq(AssertionStatus.NOTIFICATION_REQUESTED.name()));
        Mockito.verify(assertionRepository).findByEmailAndSalesforceIdAndStatus(Mockito.eq("email5"), Mockito.eq("salesforceId5"), Mockito.eq(AssertionStatus.NOTIFICATION_REQUESTED.name()));
        Mockito.verify(assertionRepository).findByEmailAndSalesforceIdAndStatus(Mockito.eq("email6"), Mockito.eq("salesforceId5"), Mockito.eq(AssertionStatus.NOTIFICATION_REQUESTED.name()));
        
        Mockito.verify(orcidApiClient).getOrcidIdForEmail(Mockito.eq("email1"));
        Mockito.verify(orcidApiClient).getOrcidIdForEmail(Mockito.eq("email2"));
        Mockito.verify(orcidApiClient).getOrcidIdForEmail(Mockito.eq("email3"));
        Mockito.verify(orcidApiClient).getOrcidIdForEmail(Mockito.eq("email4"));
        Mockito.verify(orcidApiClient, Mockito.times(2)).getOrcidIdForEmail(Mockito.eq("email5"));
        
        Mockito.verify(messageSource, Mockito.times(5)).getMessage(Mockito.eq("assertion.notifications.intro"), Mockito.isNull(), Mockito.any(Locale.class));
        Mockito.verify(messageSource, Mockito.times(5)).getMessage(Mockito.eq("assertion.notifications.subject"), Mockito.isNotNull(), Mockito.any(Locale.class));
        
        Mockito.verify(orcidRecordService).generateLinkForEmailAndSalesforceId(Mockito.eq("email1"), Mockito.eq("salesforceId1"));
        Mockito.verify(orcidRecordService).generateLinkForEmailAndSalesforceId(Mockito.eq("email2"), Mockito.eq("salesforceId2"));
        Mockito.verify(orcidRecordService).generateLinkForEmailAndSalesforceId(Mockito.eq("email3"), Mockito.eq("salesforceId3"));
        Mockito.verify(orcidRecordService).generateLinkForEmailAndSalesforceId(Mockito.eq("email4"), Mockito.eq("salesforceId4"));
        Mockito.verify(orcidRecordService, Mockito.never()).generateLinkForEmailAndSalesforceId(Mockito.eq("email5"), Mockito.eq("salesforceId4"));
        Mockito.verify(orcidRecordService, Mockito.never()).generateLinkForEmailAndSalesforceId(Mockito.eq("email5"), Mockito.eq("salesforceId5"));
        Mockito.verify(orcidRecordService).generateLinkForEmailAndSalesforceId(Mockito.eq("email6"), Mockito.eq("salesforceId5"));
        
        Mockito.verify(assertionRepository, Mockito.times(31)).save(assertionCaptor.capture()); // 31 total assertions updated (1 + 3 + 5 + 7 + 2 + 9 + 4)
        List<Assertion> assertionsUpdated = assertionCaptor.getAllValues();
        assertionsUpdated.forEach(a -> {
            if (a.getEmail().equals("email5")) {
                // no orcid id was available for email5
                assertThat(a.getStatus()).isEqualTo(AssertionStatus.PENDING.name());
            } else {
                assertThat(a.getStatus()).isEqualTo(AssertionStatus.NOTIFICATION_SENT.name());
                assertThat(a.getNotificationSent()).isNotNull();
            }
        });
        
        Mockito.verify(orcidApiClient).postNotification(notificationPermissionCaptor.capture(), Mockito.eq("orcid1"));
        NotificationPermission notificationPermission = notificationPermissionCaptor.getValue();
        checkNotificationPermissionObject(notificationPermission, "1", 1);
        
        Mockito.verify(orcidApiClient).postNotification(notificationPermissionCaptor.capture(), Mockito.eq("orcid2"));
        notificationPermission = notificationPermissionCaptor.getValue();
        checkNotificationPermissionObject(notificationPermission, "2", 3);
        
        Mockito.verify(orcidApiClient).postNotification(notificationPermissionCaptor.capture(), Mockito.eq("orcid3"));
        notificationPermission = notificationPermissionCaptor.getValue();
        checkNotificationPermissionObject(notificationPermission, "3", 5);
        
        Mockito.verify(orcidApiClient).postNotification(notificationPermissionCaptor.capture(), Mockito.eq("orcid4"));
        notificationPermission = notificationPermissionCaptor.getValue();
        checkNotificationPermissionObject(notificationPermission, "4", 7);
        
        Mockito.verify(orcidApiClient, Mockito.never()).postNotification(notificationPermissionCaptor.capture(), Mockito.eq("orcid5"));
        
        Mockito.verify(orcidApiClient).postNotification(notificationPermissionCaptor.capture(), Mockito.eq("orcid6"));
        notificationPermission = notificationPermissionCaptor.getValue();
        checkNotificationPermissionObject(notificationPermission, "6", 4);
    }
    
    @Test
    void testSendPermissionLinkNotifications_apiError() throws IOException, JAXBException {
        Mockito.when(sendNotificationsRequestRepository.findActiveRequests()).thenReturn(getListOfOneRequest("salesforceId1"));
        Mockito.when(assertionRepository.findDistinctEmailsWithNotificationRequested(Mockito.eq("salesforceId1"))).thenReturn(Arrays.asList("email1").iterator());
        Mockito.when(assertionRepository.findByEmailAndSalesforceIdAndStatus(Mockito.eq("email1"), Mockito.eq("salesforceId1"), Mockito.eq(AssertionStatus.NOTIFICATION_REQUESTED.name()))).thenReturn(getListOfAssertionsForNotification(1, "email1", "salesforceId1"));
        Mockito.when(orcidRecordService.generateLinkForEmailAndSalesforceId(Mockito.eq("email1"), Mockito.eq("salesforceId1"))).thenReturn("link1");
        
        Mockito.when(messageSource.getMessage(Mockito.eq("assertion.notifications.intro"), Mockito.isNull(), Mockito.any(Locale.class))).thenReturn("intro");
        Mockito.when(messageSource.getMessage(Mockito.eq("assertion.notifications.subject"), Mockito.isNotNull(), Mockito.any(Locale.class))).thenReturn("subject");
        
        Mockito.when(orcidApiClient.getOrcidIdForEmail(Mockito.eq("email1"))).thenReturn("orcid1");
        Mockito.when(orcidApiClient.postNotification(Mockito.any(NotificationPermission.class), Mockito.eq("orcid1"))).thenThrow(new ORCIDAPIException(400, "bad request"));
        
        notificationService.sendPermissionLinkNotifications();
        
        Mockito.verify(sendNotificationsRequestRepository).findActiveRequests();
        Mockito.verify(assertionRepository).findByEmailAndSalesforceIdAndStatus(Mockito.eq("email1"), Mockito.eq("salesforceId1"), Mockito.eq(AssertionStatus.NOTIFICATION_REQUESTED.name()));
        Mockito.verify(orcidApiClient).getOrcidIdForEmail(Mockito.eq("email1"));
        
        Mockito.verify(messageSource).getMessage(Mockito.eq("assertion.notifications.intro"), Mockito.isNull(), Mockito.any(Locale.class));
        Mockito.verify(messageSource).getMessage(Mockito.eq("assertion.notifications.subject"), Mockito.isNotNull(), Mockito.any(Locale.class));
        
        Mockito.verify(orcidRecordService).generateLinkForEmailAndSalesforceId(Mockito.eq("email1"), Mockito.eq("salesforceId1"));
        
        Mockito.verify(assertionRepository).save(assertionCaptor.capture()); 
        Assertion a = assertionCaptor.getValue();
        assertThat(a.getStatus()).isEqualTo(AssertionStatus.NOTIFICATION_FAILED.name());
    }
    
    @Test
    void testCreateSendNotificationsRequest() {
        Mockito.when(sendNotificationsRequestRepository.findActiveRequestBySalesforceId(Mockito.eq("somethingElse"))).thenReturn(new ArrayList<>());
        Mockito.when(sendNotificationsRequestRepository.insert(Mockito.any(SendNotificationsRequest.class))).thenReturn(null);
        notificationService.createSendNotificationsRequest("email", "salesforceId");
        Mockito.verify(sendNotificationsRequestRepository).insert(requestCaptor.capture());

        SendNotificationsRequest captured = requestCaptor.getValue();
        assertThat(captured.getDateRequested()).isNotNull();
        assertThat(captured.getEmail()).isEqualTo("email");
        assertThat(captured.getSalesforceId()).isEqualTo("salesforceId");
    }
    
    @Test
    void testCreateSendNotificationsRequest_requestInProgress() {
        Mockito.when(sendNotificationsRequestRepository.findActiveRequestBySalesforceId(Mockito.eq("salesforceId"))).thenReturn(getListOfOneRequest("salesforceId"));
        Assertions.assertThrows(RuntimeException.class, () -> {
            notificationService.createSendNotificationsRequest("email", "salesforceId");
        });
    }

    @Test
    void testRequestInProgress() {
        Mockito.when(sendNotificationsRequestRepository.findActiveRequestBySalesforceId(Mockito.eq("salesforceId"))).thenReturn(getListOfOneRequest("salesforceId"));
        Mockito.when(sendNotificationsRequestRepository.findActiveRequestBySalesforceId(Mockito.eq("somethingElse"))).thenReturn(new ArrayList<>());
        
        boolean inProgress = notificationService.requestInProgress("salesforceId");
        assertThat(inProgress).isEqualTo(true);
        
        inProgress = notificationService.requestInProgress("somethingElse");
        assertThat(inProgress).isEqualTo(false);
    }
    
    private List<SendNotificationsRequest> getListOfManyRequests() {
        return Arrays.asList(getRequest("salesforceId1"), getRequest("salesforceId2"), getRequest("salesforceId3"), getRequest("salesforceId4"), getRequest("salesforceId5"));
    }

    private List<SendNotificationsRequest> getListOfOneRequest(String salesforceId) {
        return Arrays.asList(getRequest(salesforceId));
    }
    
    private SendNotificationsRequest getRequest(String salesforceId) {
        SendNotificationsRequest request = new SendNotificationsRequest();
        request.setEmail("email");
        request.setSalesforceId(salesforceId);
        request.setDateRequested(Instant.now());
        return request;
    }
    
    private void checkNotificationPermissionObject(NotificationPermission notificationPermission, String variant, int expectedNumberOfItems) {
        assertThat(notificationPermission.getNotificationSubject()).isEqualTo("subject");
        assertThat(notificationPermission.getNotificationIntro()).isEqualTo("intro");
        assertThat(notificationPermission.getNotificationType()).isEqualTo(NotificationType.PERMISSION);
        assertThat(notificationPermission.getAuthorizationUrl().getUri()).isEqualTo("link" + variant);
        assertThat(notificationPermission.getItems().getItems().size()).isEqualTo(expectedNumberOfItems);
        for (int i = 0; i < expectedNumberOfItems; i++) {
            Item item = notificationPermission.getItems().getItems().get(i);
            assertThat(item.getItemName()).isEqualTo("org name " + i + " : " + "role " + i);
            assertThat(item.getItemType()).isEqualTo(ItemType.EDUCATION);
        }
    }

    private List<Assertion> getListOfAssertionsForNotification(int size, String email, String salesforceId) {
        List<Assertion> assertions = new ArrayList<>();
        for (int x = 0; x < size; x++) {
            Assertion a = new Assertion();
            a.setSalesforceId(salesforceId);
            a.setEmail(email);
            a.setStatus(AssertionStatus.NOTIFICATION_REQUESTED.name());
            a.setRoleTitle("role " + x);
            a.setOrgName("org name " + x);
            a.setAffiliationSection(AffiliationSection.EDUCATION);
            assertions.add(a);
        }
        return assertions;
    }

}