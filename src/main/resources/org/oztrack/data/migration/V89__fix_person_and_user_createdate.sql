-- if user record has no create date, use create date of a project they created
update appuser set createdate = (select min(createdate) from project where createuser_id = appuser.id) where createdate is null;

-- otherwise, use create date of a project they're involved with
update appuser set createdate = (select min(createdate) from project where id in (select project_id from project_user where user_id = appuser.id)) where createdate is null;

-- if user still has no create date, use current date
update appuser set createdate = now() where createdate is null;

-- if person has no create date, try getting from project
update person set createdate = (select min(createdate) from project where id in (select project_id from project_contribution where contributor_id = person.id)) where createdate is null;

-- otherwise, user create date of user
update person set createdate = appuser.createdate from appuser where person.id = appuser.person_id and person.createdate is null;

-- if person still has no create date, use current date
update person set createdate = now() where createdate is null;