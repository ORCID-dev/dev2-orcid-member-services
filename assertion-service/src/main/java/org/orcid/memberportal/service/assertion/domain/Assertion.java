package org.orcid.memberportal.service.assertion.domain;

import java.io.Serializable;
import java.time.Instant;
import java.util.Locale;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;
import org.orcid.memberportal.service.assertion.domain.enumeration.AffiliationSection;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "assertion")
public class Assertion implements Serializable {
    private static final long serialVersionUID = 1845971448687999429L;

    @Id
    private String id;

    @NotNull
    @Field("email")
    private String email;

    @NotNull
    @Field("affiliation_section")
    private AffiliationSection affiliationSection;

    @Size(max = 4000)
    @Field("department_name")
    private String departmentName;

    @Size(max = 4000)
    @Field("role_title")
    private String roleTitle;

    @Field("start_year")
    private String startYear;

    @Field("start_month")
    private String startMonth;

    @Field("start_day")
    private String startDay;

    @Field("end_year")
    private String endYear;

    @Field("end_month")
    private String endMonth;

    @Field("end_day")
    private String endDay;

    @NotNull
    @Field("org_name")
    private String orgName;

    @NotNull
    @Field("org_country")
    private String orgCountry;

    @NotNull
    @Field("org_city")
    private String orgCity;

    @Field("org_region")
    private String orgRegion;

    @NotNull
    @Field("disambiguated_org_id")
    private String disambiguatedOrgId;

    @Field("disambiguation_source")
    private String disambiguationSource;

    @Field("external_id")
    private String externalId;

    @Field("external_id_type")
    private String externalIdType;

    @Field("external_id_url")
    private String externalIdUrl;

    @Indexed
    @Field("put_code")
    private String putCode;

    @Indexed
    @Field("created")
    private Instant created;

    @Field("modified")
    private Instant modified;
    
    @Field("last_sync_attempt")
    private Instant lastSyncAttempt;

    @Field("last_modified_by")
    private String lastModifiedBy;

    @Field("owner_id")
    private String ownerId;

    @Indexed
    @Field("salesforce_id")
    private String salesforceId;

    @Indexed
    @Field("added_to_orcid")
    private Instant addedToORCID;

    @Field("updated_in_orcid")
    private Instant updatedInORCID;

    @Field("url")
    private String url;

    @Field("orcid_error")
    private String orcidError;
    
    @Field("notification_sent")
    private Instant notificationSent;
    
    @Field("invitation_sent")
    private Instant invitationSent;
    
    @Field("notification_last_sent")
    private Instant notificationLastSent;
    
    @Field("invitation_last_sent")
    private Instant invitationLastSent;

    @Field("orcid_id")
    private String orcidId;

    private String status;
    
    @Transient
    private String prettyStatus;
    
    @Transient
    private String permissionLink;

    public String getPermissionLink() {
        return permissionLink;
    }

    public void setPermissionLink(String permissionLink) {
        this.permissionLink = permissionLink;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = StringUtils.lowerCase(email, Locale.ENGLISH);
    }

    public AffiliationSection getAffiliationSection() {
        return affiliationSection;
    }

