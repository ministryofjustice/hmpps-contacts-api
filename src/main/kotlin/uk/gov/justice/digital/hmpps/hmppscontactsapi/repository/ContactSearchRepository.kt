package uk.gov.justice.digital.hmpps.hmppscontactsapi.repository

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.ContactEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.ContactSearchRequest
import java.time.LocalDate

@Repository
class ContactSearchRepository(
  @PersistenceContext
  private var entityManager: EntityManager,
) {
  fun searchContacts(request: ContactSearchRequest, pageable: Pageable): Page<ContactEntity> {
    val cb = entityManager.criteriaBuilder
    val cq = cb.createQuery(ContactEntity::class.java)
    val contact = cq.from(ContactEntity::class.java)

    val predicates: List<Predicate> = buildPredicates(request, cb, contact)

    cq.where(*predicates.toTypedArray())

    applySorting(pageable, cq, cb, contact)

    val resultList = entityManager.createQuery(cq)
      .setFirstResult(pageable.offset.toInt())
      .setMaxResults(pageable.pageSize)
      .resultList

    val total = getTotalCount(request)

    return PageImpl(resultList, pageable, total)
  }

  private fun applySorting(
    pageable: Pageable,
    cq: CriteriaQuery<ContactEntity>,
    cb: CriteriaBuilder,
    contact: Root<ContactEntity>,
  ) {
    if (pageable.sort.isSorted) {
      pageable.sort.forEach {
        when {
          it.isAscending -> cq.orderBy(cb.asc(contact.get<String>(it.property)))
          else -> cq.orderBy(cb.desc(contact.get<String>(it.property)))
        }
      }
    }
  }

  private fun buildPredicates(
    request: ContactSearchRequest,
    cb: CriteriaBuilder,
    contact: Root<ContactEntity>,
  ): MutableList<Predicate> {
    val predicates: MutableList<Predicate> = ArrayList()

    request.lastName.let {
      predicates.add(
        cb.like(
          cb.upper(contact.get("lastName")),
          "%${it.trim().uppercase()}%",
        ),
      )
    }
    request.firstName?.let {
      predicates.add(
        cb.like(
          cb.upper(contact.get("firstName")),
          "%${it.trim().uppercase()}%",
        ),
      )
    }
    request.middleName?.let {
      predicates.add(
        cb.like(
          cb.upper(contact.get("middleName")),
          "%${it.trim().uppercase()}%",
        ),
      )
    }
    request.dateOfBirth?.let {
      predicates.add(
        cb.equal(
          contact.get<LocalDate>("dateOfBirth"),
          request.dateOfBirth,
        ),
      )
    }
    return predicates
  }

  private fun getTotalCount(
    request: ContactSearchRequest,
  ): Long {
    val cb = entityManager.criteriaBuilder
    val countQuery = cb.createQuery(Long::class.java)
    val contact = countQuery.from(ContactEntity::class.java)

    val predicates: List<Predicate> = buildPredicates(request, cb, contact)

    countQuery.select(cb.count(contact)).where(*predicates.toTypedArray<Predicate>())
    val total = entityManager.createQuery(countQuery).singleResult
    return total
  }
}
