-- !Ups

create type job_status as enum('NotSubmitted', 'Submitted', 'InterviewScheduled', 'Interviewed', 'OfferMade', 'Rejected');
create type connection_closeness as enum('Stranger', 'Acquaintance', 'Friend', 'CloseFriend', 'FamilyMember');
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
    notes               varchar(1024)           null
);

create table if not exists connections (
    user_id             serial                  not null references user_info(id),
    connection_id       serial                  not null,
    first_name          varchar(255)            not null,
    last_name           varchar(255)            not null,
    company             varchar(255)            not null,
    job_title           varchar(255)            not null,
    email               varchar(255)            not null,
    phone_number        varchar(20)             null,
    proximity           connection_closeness    not null,
    last_contacted      timestamp               null,
    notes               varchar(1024)           null
);

create table calendar_event (
    user_id             serial                  not null references user_info(id),
    event_id            serial,
    title               varchar(255),
    all_day             boolean,
    start_timestamp     timestamp,
    end_timestamp       timestamp,
    ends_same_day       boolean
);

create sequence event_seq;

create table job_status_breakdown (
    user_id             integer,
    status              job_status,
    percentage          numeric
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


-- !Downs

drop table if exists connections;
drop table if exists job_statuses;
drop table if exists user_info;
drop table if exists calendar_event;
drop table if exists job_status_breakdown;
drop table if exists resumes;
drop table if exists job_application_files;

drop type if exists job_status;
drop type if exists connection_closeness;
drop type if exists application_file;

drop sequence if exists event_seq;