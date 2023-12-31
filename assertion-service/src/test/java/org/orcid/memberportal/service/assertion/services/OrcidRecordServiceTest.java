package org.orcid.memberportal.service.assertion.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.codehaus.jettison.json.JSONException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.orcid.memberportal.service.assertion.client.OrcidAPIClient;
import org.orcid.memberportal.service.assertion.domain.AssertionServiceUser;
import org.orcid.memberportal.service.assertion.domain.OrcidRecord;
import org.orcid.memberportal.service.assertion.domain.OrcidToken;
import org.orcid.memberportal.service.assertion.repository.OrcidRecordRepository;
import org.orcid.memberportal.service.assertion.web.rest.errors.BadRequestAlertException;

class OrcidRecordServiceTest {

    private static final String DEFAULT_JHI_USER_ID = "user-id";

    private static final String DEFAULT_LOGIN = "user@orcid.org";

    private static final String DEFAULT_SALESFORCE_ID = "salesforce-id";

    private static final String OTHER_SALESFORCE_ID = "other-salesforce-id";

    private static final String EMAIL_ONE = "emailone@record.com";

    @InjectMocks
    private OrcidRecordService orcidRecordService;

    @Mock
    private OrcidRecordRepository orcidRecordRepository;

    @Mock
    private OrcidAPIClient orcidAPIClient;

    @Mock
    private UserService assertionsUserService;
    
    @Captor
    private ArgumentCaptor<OrcidRecord> recordCaptor;

    @BeforeEach
    public void setUp() throws JSONException {
        MockitoAnnotations.initMocks(this);
        when(assertionsUserService.getLoggedInUser()).thenReturn(getUser());
        when(assertionsUserService.getLoggedInUserId()).thenReturn(getUser().getId());
    }

    private AssertionServiceUser getUser() {
        AssertionServiceUser user = new AssertionServiceUser();
        user.setId(DEFAULT_JHI_USER_ID);
        user.setEmail(DEFAULT_LOGIN);
        user.setSalesforceId(DEFAULT_SALESFORCE_ID);
        return user;
    }

    @Test
    void testCreateOrcidRecord() {
        Mockito.when(orcidRecordRepository.findOneByEmail(Mockito.anyString())).thenReturn(Optional.empty());
        Mockito.when(orcidRecordRepository.save(Mockito.any(OrcidRecord.class))).thenAnswer(new Answer<OrcidRecord>() {
            @Override
            public OrcidRecord answer(InvocationOnMock invocation) throws Throwable {
                return (OrcidRecord) invocation.getArgument(0);
            }
        });
        Mockito.when(orcidRecordRepository.insert(Mockito.any(OrcidRecord.class))).thenAnswer(new Answer<OrcidRecord>() {
            @Override
            public OrcidRecord answer(InvocationOnMock invocation) throws Throwable {
                return (OrcidRecord) invocation.getArgument(0);
            }
        });

        OrcidRecord created = orcidRecordService.createOrcidRecord(EMAIL_ONE, Instant.now(), DEFAULT_SALESFORCE_ID);
        assertNotNull(created.getCreated());
        assertNotNull(created.getModified());
        assertEquals(EMAIL_ONE, created.getEmail());

        List<OrcidToken> tokens = created.getTokens();
        OrcidToken token = tokens.get(0);

        assertEquals(DEFAULT_SALESFORCE_ID, token.getSalesforceId());
        assertEquals(null, token.getTokenId());

    }

    @Test
    void testCreateOrcidRecordWhenEmailExists() {
        Mockito.when(orcidRecordRepository.findOneByEmail(Mockito.anyString())).thenReturn(Optional.of(getOrcidRecordWithIdToken(EMAIL_ONE)));
        Assertions.assertThrows(BadRequestAlertException.class, () -> {
            orcidRecordService.createOrcidRecord(EMAIL_ONE, Instant.now(), DEFAULT_SALESFORCE_ID);
        });
    }

