CREATE SEQUENCE job_id_seq;
CREATE TABLE job (
  id    INTEGER DEFAULT nextval('job_id_seq'),
  title VARCHAR(200)
);

CREATE SEQUENCE duty_id_seq;
CREATE TABLE duty (
  id     INTEGER DEFAULT nextval('duty_id_seq') PRIMARY KEY,
  name   VARCHAR(200)
);

CREATE SEQUENCE job_duties_id_seq;
CREATE TABLE job_duties (
  id     INTEGER DEFAULT nextval('job_duties_id_seq') PRIMARY KEY,
  job_id integer not null REFERENCES job(id),
  duty_id integer not null REFERENCES duty(id)
);

CREATE SEQUENCE person_id_seq;
CREATE TABLE person (
  id             INTEGER DEFAULT nextval('person_id_seq'),
  name           VARCHAR(200),
  age            SMALLINT,
  current_job_id INTEGER NOT NULL REFERENCES job (id)
);


