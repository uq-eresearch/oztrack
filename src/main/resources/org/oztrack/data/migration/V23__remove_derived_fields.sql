alter table project
    drop column boundingbox,
    drop column firstdetectiondate,
    drop column lastdetectiondate,
    drop column detectioncount;

alter table datafile
    drop column detectioncount,
    drop column lastdetectiondate,
    drop column firstdetectiondate;