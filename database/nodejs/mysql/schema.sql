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
    hash NVARCHAR(64) NOT NULL,
    active BOOLEAN default 0,
    PRIMARY KEY(user_ID, email_address)
);

CREATE TABLE TEMPUSER (
    email_address VARCHAR(255) NOT NULL,
    DOB DATE NOT NULL,
    account_type ENUM ('BARSTAFF', 'ORGANISER', 'ADMIN') NOT NULL,
    hash NVARCHAR(64) NOT NULL NOT NULL,
    token VARCHAR(64) NOT NULL NOT NULL
);

-- Table representing barstaff. Stores all required data.
CREATE TABLE BARSTAFF (
    staff_ID INT UNSIGNED NOT NULL,
    type ENUM('Bartender', 'Mixologist', 'Barback'),
    speciality VARCHAR(255),
    description VARCHAR(255),
    proximity INT,
    hour_rate DECIMAL(4,2),
    night_rate DECIMAL(4,2),
    image_path VARCHAR(255),
    FOREIGN KEY(staff_ID) REFERENCES USER(user_ID)
                ON DELETE CASCADE ON UPDATE CASCADE
);

-- Table representing ORGANISER. Similar to BARSTAFF but requires less information
CREATE TABLE ORGANISER (
    org_ID INT UNSIGNED NOT NULL,
    org_name VARCHAR(255),
    prof_pos VARCHAR(255),
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
    jobCreation DATETIME NOT NULL DEFAULT NOW(),
    jobStart DATETIME NOT NULL,
    jobEnd DATETIME NOT NULL,
    duration INT UNSIGNED NOT NULL,
    location VARCHAR(255) NOT NULL,
    rate DECIMAL(4,2) NOT NULL,
    description VARCHAR(255),
    speciality VARCHAR(255),
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
    PRIMARY KEY(job_ID, staff_ID),
    FOREIGN KEY (job_ID) REFERENCES JOBS(job_ID)
                ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (staff_ID) REFERENCES BARSTAFF(staff_ID)
                ON DELETE CASCADE ON UPDATE CASCADE
);

