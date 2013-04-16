alter table project add column embargodate_temp date;
update project set embargodate_temp = date(embargodate);
alter table project drop column embargodate;
alter table project rename column embargodate_temp to embargodate;

alter table project add column embargonotificationdate_temp date;
update project set embargonotificationdate_temp = date(embargonotificationdate);
alter table project drop column embargonotificationdate;
alter table project rename column embargonotificationdate_temp to embargonotificationdate;