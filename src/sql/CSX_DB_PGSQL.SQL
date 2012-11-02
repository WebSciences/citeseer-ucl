#
#  CiteSeerX main database spec
#
#  IGC
#

CREATE DATABASE citeseerx CHARACTER SET utf8 COLLATE utf8_general_ci;

USE citeseerx;


CREATE TABLE papers (
  id VARCHAR(100) NOT NULL,
  version INT UNSIGNED NOT NULL,
  cluster BIGINT UNSIGNED,
  title VARCHAR(255),
  abstract TEXT,
  year INT,
  venue VARCHAR(100),
  venueType VARCHAR(20),
  pages VARCHAR(20),
  volume INT,
  number INT,
  publisher VARCHAR(100),
  pubAddress VARCHAR(100),
  tech VARCHAR(100),
  public TINYINT NOT NULL DEFAULT 1,
  ncites INT UNSIGNED NOT NULL DEFAULT 0,
  versionName VARCHAR(20),
  crawlDate TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  repositoryID VARCHAR(15),
  conversionTrace VARCHAR(255),
  selfCites INT UNSIGNED NOT NULL DEFAULT 0,
  versionTime TIMESTAMP NOT NULL,
  PRIMARY KEY(id),
  INDEX(version),
  INDEX(cluster),
  INDEX(title),
  INDEX(year),
  INDEX(versionName),
  INDEX(crawlDate),
  INDEX(repositoryID),
  INDEX(selfCites),
  INDEX(versionTime)
)
CHARACTER SET utf8 COLLATE utf8_general_ci ENGINE=INNODB;


CREATE TABLE papers_versionShadow (
	id VARCHAR(100) NOT NULL,
	title VARCHAR(100),
	abstract VARCHAR(100),
	year VARCHAR(100),
	venue VARCHAR(100),
	venueType VARCHAR(100),
	pages VARCHAR(100),
	volume VARCHAR(100),
	number VARCHAR(100),
	publisher VARCHAR(100),
	pubAddress VARCHAR(100),
	tech VARCHAR(100),
	citations VARCHAR(100),
	PRIMARY KEY(id),
	FOREIGN KEY(id) REFERENCES papers(id)
	  ON DELETE CASCADE ON UPDATE CASCADE
)
CHARACTER SET utf8 COLLATE utf8_general_ci ENGINE=INNODB;


CREATE TABLE authors (
  id SERIAL,
  cluster BIGINT UNSIGNED,
  name VARCHAR(100) NOT NULL,
  affil VARCHAR(255),
  address VARCHAR(255),
  email VARCHAR(100),
  ord INT NOT NULL,
  paperid VARCHAR(100) NOT NULL,
  PRIMARY KEY(id),
  INDEX(cluster),
  INDEX(name),
  INDEX(paperid),
  FOREIGN KEY (paperid) REFERENCES papers(id)
    ON DELETE CASCADE ON UPDATE CASCADE
)
CHARACTER SET utf8 COLLATE utf8_general_ci ENGINE=INNODB;


CREATE TABLE authors_versionShadow (
  id BIGINT UNSIGNED NOT NULL,
  name VARCHAR(100),
  affil VARCHAR(100),
  address VARCHAR(100),
  email VARCHAR(100),
  ord VARCHAR(100),
  PRIMARY KEY(id),
  FOREIGN KEY(id) REFERENCES authors(id)
    ON DELETE CASCADE ON UPDATE CASCADE
)
CHARACTER SET utf8 COLLATE utf8_general_ci ENGINE=INNODB;


CREATE TABLE acknowledgments (
  id SERIAL,
  cluster BIGINT UNSIGNED,
  name VARCHAR(255) NOT NULL,
  entType VARCHAR(20),
  ackType VARCHAR(20),
  paperid VARCHAR(100) NOT NULL,
  PRIMARY KEY(id),
  INDEX(cluster),
  INDEX(name),
  INDEX(entType),
  INDEX(ackType),
  INDEX(paperid),
  FOREIGN KEY(paperid) REFERENCES papers(id)
    ON DELETE CASCADE ON UPDATE CASCADE
)
CHARACTER SET utf8 COLLATE utf8_general_ci ENGINE=INNODB;


CREATE TABLE acknowledgments_versionShadow (
  id BIGINT UNSIGNED NOT NULL,
  name VARCHAR(100),
  entType VARCHAR(100),
  ackType VARCHAR(100),
  PRIMARY KEY(id),
  FOREIGN KEY(id) REFERENCES acknowledgments(id)
    ON DELETE CASCADE ON UPDATE CASCADE
)
CHARACTER SET utf8 COLLATE utf8_general_ci ENGINE=INNODB;