    @Test
    void testUpdateOrcidRecord() {
        Mockito.when(orcidRecordRepository.findOneByEmail(Mockito.anyString())).thenReturn(Optional.of(getOrcidRecordWithIdToken(EMAIL_ONE)));
        Mockito.when(orcidRecordRepository.save(Mockito.any(OrcidRecord.class))).thenAnswer(new Answer<OrcidRecord>() {
            @Override
            public OrcidRecord answer(InvocationOnMock invocation) throws Throwable {
                return (OrcidRecord) invocation.getArgument(0);
            }
        });

        OrcidRecord recordOne = getOrcidRecordWithIdToken(EMAIL_ONE);
        recordOne.setId("xyz");
        List<OrcidToken> tokens = recordOne.getTokens();
        OrcidToken token = tokens.get(0);
        OrcidToken token2 = new OrcidToken(OTHER_SALESFORCE_ID, "tokenid2");
        tokens.add(token2);
        recordOne.setTokens(tokens);
        recordOne.setModified(Instant.now());

        OrcidRecord updated = orcidRecordService.updateOrcidRecord(recordOne);
        assertNotNull(updated.getCreated());
        assertNotNull(updated.getModified());

        tokens = updated.getTokens();
        token = tokens.get(1);
        assertEquals(OTHER_SALESFORCE_ID, token.getSalesforceId());
        assertEquals("tokenid2", token.getTokenId());
    }
    
    @Test
    void testRevokeIdToken() {
        OrcidRecord recordWithMoreThanOneToken = getOrcidRecordWithIdToken("email");
        recordWithMoreThanOneToken.getTokens().add(new OrcidToken("some other org", "not important"));
        Mockito.when(orcidRecordRepository.findOneByEmail(Mockito.eq("email"))).thenReturn(Optional.of(recordWithMoreThanOneToken));
        
        orcidRecordService.revokeIdToken("email", DEFAULT_SALESFORCE_ID);
        
        Mockito.verify(orcidRecordRepository).save(recordCaptor.capture());
        OrcidRecord saved = recordCaptor.getValue();
        assertEquals(2, saved.getTokens().size());
        assertEquals(DEFAULT_SALESFORCE_ID, saved.getTokens().get(0).getSalesforceId());
        assertNotNull(saved.getTokens().get(0).getRevokedDate());
    }
    
    @Test
    void testDeleteOrcidRecordTokenByEmailAndSalesforceIdWhereNoOtherTokens() {
        OrcidRecord record = getOrcidRecordWithIdToken("email");
        Mockito.when(orcidRecordRepository.findOneByEmail(Mockito.eq("email"))).thenReturn(Optional.of(record));
        orcidRecordService.deleteOrcidRecordTokenByEmailAndSalesforceId("email", DEFAULT_SALESFORCE_ID);
        Mockito.verify(orcidRecordRepository).save(recordCaptor.capture()); // for removing tokens
        Mockito.verify(orcidRecordRepository).delete(Mockito.any(OrcidRecord.class)); // for removing record
        
        OrcidRecord captured = recordCaptor.getValue();
        assertEquals(0, captured.getTokens().size());
    }
    
    @Test
    void testDeleteOrcidRecordTokenByEmailAndSalesforceIdWhereOtherTokensExist() {
        OrcidRecord record = getOrcidRecordWithIdToken("email");
        record.getTokens().add(new OrcidToken("something else", "some token id"));
        Mockito.when(orcidRecordRepository.findOneByEmail(Mockito.eq("email"))).thenReturn(Optional.of(record));
        orcidRecordService.deleteOrcidRecordTokenByEmailAndSalesforceId("email", DEFAULT_SALESFORCE_ID);
        Mockito.verify(orcidRecordRepository).save(recordCaptor.capture()); // for removing tokens
        Mockito.verify(orcidRecordRepository, Mockito.never()).delete(Mockito.any(OrcidRecord.class)); // for removing record
        
        OrcidRecord captured = recordCaptor.getValue();
        assertEquals(1, captured.getTokens().size());
    }
    
    @Test
    void testStoreIdToken() {
        OrcidRecord record = getOrcidRecordWithIdToken("email");
        Mockito.when(orcidRecordRepository.findOneByEmail(Mockito.eq("email"))).thenReturn(Optional.of(record));
        
        orcidRecordService.storeIdToken("email", "id-token", "orcid", DEFAULT_SALESFORCE_ID);
        
        Mockito.verify(orcidRecordRepository).save(recordCaptor.capture());
        OrcidRecord captured = recordCaptor.getValue();
        assertEquals(1, captured.getTokens().size());
    }
    
    @Test
    void testStoreIdTokenWherePreviousTokenRevoked() {
        OrcidRecord record = getOrcidRecordWithRevokedToken("email");
        
        Mockito.when(orcidRecordRepository.findOneByEmail(Mockito.eq("email"))).thenReturn(Optional.of(record));
        
        orcidRecordService.storeIdToken("email", "new token", "orcid", DEFAULT_SALESFORCE_ID);
        
        Mockito.verify(orcidRecordRepository).save(recordCaptor.capture());
        OrcidRecord captured = recordCaptor.getValue();
        assertEquals(1, captured.getTokens().size());
        assertEquals("new token", captured.getTokens().get(0).getTokenId());
    }
    
