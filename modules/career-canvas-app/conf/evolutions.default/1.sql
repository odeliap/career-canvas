-- !Ups

create type job_status as enum('NotSubmitted', 'Submitted', 'InterviewScheduled', 'Interviewed', 'OfferMade', 'Rejected');
create type connection_closeness as enum('Stranger', 'Acquaintance', 'Friend', 'CloseFriend', 'FamilyMember');

create table if not exists user_info (
    id                  serial                  primary key,
    email               varchar(255)            not null unique,
    password            varchar(255)            not null,
    full_name           varchar(255)            not null,
    last_login          timestamp
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


-- !Downs

drop table if exists connections;
drop table if exists job_statuses;
drop table if exists user_info;

drop type if exists job_status;
drop type if exists connection_closeness;