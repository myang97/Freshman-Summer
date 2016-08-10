/* PHASE 1 */
create table APP.RAW_XM_SONGS (
ID integer not null generated always as identity (start with 1, increment by 1),
SOURCE varchar(255),
ARTIST varchar(255),
TITLE varchar(255),
SONG varchar(255),
DATE timestamp,
TWITTER_ID bigint,
unique (ID)
)
drop table APP.RAW_XM_SONGS

select * from APP.RAW_XM_SONGS

create table APP.RAW_XM_GARBAGE (
ID integer not null generated always as identity (start with 1, increment by 1),
SOURCE varchar(255),
ARTIST varchar(255),
TITLE varchar(255),
SONG varchar(255),
DATE timestamp,
TWITTER_ID bigint,
unique (ID)
)
drop table APP.RAW_XM_GARBAGE

drop table APP.RAW_XM_GARBAGE;
drop table APP.RAW_XM_SONGS;
drop table APP.XM_SONGS

select * from APP.RAW_XM_SONGS ORDER BY TITLE
select * from APP.RAW_XM_GARBAGE
select * from APP.XM_SONGS 

create table APP.CONTROL (
ID integer not null,
SOURCE varchar(255) not null,
NEWEST_DATE timestamp not null,
OLDEST_DATE timestamp not null,
unique (ID)
)

drop table APP.CONTROL

select * from APP.CONTROL
/* low date to when internet was invented */
insert into APP.CONTROL values (1, 'bpm_playlist', '1983-01-01 00:00:00', '2100-01-01 00:00:00')

/* restart initial load */
truncate table APP.RAW_XM_SONGS
truncate table APP.RAW_XM_GARBAGE
update APP.CONTROL set NEWEST_DATE = '1983-01-01 00:00:00', OLDEST_DATE = '2100-01-01 00:00:00' where SOURCE = 'bpm_playlist'


/* PHASE 2 */
create table APP.XM_SONGS (
ID integer not null generated always as identity (start with 1, increment by 1),
ARTIST varchar(255),
TITLE varchar(255),
SONG varchar(255),
NEWEST_DATE timestamp,
OLDEST_DATE timestamp,
PLAY_COUNT int,
unique (ID)
)
drop table APP.XM_SONGS

/* restart consolidate */
truncate table APP.XM_SONGS

/* sanity check after consolidation */
select * from APP.RAW_XM_SONGS
select sum(PLAY_COUNT) sum1 from APP.XM_SONGS
select count(*) from APP.XM_SONGS


/* PHASE 3 */
create table APP.YOUTUBE_LINKS (
ID integer not null generated always as identity (start with 1, increment by 1),
SONG_NAME varchar(255), 
YOUTUBE_LINK varchar(255),
YOUTUBE_TITLE varchar(255),
unique (ID)
)
drop table APP.YOUTUBE_LINKS

select * from APP.XM_SONGS order by PLAY_COUNT desc
select * from APP.YOUTUBE_LINKS


create table APP.PREF_EVENTS (
EVENT_TIME timestamp,
PLAY_DATE timestamp,
EVENT_NAME varchar(255),
SONG varchar(255), 
ARTIST varchar(255),
PLAYLIST_NUM int,
LISTENED_NUM int,
PLUS int, 
MINUS int, 
LISTENED_AMT double, 
DURATION double, 
WEIGHT int, 
UPVOTE int, 
DOWNVOTE int, 
PLAY_COUNT int, 
TAG varchar(255)
)

select * from APP.RAW_XM_SONGS order by DATE DESC;
select * from APP.XM_TABLE order by SONG asc;
select * from APP.HISTORY order by OLDEST_DATE asc;
select count(*) from APP.XM_TABLE
select count(*) from APP.GARBAGE

select DATE from APP.XM_TABLE order by DATE asc fetch first 1 rows only

delete from APP.HISTORY;
insert into APP.HISTORY values ('BPM', 0, 'NEWEST', 983330249015844864, 'OLDEST');
delete from APP.XM_TABLE;
delete from APP.GARBAGE;

delete from APP.MUSIC;
select * from APP.MUSIC order by SONG_NAME asc;
select count(*) from APP.MUSIC;
select * from APP.MUSIC
	where OLDEST_DATE <='2013-01-01 00:00:00' and PLAY_COUNT <=5
	order by PLAY_COUNT desc, NEWEST_DATE desc;


delete from APP.MUSIC
	where OLDEST_DATE <='2013-01-01 00:00:00' and PLAY_COUNT <=5;
delete from APP.MUSIC
	where OLDEST_DATE <='2012-01-01 00:00:00' and PLAY_COUNT <=15;
delete from APP.MUSIC
	where PLAY_COUNT =1;
	
delete from APP.YOUTUBE_LINKS;
select * from APP.YOUTUBE_LINKS;
