update project set
    datadirectorypath = regexp_replace(datadirectorypath, '.*/(project-[0-9]+)$', '\1'),
    imagefilelocation = regexp_replace(imagefilelocation, '.*/(project-[0-9]+)/img/(.*)$', '\2');

update datafile set
    oztrackfilename = regexp_replace(oztrackfilename, '.*/(project-[0-9]+)/(.*)$', '\2');

alter table project
    rename imagefilelocation to imagefilepath;

alter table datafile
    rename oztrackfilename to datafilepath;