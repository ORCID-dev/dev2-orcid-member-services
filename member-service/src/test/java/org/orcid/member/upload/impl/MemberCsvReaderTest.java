package org.orcid.member.upload.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.orcid.member.domain.Member;
import org.orcid.member.repository.MemberRepository;
import org.orcid.member.service.user.MemberServiceUser;
import org.orcid.member.upload.MemberUpload;
import org.orcid.member.validation.MemberValidator;
import org.springframework.context.MessageSource;

class MemberCsvReaderTest {

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private MessageSource messageSource;

	@Mock
	private MemberValidator memberValidator;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@InjectMocks
	private MemberCsvReader reader;

	@Test
	void testReadMembersUpload() throws IOException {
		Mockito.when(memberRepository.findBySalesforceId(Mockito.anyString())).thenReturn(Optional.empty());
		Mockito.when(memberValidator.validate(Mockito.any(Member.class), Mockito.any(MemberServiceUser.class), Mockito.eq(true)))
				.thenReturn(new ArrayList<>());

		InputStream inputStream = getClass().getResourceAsStream("/members.csv");
		MemberUpload upload = reader.readMemberUpload(inputStream, getUser());

		assertEquals(2, upload.getMembers().size());

		Member one = upload.getMembers().get(0);
		Member two = upload.getMembers().get(1);

		assertTrue(one.getAssertionServiceEnabled());
		assertFalse(two.getAssertionServiceEnabled());

		assertEquals("XXXX-XXXX-XXXX-XXX1", one.getClientId());
		assertEquals("XXXX-XXXX-XXXX-XXX2", two.getClientId());

		assertFalse(one.getIsConsortiumLead());
		assertFalse(two.getIsConsortiumLead());

		assertEquals("salesforce-1", one.getSalesforceId());
		assertEquals("salesforce-2", two.getSalesforceId());

		assertEquals("salesforce-parent", one.getParentSalesforceId());
		assertEquals("salesforce-parent", two.getParentSalesforceId());

		assertEquals("some-client-name", one.getClientName());
		assertEquals("some-other-client-name", two.getClientName());
	}

	private MemberServiceUser getUser() {
		MemberServiceUser user = new MemberServiceUser();
		user.setLangKey("en");
		return user;
	}

}
