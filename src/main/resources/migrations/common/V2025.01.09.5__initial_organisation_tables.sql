--
-- Tables, indexes and reference data required for an organisation and a contacts employment with an organisation.
--

---------------------------------------------------------------------------------------
-- Organisations have several small pieces of metadata associated with them.
-- This table stores that metadata and has a unique primary id range to avoid collision with CORPORATE_ID in NOMIS.
----------------------------------------------------------------------------------------

CREATE TABLE organisation
(
    organisation_id int GENERATED BY DEFAULT AS IDENTITY (START WITH 20000000) PRIMARY KEY,
    organisation_name varchar(40) NOT NULL,
    programme_number varchar(40), -- FEI_NUMBER in NOMIS
    vat_number varchar(12),
    caseload_id varchar(6),
    comments varchar(240),
    contact_person_name varchar(40),
    created_by varchar(100) NOT NULL,
    created_time timestamp NOT NULL DEFAULT current_timestamp,
    updated_by varchar(100),
    updated_time timestamp
);

CREATE INDEX idx_organisation_organisation_name ON organisation(organisation_name);
CREATE INDEX idx_organisation_created_time ON organisation(created_time);

---------------------------------------------------------------------------------------
-- Organisations may have multiple types.
-- This table stores the types of an organisation.
----------------------------------------------------------------------------------------

CREATE TABLE organisation_type(
                                  organisation_type_id int GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                                  organisation_id bigint NOT NULL REFERENCES organisation(organisation_id),
                                  organisation_type varchar(12) NOT NULL, -- Reference codes - ORGANISATION_TYPE
                                  created_by varchar(100) NOT NULL,
                                  created_time timestamp NOT NULL DEFAULT current_timestamp,
                                  updated_by varchar(100),
                                  updated_time timestamp
);

CREATE INDEX idx_organisation_type_organisation_id ON organisation_type(organisation_id);
CREATE INDEX idx_organisation_type_created_time ON organisation_type(created_time);

---------------------------------------------------------------------------------------
-- Organisations may have multiple telephone numbers.
-- This table stores the telephone numbers related to an organisation.
----------------------------------------------------------------------------------------

CREATE TABLE organisation_phone
(
    organisation_phone_id bigserial NOT NULL CONSTRAINT organisation_phone_id_pk PRIMARY KEY,
    organisation_id bigint NOT NULL REFERENCES organisation(organisation_id),
    phone_type varchar(12) NOT NULL, -- Reference codes - PHONE_TYPE
    phone_number varchar(40) NOT NULL,
    ext_number varchar(7),
    created_by varchar(100) NOT NULL,
    created_time timestamp NOT NULL DEFAULT current_timestamp,
    updated_by varchar(100),
    updated_time timestamp
);

CREATE INDEX idx_organisation_phone_organisation_id ON organisation_phone(organisation_id);
CREATE INDEX idx_organisation_phone_number ON organisation_phone(phone_number);
CREATE INDEX idx_organisation_phone_created_time ON organisation_phone(created_time);

---------------------------------------------------------------------------------------
-- Organisations may have multiple email addresses.
-- This table stores the email addresses related to an organisation.
----------------------------------------------------------------------------------------

CREATE TABLE organisation_email
(
    organisation_email_id bigserial NOT NULL CONSTRAINT organisation_email_id_pk PRIMARY KEY,
    organisation_id bigint NOT NULL REFERENCES organisation(organisation_id),
    email_address varchar(240) NOT NULL,
    created_by varchar(100) NOT NULL,
    created_time timestamp NOT NULL DEFAULT current_timestamp,
    updated_by varchar(100),
    updated_time timestamp
);

CREATE INDEX idx_organisation_email_organisation_id ON organisation_email(organisation_id);
CREATE INDEX idx_organisation_email_address ON organisation_email(email_address);

---------------------------------------------------------------------------------------
-- Organisations may have multiple web addresses.
-- This table stores the web addresses related to an organisation.
----------------------------------------------------------------------------------------

CREATE TABLE organisation_web_address
(
    organisation_web_id bigserial NOT NULL CONSTRAINT organisation_web_id_pk PRIMARY KEY,
    organisation_id bigint NOT NULL REFERENCES organisation(organisation_id),
    web_address varchar(240) NOT NULL,
    created_by varchar(100) NOT NULL,
    created_time timestamp NOT NULL DEFAULT current_timestamp,
    updated_by varchar(100),
    updated_time timestamp
);

CREATE INDEX idx_organisation_web_address_organisation_id ON organisation_web_address(organisation_id);
CREATE INDEX idx_organisation_web_address_web_address ON organisation_web_address(web_address);


---------------------------------------------------------------------------------------
-- Organisations may have one or more addresses.
-- This table holds the details of addresses
----------------------------------------------------------------------------------------

CREATE TABLE organisation_address
(
    organisation_address_id bigserial NOT NULL CONSTRAINT organisation_address_id_pk PRIMARY KEY,
    organisation_id bigint NOT NULL REFERENCES organisation(organisation_id),
    address_type varchar(12), -- Reference code - ADDRESS_TYPE e.g. HOME, WORK
    primary_address boolean NOT NULL DEFAULT false,
    mail_address boolean NOT NULL DEFAULT false,
    service_address boolean NOT NULL DEFAULT false,
    flat varchar(30),
    property varchar(50),
    street varchar(160),
    area varchar(70),
    city_code varchar(12), -- Reference code - CITY
    county_code varchar(12), -- Reference code - COUNTY
    post_code varchar(12),
    country_code varchar(12), -- Reference code - COUNTRY
    special_needs_code varchar(12),	-- Reference code - ORG_ADDRESS_SPECIAL_NEEDS
    contact_person_name	varchar(40),
    business_hours varchar(60),
    start_date date,
    end_date date,
    no_fixed_address boolean NOT NULL DEFAULT false,
    comments varchar(240),
    created_by varchar(100) NOT NULL,
    created_time timestamp NOT NULL DEFAULT current_timestamp,
    updated_by varchar(100),
    updated_time timestamp
);

