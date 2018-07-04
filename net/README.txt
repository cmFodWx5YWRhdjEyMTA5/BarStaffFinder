## run as root

-- create test user
CREATE USER 'mlbtest'@'localhost' IDENTIFIED BY 'mlbdb';

-- grant privilege to db
GRANT ALL PRIVILEGES ON MyLocalBartender.* TO mlbtest@localhost;