-- Table with availability of each member of barstaff. Will need Regex to process.
CREATE TABLE AVAILABILITY (
    cal_ID INT UNSIGNED NOT NULL AUTO_INCREMENT,
    staff_ID INT UNSIGNED NOT NULL,
    day ENUM('Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday') NOT NULL,
    startTime TIME NOT NULL,
    endTime TIME NOT NULL,
    PRIMARY KEY (cal_ID),
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
    chat_ID INT UNSIGNED NOT NULL AUTO_INCREMENT,
    org_ID INT UNSIGNED NOT NULL,
    staff_ID INT UNSIGNED NOT NULL,
    job_ID INT UNSIGNED NOT NULL,
    PRIMARY KEY (chat_ID),
    FOREIGN KEY (job_ID) REFERENCES JOBS(job_ID) 
                ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (org_ID) REFERENCES ORGANISER(org_ID) 
                ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (staff_ID) REFERENCES BARSTAFF(staff_ID) 
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


-- INSERT INTO USER(email_address, hash, DOB, account_type) VALUES('cle@cavs.com', 'sdsd', '1984-12-30','ORGANISER'); 
  
-- INSERT INTO USER(email_address, hash, DOB, account_type) VALUES('cle3@cavs.com', 'sdd', '1988-05-09','ORGANISER');
 
-- INSERT INTO USER(email_address, hash, DOB, account_type) VALUES('cle2@cavs.com', 'sfjdd', '1992-01-12','ORGANISER');
 
-- INSERT INTO USER(email_address, hash, DOB, account_type) VALUES('buce@cavs.com', 'poo', '1994-04-18','BARSTAFF'); 
 
-- INSERT INTO USER(email_address, hash, DOB, account_type) VALUES('sout@cavs.com', 'keyy', '1956-11-30','BARSTAFF'); 

-- INSERT INTO USER(email_address, hash, DOB, account_type) VALUES('the_ring@cavs.com', 'dager', '1995-11-20','BARSTAFF'); 
                                                                 
-- UPDATE USER SET first_name = "LeBron", last_name ="James", gender = "M", mobile_number = "01234292834", postcode = "jdjdjdj", active = 1 WHERE user_ID = 1;
-- UPDATE USER SET first_name = "Kendis", last_name ="Parker", gender = "F", mobile_number = "0123849534", postcode = "jdjdjdj", active = 1 WHERE user_ID = 2;
-- UPDATE USER SET first_name = "Kyrie", last_name ="Irving", gender = "M", mobile_number = "01234288834", postcode = "jdjdjdj", active = 1 WHERE user_ID = 3;
-- UPDATE USER SET first_name = "Kevin", last_name ="Love", gender = "M", mobile_number = "01234292834", postcode = "jdjdjdj", active = 1 WHERE user_ID = 4;
-- UPDATE USER SET first_name = "Easy", last_name ="Ball", gender = "F", mobile_number = "01234292834", postcode = "jdjdjdj", active = 1 WHERE user_ID = 5;
-- -- UPDATE USER SET first_name = "Bryan", last_name ="Fiore", gender = "M", mobile_number = "09283745832", postcode = "jdjdjdj", active = 0 WHERE user_ID = 6;
 
-- INSERT INTO BARSTAFF(staff_ID, type, speciality, description, proximity, hour_rate, night_rate, image_path) VALUES (4, "Mixologist", "Cooking", "okay easy ball", 90, 11, 12, "somewhere");
-- INSERT INTO BARSTAFF(staff_ID, type, speciality, description, proximity, hour_rate, night_rate, image_path) VALUES (5, "Barback", "Desscrig", "mmmhhh", 18, 23, 19, "somewhere here");
-- INSERT INTO BARSTAFF(staff_ID) VALUES (6);

-- INSERT INTO ORGANISER(org_ID, prof_pos) VALUES(1, "Outstading numbers");
-- INSERT INTO ORGANISER(org_ID, prof_pos) VALUES(2, "Averaging good");
-- INSERT INTO ORGANISER(org_ID, prof_pos) VALUES(3, "Real rookie of the year");
  
-- INSERT INTO JOBS(org_ID, staff_ID, jobDateTime, duration, location, rate, speciality, private, status) VALUES(1,NULL,"1922-09-02", 23, "Jipesh house", 34.21,"special", 1, "available");
-- INSERT INTO JOBS(org_ID, staff_ID, jobDateTime, duration, location, rate, speciality, private, status) VALUES(2,NULL,"1942-12-02", 223, "Nothing", 34.21, "special",0, "available");
-- INSERT INTO JOBS(org_ID, staff_ID, jobDateTime, duration, location, rate, speciality, private, status) VALUES(1,NULL,"1925-09-02", 123, "Myabe", 2.1, "special", 0, "available");
-- INSERT INTO JOBS(org_ID, staff_ID, jobDateTime, duration, location, rate, speciality, private, status) VALUES(3,NULL,"1965-02-02", 2, "Jipesh why Im", 3.21, "special", 1, "available");
-- INSERT INTO JOBS(org_ID, staff_ID, jobDateTime, duration, location, rate, speciality, private, status) VALUES(2,NULL,"1999-11-02", 3, "writing actuall", 2.23, "special", 0, "available");
-- INSERT INTO JOBS(org_ID, staff_ID, jobDateTime, duration, location, rate, speciality, private, status) VALUES(1,NULL,"1925-12-02", 0, "names", 9.23, "special", 0, "available");
-- INSERT INTO JOBS(org_ID, staff_ID, jobDateTime, duration, location, rate, speciality, private, status) VALUES(3,NULL,"1925-12-02", 0, "Private jobs", 34.2, "bs", 1, "available");

-- INSERT INTO APPLICANTS(job_ID, staff_ID, message) VALUES (1,4, "Full covever");
-- INSERT INTO APPLICANTS(job_ID, staff_ID, message) VALUES (7,4, "To accept cool");
-- INSERT INTO APPLICANTS(job_ID, staff_ID, message) VALUES (2,4, "LeBron is quite good");
-- INSERT INTO APPLICANTS(job_ID, staff_ID, message) VALUES (1,5, "Do you know about Kyrie");
-- INSERT INTO APPLICANTS(job_ID, staff_ID, message) VALUES (4,4, "Don't reach young blood");
-- INSERT INTO APPLICANTS(job_ID, staff_ID, message) VALUES (2,5, "Is all about buckets");
-- INSERT INTO APPLICANTS(job_ID, staff_ID, message) VALUES (3,4, "Give me that, that is mine");
-- INSERT INTO APPLICANTS(job_ID, staff_ID, message) VALUES (4,5, "I am the best");
-- INSERT INTO APPLICANTS(job_ID, staff_ID, message) VALUES (6,5, "The message");





-- From(user_id)
-- To(user_id)
-- Messag(text)
-- DatTime(DateTime)