    public void setAffiliationSection(AffiliationSection affiliationSection) {
        this.affiliationSection = affiliationSection;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getRoleTitle() {
        return roleTitle;
    }

    public void setRoleTitle(String roleTitle) {
        this.roleTitle = roleTitle;
    }

    public String getStartYear() {
        return startYear;
    }

    public void setStartYear(String startYear) {
        this.startYear = startYear;
    }

    public String getStartMonth() {
        return startMonth;
    }

    public void setStartMonth(String startMonth) {
        this.startMonth = startMonth;
    }

    public String getStartDay() {
        return startDay;
    }

    public void setStartDay(String startDay) {
        this.startDay = startDay;
    }

    public String getEndYear() {
        return endYear;
    }

    public void setEndYear(String endYear) {
        this.endYear = endYear;
    }

    public String getEndMonth() {
        return endMonth;
    }

    public void setEndMonth(String endMonth) {
        this.endMonth = endMonth;
    }

    public String getEndDay() {
        return endDay;
    }

    public void setEndDay(String endDay) {
        this.endDay = endDay;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getOrgCountry() {
        return orgCountry;
    }

    public void setOrgCountry(String orgCountry) {
        this.orgCountry = orgCountry;
    }

    public String getOrgCity() {
        return orgCity;
    }

    public void setOrgCity(String orgCity) {
        this.orgCity = orgCity;
    }

    public String getOrgRegion() {
        return orgRegion;
    }

    public void setOrgRegion(String orgRegion) {
        this.orgRegion = orgRegion;
    }

    public String getDisambiguatedOrgId() {
        return disambiguatedOrgId;
    }

    public void setDisambiguatedOrgId(String disambiguatedOrgId) {
        this.disambiguatedOrgId = disambiguatedOrgId;
    }

    public String getDisambiguationSource() {
        return disambiguationSource;
    }

    public void setDisambiguationSource(String disambiguationSource) {
        this.disambiguationSource = disambiguationSource;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getExternalIdType() {
        return externalIdType;
    }

    public void setExternalIdType(String externalIdType) {
        this.externalIdType = externalIdType;
    }

    public String getExternalIdUrl() {
        return externalIdUrl;
    }

    public void setExternalIdUrl(String externalIdUrl) {
        this.externalIdUrl = externalIdUrl;
    }

    public String getPutCode() {
        return putCode;
    }

    public void setPutCode(String putCode) {
        this.putCode = putCode;
    }

    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Instant getModified() {
        return modified;
    }

    public void setModified(Instant modified) {
        this.modified = modified;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Instant getLastSyncAttempt() {
        return lastSyncAttempt;
    }

    public void setLastSyncAttempt(Instant lastSyncAttempt) {
        this.lastSyncAttempt = lastSyncAttempt;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters
    // and setters here, do not remove
    public String getSalesforceId() {
        return salesforceId;
    }

    public void setSalesforceId(String salesforceId) {
        this.salesforceId = salesforceId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getAddedToORCID() {
        return addedToORCID;
    }

    public void setAddedToORCID(Instant addedToORCID) {
        this.addedToORCID = addedToORCID;
    }

    public Instant getUpdatedInORCID() {
        return updatedInORCID;
    }

    public void setUpdatedInORCID(Instant updatedInORCID) {
        this.updatedInORCID = updatedInORCID;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getOrcidError() {
        return orcidError;
    }

    public void setOrcidError(String orcidError) {
        this.orcidError = orcidError;
    }

    public String getOrcidId() {
        return orcidId;
    }

    public void setOrcidId(String orcidId) {
        this.orcidId = orcidId;
    }
    
    public String getPrettyStatus() {
        return prettyStatus;
    }

    public void setPrettyStatus(String prettyStatus) {
        this.prettyStatus = prettyStatus;
    }
    
    /**
     * Returns the date the notification was first sent to the orcid user associated with this affiliation
     * 
     * @return the Instant representing the invitation date
     */
    public Instant getNotificationSent() {
        return notificationSent;
    }

    public void setNotificationSent(Instant notificationSent) {
        this.notificationSent = notificationSent;
    }
    
    /**
     * Returns the date the invitation was first sent to the email associated with this affiliation
     * 
     * @return the Instant representing the invitation date
     */
    public Instant getInvitationSent() {
        return invitationSent;
    }

    public void setInvitationSent(Instant invitationSent) {
        this.invitationSent = invitationSent;
    }
    
    /**
     * Returns the date the notification was last sent to the orcid user associated with this affiliation
     * 
     * @return the Instant representing the invitation date
     */
    public Instant getNotificationLastSent() {
        return notificationLastSent;
    }

    public void setNotificationLastSent(Instant notificationLastSent) {
        this.notificationLastSent = notificationLastSent;
    }

    /**
     * Returns the date the invitation was last sent to the email associated with this affiliation
     * 
     * @return the Instant representing the invitation date
     */
    public Instant getInvitationLastSent() {
        return invitationLastSent;
    }

    public void setInvitationLastSent(Instant invitationLastSent) {
        this.invitationLastSent = invitationLastSent;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((addedToORCID == null) ? 0 : addedToORCID.hashCode());
        result = prime * result + ((affiliationSection == null) ? 0 : affiliationSection.hashCode());
        result = prime * result + ((created == null) ? 0 : created.hashCode());
        result = prime * result + ((departmentName == null) ? 0 : departmentName.hashCode());
        result = prime * result + ((disambiguatedOrgId == null) ? 0 : disambiguatedOrgId.hashCode());
        result = prime * result + ((disambiguationSource == null) ? 0 : disambiguationSource.hashCode());
        result = prime * result + ((email == null) ? 0 : email.hashCode());
        result = prime * result + ((endDay == null) ? 0 : endDay.hashCode());
        result = prime * result + ((endMonth == null) ? 0 : endMonth.hashCode());
        result = prime * result + ((endYear == null) ? 0 : endYear.hashCode());
        result = prime * result + ((externalId == null) ? 0 : externalId.hashCode());
        result = prime * result + ((externalIdType == null) ? 0 : externalIdType.hashCode());
        result = prime * result + ((externalIdUrl == null) ? 0 : externalIdUrl.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((modified == null) ? 0 : modified.hashCode());
        result = prime * result + ((lastSyncAttempt == null) ? 0 : lastSyncAttempt.hashCode());
        result = prime * result + ((orcidError == null) ? 0 : orcidError.hashCode());
        result = prime * result + ((orgCity == null) ? 0 : orgCity.hashCode());
        result = prime * result + ((orgCountry == null) ? 0 : orgCountry.hashCode());
        result = prime * result + ((orgName == null) ? 0 : orgName.hashCode());
        result = prime * result + ((orgRegion == null) ? 0 : orgRegion.hashCode());
        result = prime * result + ((ownerId == null) ? 0 : ownerId.hashCode());
        result = prime * result + ((putCode == null) ? 0 : putCode.hashCode());
        result = prime * result + ((roleTitle == null) ? 0 : roleTitle.hashCode());
        result = prime * result + ((salesforceId == null) ? 0 : salesforceId.hashCode());
        result = prime * result + ((startDay == null) ? 0 : startDay.hashCode());
        result = prime * result + ((startMonth == null) ? 0 : startMonth.hashCode());
        result = prime * result + ((startYear == null) ? 0 : startYear.hashCode());
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        result = prime * result + ((url == null) ? 0 : url.hashCode());
        result = prime * result + ((updatedInORCID == null) ? 0 : updatedInORCID.hashCode());
        result = prime * result + ((orcidId == null) ? 0 : orcidId.hashCode());
        result = prime * result + ((notificationSent == null) ? 0 : notificationSent.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Assertion other = (Assertion) obj;
        if (addedToORCID == null) {
            if (other.addedToORCID != null)
                return false;
        } else if (!addedToORCID.equals(other.addedToORCID))
            return false;
        if (affiliationSection != other.affiliationSection)
            return false;
        if (created == null) {
            if (other.created != null)
                return false;
        } else if (!created.equals(other.created))
            return false;
        if (departmentName == null) {
            if (other.departmentName != null)
                return false;
        } else if (!departmentName.equals(other.departmentName))
            return false;
        if (disambiguatedOrgId == null) {
            if (other.disambiguatedOrgId != null)
                return false;
        } else if (!disambiguatedOrgId.equals(other.disambiguatedOrgId))
            return false;
        if (disambiguationSource == null) {
            if (other.disambiguationSource != null)
                return false;
        } else if (!disambiguationSource.equals(other.disambiguationSource))
            return false;
        if (email == null) {
            if (other.email != null)
                return false;
        } else if (!email.equals(other.email))
            return false;
        if (endDay == null) {
            if (other.endDay != null)
                return false;
        } else if (!endDay.equals(other.endDay))
            return false;
        if (endMonth == null) {
            if (other.endMonth != null)
                return false;
        } else if (!endMonth.equals(other.endMonth))
            return false;
        if (endYear == null) {
            if (other.endYear != null)
                return false;
        } else if (!endYear.equals(other.endYear))
            return false;
        if (externalId == null) {
            if (other.externalId != null)
                return false;
        } else if (!externalId.equals(other.externalId))
            return false;
        if (externalIdType == null) {
            if (other.externalIdType != null)
                return false;
        } else if (!externalIdType.equals(other.externalIdType))
            return false;
        if (externalIdUrl == null) {
            if (other.externalIdUrl != null)
                return false;
        } else if (!externalIdUrl.equals(other.externalIdUrl))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (modified == null) {
            if (other.modified != null)
                return false;
        } else if (!modified.equals(other.modified))
            return false;
        if (lastSyncAttempt == null) {
            if (other.lastSyncAttempt != null)
                return false;
        } else if (!lastSyncAttempt.equals(other.lastSyncAttempt))
            return false;
        if (orcidError == null) {
            if (other.orcidError != null)
                return false;
        } else if (!orcidError.equals(other.orcidError))
            return false;
        if (orgCity == null) {
            if (other.orgCity != null)
                return false;
        } else if (!orgCity.equals(other.orgCity))
            return false;
        if (orgCountry == null) {
            if (other.orgCountry != null)
                return false;
        } else if (!orgCountry.equals(other.orgCountry))
            return false;
        if (orgName == null) {
            if (other.orgName != null)
                return false;
        } else if (!orgName.equals(other.orgName))
            return false;
        if (orgRegion == null) {
            if (other.orgRegion != null)
                return false;
        } else if (!orgRegion.equals(other.orgRegion))
            return false;
        if (ownerId == null) {
            if (other.ownerId != null)
                return false;
        } else if (!ownerId.equals(other.ownerId))
            return false;
        if (putCode == null) {
            if (other.putCode != null)
                return false;
        } else if (!putCode.equals(other.putCode))
            return false;
        if (roleTitle == null) {
            if (other.roleTitle != null)
                return false;
        } else if (!roleTitle.equals(other.roleTitle))
            return false;
        if (salesforceId == null) {
            if (other.salesforceId != null)
                return false;
        } else if (!salesforceId.equals(other.salesforceId))
            return false;
        if (startDay == null) {
            if (other.startDay != null)
                return false;
        } else if (!startDay.equals(other.startDay))
            return false;
        if (startMonth == null) {
            if (other.startMonth != null)
                return false;
        } else if (!startMonth.equals(other.startMonth))
            return false;
        if (startYear == null) {
            if (other.startYear != null)
                return false;
        } else if (!startYear.equals(other.startYear))
            return false;
        if (status == null) {
            if (other.status != null)
                return false;
        } else if (!status.equals(other.status))
            return false;
        if (url == null) {
            if (other.url != null)
                return false;
        } else if (!url.equals(other.url))
            return false;
        if (updatedInORCID == null) {
            if (other.updatedInORCID != null)
                return false;
        } else if (!updatedInORCID.equals(other.updatedInORCID))
            return false;
        if (orcidId == null) {
            if (other.orcidId != null)
                return false;
        } else if (!orcidId.equals(other.orcidId))
            return false;
        if (notificationSent == null) {
            if (other.notificationSent != null)
                return false;
        } else if (!notificationSent.equals(other.notificationSent))
            return false;
        return true;
    }
}
