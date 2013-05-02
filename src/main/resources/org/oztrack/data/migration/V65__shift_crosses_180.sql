update positionfixlayer
set locationgeometry = ST_Shift_Longitude(locationgeometry)
where project_id in (select id from project where crosses180 = true);

update trajectorylayer
set trajectorygeometry = ST_Shift_Longitude(trajectorygeometry)
where project_id in (select id from project where crosses180 = true);