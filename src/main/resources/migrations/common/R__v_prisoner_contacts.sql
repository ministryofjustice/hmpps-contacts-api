--
-- Creates a view over the contact, contact_address and prisoner_contact tables
-- to return a list of active or inactive contacts, and their primary addresses,
-- for a prisoner.
--
CREATE OR REPLACE VIEW v_prisoner_contacts
AS
  select
      c.contact_id,
      c.title,
      rc1.description as title_description,
      c.first_name,
      c.middle_names,
      c.last_name,
      c.date_of_birth,
      c.estimated_is_over_eighteen,
      ca.contact_address_id,
      ca.flat,
      ca.property,
      ca.street,
      ca.area,
      ca.city_code,
      city_ref.nomis_description as city_description,
      ca.county_code,
      county_ref.nomis_description as county_description,
      ca.post_code,
      ca.country_code,
      country_ref.nomis_description as country_description,
      cp.contact_phone_id,
      cp.phone_type,
      rc2.description as phone_type_description,
      cp.phone_number,
      cp.ext_number,
      ce.contact_email_id,
      ce.email_type,
      rc3.description as email_type_description,
      ce.email_address,
      pc.prisoner_contact_id,
      pc.prisoner_number,
      pc.relationship_type,
      rc4.description as relationship_description,
      pc.active,
      pc.can_be_contacted,
      pc.approved_visitor,
      pc.aware_of_charges,
      pc.next_of_kin,
      pc.emergency_contact,
      pc.comments
  from contact c
       join prisoner_contact pc ON pc.contact_id = c.contact_id
  left join contact_address ca ON ca.contact_id = c.contact_id AND ca.primary_address = true
  left join (select contact_id, contact_phone_id, phone_type, phone_number, ext_number, row_number() over (partition by contact_id order by created_time desc) as rn
            from contact_phone) cp on (cp.contact_id = c.contact_id and cp.rn = 1)
  left join contact_email ce ON ce.contact_id = c.contact_id AND ce.primary_email = true
  left join reference_codes rc1 ON rc1.group_code = 'TITLE' and rc1.code = c.title
  left join reference_codes rc2 ON rc2.group_code = 'PHONE_TYPE' and rc2.code = cp.phone_type
  left join reference_codes rc3 ON rc3.group_code = 'EMAIL_TYPE' and rc3.code = ce.email_type
  left join reference_codes rc4 ON rc4.group_code = 'RELATIONSHIP' and rc4.code = pc.relationship_type
  left join city_reference city_ref on city_ref.nomis_code = ca.city_code
  left join county_reference county_ref on county_ref.nomis_code = ca.county_code
  left join country_reference country_ref on country_ref.nomis_code = ca.country_code
  where pc.contact_id = c.contact_id
  order by pc.created_time desc;

-- End