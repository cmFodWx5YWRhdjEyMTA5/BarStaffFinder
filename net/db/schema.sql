DROP SCHEMA IF EXISTS MyLocalBartender;

CREATE SCHEMA IF NOT EXISTS MyLocalBartender;
USE MyLocalBartender;


DROP TABLE IF EXISTS REGISTRATION;
DROP TABLE IF EXISTS APPLICANTS;
DROP TABLE IF EXISTS FAVOURITES;
DROP TABLE IF EXISTS JOBS;
DROP TABLE IF EXISTS TEMPUSER; 
DROP TABLE IF EXISTS AVAILABILITY;
DROP TABLE IF EXISTS BARSTAFF;
DROP TABLE IF EXISTS ORGANISER;
DROP TABLE IF EXISTS USER;
DROP TABLE IF EXISTS CHATS;
DROP TABLE IF EXISTS CHATLOGS;

-- Generic USER-table, maintains a unique ID for each user and stores shared attributes
-- Active becomes true when all required details are filled in
CREATE TABLE USER (
    user_ID INT UNSIGNED NOT NULL AUTO_INCREMENT,
    account_type ENUM ('BARSTAFF', 'ORGANISER', 'ADMIN') NOT NULL,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    gender ENUM('M','F'),
    DOB DATE NOT NULL,
    email_address VARCHAR(255) NOT NULL UNIQUE,
    mobile_number VARCHAR(255),
    postcode VARCHAR(10),
    city VARCHAR(255),
    town VARCHAR(255),
    stripe_token VARCHAR(255),
    card_id VARCHAR(255),
    last_4 CHAR(4),
    hash NVARCHAR(64) NOT NULL,
    active BOOLEAN default 0,
    PRIMARY KEY(user_ID, email_address)
);

CREATE TABLE TEMPUSER (
    email_address VARCHAR(255) NOT NULL,
    DOB DATE NOT NULL,
    account_type ENUM ('BARSTAFF', 'ORGANISER', 'ADMIN') NOT NULL,
    hash NVARCHAR(64) NOT NULL,
    token VARCHAR(64) NOT NULL 
);

