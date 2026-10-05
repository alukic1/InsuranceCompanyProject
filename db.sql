-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `mydb` DEFAULT CHARACTER SET utf8mb3 ;
USE `mydb` ;

-- -----------------------------------------------------
-- Table `mydb`.`insurance_type`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`insurance_type` (
  `idinsurance_type` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `description` VARCHAR(1000) NULL DEFAULT NULL,
  `price` DECIMAL(38,2) NOT NULL,
  PRIMARY KEY (`idinsurance_type`))
ENGINE = InnoDB
AUTO_INCREMENT = 3
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `mydb`.`user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`user` (
  `iduser` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `lastname` VARCHAR(60) NOT NULL,
  `username` VARCHAR(45) NOT NULL,
  `email` VARCHAR(320) NOT NULL,
  `password` VARCHAR(245) NOT NULL,
  `blocked` BIT(1) NULL DEFAULT NULL,
  `role` VARCHAR(20) NOT NULL,
  `jmbg` VARCHAR(255) NOT NULL,
  `failed_login_attempts` INT NULL DEFAULT NULL,
  PRIMARY KEY (`iduser`),
  UNIQUE INDEX `username_UNIQUE` (`username` ASC) VISIBLE,
  UNIQUE INDEX `email_UNIQUE` (`email` ASC) VISIBLE)
ENGINE = InnoDB
AUTO_INCREMENT = 4
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `mydb`.`insurance_policy`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`insurance_policy` (
  `idinsurance_policy` BIGINT NOT NULL AUTO_INCREMENT,
  `iduser` BIGINT NOT NULL,
  `idtype` BIGINT NOT NULL,
  `start_date` DATE NOT NULL,
  `end_date` DATE NOT NULL,
  `price_paid` DECIMAL(38,2) NULL DEFAULT NULL,
  `pdf_url` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`idinsurance_policy`),
  INDEX `fk_insurancepolicy_user_idx` (`iduser` ASC) VISIBLE,
  INDEX `fk_insurancepolicy_type_idx` (`idtype` ASC) VISIBLE,
  CONSTRAINT `FKami1vhcpaexp9f74xe16rg4hv`
    FOREIGN KEY (`idtype`)
    REFERENCES `mydb`.`insurance_type` (`idinsurance_type`),
  CONSTRAINT `FKh14vm0i006soeg7pvg4rlba5v`
    FOREIGN KEY (`iduser`)
    REFERENCES `mydb`.`user` (`iduser`))
ENGINE = InnoDB
AUTO_INCREMENT = 5
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `mydb`.`pending_payment`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`pending_payment` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `amount` DECIMAL(38,2) NULL DEFAULT NULL,
  `created_at` DATETIME(6) NULL DEFAULT NULL,
  `insurance_type_id` BIGINT NULL DEFAULT NULL,
  `session_id` VARCHAR(255) NULL DEFAULT NULL,
  `user_id` BIGINT NULL DEFAULT NULL,
  `end_date` DATE NULL DEFAULT NULL,
  `start_date` DATE NULL DEFAULT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 9
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `mydb`.`transaction`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`transaction` (
  `idtransaction` BIGINT NOT NULL AUTO_INCREMENT,
  `idpolicy` BIGINT NOT NULL,
  `amount` DECIMAL(38,2) NOT NULL,
  `payment_status` ENUM('SUCCESS', 'FAILED', 'PENDING') NOT NULL,
  `time` TIMESTAMP NOT NULL,
  PRIMARY KEY (`idtransaction`),
  INDEX `fk_transaction_policy_idx` (`idpolicy` ASC) VISIBLE,
  CONSTRAINT `FKr7fs4e3pbbm39rggl3gx8vp93`
    FOREIGN KEY (`idpolicy`)
    REFERENCES `mydb`.`insurance_policy` (`idinsurance_policy`))
ENGINE = InnoDB
AUTO_INCREMENT = 3
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `mydb`.`verification_code`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`verification_code` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `code` VARCHAR(255) NULL DEFAULT NULL,
  `expires_at` DATETIME(6) NULL DEFAULT NULL,
  `username` VARCHAR(255) NULL DEFAULT NULL,
  `failed_attempts` INT NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 12
DEFAULT CHARACTER SET = utf8mb3;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
