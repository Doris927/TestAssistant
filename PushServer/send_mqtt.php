
<?php
header("Content-Type: text/html;charset=utf-8");
require('SAM/php_sam.php');

//create a new connection object
$conn = new SAMConnection();

//start initialise the connection
$ip=$_REQUEST['ip'];
$port=$_REQUEST['port'];
$conn->connect(SAM_MQTT, array(SAM_HOST => $ip,
                               SAM_PORT => $port));
//create a new MQTT message with the output of the shell command as the body

$msgCpu = new SAMMessage($_REQUEST['title']);


//send the message on the topic cpu
$conn->send('topic://'.'gpio', $msgCpu);//这里修改topic
         
$conn->disconnect();

require_once 'functions.php';
$conn=connectDB();
if($conn){
    mysql_select_db(SELECT_DB);
    $time=time();
    $title=$_REQUEST['title'];
    $link=$_REQUEST['link'];
    mysql_query("INSERT INTO testinfo(title,link,time) VALUES('$title','$link',$time)");
}else{
    echo '数据库连接失败';
}
if(mysql_errno()){
    echo mysql_error();
}else{
    echo '考试咨询插入成功';
}

?>
