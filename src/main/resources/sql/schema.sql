USE `dbtest`;

CREATE TABLE `project_dependency` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `project` VARCHAR(45) NULL COMMENT 'mvn project name',
  `library_name` VARCHAR(255) NULL COMMENT 'idea library name',
  `group_id` VARCHAR(255) NULL COMMENT 'dependency groupId',
  `artifact_id` VARCHAR(255) NULL COMMENT 'dependency artifactId',
  `version` VARCHAR(255) NULL COMMENT 'dependency version',
  `mvnrepository_url` VARCHAR(255) NULL COMMENT 'mvnrepository.com home page',
  `dependency_xml` VARCHAR(1024) NULL COMMENT 'mvn <dependency>',
  `central_url` VARCHAR(1024) NULL COMMENT 'https://repo1.maven.org/maven2/',
  `update_time` DATETIME NULL DEFAULT now() COMMENT 'last update time',
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_bin;

CREATE TABLE `mvnrepository_dependency` (
  `id` INT NOT NULL AUTO_INCREMENT,  
  `library_name` VARCHAR(255) NOT NULL COMMENT 'idea library name',
  `group_id` VARCHAR(255) NULL COMMENT 'dependency groupId',
  `artifact_id` VARCHAR(255) NULL COMMENT 'dependency artifactId',
  `version` VARCHAR(255) NULL COMMENT 'dependency version',
  `mvnrepository_url` VARCHAR(255) NULL COMMENT 'mvnrepository.com home page',
  `dependency_xml` VARCHAR(1024) NULL COMMENT 'mvn <dependency>',
  `central_url` VARCHAR(1024) NULL COMMENT 'https://repo1.maven.org/maven2/',
  `update_time` DATETIME NULL DEFAULT now() COMMENT 'last update time',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `library_name_UNIQUE` (`library_name` ASC) VISIBLE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_bin;

CREATE TABLE `mvn_repository` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `repository` VARCHAR(255) NULL COMMENT 'repository name',
  `repository_url` VARCHAR(255) NULL COMMENT 'repository url',
  `update_time` DATETIME NULL DEFAULT now() COMMENT 'last update time',
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_bin;

CREATE TABLE `mvn_dependency` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `project` VARCHAR(45) NULL COMMENT 'mvn project name',
  `library_name` VARCHAR(255) NULL COMMENT 'idea library name',
  `repository` VARCHAR(255) NULL COMMENT 'mvn repository',
  `library_url` VARCHAR(255) NULL COMMENT 'mvn library url',
  `check_result` TINYINT NULL COMMENT 'check result',
  `update_time` DATETIME NULL DEFAULT now() COMMENT 'last update time',
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_bin;