CREATE TABLE acknowledgmentContexts (
  id SERIAL,
  ackid BIGINT UNSIGNED NOT NULL,
  context TEXT NOT NULL,
  PRIMARY KEY(id),
  INDEX(ackid),
  FOREIGN KEY(ackid) REFERENCES acknowledgments(id)
    ON DELETE CASCADE ON UPDATE CASCADE
)
CHARACTER SET utf8 COLLATE utf8_general_ci ENGINE=INNODB;


CREATE TABLE citations (
  id SERIAL,
  cluster BIGINT UNSIGNED,
  authors TEXT,
  title VARCHAR(255),
  venue VARCHAR(255),
  venueType VARCHAR(20),
  year INT,
  pages VARCHAR(20),
  editors TEXT,
  publisher VARCHAR(100),
  pubAddress VARCHAR(100),
  volume INT,
  number INT,
  tech VARCHAR(100),
  raw TEXT,
  paperid VARCHAR(100) NOT NULL,
  self TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY(id),
  INDEX(cluster),
  INDEX(title),
  INDEX(venue),
  INDEX(venueType),
  INDEX(year),
  INDEX(publisher),
  INDEX(paperid),
  INDEX(self),
  FOREIGN KEY (paperid) REFERENCES papers(id)
    ON DELETE CASCADE ON UPDATE CASCADE
)
CHARACTER SET utf8 COLLATE utf8_general_ci ENGINE=INNODB;


CREATE TABLE citationContexts (
  id SERIAL,
  citationid BIGINT UNSIGNED NOT NULL,
  context TEXT NOT NULL,
  PRIMARY KEY(id),
  INDEX(citationid),
  FOREIGN KEY (citationid) REFERENCES citations(id)
    ON DELETE CASCADE ON UPDATE CASCADE
)
CHARACTER SET utf8 COLLATE utf8_general_ci ENGINE=INNODB;


CREATE TABLE keywords (
  id SERIAL,
  keyword VARCHAR(100) NOT NULL,
  paperid VARCHAR(100) NOT NULL,
  PRIMARY KEY(id),
  INDEX(keyword),
  INDEX(paperid),
  FOREIGN KEY(paperid) REFERENCES papers(id)
    ON DELETE CASCADE ON UPDATE CASCADE
)
CHARACTER SET utf8 COLLATE utf8_general_ci ENGINE=INNODB;


CREATE TABLE keywords_versionShadow (
  id BIGINT UNSIGNED NOT NULL,
  keyword VARCHAR(100),
  PRIMARY KEY(id),
  FOREIGN KEY(id) REFERENCES keywords(id)
    ON DELETE CASCADE ON UPDATE CASCADE
)
CHARACTER SET utf8 COLLATE utf8_general_ci ENGINE=INNODB;


CREATE TABLE checksum (
  sha1 VARCHAR(100) NOT NULL,
  paperid VARCHAR(100) NOT NULL,
  filetype VARCHAR(10) NOT NULL,
  PRIMARY KEY(sha1),
  INDEX(paperid),
  INDEX(filetype),
  FOREIGN KEY(paperid) REFERENCES papers(id)
    ON DELETE CASCADE ON UPDATE CASCADE
)
CHARACTER SET utf8 COLLATE utf8_general_ci ENGINE=INNODB;


CREATE TABLE paperVersions (
  id SERIAL,
  name VARCHAR(20),
  paperid VARCHAR(100) NOT NULL,
  version INT NOT NULL,
  repositoryID VARCHAR(15) NOT NULL,
  path VARCHAR(255) NOT NULL,
  deprecated TINYINT NOT NULL DEFAULT 0,
  spam TINYINT NOT NULL DEFAULT 0,
  time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY(id),
  INDEX(name),
  INDEX(paperid),
  INDEX(version),
  INDEX(repositoryID),
  INDEX(deprecated),
  INDEX(spam),
  FOREIGN KEY(paperid) REFERENCES papers(id)
    ON DELETE CASCADE ON UPDATE CASCADE
)
CHARACTER SET utf8 COLLATE utf8_general_ci ENGINE=INNODB;


#CREATE TABLE externalPaperData (
#  id SERIAL,
#  cluster BIGINT UNSIGNED,
#  source VARCHAR(100) NOT NULL,
#  date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
#  repositoryID VARCHAR(15) NOT NULL,
#  path VARCHAR(255) NOT NULL,
#  PRIMARY KEY(id),
#  INDEX(source),
#  INDEX(date)
#)
#CHARACTER SET utf8 COLLATE utf8_general_ci ENGINE=INNODB;


