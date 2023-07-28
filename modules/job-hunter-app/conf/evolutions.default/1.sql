-- !Ups

create type job_status as enum('NotSubmitted', 'Submitted', 'InterviewScheduled', 'Interviewed', 'OfferMade', 'Accepted', 'Rejected');
create type connection_closeness as enum('Stranger', 'Acquaintance', 'Friend', 'CloseFriend', 'FamilyMember');

create table if not exists user_info (
    id                  serial                  primary key,
    username            varchar(255)            not null,
    password            varchar(255)            not null,
    email               varchar(255)            not null unique,
    last_login          timestamp,
    login_attempts      serial                  default 0,
    account_locked      boolean                 default false
);

create table if not exists job_statuses (
    user_id             serial                  not null references user_info(id),
    job_id              serial                  not null,
    company             varchar(255)            not null,
    job_title           varchar(255)            not null,
    status              job_status              not null,
    last_update         timestamp,
    interview_stage     serial,
    notes               varchar(1024)
);

create table if not exists connections (
    user_id             serial                  not null references user_info(id),
    first_name          varchar(255)            not null,
    last_name           varchar(255)            not null,
    company             varchar(255)            not null,
    job_title           varchar(255)            not null,
    email               varchar(255)            not null,
    phone_number        varchar(20),
    last_contacted      timestamp,
    notes               varchar(1024),
    proximity           connection_closeness    not null,
);


-- !Downs

drop table if exists user_info;
drop table if exists job_statuses;
drop table if exists connections;

drop type if exists job_status;
drop type if exists connection_closeness;