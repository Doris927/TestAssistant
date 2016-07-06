<?php
/**
 * Created by PhpStorm.
 * User: Administrator
 * Date: 2015/4/5
 * Time: 15:04
 */
require_once 'config.php';
function connectDB(){
    return mysql_connect(MYSQL_HOST,MYSQL_USER,MYSQL_PW);
}