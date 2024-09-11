package uk.gov.justice.digital.hmpps.hmppscontactsapi.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.ContactEntity
import java.time.LocalDate

@Repository
interface ContactRepository : JpaRepository<ContactEntity, Long> {
  @Query(
    """
          SELECT c FROM ContactEntity c 
          WHERE (:lastName IS NULL OR c.lastName LIKE CONCAT('%', :lastName, '%'))
          AND (:firstName IS NULL OR c.firstName LIKE CONCAT('%', :firstName, '%'))
          AND (:middleName IS NULL OR c.middleName LIKE CONCAT('%', :middleName, '%'))
          AND ((cast(:dateOfBirth as date) IS NULL) OR c.dateOfBirth = :dateOfBirth)
        """,
  )
  fun searchContacts(
    @Param("lastName") lastName: String,
    @Param("firstName") firstName: String?,
    @Param("middleName") middleName: String?,
    @Param("dateOfBirth") dateOfBirth: LocalDate?,
    pageable: Pageable,
  ): Page<ContactEntity>
}
