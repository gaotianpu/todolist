-- MySQL dump 10.13  Distrib 5.7.2-m12, for Win64 (x86_64)
--
-- Host: localhost    Database: planc
-- ------------------------------------------------------
-- Server version	5.7.2-m12

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `subjects`
--

DROP TABLE IF EXISTS `subjects`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `subjects` (
  `pk_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `subject` varchar(200) NOT NULL,
  `body` varchar(3000) NOT NULL,
  `participator_ids` varchar(3000) DEFAULT NULL,
  `created_date` datetime NOT NULL DEFAULT '1901-01-01 00:00:00',
  `last_update` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP,
  `task_status` tinyint(4) DEFAULT '0' COMMENT '尚未开始 NotBegun 0，已开始 Doing 10，结束Done2，Block3',
  `closed_date` datetime DEFAULT NULL,
  `is_delete` tinyint(4) DEFAULT NULL,
  `plan_start_date` datetime DEFAULT NULL,
  `plan_closed_date` datetime DEFAULT NULL,
  `start_date` datetime DEFAULT NULL,
  `terms` varchar(3000) DEFAULT NULL,
  `tf_idf` varchar(3000) DEFAULT NULL,
  PRIMARY KEY (`pk_id`),
  KEY `ix_user_id` (`user_id`) USING BTREE,
  KEY `ix_plan_start_date` (`plan_start_date`) USING BTREE
) ENGINE=MyISAM CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
DROP TABLE IF EXISTS `term_doc_count`;
CREATE TABLE `term_doc_count` (
  `term` varchar(255) NOT NULL DEFAULT '',
  `count` int(11) NOT NULL DEFAULT '0',
  `idf` float DEFAULT NULL,
  `last_update` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`term`),
  KEY `ix_count` (`count`)
) ENGINE=MyISAM CHARSET=utf8;

-- Dump completed on 2013-12-27 16:34:25
