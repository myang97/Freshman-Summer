/* PHASE 1 */
drop table APP.RAW_XM_GARBAGE;
drop table APP.RAW_XM_SONGS;
drop table APP.XM_SONGS;
drop table APP.YOUTUBE_LINKS;
drop table APP.PLAYLIST_SONGS;
drop table APP.CONTROL;
drop table APP.PREF_EVENTS;
drop table APP.PREF_EVENTS_DISPLAYED;

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


create table APP.YOUTUBE_LINKS (
ID integer not null generated always as identity (start with 1, increment by 1),
SONG_NAME varchar(255), 
YOUTUBE_LINK varchar(255),
YOUTUBE_TITLE varchar(255),
VIEW_COUNT integer,
NEWEST_DATE timestamp,
OLDEST_DATE timestamp,
PLAY_COUNT int,
unique (ID)
)

create table APP.PLAYLIST_SONGS (
ID integer,
SONG_NAME varchar(255), 
YOUTUBE_LINK varchar(255),
YOUTUBE_TITLE varchar(255),
VIEW_COUNT integer,
NEWEST_DATE timestamp,
OLDEST_DATE timestamp,
PLAY_COUNT int
)

create table APP.PREF_EVENTS (
SONG varchar(255), 
LISTENED_AMT double, 
DURATION double, 
UPVOTE int,
DOWNVOTE int,
EVENT varchar(255)
)

create table APP.PREF_EVENTS_DISPLAYED (
SONG varchar(255), 
TOTAL_LISTENED double, 
UPVOTE int,
DOWNVOTE int,
RATING int
)

select * from APP.PREF_EVENTS
drop table APP.PREF_EVENTS

select * from APP.PREF_EVENTS_DISPLAYED
drop table APP.PREF_EVENTS_DISPLAYED

select * from APP.YOUTUBE_LINKS
select * from APP.PLAYLIST_SONGS
select ID, SONG_NAME, YOUTUBE_LINK, PLAY_COUNT from APP.PLAYLIST_SONGS where SONG_NAME LIKE '%Ride It%'

drop table APP.PLAYLIST_SONGS 
drop table APP.YOUTUBE_LINKS

delete FROM APP.YOUTUBE_LINKS WHERE SONG_NAME LIKE 'B$'
select count(*) from APP.YOUTUBE_LINKS WHERE SONG_NAME LIKE 'B%'


select * FROM APP.RAW_XM_SONGS WHERE SONG LIKE 'ty%' ORDER BY SONG ASC, DATE ASC
select * FROM APP.RAW_XM_GARBAGE
select TOP 10 PERCENT * FROM APP.XM_SONGS ORDER BY PLAY_COUNT DESC, NEWEST_DATE DESC
select * FROM APP.XM_SONGS order by PLAY_COUNT desc, NEWEST_DATE desc
SELECT * FROM APP.XM_SONGS ORDER BY RANDOM() OFFSET 0 ROWS FETCH NEXT 200 ROWS ONLY

create table APP.CONTROL (
ID integer not null,
SOURCE varchar(255) not null,
NEWEST_DATE timestamp not null,
OLDEST_DATE timestamp not null,
unique (ID)
)

insert into APP.CONTROL values (1, 'bpm_playlist', '1983-01-01 00:00:00', '2100-01-01 00:00:00')


select * FROM APP.CONTROL
drop * FROM APP.CONTROL