CREATE INDEX idx_organisation_address_organisation_id ON organisation_address(organisation_id);

---------------------------------------------------------------
-- Address-specific phone numbers for organisations.
-- Currently modelled as a join-table between organisation_phone and organisation_address.
----------------------------------------------------------------------------------------

CREATE TABLE organisation_address_phone
(
    organisation_address_phone_id bigserial NOT NULL CONSTRAINT organisation_address_phone_id_pk PRIMARY KEY,
    organisation_id bigint NOT NULL REFERENCES organisation(organisation_id),
    organisation_address_id bigint NOT NULL REFERENCES organisation_address(organisation_address_id),
    organisation_phone_id bigint NOT NULL REFERENCES organisation_phone(organisation_phone_id),
    created_by varchar(100) NOT NULL,
    created_time timestamp NOT NULL DEFAULT current_timestamp,
    updated_by varchar(100),
    updated_time timestamp
);

CREATE INDEX idx_organisation_address_phone_organisation_id ON organisation_address_phone(organisation_id);
CREATE INDEX idx_organisation_address_phone_organisation_address_id ON organisation_address_phone(organisation_address_id);
CREATE INDEX idx_organisation_address_phone_organisation_phone_id ON organisation_address_phone(organisation_phone_id);

---------------------------------------------------------------------------------------
-- Contacts may have multiple current or former employments with an organisation.
-- This table maps the organisation and contact ids along with an active state.
----------------------------------------------------------------------------------------

CREATE TABLE employment
(
    employment_id bigserial NOT NULL CONSTRAINT employment_id_pk PRIMARY KEY,
    organisation_id bigint NOT NULL REFERENCES organisation(organisation_id),
    contact_id bigint NOT NULL REFERENCES contact(contact_id),
    active boolean NOT NULL,
    created_by varchar(100) NOT NULL,
    created_time timestamp NOT NULL DEFAULT current_timestamp,
    updated_by varchar(100),
    updated_time timestamp
);

CREATE INDEX idx_employment_organisation_id ON employment(organisation_id);
CREATE INDEX idx_employment_contact_id ON employment(contact_id);

---------------------------------------------------------------------------------------
-- Reference data for organisations.
----------------------------------------------------------------------------------------
insert into reference_codes
(group_code, code, description, display_order, is_active, created_by)
values ('ORGANISATION_TYPE', 'OTH', 'Other', 1, true, 'JAMES'),
       ('ORGANISATION_TYPE', 'TRUST', 'Trust', 1, true, 'JAMES'),
       ('ORGANISATION_TYPE', 'TEA', 'Teacher', 1, true, 'JAMES'),
       ('ORGANISATION_TYPE', 'SWO', 'Social Worker', 1, true, 'JAMES'),
       ('ORGANISATION_TYPE', 'SOL', 'Solicitor', 1, true, 'JAMES'),
       ('ORGANISATION_TYPE', 'RABBI', 'Rabbi', 1, true, 'JAMES'),
       ('ORGANISATION_TYPE', 'PV', 'Prison Visitor', 1, true, 'JAMES'),
       ('ORGANISATION_TYPE', 'PROB', 'Probation Officer', 1, true, 'JAMES'),
       ('ORGANISATION_TYPE', 'PRE', 'Priest or Other Clergy', 1, true, 'JAMES'),
       ('ORGANISATION_TYPE', 'DOCTOR', 'Doctor', 1, true, 'JAMES'),
       ('ORGANISATION_TYPE', 'HDC', 'Home Detention Curfew Provider', 1, true, 'JAMES'),
       ('ORGANISATION_TYPE', 'IMAM', 'Imam', 1, true, 'JAMES'),
       ('ORGANISATION_TYPE', 'YOTWORKER', 'YOT Offender Supervisor/Manager', 1, true, 'JAMES'),
       ('ORGANISATION_TYPE', 'POL', 'Police Officer', 1, true, 'JAMES'),
       ('ORGANISATION_TYPE', 'PROG', 'Accredited Programme Provider', 2, true, 'JAMES'),
       ('ORGANISATION_TYPE', 'ACCOM', 'Accommodation Provider', 3, true, 'JAMES'),
       ('ORGANISATION_TYPE', 'APPPREM', 'Approved Premises', 4, true, 'JAMES'),
       ('ORGANISATION_TYPE', 'DRR', 'Drugs Intervention Provider', 6, true, 'JAMES'),
       ('ORGANISATION_TYPE', 'ATEST', 'Alcohol Testing Provider', 7, true, 'JAMES'),
       ('ORGANISATION_TYPE', 'ACOUNS', 'Alcohol Counselling Provider', 8, true, 'JAMES'),
       ('ORGANISATION_TYPE', 'BSKILLS', 'Basic Skills Provider', 9, true, 'JAMES'),
       ('ORGANISATION_TYPE', 'ETRAIN', 'Employment Training Provider', 10, true, 'JAMES'),
       ('ORGANISATION_TYPE', 'DCOUNS', 'Debt Counselling Provider', 11, true, 'JAMES'),
       ('ORG_ADDRESS_SPECIAL_NEEDS', 'DEAF', 'Hearing Impaired Translation', 99, true, 'JAMES'),
       ('ORG_ADDRESS_SPECIAL_NEEDS', 'DISABLED', 'Disabled Access', 99, true, 'JAMES')
;

-- End