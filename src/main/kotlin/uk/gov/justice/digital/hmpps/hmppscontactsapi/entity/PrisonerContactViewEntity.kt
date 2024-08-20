package uk.gov.justice.digital.hmpps.hmppscontactsapi.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDate

@Entity
@Table(name = "prisoner_contact_view")
data class PrisonerContactViewEntity(

    @Id
    @Column(name = "prisoner_contact_id")
    val prisonerContactId: Long,

    @Column(name = "contact_id")
    val contactId: Long,

    @Column(name = "surname")
    val surname: String,

    @Column(name = "forename")
    val forename: String,

    @Column(name = "middle_name")
    val middleName: String?,

    @Column(name = "date_of_birth")
    val dateOfBirth: LocalDate,

    @Column(name = "relationship_code")
    val relationshipCode: String?,

    @Column(name = "relationship_description")
    val relationshipDescription: String?,

    @Column(name = "addresses")
    val addresses: String,

    @Column(name = "approved_visitor")
    val approvedVisitor: Boolean
)
