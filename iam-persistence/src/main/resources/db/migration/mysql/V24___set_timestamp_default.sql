ALTER TABLE iam_group_request 
  MODIFY CREATIONTIME TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  MODIFY LASTUPDATETIME TIMESTAMP DEFAULT '2000-01-01 00:00:00';