drop table if exists users_roles;
drop table if exists users;
drop table if exists roles;

create table if not exists users
(
    id            bigint generated by default as identity primary key,
    login         varchar(30)  not null,
    password      varchar(255) not null,
    refresh_token varchar(255)
);

create table if not exists roles
(
    id   bigint generated by default as identity primary key,
    role varchar(20) not null unique
);

create table if not exists users_roles
(
    user_id bigint not null references users (id) on delete cascade ,
    role_id bigint not null references roles (id) on delete cascade ,
    unique (user_id, role_id)
);

insert into roles (role)
values ('USER'),
       ('ADMIN');

