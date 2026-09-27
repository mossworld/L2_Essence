DROP TABLE IF EXISTS `gameservers`;
CREATE TABLE IF NOT EXISTS `gameservers` (
  `server_id` int(11) NOT NULL DEFAULT '0',
  `server_name` varchar(50) NOT NULL DEFAULT '',
  `hexid` varchar(50) NOT NULL DEFAULT '',
  `host` varchar(50) NOT NULL DEFAULT '',
  PRIMARY KEY (`server_id`)
) DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

INSERT INTO `gameservers` VALUES ('1', 'moss', '5734b117cf1debd6d19f764b480d569a', '127.0.0.1');