CREATE TABLE userCorrections (
  id SERIAL,
  userid VARCHAR(100) NOT NULL,
  paperid VARCHAR(100) NOT NULL,
  date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  repositoryID VARCHAR(15) NOT NULL,
  path VARCHAR(255) NOT NULL,
  PRIMARY KEY(id),
  INDEX(userid),
  INDEX(paperid),
  FOREIGN KEY(paperid) REFERENCES papers(id)
    ON DELETE CASCADE ON UPDATE CASCADE
)
CHARACTER SET utf8 COLLATE utf8_general_ci ENGINE=INNODB;


CREATE TABLE urls (
  id SERIAL,
  url VARCHAR(255) NOT NULL,
  paperid VARCHAR(100) NOT NULL,
  PRIMARY KEY(id),
  INDEX(url),
  INDEX(paperid),
  FOREIGN KEY(paperid) REFERENCES papers(id)
    ON DELETE CASCADE ON UPDATE CASCADE
)
CHARACTER SET utf8 COLLATE utf8_general_ci ENGINE=INNODB;


CREATE TABLE hubUrls (
  id SERIAL,
  url VARCHAR(255) UNIQUE NOT NULL,
  lastCrawl TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  repositoryID VARCHAR(15),
  PRIMARY KEY(id),
  INDEX(url),
  INDEX(lastCrawl),
  INDEX(repositoryID)
)
CHARACTER SET utf8 COLLATE utf8_general_ci ENGINE=INNODB;


CREATE TABLE hubMap (
  id SERIAL,
  urlid BIGINT UNSIGNED NOT NULL,
  hubid BIGINT UNSIGNED NOT NULL,
  PRIMARY KEY(id),
  INDEX(urlid),
  INDEX(hubid),
  FOREIGN KEY(urlid) REFERENCES urls(id)
    ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY(hubid) REFERENCES hubUrls(id)
    ON DELETE CASCADE ON UPDATE CASCADE
)
CHARACTER SET utf8 COLLATE utf8_general_ci ENGINE=INNODB;


CREATE TABLE legacyIDMap (
  id SERIAL,
  paperid VARCHAR(100) NOT NULL,
  legacyid INT UNSIGNED NOT NULL,
  PRIMARY KEY(id),
  INDEX(paperid),
  INDEX(legacyid),
  FOREIGN KEY(paperid) REFERENCES papers(id)
    ON DELETE CASCADE ON UPDATE CASCADE
)
CHARACTER SET utf8 COLLATE utf8_general_ci ENGINE=INNODB;


CREATE TABLE citecharts (
  id VARCHAR(100) NOT NULL,
  lastNcites INT UNSIGNED NOT NULL,
  PRIMARY KEY(id),
  INDEX(lastNcites),
  FOREIGN KEY(id) REFERENCES papers(id)
    ON DELETE CASCADE ON UPDATE CASCADE
)
ENGINE=INNODB;


CREATE TABLE textSources (
  name VARCHAR(50) NOT NULL,
  content TEXT,
  PRIMARY KEY(name)
)
CHARACTER SET utf8 COLLATE utf8_general_ci ENGINE=INNODB;


CREATE TABLE tags (
  id SERIAL,
  paperid VARCHAR(100) NOT NULL,
  tag VARCHAR(50) NOT NULL,
  count INT UNSIGNED NOT NULL DEFAULT 0,
  PRIMARY KEY(id),
  INDEX(paperid),
  INDEX(tag),
  FOREIGN KEY(paperid) REFERENCES papers(id)
    ON DELETE CASCADE ON UPDATE CASCADE
)
CHARACTER SET utf8 COLLATE utf8_general_ci ENGINE=INNODB;


DELIMITER //

#
# Triggers for keeping track of version time information by copying the version
# time from the paperVersions table to the papers table on version updates.
#

CREATE TRIGGER update_pvtime BEFORE UPDATE ON papers
  FOR EACH ROW BEGIN
    IF NEW.version != OLD.version THEN
      SET NEW.versionTime=(SELECT time FROM paperVersions where paperid=NEW.id AND version=NEW.version);
    END IF;
  END;//


CREATE TRIGGER update_vvtime AFTER UPDATE ON paperVersions
  FOR EACH ROW BEGIN
    IF NEW.time != OLD.time THEN
      UPDATE papers SET papers.versionTime=NEW.time WHERE papers.id=NEW.paperid AND papers.version=NEW.version;
    END IF;
  END;//


DELIMITER ;


GRANT ALL ON citeseerx.* TO '$USERNAME$'@'$DOMAIN$' IDENTIFIED BY '$PASSWORD$';
