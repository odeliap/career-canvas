-- !Ups

create type job_status as enum('NotSubmitted', 'Submitted', 'InterviewScheduled', 'Interviewed', 'OfferMade', 'Rejected');
create type application_file as enum('CoverLetter', 'Response');

create table if not exists user_info (
    id                  serial                  primary key,
    email               varchar(320)            not null unique,
    password            varchar(255)            not null,
    full_name           varchar(255)            not null,
    last_login          timestamp,
    linkedin            varchar(255),
    github              varchar(255),
    website             varchar(255)
);

create table if not exists job_statuses (
    user_id             serial                  not null references user_info(id),
    job_id              serial                  not null unique,
    company             varchar(255)            not null,
    job_title           varchar(255)            not null,
    posting_url         varchar(255)            not null,
    status              job_status              not null,
    app_submission_date timestamp               null,
    last_update         timestamp,
    interview_round     int                     null,
    notes               varchar(1024)           null,
    starred             boolean                 default false
);

create table if not exists job_metadata (
    job_id              serial                  not null references job_statuses(job_id),
    location            varchar(1024)            not null,
    salary              varchar(1024)            not null,
    job_description     varchar(1024)           not null,
    company_description varchar(1024)           not null
);

create table resumes (
    user_id             serial                 not null references user_info(id),
    version             serial,
    name                varchar(255)           not null,
    bucket              varchar(120)           not null,
    prefix              varchar(320)           not null,
    upload_date         timestamp
);

create table job_application_files (
    user_id             serial                 not null references user_info(id),
    job_id              serial                 not null references job_statuses(job_id),
    file_id             serial,
    name                varchar(255)           not null,
    file_type           application_file       not null,
    bucket              varchar(120)           not null,
    prefix              varchar(320)           not null,
    upload_date         timestamp
);

create table user_reset_codes (
    user_id             serial                 not null references user_info(id),
    code                varchar(6)             not null,
    expiration_time     timestamp
);


-- !Downs

drop table if exists user_info;
drop table if exists job_statuses;
drop table if exists job_metadata;
drop table if exists resumes;
drop table if exists job_application_files;
drop table if exists user_reset_codes;

drop type if exists job_status;
drop type if exists application_file;
