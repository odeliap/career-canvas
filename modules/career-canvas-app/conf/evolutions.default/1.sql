-- !Ups

create type job_status as enum('Bookmarked', 'Applying', 'Applied', 'Interviewing', 'Offer', 'Rejected');
create type job_type as enum('FullTime', 'Contact', 'PartTime', 'Internship', 'Temporary');
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

create table if not exists job_descriptions (
    job_id              serial                  not null unique,
    about               varchar(1024)           not null,
    requirements        varchar(1024)           not null,
    tech_stack          varchar(1024)           not null
);

create table if not exists job_statuses (
    user_id             serial                  not null references user_info(id),
    job_id              serial                  not null references job_descriptions(job_id),
    posting_url         varchar(255)            not null,
    company             varchar(255)            not null,
    job_title           varchar(255)            not null,
    jobtype             job_type                not null,
    location            varchar(255)            not null,
    salary_range        varchar(255)            not null,
    status              job_status              not null,
    app_submission_date timestamp               null,
    last_update         timestamp,
    notes               varchar(1024)           null,
    starred             boolean                 default false
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
    job_id              serial                 not null references job_descriptions(job_id),
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

drop table if exists user_reset_codes;
drop table if exists job_application_files;
drop table if exists resumes;
drop table if exists job_statuses;
drop table if exists job_descriptions;
drop table if exists user_info;

drop type if exists job_status;
drop type if exists job_type;
drop type if exists application_file;
