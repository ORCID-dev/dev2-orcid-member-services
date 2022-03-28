package org.orcid.memberportal.service.assertion.services;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.orcid.memberportal.service.assertion.config.ApplicationProperties;
import org.orcid.memberportal.service.assertion.domain.AssertionServiceUser;
import org.orcid.memberportal.service.assertion.domain.OrcidRecord;
import org.orcid.memberportal.service.assertion.domain.OrcidToken;
import org.orcid.memberportal.service.assertion.repository.OrcidRecordRepository;
import org.orcid.memberportal.service.assertion.security.EncryptUtil;
import org.orcid.memberportal.service.assertion.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class OrcidRecordService {
    
    private static final Logger LOG = LoggerFactory.getLogger(OrcidRecordService.class);

    @Autowired
    private OrcidRecordRepository orcidRecordRepository;

    @Autowired
    private EncryptUtil encryptUtil;

    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    private UserService assertionsUserService;

    public Optional<OrcidRecord> findOneByEmail(String email) {
        return orcidRecordRepository.findOneByEmail(email);
    }

    public OrcidRecord createOrcidRecord(String email, Instant now, String salesForceId) {
        Optional<OrcidRecord> optional = findOneByEmail(email);
        if (optional.isPresent()) {
            throw new BadRequestAlertException("An Orcid Record with the email: " + email + " already exists.", "orcidRecord", "orcidRecordEmailUsed");
        }

        OrcidRecord or = new OrcidRecord();
        or.setEmail(email);
        List<OrcidToken> tokens = new ArrayList<OrcidToken>();
        tokens.add(new OrcidToken(salesForceId, null));
        or.setTokens(tokens);
        or.setCreated(now);
        or.setModified(now);

        return orcidRecordRepository.insert(or);
    }

    public void createOrcidRecords(Set<String> emails, String salesForceId) {
        Instant now = Instant.now();
        // Create assertions
        for (String e : emails) {
            if (!findOneByEmail(e).isPresent()) {
                createOrcidRecord(e, now, salesForceId);
            }
        }
    }

    public void deleteOrcidRecord(OrcidRecord orcidRecord) {
        orcidRecordRepository.delete(orcidRecord);
    }

    public void storeIdToken(String emailInStatus, String idToken, String orcidIdInJWT, String salesForceId) {
        OrcidRecord orcidRecord = orcidRecordRepository.findOneByEmail(emailInStatus)
                .orElseThrow(() -> new IllegalArgumentException("Unable to find userInfo for email: " + emailInStatus));
        List<OrcidToken> tokens = orcidRecord.getTokens();
        List<OrcidToken> updatedTokens = new ArrayList<OrcidToken>();
        OrcidToken newToken = new OrcidToken(salesForceId, idToken);
        if (tokens == null || tokens.size() == 0) {
            updatedTokens.add(newToken);
        } else {
            for (OrcidToken token : tokens) {
                if (StringUtils.equals(token.getSalesforceId(), salesForceId)) {
                    updatedTokens.add(newToken);
                } else {
                    updatedTokens.add(token);
                }
            }
        }
        orcidRecord.setTokens(updatedTokens);
        orcidRecord.setModified(Instant.now());
        orcidRecord.setOrcid(orcidIdInJWT);
        orcidRecord.setRevokeNotificationSentDate(null);
        orcidRecordRepository.save(orcidRecord);
    }

    public void storeUserDeniedAccess(String emailInStatus, String salesforceId) {
        OrcidRecord orcidRecord = orcidRecordRepository.findOneByEmail(emailInStatus)
                .orElseThrow(() -> new IllegalArgumentException("Unable to find userInfo for email: " + emailInStatus));
        List<OrcidToken> tokens = orcidRecord.getTokens();
        List<OrcidToken> updatedTokens = new ArrayList<OrcidToken>();
        OrcidToken deniedToken = new OrcidToken(salesforceId, null);
        deniedToken.setDeniedDate(Instant.now());
        
        if (tokens == null || tokens.size() == 0) {
            updatedTokens.add(deniedToken);
        } else {
            for (OrcidToken token : tokens) {
                if (StringUtils.equals(token.getSalesforceId(), salesforceId)) {
                    updatedTokens.add(deniedToken);
                } else {
                    updatedTokens.add(token);
                }
            }
        }
        orcidRecord.setTokens(updatedTokens);
        orcidRecord.setModified(Instant.now());
        orcidRecordRepository.save(orcidRecord);
    }

    public void storeUserRevokedAccess(String emailInStatus, String salesForceId) {
        OrcidRecord orcidRecord = orcidRecordRepository.findOneByEmail(emailInStatus)
                .orElseThrow(() -> new IllegalArgumentException("Unable to find userInfo for email: " + emailInStatus));
        List<OrcidToken> tokens = orcidRecord.getTokens();
        List<OrcidToken> updatedTokens = new ArrayList<OrcidToken>();
        OrcidToken revokedToken = new OrcidToken(salesForceId, null);
        revokedToken.setRevokedDate(Instant.now());
        if (tokens == null || tokens.size() == 0) {
            updatedTokens.add(revokedToken);
        } else {
            for (OrcidToken token : tokens) {
                if (StringUtils.equals(token.getSalesforceId(), salesForceId)) {
                    updatedTokens.add(revokedToken);
                } else {
                    updatedTokens.add(token);
                }
            }
        }
        orcidRecord.setTokens(updatedTokens);
        orcidRecord.setModified(Instant.now());
        orcidRecordRepository.save(orcidRecord);
    }

    public List<OrcidRecord> getRecordsWithoutTokens(String salesForceId) {
        return orcidRecordRepository.findAllToInvite(salesForceId);
    }

    public String generateLinkForEmail(String email) {
        String landingPageUrl = applicationProperties.getLandingPageUrl();
        String salesForceId = assertionsUserService.getLoggedInUserSalesforceId();
        Optional<OrcidRecord> record = orcidRecordRepository.findOneByEmail(email);
        if (!record.isPresent()) {
            createOrcidRecord(email, Instant.now(), salesForceId);
        }
        return landingPageUrl + "?state=" + encryptUtil.encrypt(salesForceId + "&&" + email);
    }

    public OrcidRecord updateOrcidRecord(OrcidRecord orcidRecord) {
        return orcidRecordRepository.save(orcidRecord);
    }

    public Page<OrcidRecord> findBySalesforceId(Pageable pageable) {
        AssertionServiceUser user = assertionsUserService.getLoggedInUser();
        Page<OrcidRecord> orcidRecords = orcidRecordRepository.findBySalesforceId(user.getSalesforceId(), pageable);
        orcidRecords.forEach(a -> {
            if (StringUtils.isBlank(a.getToken(user.getSalesforceId()))) {
                a.setOrcid(null);
            }
        });
        return orcidRecords;
    }

    public OrcidRecord findById(String id) {
        AssertionServiceUser user = assertionsUserService.getLoggedInUser();
        Optional<OrcidRecord> optional = orcidRecordRepository.findById(id);
        if (!optional.isPresent()) {
            throw new IllegalArgumentException("Invalid assertion id");
        }
        OrcidRecord record = optional.get();
        if (StringUtils.isBlank(record.getToken(user.getSalesforceId()))) {
            record.setOrcid(null);
        }
        return record;
    }

    public void revokeIdToken(String email, String salesForceId) {
        LOG.info("Revoking id token for email {}, salesforce id {}", email, salesForceId);
        OrcidRecord orcidRecord = orcidRecordRepository.findOneByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Unable to find userInfo for email: " + email));
        
        Instant now = Instant.now();
        List<OrcidToken> tokens = orcidRecord.getTokens();
        List<OrcidToken> updatedTokens = new ArrayList<OrcidToken>();
        OrcidToken revokedToken = new OrcidToken(salesForceId, null);
        revokedToken.setRevokedDate(now);
        if (tokens == null || tokens.size() == 0) {
            updatedTokens.add(revokedToken);
        } else {
            for (OrcidToken token : tokens) {
                if (StringUtils.equals(token.getSalesforceId(), salesForceId)) {
                    updatedTokens.add(revokedToken);
                } else {
                    updatedTokens.add(token);
                }
            }
        }
        orcidRecord.setTokens(updatedTokens);
        orcidRecord.setModified(now);
        orcidRecordRepository.save(orcidRecord);
    }

}
