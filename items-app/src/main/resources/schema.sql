create table ITEMS(
ID uuid default random_uuid(), NAME varchar(50), 
DESCRIPTION varchar(100), 
STATUS varchar(20) default 'NOTDONE', COMPLETION_DATE timestamp, 
CREATED timestamp default current_timestamp(), CREATED_BY varchar(50) default 'nmoustafa',
LAST_MODIFIED timestamp, LAST_MODIFIED_BY varchar(50),
VERSION number default 0);