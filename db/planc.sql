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
-- Table structure for table `cloud_app_mapping`
--

DROP TABLE IF EXISTS `cloud_app_mapping`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cloud_app_mapping` (
  `pk_id` bigint(20) NOT NULL,
  `cust_id` bigint(20) DEFAULT NULL,
  `device_id` int(11) DEFAULT NULL,
  `cloud_subject_id` bigint(20) DEFAULT NULL,
  `app_subject_id` bigint(20) DEFAULT NULL,
  `is_sync` int(11) DEFAULT NULL,
  `last_sync` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sessions`
--

DROP TABLE IF EXISTS `sessions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sessions` (
  `session_id` char(128) NOT NULL,
  `atime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `data` text,
  UNIQUE KEY `session_id` (`session_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `subject_participator_relation`
--

DROP TABLE IF EXISTS `subject_participator_relation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `subject_participator_relation` (
  `subject_id` bigint(20) NOT NULL,
  `participator_id` bigint(20) NOT NULL,
  `is_delete` tinyint(4) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `subject_reviews`
--

DROP TABLE IF EXISTS `subject_reviews`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `subject_reviews` (
  `review_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `subject_id` bigint(20) NOT NULL,
  `responder_id` bigint(20) NOT NULL,
  `review_content` varchar(3000) NOT NULL,
  `created_date` datetime NOT NULL,
  `last_update` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP,
  `is_delete` tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`review_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `subject_similarity`
--

DROP TABLE IF EXISTS `subject_similarity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `subject_similarity` (
  `subject_id` int(11) NOT NULL,
  `subject_id_1` int(11) NOT NULL,
  `Similarity` float DEFAULT NULL,
  `las_update` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

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
  `is_delete` tinyint(4) DEFAULT '0',
  `plan_start_date` date DEFAULT NULL,
  `plan_closed_date` date DEFAULT NULL,
  `start_date` date DEFAULT NULL,
  `terms` varchar(3000) NOT NULL DEFAULT '',
  `tf_idf` varchar(3000) NOT NULL DEFAULT '',
  `device_type` varchar(200) DEFAULT NULL,
  `device_no` varchar(200) DEFAULT NULL,
  `local_id` bigint(20) DEFAULT NULL,
  `app_created_date` datetime DEFAULT NULL,
  `is_todo` int(11) NOT NULL DEFAULT '0',
  `is_remind` int(11) NOT NULL DEFAULT '0',
  `parent_id` bigint(20) NOT NULL DEFAULT '0',
  `remind_datetime` datetime DEFAULT NULL,
  `remind_frequency` int(11) DEFAULT '0',
  `remind_next` datetime DEFAULT NULL,
  `version` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`pk_id`),
  KEY `ix_user_id` (`user_id`) USING BTREE,
  KEY `ix_plan_start_date` (`plan_start_date`) USING BTREE
) ENGINE=MyISAM AUTO_INCREMENT=859 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `subjects_old`
--

DROP TABLE IF EXISTS `subjects_old`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `subjects_old` (
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
) ENGINE=MyISAM AUTO_INCREMENT=127 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `term_doc`
--

DROP TABLE IF EXISTS `term_doc`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `term_doc` (
  `user_id` bigint(20) DEFAULT NULL,
  `term_id` bigint(20) DEFAULT NULL,
  `doc_id` bigint(20) DEFAULT NULL,
  `term_count` int(11) DEFAULT '1',
  `tf_idf` float DEFAULT NULL,
  `last_update` datetime DEFAULT NULL,
  `tf` float DEFAULT NULL,
  UNIQUE KEY `uniq_term_doc` (`term_id`,`doc_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `term_doc_count`
--

DROP TABLE IF EXISTS `term_doc_count`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `term_doc_count` (
  `pk_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `term` varchar(255) NOT NULL DEFAULT '',
  `count` int(11) NOT NULL DEFAULT '0',
  `idf` float DEFAULT NULL,
  `last_update` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP,
  `sogou_ix_count` bigint(20) DEFAULT NULL,
  `sogou_idf` float DEFAULT NULL,
  `sogou_last_get` datetime DEFAULT NULL,
  `sogou_tf_idf` float DEFAULT NULL,
  PRIMARY KEY (`pk_id`),
  KEY `ix_count` (`count`)
) ENGINE=MyISAM AUTO_INCREMENT=1216 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `terms`
--

DROP TABLE IF EXISTS `terms`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `terms` (
  `term_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `term` varchar(100) NOT NULL DEFAULT '',
  `term_len` int(11) DEFAULT NULL,
  `term_sort` int(11) DEFAULT NULL,
  `count_domain` int(11) DEFAULT NULL,
  `idf_domain` float DEFAULT NULL,
  `count_se` int(11) NOT NULL DEFAULT '0',
  `idf_se` float DEFAULT '0',
  `last_update` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `last_get_se` datetime DEFAULT NULL,
  `word_tag` int(11) DEFAULT NULL,
  PRIMARY KEY (`term_id`),
  UNIQUE KEY `ix_term` (`term`) USING HASH
) ENGINE=InnoDB AUTO_INCREMENT=3985 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_account_validate`
--

DROP TABLE IF EXISTS `user_account_validate`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_account_validate` (
  `pk_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `validate_sort` int(11) NOT NULL,
  `validate_account` varchar(50) NOT NULL,
  `validate_code` varchar(50) NOT NULL,
  `code_status` int(11) NOT NULL,
  `creation_date` datetime NOT NULL,
  `last_update` datetime NOT NULL,
  PRIMARY KEY (`pk_id`),
  KEY `ix_multi` (`user_id`,`validate_sort`,`validate_code`,`code_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_devices`
--

DROP TABLE IF EXISTS `user_devices`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_devices` (
  `pk_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL DEFAULT '0',
  `device_no` varchar(50) DEFAULT '',
  `device_type` varchar(50) DEFAULT NULL,
  `creation_date` datetime DEFAULT NULL,
  `last_update` datetime DEFAULT NULL,
  `os_type` varchar(50) DEFAULT NULL,
  `access_token` varchar(50) DEFAULT NULL,
  `channel` varchar(50) DEFAULT NULL,
  `version` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `uniq_id` (`user_id`,`device_no`) USING HASH
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `users` (
  `pk_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `mobile` bigint(20) DEFAULT NULL,
  `email` varchar(50) DEFAULT NULL,
  `nick_name` varchar(50) DEFAULT NULL,
  `password` varchar(100) DEFAULT NULL,
  `creation_date` datetime DEFAULT NULL,
  `last_update` datetime DEFAULT NULL,
  `last_login` datetime DEFAULT NULL,
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `ix_mobile` (`mobile`),
  UNIQUE KEY `ix_email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2014-07-23 11:56:04