-- Table representing barstaff. Stores all required data.
CREATE TABLE BARSTAFF (
    staff_ID INT UNSIGNED NOT NULL,
    speciality VARCHAR(255),
    description VARCHAR(255),
    experience VARCHAR(255),
    proximity INT,
    hour_rate DECIMAL(4,2),
    night_rate DECIMAL(4,2),
    image_path VARCHAR(255),
    FOREIGN KEY(staff_ID) REFERENCES USER(user_ID)
                ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE SPECIALITY (
    spec_ID INT UNSIGNED NOT NULL AUTO_INCREMENT,
    value VARCHAR(255) NOT NULL UNIQUE,
    PRIMARY KEY(spec_ID)
);

CREATE TABLE HASSPECIALITY (
   spec_ID INT UNSIGNED NOT NULL,
   user_ID INT UNSIGNED NOT NULL,
   PRIMARY KEY(spec_ID, user_ID),
   FOREIGN KEY(user_ID) REFERENCES USER(user_ID)
                ON DELETE CASCADE ON UPDATE CASCADE,
   FOREIGN KEY(spec_ID) REFERENCES SPECIALITY(spec_ID)
);

-- Table representing ORGANISER. Similar to BARSTAFF but requires less information
CREATE TABLE ORGANISER (
    org_ID INT UNSIGNED NOT NULL,
    prof_pos VARCHAR(255),
    event_type VARCHAR(255),
    FOREIGN KEY(org_ID) REFERENCES USER(user_ID)
                ON DELETE CASCADE ON UPDATE CASCADE
);

-- Temporarily store users in this table until account is confirmed via email
CREATE TABLE REGISTRATION (
    email_address VARCHAR(255) NOT NULL,
    DOB DATE NOT NULL,
    account_type ENUM ('BARSTAFF', 'ORGANISER', 'ADMIN') NOT NULL,
    hash NVARCHAR(64) NOT NULL NOT NULL,
    token VARCHAR(64) NOT NULL NOT NULL,
    PRIMARY KEY (email_address)
);

-- Table filled with all active jobs. Private jobs = 1 cannot be seen on the adverts board
CREATE TABLE JOBS (
    job_ID INT UNSIGNED NOT NULL AUTO_INCREMENT,
    org_ID INT UNSIGNED NOT NULL,
    staff_ID INT UNSIGNED, 
    title VARCHAR(255),
    job_creation DATETIME NOT NULL DEFAULT NOW(),
    job_start DATETIME NOT NULL,
    job_end DATETIME NOT NULL,
    duration INT UNSIGNED NOT NULL,
    postcode VARCHAR(255) NOT NULL,
    street_name VARCHAR(255) NOT NULL,
    no INT UNSIGNED NOT NULL,
    city VARCHAR(255) NOT NULL,
    rate DECIMAL(8,2) NOT NULL,
    description VARCHAR(255),
    speciality ENUM('Bartender', 'Mixologist', 'Barback'),
    pay DECIMAL(8,2),
    type VARCHAR(255),
    private BOOLEAN NOT NULL,
    status ENUM('available', 'accepted', 'paid', 'ongoing', 'completed'),
    PRIMARY KEY(job_ID),
    FOREIGN KEY (org_ID) REFERENCES ORGANISER(org_ID)
                ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (staff_ID) REFERENCES BARSTAFF(staff_ID)
                ON DELETE CASCADE ON UPDATE CASCADE
);


-- Table stores ID of staff applying for a specific job.
CREATE TABLE APPLICANTS (
    job_ID INT UNSIGNED NOT NULL,
    staff_ID INT UNSIGNED NOT NULL,
    message VARCHAR(255) default NULL,
    accepted BOOLEAN default 0,
    PRIMARY KEY(job_ID, staff_ID),
    FOREIGN KEY (job_ID) REFERENCES JOBS(job_ID)
                ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (staff_ID) REFERENCES BARSTAFF(staff_ID)
                ON DELETE CASCADE ON UPDATE CASCADE
);

-- Table with availability of each member of barstaff. Will need Regex to process.
CREATE TABLE AVAILABILITY (
    staff_ID INT UNSIGNED NOT NULL,
    day ENUM('Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday') NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    PRIMARY KEY(staff_ID, day),
    FOREIGN KEY (staff_ID) REFERENCES BARSTAFF(staff_ID)
                ON DELETE CASCADE ON UPDATE CASCADE
);


-- Table to store favourite barstaff of each organiser, if they so wish
CREATE TABLE FAVOURITES (
    org_ID INT UNSIGNED,
    staff_ID INT UNSIGNED,
    PRIMARY KEY (org_ID, staff_ID),
    FOREIGN KEY (org_ID) REFERENCES ORGANISER(org_ID) 
                ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (staff_ID) REFERENCES BARSTAFF(staff_ID) 
                ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE CHATS (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    user_ID INT UNSIGNED NOT NULL,
	job_ID INT UNSIGNED NOT NULL,
	chat_message TEXT NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (job_ID) REFERENCES JOBS(job_ID) 
                ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (user_ID) REFERENCES USER(user_ID) 
                ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE CHATLOGS (
    sender INT UNSIGNED NOT NULL,
    chat_ID INT UNSIGNED NOT NULL,
    message_contents VARCHAR(255) NOT NULL,
    message_timestamp DATETIME NOT NULL DEFAULT NOW(),
    FOREIGN KEY (chat_ID) REFERENCES CHATS(chat_ID) 
                ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (sender) REFERENCES USER(user_ID) 
                ON DELETE CASCADE ON UPDATE CASCADE           
);

CREATE TABLE ONLINE_USERS(
    user_ID INT UNSIGNED NOT NULL,
    stripe_token VARCHAR(255),
    FOREIGN KEY (user_ID) REFERENCES USER(user_ID) 
                ON DELETE CASCADE ON UPDATE CASCADE
);


INSERT INTO USER(first_name, last_name, gender, DOB, email_address, mobile_number, postcode, city, town, hash, account_type, active) VALUES ("LeBron", "Smith", "F", "1989-5-9", "root@admin.com", "91123934567", "EC1A1LP", "city", "town", "test", "ADMIN","1");
INSERT INTO USER(first_name, last_name, gender, DOB, email_address, mobile_number, postcode, city, town, hash, account_type, active) VALUES ("Rihanna", "Williams", "M", "1903-10-3", "user2@cavs.com", "91123934567", "EC1A1UG", "city", "town", "hash2", "BARSTAFF","1");
INSERT INTO USER(first_name, last_name, gender, DOB, email_address, mobile_number, postcode, city, town, hash, account_type, active) VALUES ("Becky", "James", "F", "1960-1-24", "user3@cavs.com", "91123934567", "OX11AQ", "city", "town", "hash3", "BARSTAFF","1");
INSERT INTO USER(first_name, last_name, gender, DOB, email_address, mobile_number, postcode, city, town, hash, account_type, active) VALUES ("Rihanna", "Irving", "M", "1971-8-9", "user4@cavs.com", "91123934567", "OX11AQ", "city", "town", "hash4", "BARSTAFF","1");
INSERT INTO USER(first_name, last_name, gender, DOB, email_address, mobile_number, postcode, city, town, hash, account_type, active) VALUES ("Kevin", "Irving", "M", "1983-6-5", "user5@cavs.com", "91123934567", "EC1A2AR", "city", "town", "hash5", "BARSTAFF","1");
INSERT INTO USER(first_name, last_name, gender, DOB, email_address, mobile_number, postcode, city, town, hash, account_type, active) VALUES ("Jipesh", "Williams", "F", "1931-7-29", "user6@cavs.com", "91123934567", "EC1A1LP", "city", "town", "hash6", "BARSTAFF","0");
INSERT INTO USER(first_name, last_name, gender, DOB, email_address, mobile_number, postcode, city, town, hash, account_type, active) VALUES ("Kyrie", "Thompson", "M", "1947-6-3", "user7@cavs.com", "91123934567", "LE11ZQ", "city", "town", "hash7", "ORGANISER","1");
INSERT INTO USER(first_name, last_name, gender, DOB, email_address, mobile_number, postcode, city, town, hash, account_type, active) VALUES ("J.R", "Jefferson", "M", "1923-2-25", "user8@cavs.com", "91123934567", "TS11DD", "city", "town", "hash8", "ORGANISER","1");
INSERT INTO USER(first_name, last_name, gender, DOB, email_address, mobile_number, postcode, city, town, hash, account_type, active) VALUES ("Kevin", "Bogut", "F", "1999-12-29", "user9@cavs.com", "91123934567", "E10QH", "city", "town", "hash9", "ORGANISER","1");
INSERT INTO USER(first_name, last_name, gender, DOB, email_address, mobile_number, postcode, city, town, hash, account_type, active) VALUES ("James", "Smith", "M", "1946-7-23", "user10@cavs.com", "91123934567", "EC1A1UG", "city", "town", "hash10", "ORGANISER","1");
INSERT INTO USER(first_name, last_name, gender, DOB, email_address, mobile_number, postcode, city, town, hash, account_type, active) VALUES ("James", "Irving", "M", "1929-7-17", "user11@cavs.com", "91123934567", "TS11DD", "city", "town", "hash11", "ORGANISER","0");
INSERT INTO BARSTAFF(staff_ID, speciality, description, experience, proximity, hour_rate, night_rate, image_path) VALUES(2, "speciality", "description", "experience", 19, 15, 18, "image_path");
INSERT INTO BARSTAFF(staff_ID, speciality, description, experience, proximity, hour_rate, night_rate, image_path) VALUES(3, "speciality", "description", "experience", 42, 6, 2, "image_path");
INSERT INTO BARSTAFF(staff_ID, speciality, description, experience, proximity, hour_rate, night_rate, image_path) VALUES(4, "speciality", "description", "experience", 15, 23, 1, "image_path");
INSERT INTO BARSTAFF(staff_ID, speciality, description, experience, proximity, hour_rate, night_rate, image_path) VALUES(5, "speciality", "description", "experience", 30, 5, 16, "image_path");
INSERT INTO BARSTAFF(staff_ID, speciality, description, experience, proximity, hour_rate, night_rate, image_path) VALUES(6, "speciality", "description", "experience", 2, 8, 16, "image_path");
INSERT INTO AVAILABILITY(staff_ID, day, start_time, end_time) VALUES(2, "Monday", "1:00", "17:00");
INSERT INTO AVAILABILITY(staff_ID, day, start_time, end_time) VALUES(2, "Tuesday", "1:00", "15:00");
INSERT INTO AVAILABILITY(staff_ID, day, start_time, end_time) VALUES(2, "Wednesday", "1:00", "12:00");
INSERT INTO AVAILABILITY(staff_ID, day, start_time, end_time) VALUES(2, "Thursday", "10:00", "21:00");
INSERT INTO AVAILABILITY(staff_ID, day, start_time, end_time) VALUES(2, "Friday", "3:00", "23:00");
INSERT INTO AVAILABILITY(staff_ID, day, start_time, end_time) VALUES(2, "Saturday", "6:00", "19:00");
INSERT INTO AVAILABILITY(staff_ID, day, start_time, end_time) VALUES(2, "Sunday", "7:00", "13:00");
INSERT INTO AVAILABILITY(staff_ID, day, start_time, end_time) VALUES(3, "Monday", "6:00", "14:00");
INSERT INTO AVAILABILITY(staff_ID, day, start_time, end_time) VALUES(3, "Tuesday", "5:00", "14:00");
INSERT INTO AVAILABILITY(staff_ID, day, start_time, end_time) VALUES(3, "Wednesday", "11:00", "20:00");
INSERT INTO AVAILABILITY(staff_ID, day, start_time, end_time) VALUES(3, "Thursday", "10:00", "17:00");
INSERT INTO AVAILABILITY(staff_ID, day, start_time, end_time) VALUES(3, "Friday", "7:00", "23:00");
INSERT INTO AVAILABILITY(staff_ID, day, start_time, end_time) VALUES(3, "Saturday", "9:00", "19:00");
INSERT INTO AVAILABILITY(staff_ID, day, start_time, end_time) VALUES(3, "Sunday", "7:00", "16:00");
INSERT INTO AVAILABILITY(staff_ID, day, start_time, end_time) VALUES(4, "Monday", "3:00", "18:00");
INSERT INTO AVAILABILITY(staff_ID, day, start_time, end_time) VALUES(4, "Tuesday", "2:00", "23:00");
INSERT INTO AVAILABILITY(staff_ID, day, start_time, end_time) VALUES(4, "Wednesday", "8:00", "13:00");
INSERT INTO AVAILABILITY(staff_ID, day, start_time, end_time) VALUES(4, "Thursday", "5:00", "14:00");
INSERT INTO AVAILABILITY(staff_ID, day, start_time, end_time) VALUES(4, "Friday", "4:00", "13:00");
INSERT INTO AVAILABILITY(staff_ID, day, start_time, end_time) VALUES(4, "Saturday", "0:00", "20:00");
INSERT INTO AVAILABILITY(staff_ID, day, start_time, end_time) VALUES(4, "Sunday", "6:00", "17:00");
INSERT INTO AVAILABILITY(staff_ID, day, start_time, end_time) VALUES(5, "Monday", "0:00", "19:00");
INSERT INTO AVAILABILITY(staff_ID, day, start_time, end_time) VALUES(5, "Tuesday", "5:00", "15:00");
INSERT INTO AVAILABILITY(staff_ID, day, start_time, end_time) VALUES(5, "Wednesday", "11:00", "13:00");
INSERT INTO AVAILABILITY(staff_ID, day, start_time, end_time) VALUES(5, "Thursday", "0:00", "14:00");
INSERT INTO AVAILABILITY(staff_ID, day, start_time, end_time) VALUES(5, "Friday", "2:00", "12:00");
INSERT INTO AVAILABILITY(staff_ID, day, start_time, end_time) VALUES(5, "Saturday", "8:00", "13:00");
INSERT INTO AVAILABILITY(staff_ID, day, start_time, end_time) VALUES(5, "Sunday", "9:00", "19:00");
INSERT INTO AVAILABILITY(staff_ID, day, start_time, end_time) VALUES(6, "Monday", "2:00", "18:00");
INSERT INTO AVAILABILITY(staff_ID, day, start_time, end_time) VALUES(6, "Tuesday", "2:00", "13:00");
INSERT INTO AVAILABILITY(staff_ID, day, start_time, end_time) VALUES(6, "Wednesday", "10:00", "16:00");
INSERT INTO AVAILABILITY(staff_ID, day, start_time, end_time) VALUES(6, "Thursday", "0:00", "21:00");
INSERT INTO AVAILABILITY(staff_ID, day, start_time, end_time) VALUES(6, "Friday", "8:00", "18:00");
INSERT INTO AVAILABILITY(staff_ID, day, start_time, end_time) VALUES(6, "Saturday", "7:00", "15:00");
INSERT INTO AVAILABILITY(staff_ID, day, start_time, end_time) VALUES(6, "Sunday", "5:00", "13:00");
INSERT INTO ORGANISER(org_ID, prof_pos ) VALUES (7, "prof_pos");
INSERT INTO ORGANISER(org_ID, prof_pos ) VALUES (8, "prof_pos");
INSERT INTO ORGANISER(org_ID, prof_pos ) VALUES (9, "prof_pos");
INSERT INTO ORGANISER(org_ID, prof_pos ) VALUES (10, "prof_pos");
INSERT INTO ORGANISER(org_ID, prof_pos ) VALUES (11, "prof_pos");
INSERT INTO JOBS(org_ID, job_creation, job_start, job_end, title, description, type, duration, postcode, street_name, no, city, rate, speciality, private, status) VALUES (9, NOW(), NOW(), NOW(), "Restaurant/Bar manager", "Delivering great guest service by preparing and serving drinks at the bar and serving meals to the table. Must have a great personality and like to work as part...", "Wedding party", "89", "E10AN","street_name","85","Birmingham","92","Barback", "1", "available");
INSERT INTO JOBS(org_ID, job_creation, job_start, job_end, title, description, type, duration, postcode, street_name, no, city, rate, speciality, private, status) VALUES (11, NOW(), NOW(), NOW(), "Casual Bar Staff", "At JD Wetherspoon we like to keep it simple, our bar team members are the front line in our pubs for our customers, ensuring that everyone has the best...", "Jipesh house", "39", "E10QH","street_name","89","Stratford","15","Barback", "1", "available");
INSERT INTO JOBS(org_ID, job_creation, job_start, job_end, title, description, type, duration, postcode, street_name, no, city, rate, speciality, private, status) VALUES (11, NOW(), NOW(), NOW(), "Casual Bar Staff", "Delivering great guest service by preparing and serving drinks at the bar and serving meals to the table. Must have a great personality and like to work as part...", "Basketball event", "70", "TS11PT","street_name","24","Cheltenham","25","Bartender", "1", "available");
INSERT INTO JOBS(org_ID, job_creation, job_start, job_end, title, description, type, duration, postcode, street_name, no, city, rate, speciality, private, status) VALUES (10, NOW(), NOW(), NOW(), "Bar Staff / 6346 - The Granite City, Aberdeen Airport", "At JD Wetherspoon we like to keep it simple, our bar team members are the front line in our pubs for our customers, ensuring that everyone has the best...", "Basketball event", "50", "EC1A1LP","street_name","84","Cheltenham","49","Mixologist", "0", "available");
INSERT INTO JOBS(org_ID, job_creation, job_start, job_end, title, description, type, duration, postcode, street_name, no, city, rate, speciality, private, status) VALUES (8, NOW(), NOW(), NOW(), "Restaurant/Bar manager", "At JD Wetherspoon we like to keep it simple, our bar team members are the front line in our pubs for our customers, ensuring that everyone has the best...", "Birthday party", "33", "E10AN","street_name","41","Luton","13","Mixologist", "0", "available");
INSERT INTO JOBS(org_ID, job_creation, job_start, job_end, title, description, type, duration, postcode, street_name, no, city, rate, speciality, private, status) VALUES (11, NOW(), NOW(), NOW(), "Casual Bar Staff", "Delivering great guest service by preparing and serving drinks at the bar and serving meals to the table. Must have a great personality and like to work as part...", "Birthday party", "80", "SW161AT","street_name","23","Manchester","90","Mixologist", "0", "available");
INSERT INTO JOBS(org_ID, job_creation, job_start, job_end, title, description, type, duration, postcode, street_name, no, city, rate, speciality, private, status) VALUES (9, NOW(), NOW(), NOW(), "Casual Bar Staff", "The Barbican Bars are looking to bring in a number of enthusiastic staff on a casual basis to provide support to the Bar Managers & Supervisors in the running...", "Yuzuka", "35", "TS11PT","street_name","67","Manchester","23","Barback", "1", "available");
INSERT INTO JOBS(org_ID, job_creation, job_start, job_end, title, description, type, duration, postcode, street_name, no, city, rate, speciality, private, status) VALUES (11, NOW(), NOW(), NOW(), "Casual Bar Staff", "At JD Wetherspoon we like to keep it simple, our bar team members are the front line in our pubs for our customers, ensuring that everyone has the best...", "Yuzuka", "51", "EC1A2AR","street_name","29","Stratford","75","Mixologist", "1", "available");
INSERT INTO JOBS(org_ID, job_creation, job_start, job_end, title, description, type, duration, postcode, street_name, no, city, rate, speciality, private, status) VALUES (10, NOW(), NOW(), NOW(), "Casual Bar Staff", "The Barbican Bars are looking to bring in a number of enthusiastic staff on a casual basis to provide support to the Bar Managers & Supervisors in the running...", "Wedding party", "74", "AB101AA","street_name","24","East London","73","Bartender", "1", "available");
INSERT INTO JOBS(org_ID, job_creation, job_start, job_end, title, description, type, duration, postcode, street_name, no, city, rate, speciality, private, status) VALUES (11, NOW(), NOW(), NOW(), "Part-Time Bar Assistant", "Delivering great guest service by preparing and serving drinks at the bar and serving meals to the table. Must have a great personality and like to work as part...", "Yuzuka", "64", "WD11AA","street_name","29","London","67","Bartender", "1", "available");
INSERT INTO JOBS(org_ID, job_creation, job_start, job_end, title, description, type, duration, postcode, street_name, no, city, rate, speciality, private, status) VALUES (10, NOW(), NOW(), NOW(), "Part-Time Bar Assistant", "At JD Wetherspoon we like to keep it simple, our bar team members are the front line in our pubs for our customers, ensuring that everyone has the best...", "Wedding party", "10", "TS11DD","street_name","69","Liverpool","85","Bartender", "1", "available");
INSERT INTO JOBS(org_ID, job_creation, job_start, job_end, title, description, type, duration, postcode, street_name, no, city, rate, speciality, private, status) VALUES (7, NOW(), NOW(), NOW(), "Part-Time Bar Assistant", "At JD Wetherspoon we like to keep it simple, our bar team members are the front line in our pubs for our customers, ensuring that everyone has the best...", "Basketball event", "99", "E10HZ","street_name","36","Manchester","97","Bartender", "1", "available");
INSERT INTO JOBS(org_ID, job_creation, job_start, job_end, title, description, type, duration, postcode, street_name, no, city, rate, speciality, private, status) VALUES (7, NOW(), NOW(), NOW(), "Bar Staff / 6346 - The Granite City, Aberdeen Airport", "At JD Wetherspoon we like to keep it simple, our bar team members are the front line in our pubs for our customers, ensuring that everyone has the best...", "Birthday party", "39", "SW161AT","street_name","22","Birmingham","50","Bartender", "1", "available");
INSERT INTO JOBS(org_ID, job_creation, job_start, job_end, title, description, type, duration, postcode, street_name, no, city, rate, speciality, private, status) VALUES (10, NOW(), NOW(), NOW(), "Casual Bar Staff", "At JD Wetherspoon we like to keep it simple, our bar team members are the front line in our pubs for our customers, ensuring that everyone has the best...", "Birthday party", "25", "EC1A1NE","street_name","69","Luton","55","Bartender", "0", "available");
INSERT INTO JOBS(org_ID, job_creation, job_start, job_end, title, description, type, duration, postcode, street_name, no, city, rate, speciality, private, status) VALUES (8, NOW(), NOW(), NOW(), "Restaurant/Bar manager", "Bar Staff role:. What we look for in our Bar Staff:. As a member of Bar Staff in our business you will play a key role in providing amazing experiences for our...", "Basketball event", "33", "EC1A1LP","street_name","78","London","67","Mixologist", "0", "available");
INSERT INTO JOBS(org_ID, job_creation, job_start, job_end, title, description, type, duration, postcode, street_name, no, city, rate, speciality, private, status) VALUES (11, NOW(), NOW(), NOW(), "Part-Time Bar Assistant", "Delivering great guest service by preparing and serving drinks at the bar and serving meals to the table. Must have a great personality and like to work as part...", "Jipesh house", "53", "LE11ZQ","street_name","93","Liverpool","46","Mixologist", "1", "available");
INSERT INTO JOBS(org_ID, job_creation, job_start, job_end, title, description, type, duration, postcode, street_name, no, city, rate, speciality, private, status) VALUES (8, NOW(), NOW(), NOW(), "Casual Bar Staff", "The Barbican Bars are looking to bring in a number of enthusiastic staff on a casual basis to provide support to the Bar Managers & Supervisors in the running...", "Yuzuka", "79", "SW161AT","street_name","61","Birmingham","4","Bartender", "0", "available");
INSERT INTO JOBS(org_ID, job_creation, job_start, job_end, title, description, type, duration, postcode, street_name, no, city, rate, speciality, private, status) VALUES (10, NOW(), NOW(), NOW(), "Casual Bar Staff", "The Barbican Bars are looking to bring in a number of enthusiastic staff on a casual basis to provide support to the Bar Managers & Supervisors in the running...", "Yuzuka", "49", "OX11AQ","street_name","7","London","27","Barback", "1", "available");
INSERT INTO JOBS(org_ID, job_creation, job_start, job_end, title, description, type, duration, postcode, street_name, no, city, rate, speciality, private, status) VALUES (7, NOW(), NOW(), NOW(), "Casual Bar Staff", "Bar Staff role:. What we look for in our Bar Staff:. As a member of Bar Staff in our business you will play a key role in providing amazing experiences for our...", "Birthday party", "9", "EC1A1LP","street_name","78","Stratford","74","Mixologist", "1", "available");
INSERT INTO JOBS(org_ID, job_creation, job_start, job_end, title, description, type, duration, postcode, street_name, no, city, rate, speciality, private, status) VALUES (10, NOW(), NOW(), NOW(), "Part-Time Bar Assistant", "At JD Wetherspoon we like to keep it simple, our bar team members are the front line in our pubs for our customers, ensuring that everyone has the best...", "Jipesh house", "49", "SW161AT","street_name","52","Luton","1","Mixologist", "0", "available");
UPDATE JOBS SET job_creation = (SELECT DATE_SUB(DATE(NOW()), INTERVAL 19 DAY)) WHERE job_ID=1;
UPDATE JOBS SET job_start = (SELECT DATE_ADD((SELECT DATE_SUB(DATE(NOW()), INTERVAL 19 DAY)), INTERVAL 0 DAY)) WHERE job_ID=1;
UPDATE JOBS SET job_end = (SELECT DATE_ADD((SELECT DATE_ADD((SELECT DATE_SUB(DATE(NOW()), INTERVAL 19 DAY)), INTERVAL 0 DAY)), INTERVAL 0 DAY)) WHERE job_ID=1;
UPDATE JOBS SET job_creation = (SELECT DATE_SUB(DATE(NOW()), INTERVAL 43 DAY)) WHERE job_ID=2;
UPDATE JOBS SET job_start = (SELECT DATE_ADD((SELECT DATE_SUB(DATE(NOW()), INTERVAL 43 DAY)), INTERVAL 0 DAY)) WHERE job_ID=2;
UPDATE JOBS SET job_end = (SELECT DATE_ADD((SELECT DATE_ADD((SELECT DATE_SUB(DATE(NOW()), INTERVAL 43 DAY)), INTERVAL 0 DAY)), INTERVAL 0 DAY)) WHERE job_ID=2;
UPDATE JOBS SET job_creation = (SELECT DATE_SUB(DATE(NOW()), INTERVAL 21 DAY)) WHERE job_ID=3;
UPDATE JOBS SET job_start = (SELECT DATE_ADD((SELECT DATE_SUB(DATE(NOW()), INTERVAL 21 DAY)), INTERVAL 2 DAY)) WHERE job_ID=3;
UPDATE JOBS SET job_end = (SELECT DATE_ADD((SELECT DATE_ADD((SELECT DATE_SUB(DATE(NOW()), INTERVAL 21 DAY)), INTERVAL 2 DAY)), INTERVAL 2 DAY)) WHERE job_ID=3;
UPDATE JOBS SET job_creation = (SELECT DATE_SUB(DATE(NOW()), INTERVAL 30 DAY)) WHERE job_ID=4;
UPDATE JOBS SET job_start = (SELECT DATE_ADD((SELECT DATE_SUB(DATE(NOW()), INTERVAL 30 DAY)), INTERVAL 0 DAY)) WHERE job_ID=4;
UPDATE JOBS SET job_end = (SELECT DATE_ADD((SELECT DATE_ADD((SELECT DATE_SUB(DATE(NOW()), INTERVAL 30 DAY)), INTERVAL 0 DAY)), INTERVAL 0 DAY)) WHERE job_ID=4;
UPDATE JOBS SET job_creation = (SELECT DATE_SUB(DATE(NOW()), INTERVAL 3 DAY)) WHERE job_ID=5;
UPDATE JOBS SET job_start = (SELECT DATE_ADD((SELECT DATE_SUB(DATE(NOW()), INTERVAL 3 DAY)), INTERVAL 0 DAY)) WHERE job_ID=5;
UPDATE JOBS SET job_end = (SELECT DATE_ADD((SELECT DATE_ADD((SELECT DATE_SUB(DATE(NOW()), INTERVAL 3 DAY)), INTERVAL 0 DAY)), INTERVAL 0 DAY)) WHERE job_ID=5;
UPDATE JOBS SET job_creation = (SELECT DATE_SUB(DATE(NOW()), INTERVAL 12 DAY)) WHERE job_ID=6;
UPDATE JOBS SET job_start = (SELECT DATE_ADD((SELECT DATE_SUB(DATE(NOW()), INTERVAL 12 DAY)), INTERVAL 2 DAY)) WHERE job_ID=6;
UPDATE JOBS SET job_end = (SELECT DATE_ADD((SELECT DATE_ADD((SELECT DATE_SUB(DATE(NOW()), INTERVAL 12 DAY)), INTERVAL 2 DAY)), INTERVAL 2 DAY)) WHERE job_ID=6;
UPDATE JOBS SET job_creation = (SELECT DATE_SUB(DATE(NOW()), INTERVAL 6 DAY)) WHERE job_ID=7;
UPDATE JOBS SET job_start = (SELECT DATE_ADD((SELECT DATE_SUB(DATE(NOW()), INTERVAL 6 DAY)), INTERVAL 1 DAY)) WHERE job_ID=7;
UPDATE JOBS SET job_end = (SELECT DATE_ADD((SELECT DATE_ADD((SELECT DATE_SUB(DATE(NOW()), INTERVAL 6 DAY)), INTERVAL 1 DAY)), INTERVAL 1 DAY)) WHERE job_ID=7;
UPDATE JOBS SET job_creation = (SELECT DATE_SUB(DATE(NOW()), INTERVAL 46 DAY)) WHERE job_ID=8;
UPDATE JOBS SET job_start = (SELECT DATE_ADD((SELECT DATE_SUB(DATE(NOW()), INTERVAL 46 DAY)), INTERVAL 2 DAY)) WHERE job_ID=8;
UPDATE JOBS SET job_end = (SELECT DATE_ADD((SELECT DATE_ADD((SELECT DATE_SUB(DATE(NOW()), INTERVAL 46 DAY)), INTERVAL 2 DAY)), INTERVAL 2 DAY)) WHERE job_ID=8;
UPDATE JOBS SET job_creation = (SELECT DATE_SUB(DATE(NOW()), INTERVAL 37 DAY)) WHERE job_ID=9;
UPDATE JOBS SET job_start = (SELECT DATE_ADD((SELECT DATE_SUB(DATE(NOW()), INTERVAL 37 DAY)), INTERVAL 1 DAY)) WHERE job_ID=9;
UPDATE JOBS SET job_end = (SELECT DATE_ADD((SELECT DATE_ADD((SELECT DATE_SUB(DATE(NOW()), INTERVAL 37 DAY)), INTERVAL 1 DAY)), INTERVAL 1 DAY)) WHERE job_ID=9;
UPDATE JOBS SET job_creation = (SELECT DATE_SUB(DATE(NOW()), INTERVAL 14 DAY)) WHERE job_ID=10;
UPDATE JOBS SET job_start = (SELECT DATE_ADD((SELECT DATE_SUB(DATE(NOW()), INTERVAL 14 DAY)), INTERVAL 1 DAY)) WHERE job_ID=10;
UPDATE JOBS SET job_end = (SELECT DATE_ADD((SELECT DATE_ADD((SELECT DATE_SUB(DATE(NOW()), INTERVAL 14 DAY)), INTERVAL 1 DAY)), INTERVAL 1 DAY)) WHERE job_ID=10;
UPDATE JOBS SET job_creation = (SELECT DATE_SUB(DATE(NOW()), INTERVAL 1 DAY)) WHERE job_ID=11;
UPDATE JOBS SET job_start = (SELECT DATE_ADD((SELECT DATE_SUB(DATE(NOW()), INTERVAL 1 DAY)), INTERVAL 2 DAY)) WHERE job_ID=11;
UPDATE JOBS SET job_end = (SELECT DATE_ADD((SELECT DATE_ADD((SELECT DATE_SUB(DATE(NOW()), INTERVAL 1 DAY)), INTERVAL 2 DAY)), INTERVAL 2 DAY)) WHERE job_ID=11;
UPDATE JOBS SET job_creation = (SELECT DATE_SUB(DATE(NOW()), INTERVAL 41 DAY)) WHERE job_ID=12;
UPDATE JOBS SET job_start = (SELECT DATE_ADD((SELECT DATE_SUB(DATE(NOW()), INTERVAL 41 DAY)), INTERVAL 1 DAY)) WHERE job_ID=12;
UPDATE JOBS SET job_end = (SELECT DATE_ADD((SELECT DATE_ADD((SELECT DATE_SUB(DATE(NOW()), INTERVAL 41 DAY)), INTERVAL 1 DAY)), INTERVAL 1 DAY)) WHERE job_ID=12;
UPDATE JOBS SET job_creation = (SELECT DATE_SUB(DATE(NOW()), INTERVAL 12 DAY)) WHERE job_ID=13;
UPDATE JOBS SET job_start = (SELECT DATE_ADD((SELECT DATE_SUB(DATE(NOW()), INTERVAL 12 DAY)), INTERVAL 0 DAY)) WHERE job_ID=13;
UPDATE JOBS SET job_end = (SELECT DATE_ADD((SELECT DATE_ADD((SELECT DATE_SUB(DATE(NOW()), INTERVAL 12 DAY)), INTERVAL 0 DAY)), INTERVAL 0 DAY)) WHERE job_ID=13;
UPDATE JOBS SET job_creation = (SELECT DATE_SUB(DATE(NOW()), INTERVAL 43 DAY)) WHERE job_ID=14;
UPDATE JOBS SET job_start = (SELECT DATE_ADD((SELECT DATE_SUB(DATE(NOW()), INTERVAL 43 DAY)), INTERVAL 1 DAY)) WHERE job_ID=14;
UPDATE JOBS SET job_end = (SELECT DATE_ADD((SELECT DATE_ADD((SELECT DATE_SUB(DATE(NOW()), INTERVAL 43 DAY)), INTERVAL 1 DAY)), INTERVAL 1 DAY)) WHERE job_ID=14;
UPDATE JOBS SET job_creation = (SELECT DATE_SUB(DATE(NOW()), INTERVAL 2 DAY)) WHERE job_ID=15;
UPDATE JOBS SET job_start = (SELECT DATE_ADD((SELECT DATE_SUB(DATE(NOW()), INTERVAL 2 DAY)), INTERVAL 0 DAY)) WHERE job_ID=15;
UPDATE JOBS SET job_end = (SELECT DATE_ADD((SELECT DATE_ADD((SELECT DATE_SUB(DATE(NOW()), INTERVAL 2 DAY)), INTERVAL 0 DAY)), INTERVAL 0 DAY)) WHERE job_ID=15;
UPDATE JOBS SET job_creation = (SELECT DATE_SUB(DATE(NOW()), INTERVAL 31 DAY)) WHERE job_ID=16;
UPDATE JOBS SET job_start = (SELECT DATE_ADD((SELECT DATE_SUB(DATE(NOW()), INTERVAL 31 DAY)), INTERVAL 2 DAY)) WHERE job_ID=16;
UPDATE JOBS SET job_end = (SELECT DATE_ADD((SELECT DATE_ADD((SELECT DATE_SUB(DATE(NOW()), INTERVAL 31 DAY)), INTERVAL 2 DAY)), INTERVAL 2 DAY)) WHERE job_ID=16;
UPDATE JOBS SET job_creation = (SELECT DATE_SUB(DATE(NOW()), INTERVAL 19 DAY)) WHERE job_ID=17;
UPDATE JOBS SET job_start = (SELECT DATE_ADD((SELECT DATE_SUB(DATE(NOW()), INTERVAL 19 DAY)), INTERVAL 2 DAY)) WHERE job_ID=17;
UPDATE JOBS SET job_end = (SELECT DATE_ADD((SELECT DATE_ADD((SELECT DATE_SUB(DATE(NOW()), INTERVAL 19 DAY)), INTERVAL 2 DAY)), INTERVAL 2 DAY)) WHERE job_ID=17;
UPDATE JOBS SET job_creation = (SELECT DATE_SUB(DATE(NOW()), INTERVAL 50 DAY)) WHERE job_ID=18;
UPDATE JOBS SET job_start = (SELECT DATE_ADD((SELECT DATE_SUB(DATE(NOW()), INTERVAL 50 DAY)), INTERVAL 1 DAY)) WHERE job_ID=18;
UPDATE JOBS SET job_end = (SELECT DATE_ADD((SELECT DATE_ADD((SELECT DATE_SUB(DATE(NOW()), INTERVAL 50 DAY)), INTERVAL 1 DAY)), INTERVAL 1 DAY)) WHERE job_ID=18;
UPDATE JOBS SET job_creation = (SELECT DATE_SUB(DATE(NOW()), INTERVAL 11 DAY)) WHERE job_ID=19;
UPDATE JOBS SET job_start = (SELECT DATE_ADD((SELECT DATE_SUB(DATE(NOW()), INTERVAL 11 DAY)), INTERVAL 2 DAY)) WHERE job_ID=19;
UPDATE JOBS SET job_end = (SELECT DATE_ADD((SELECT DATE_ADD((SELECT DATE_SUB(DATE(NOW()), INTERVAL 11 DAY)), INTERVAL 2 DAY)), INTERVAL 2 DAY)) WHERE job_ID=19;
UPDATE JOBS SET job_creation = (SELECT DATE_SUB(DATE(NOW()), INTERVAL 48 DAY)) WHERE job_ID=20;
UPDATE JOBS SET job_start = (SELECT DATE_ADD((SELECT DATE_SUB(DATE(NOW()), INTERVAL 48 DAY)), INTERVAL 0 DAY)) WHERE job_ID=20;
UPDATE JOBS SET job_end = (SELECT DATE_ADD((SELECT DATE_ADD((SELECT DATE_SUB(DATE(NOW()), INTERVAL 48 DAY)), INTERVAL 0 DAY)), INTERVAL 0 DAY)) WHERE job_ID=20;
INSERT INTO APPLICANTS(job_ID, staff_ID, message) VALUES(6, 2, "message");
INSERT INTO APPLICANTS(job_ID, staff_ID, message) VALUES(18, 3, "message");
INSERT INTO APPLICANTS(job_ID, staff_ID, message) VALUES(17, 2, "message");
INSERT INTO APPLICANTS(job_ID, staff_ID, message) VALUES(18, 2, "message");
INSERT INTO APPLICANTS(job_ID, staff_ID, message) VALUES(3, 4, "message");
INSERT INTO APPLICANTS(job_ID, staff_ID, message) VALUES(7, 5, "message");
INSERT INTO APPLICANTS(job_ID, staff_ID, message) VALUES(17, 4, "message");
INSERT INTO APPLICANTS(job_ID, staff_ID, message) VALUES(13, 6, "message");
INSERT INTO APPLICANTS(job_ID, staff_ID, message) VALUES(14, 4, "message");
INSERT INTO APPLICANTS(job_ID, staff_ID, message) VALUES(18, 3, "message");
INSERT INTO APPLICANTS(job_ID, staff_ID, message) VALUES(20, 5, "message");
INSERT INTO APPLICANTS(job_ID, staff_ID, message) VALUES(3, 2, "message");
INSERT INTO APPLICANTS(job_ID, staff_ID, message) VALUES(2, 6, "message");
INSERT INTO APPLICANTS(job_ID, staff_ID, message) VALUES(8, 5, "message");
INSERT INTO APPLICANTS(job_ID, staff_ID, message) VALUES(3, 6, "message");
INSERT INTO APPLICANTS(job_ID, staff_ID, message) VALUES(11, 4, "message");
INSERT INTO APPLICANTS(job_ID, staff_ID, message) VALUES(4, 6, "message");
INSERT INTO APPLICANTS(job_ID, staff_ID, message) VALUES(13, 4, "message");
INSERT INTO APPLICANTS(job_ID, staff_ID, message) VALUES(14, 6, "message");
INSERT INTO APPLICANTS(job_ID, staff_ID, message) VALUES(5, 3, "message");