    @Test
    void testStoreIdTokenWhereMultipleLegacyTokensReplaced() {
        OrcidRecord record = getOrcidRecordWithRevokedToken("email");
        record.getTokens().add(new OrcidToken(DEFAULT_SALESFORCE_ID, "erm"));
        record.getTokens().add(new OrcidToken(DEFAULT_SALESFORCE_ID, "errr"));
        record.getTokens().add(new OrcidToken(DEFAULT_SALESFORCE_ID, "hmmm"));
        
        Mockito.when(orcidRecordRepository.findOneByEmail(Mockito.eq("email"))).thenReturn(Optional.of(record));
        
        orcidRecordService.storeIdToken("email", "new token", "orcid", DEFAULT_SALESFORCE_ID);
        
        Mockito.verify(orcidRecordRepository).save(recordCaptor.capture());
        OrcidRecord captured = recordCaptor.getValue();
        assertEquals(1, captured.getTokens().size());
        assertEquals("new token", captured.getTokens().get(0).getTokenId());
    }
    
    @Test
    void testUserHasGrantedOrDeniedPermission() {
        OrcidRecord responded = getOrcidRecordWithToken("responded", new OrcidToken(DEFAULT_SALESFORCE_ID, "token"));
        Mockito.when(orcidRecordRepository.findOneByEmail(Mockito.eq("responded"))).thenReturn(Optional.of(responded));
        
        OrcidRecord notResponded = getOrcidRecordWithToken("not responded", new OrcidToken(DEFAULT_SALESFORCE_ID, null));
        Mockito.when(orcidRecordRepository.findOneByEmail(Mockito.eq("not responded"))).thenReturn(Optional.of(notResponded));
        
        assertTrue(orcidRecordService.userHasGrantedOrDeniedPermission("responded", DEFAULT_SALESFORCE_ID));
        assertFalse(orcidRecordService.userHasGrantedOrDeniedPermission("not responded", DEFAULT_SALESFORCE_ID));
    }
    

    /*
     * @Test void testUpdateNonExistentOrcidRecord() {
     * Mockito.when(orcidRecordRepository.findOneByEmail(Mockito.anyString())).
     * thenReturn(Optional.of(getOrcidRecordWithIdToken(EMAIL_ONE)));
     * Mockito.when(orcidRecordRepository.save(Mockito.any(OrcidRecord.class))).
     * thenAnswer(new Answer<OrcidRecord>() {
     * 
     * @Override public OrcidRecord answer(InvocationOnMock invocation) throws
     * Throwable { return (OrcidRecord) invocation.getArgument(0); } });
     * 
     * OrcidRecord recordOne = getOrcidRecordWithIdToken(EMAIL_ONE);
     * recordOne.setId("xyze");
     * Assertions.assertThrows(BadRequestAlertException.class, () -> {
     * orcidRecordService.updateOrcidRecord(recordOne); }); }
     */

    private OrcidRecord getOrcidRecordWithIdToken(String email) {
        OrcidRecord record = new OrcidRecord();
        record.setCreated(Instant.now());
        record.setEmail(email);
        List<OrcidToken> tokens = new ArrayList<OrcidToken>();
        OrcidToken newToken = new OrcidToken(DEFAULT_SALESFORCE_ID, "idToken");
        tokens.add(newToken);
        record.setTokens(tokens);
        record.setId("xyz");
        record.setOrcid("orcid");
        return record;
    }
    
    private OrcidRecord getOrcidRecordWithRevokedToken(String email) {
        OrcidRecord record = new OrcidRecord();
        record.setCreated(Instant.now());
        record.setEmail(email);
        List<OrcidToken> tokens = new ArrayList<OrcidToken>();
        OrcidToken newToken = new OrcidToken(DEFAULT_SALESFORCE_ID, "revoked token");
        newToken.setRevokedDate(Instant.now());
        tokens.add(newToken);
        record.setTokens(tokens);
        record.setId("xyz");
        record.setOrcid("orcid");
        return record;
    }
    
    private OrcidRecord getOrcidRecordWithToken(String email, OrcidToken token) {
        OrcidRecord record = new OrcidRecord();
        record.setCreated(Instant.now());
        record.setEmail(email);
        List<OrcidToken> tokens = new ArrayList<OrcidToken>();
        tokens.add(token);
        record.setTokens(tokens);
        return record;
    }

}
