update analysis_parameter set name = 'hEstimator' where name = 'h' and value in ('href', 'LSCV');
update analysis_parameter set name = 'hValue'     where name = 'h';