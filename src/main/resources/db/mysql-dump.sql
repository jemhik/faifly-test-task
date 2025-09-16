-- MySQL 8 dump for faifly-test-task (schema + minimal sample data)

DROP TABLE IF EXISTS visit;
DROP TABLE IF EXISTS doctor;
DROP TABLE IF EXISTS patient;

CREATE TABLE doctor (
  id BIGINT NOT NULL AUTO_INCREMENT,
  first_name VARCHAR(255) NOT NULL,
  last_name VARCHAR(255) NOT NULL,
  timezone VARCHAR(255) NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE patient (
  id BIGINT NOT NULL AUTO_INCREMENT,
  first_name VARCHAR(255) NOT NULL,
  last_name VARCHAR(255) NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE visit (
  id BIGINT NOT NULL AUTO_INCREMENT,
  start_utc TIMESTAMP NOT NULL,
  end_utc TIMESTAMP NOT NULL,
  patient_id BIGINT NOT NULL,
  doctor_id BIGINT NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_visit_patient FOREIGN KEY (patient_id) REFERENCES patient(id),
  CONSTRAINT fk_visit_doctor FOREIGN KEY (doctor_id) REFERENCES doctor(id),
  INDEX idx_visit_patient (patient_id),
  INDEX idx_visit_doctor_time (doctor_id, start_utc, end_utc)
) ENGINE=InnoDB;

-- Minimal sample data
INSERT INTO doctor (first_name, last_name, timezone) VALUES
 ('John','Doe','Europe/Berlin'),
 ('Anna','Smith','America/New_York');

INSERT INTO patient (first_name, last_name) VALUES
 ('Alice','Brown'),
 ('Bob','Johnson');

-- Visits (stored in UTC)
-- Alice visits John on 2025-01-10 10:00-10:30 Europe/Berlin
INSERT INTO visit (start_utc, end_utc, patient_id, doctor_id) VALUES
 (TIMESTAMP('2025-01-10 09:00:00'), TIMESTAMP('2025-01-10 09:30:00'), 1, 1);
-- Bob visits Anna on 2025-02-15 14:00-15:00 America/New_York
INSERT INTO visit (start_utc, end_utc, patient_id, doctor_id) VALUES
 (TIMESTAMP('2025-02-15 19:00:00'), TIMESTAMP('2025-02-15 20:00:00'), 2, 2);


-- Performance indexes for faster queries on large datasets
CREATE INDEX IF NOT EXISTS idx_patient_first_name ON patient(first_name);
CREATE INDEX IF NOT EXISTS idx_patient_last_name ON patient(last_name);
CREATE INDEX IF NOT EXISTS idx_visit_patient_doctor_start ON visit(patient_id, doctor_id, start_utc);
