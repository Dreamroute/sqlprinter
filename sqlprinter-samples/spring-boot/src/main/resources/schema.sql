DROP TABLE IF EXISTS `smart_user`;

CREATE TABLE `smart_user`
(
    `id`       bigint(0)   NOT NULL AUTO_INCREMENT,
    `name`     varchar(32) NULL,
    `password` varchar(32) NULL,
    `version`  bigint(0)   NULL DEFAULT 0,
    PRIMARY KEY (`id`)
);

INSERT INTO `smart_user`(`id`, `name`, `password`, `version`) VALUES (1, 'w.dehai', '123456', 0);