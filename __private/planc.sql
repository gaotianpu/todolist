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
`pk_id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`user_id`  bigint(20) NOT NULL ,
`subject`  varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`body`  varchar(3000) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`participator_ids`  varchar(3000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`created_date`  datetime NOT NULL DEFAULT '1901-01-01 00:00:00' ,
`last_update`  timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP ,
`task_status`  tinyint(4) NULL DEFAULT 0 COMMENT '尚未开始 NotBegun 0，已开始 Doing 10，结束Done2，Block3' ,
`closed_date`  datetime NULL DEFAULT NULL ,
`is_delete`  tinyint(4) NULL DEFAULT NULL ,
`plan_start_date`  datetime NULL DEFAULT NULL ,
`plan_closed_date`  datetime NULL DEFAULT NULL ,
`start_date`  datetime NULL DEFAULT NULL ,
PRIMARY KEY (`pk_id`),
INDEX `ix_user_id` (`user_id`) USING BTREE ,
INDEX `ix_plan_start_date` (`plan_start_date`) USING BTREE 
)
ENGINE=MyISAM
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2013-12-27 16:34:25
