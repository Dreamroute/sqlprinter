DROP TABLE IF EXISTS `smart_user`;
DROP TABLE IF EXISTS `smart_typehandler`;

CREATE TABLE `smart_user`
(
    `id`       bigint(0) NOT NULL AUTO_INCREMENT,
    `name`     varchar(32) NULL,
    `password` varchar(32) NULL,
    `version`  bigint(0) NULL DEFAULT 0,
    `birthday` datetime DEFAULT NULL,
    `gender`    tinyint(4) null,
    PRIMARY KEY (`id`)
);