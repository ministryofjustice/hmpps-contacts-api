--
-- Updating off_relation reference data to official_relationship
--

update reference_codes set group_code = 'OFFICIAL_RELATIONSHIP' where group_code = 'OFF_RELATION';

-- End