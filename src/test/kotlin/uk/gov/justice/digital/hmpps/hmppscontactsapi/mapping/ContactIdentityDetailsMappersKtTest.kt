package uk.gov.justice.digital.hmpps.hmppscontactsapi.mapping

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.ContactIdentityDetailsEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.response.ContactIdentityDetails
import java.time.LocalDateTime

class ContactIdentityDetailsMappersKtTest {
  @Test
  fun `should map from entity to domain`() {
    assertThat(
      ContactIdentityDetailsEntity(
        contactIdentityId = 1,
        contactId = 1,
        identityType = "PASSPORT",
        identityTypeDescription = "Passport",
        identityValue = "132456789",
        issuingAuthority = "UK",
        verified = false,
        verifiedBy = "VER",
        verifiedTime = LocalDateTime.of(2024, 1, 1, 1, 1, 1),
        createdBy = "CRE",
        createdTime = LocalDateTime.of(2024, 2, 2, 2, 2, 2),
        amendedBy = "AMD",
        amendedTime = LocalDateTime.of(2024, 3, 3, 3, 3, 3),
      ).toModel(),
    ).isEqualTo(
      ContactIdentityDetails(
        contactIdentityId = 1,
        contactId = 1,
        identityType = "PASSPORT",
        identityTypeDescription = "Passport",
        identityValue = "132456789",
        issuingAuthority = "UK",
        verified = false,
        verifiedBy = "VER",
        verifiedTime = LocalDateTime.of(2024, 1, 1, 1, 1, 1),
        createdBy = "CRE",
        createdTime = LocalDateTime.of(2024, 2, 2, 2, 2, 2),
        amendedBy = "AMD",
        amendedTime = LocalDateTime.of(2024, 3, 3, 3, 3, 3),
      ),
    )
  }
}
