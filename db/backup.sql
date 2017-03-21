-- MySQL dump 10.13  Distrib 5.7.17, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: cssr
-- ------------------------------------------------------
-- Server version	5.7.17

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
-- Table structure for table `email`
--

DROP TABLE IF EXISTS `email`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `email` (
  `emailId` bigint(20) NOT NULL AUTO_INCREMENT,
  `to` varchar(1000) NOT NULL,
  `bcc` varchar(1000) DEFAULT NULL,
  `cc` varchar(1000) DEFAULT NULL,
  `subject` varchar(1000) NOT NULL,
  `body` text NOT NULL,
  `sentDate` datetime DEFAULT NULL,
  `tryCount` int(11) NOT NULL DEFAULT '0',
  `error` text,
  `insertedAt` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`emailId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `email`
--

LOCK TABLES `email` WRITE;
/*!40000 ALTER TABLE `email` DISABLE KEYS */;
/*!40000 ALTER TABLE `email` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary view structure for view `emailtosend`
--

DROP TABLE IF EXISTS `emailtosend`;
/*!50001 DROP VIEW IF EXISTS `emailtosend`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE VIEW `emailtosend` AS SELECT 
 1 AS `emailId`,
 1 AS `to`,
 1 AS `bcc`,
 1 AS `cc`,
 1 AS `subject`,
 1 AS `body`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `presentation`
--

DROP TABLE IF EXISTS `presentation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `presentation` (
  `presentationId` int(11) NOT NULL AUTO_INCREMENT,
  `dateTime` datetime NOT NULL,
  `location` varchar(1000) NOT NULL,
  `speakerId` bigint(20) NOT NULL,
  `title` varchar(100) NOT NULL,
  `abstract` varchar(5000) DEFAULT NULL,
  PRIMARY KEY (`presentationId`),
  KEY `FK_presentation_user_idx` (`speakerId`),
  CONSTRAINT `FK_presentation_user` FOREIGN KEY (`speakerId`) REFERENCES `users` (`userId`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `presentation`
--

LOCK TABLES `presentation` WRITE;
/*!40000 ALTER TABLE `presentation` DISABLE KEYS */;
/*!40000 ALTER TABLE `presentation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `presentationfile`
--

DROP TABLE IF EXISTS `presentationfile`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `presentationfile` (
  `presentationFileId` bigint(20) NOT NULL,
  `presentationId` int(11) NOT NULL,
  `type` char(1) NOT NULL,
  `content` blob,
  `contentLink` varchar(2000) DEFAULT NULL,
  PRIMARY KEY (`presentationFileId`),
  KEY `FK_presentationFile_presentatin_idx` (`presentationId`),
  CONSTRAINT `FK_presentationFile_presentatin` FOREIGN KEY (`presentationId`) REFERENCES `presentation` (`presentationId`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `presentationfile`
--

LOCK TABLES `presentationfile` WRITE;
/*!40000 ALTER TABLE `presentationfile` DISABLE KEYS */;
/*!40000 ALTER TABLE `presentationfile` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `roles` (
  `roleId` int(11) NOT NULL,
  `roleName` varchar(1000) DEFAULT NULL,
  PRIMARY KEY (`roleId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES (1,'Admin'),(2,'SGL');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `subscription`
--

DROP TABLE IF EXISTS `subscription`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `subscription` (
  `subscriptionId` bigint(20) NOT NULL,
  `presentationId` int(11) NOT NULL,
  `userId` bigint(20) NOT NULL,
  `sandwichType` char(1) DEFAULT NULL,
  `drink` tinyint(4) NOT NULL,
  PRIMARY KEY (`subscriptionId`),
  UNIQUE KEY `UK_user_subs` (`userId`,`presentationId`),
  KEY `FK_subscription_presentation_idx` (`presentationId`),
  CONSTRAINT `FK_subscription_presentation` FOREIGN KEY (`presentationId`) REFERENCES `presentation` (`presentationId`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_subscription_user` FOREIGN KEY (`userId`) REFERENCES `users` (`userId`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `subscription`
--

LOCK TABLES `subscription` WRITE;
/*!40000 ALTER TABLE `subscription` DISABLE KEYS */;
/*!40000 ALTER TABLE `subscription` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user2roles`
--

DROP TABLE IF EXISTS `user2roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user2roles` (
  `user2RoleId` bigint(20) NOT NULL AUTO_INCREMENT,
  `userId` bigint(20) NOT NULL,
  `roleId` int(11) NOT NULL,
  PRIMARY KEY (`user2RoleId`),
  UNIQUE KEY `UK_user2Roles` (`userId`,`roleId`),
  KEY `FK_user2Roles_roles_idx` (`roleId`),
  CONSTRAINT `FK_user2Roles_roles` FOREIGN KEY (`roleId`) REFERENCES `roles` (`roleId`),
  CONSTRAINT `FK_user2Roles_users` FOREIGN KEY (`userId`) REFERENCES `users` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user2roles`
--

LOCK TABLES `user2roles` WRITE;
/*!40000 ALTER TABLE `user2roles` DISABLE KEYS */;
/*!40000 ALTER TABLE `user2roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `users` (
  `userId` bigint(20) NOT NULL AUTO_INCREMENT,
  `displayName` varchar(1000) DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `passwordEnc` varchar(1000) DEFAULT NULL,
  `tempToken` varchar(1000) DEFAULT NULL,
  `tempTokenExpiresAt` datetime DEFAULT NULL,
  PRIMARY KEY (`userId`),
  UNIQUE KEY `email_UNIQUE` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Final view structure for view `emailtosend`
--

/*!50001 DROP VIEW IF EXISTS `emailtosend`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `emailtosend` AS select `email`.`emailId` AS `emailId`,`email`.`to` AS `to`,`email`.`bcc` AS `bcc`,`email`.`cc` AS `cc`,`email`.`subject` AS `subject`,`email`.`body` AS `body` from `email` where (isnull(`email`.`sentDate`) and (`email`.`tryCount` < 3) and ((to_days(now()) - to_days(`email`.`insertedAt`)) < 30)) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-03-21 14:50:46
