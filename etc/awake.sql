-- MySQL dump 10.13  Distrib 5.6.24, for Win64 (x86_64)
--
-- Host: localhost    Database: awake
-- ------------------------------------------------------
-- Server version	5.6.26-log

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
-- Dumping data for table `female`
--

LOCK TABLES `female` WRITE;
/*!40000 ALTER TABLE `female` DISABLE KEYS */;
/*!40000 ALTER TABLE `female` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `house`
--

LOCK TABLES `house` WRITE;
/*!40000 ALTER TABLE `house` DISABLE KEYS */;
INSERT INTO `house` VALUES (22,'220/11',NULL,NULL),(23,'220/11',NULL,NULL),(25,'220/11',NULL,NULL),(27,'220/11',NULL,NULL),(29,'220/11',NULL,NULL),(30,'220/11',NULL,NULL),(31,'220/11',NULL,NULL),(32,'220/11',NULL,NULL),(33,'220/11',NULL,NULL),(34,'220/11',NULL,NULL),(35,'220/11',NULL,NULL),(36,'220/11',NULL,NULL),(37,'220/11',NULL,NULL),(38,'220/11',NULL,NULL),(39,'220/11',NULL,NULL);
/*!40000 ALTER TABLE `house` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `house_entrance`
--

LOCK TABLES `house_entrance` WRITE;
/*!40000 ALTER TABLE `house_entrance` DISABLE KEYS */;
/*!40000 ALTER TABLE `house_entrance` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `house_owner`
--

LOCK TABLES `house_owner` WRITE;
/*!40000 ALTER TABLE `house_owner` DISABLE KEYS */;
INSERT INTO `house_owner` VALUES (27,20,33),(28,20,34),(29,22,35),(30,22,36),(31,23,37),(32,23,38),(33,25,39),(34,25,40),(35,27,41),(36,27,42),(37,29,43),(38,29,44),(39,31,45),(40,31,46);
/*!40000 ALTER TABLE `house_owner` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `male`
--

LOCK TABLES `male` WRITE;
/*!40000 ALTER TABLE `male` DISABLE KEYS */;
/*!40000 ALTER TABLE `male` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `owner`
--

LOCK TABLES `owner` WRITE;
/*!40000 ALTER TABLE `owner` DISABLE KEYS */;
INSERT INTO `owner` VALUES (35,80),(36,20),(37,80),(38,20),(39,80),(40,20),(41,80),(42,20),(43,80),(44,20),(45,80),(46,20);
/*!40000 ALTER TABLE `owner` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `person`
--

LOCK TABLES `person` WRITE;
/*!40000 ALTER TABLE `person` DISABLE KEYS */;
/*!40000 ALTER TABLE `person` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `window`
--

LOCK TABLES `window` WRITE;
/*!40000 ALTER TABLE `window` DISABLE KEYS */;
INSERT INTO `window` VALUES (27,'small',30),(28,'large',30),(29,'small',32),(30,'large',32),(31,'small',33),(32,'large',33),(33,'small',34),(34,'large',34),(35,'small',35),(36,'large',35),(37,'small',36),(38,'large',36),(39,'small',37),(40,'large',37),(41,'small',38),(42,'large',38),(43,'small',39),(44,'large',39);
/*!40000 ALTER TABLE `window` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-03-16 16:18:15
