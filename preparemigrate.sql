alter table curri_job modify column approver INT(11) NULL;
update curri_job set approver = null where approver  = -1;
drop table Managesubtopic;