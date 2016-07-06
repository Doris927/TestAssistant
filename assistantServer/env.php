<?php
header('Content-Type: text/plain; charset=UTF-8');

if(count($_ENV) != 0){
	$conn=mysql_connect('10.9.1.188:3306', '4n2UFVHugTqfsZsh', 'u6yHMHgUUpzc9Qvs');

	if($conn){
	    echo 'success';
	    mysql_select_db('cf_71415529_4256_4e43_a953_0d17e9bd5182');
	    $res = mysql_query('select * from student');
	    $suibian = mysql_fetch_assoc($res);
	    print_r($suibian);
	}
	else{
	    echo 'fail';
	}
}
else{
	$conn=mysql_connect('172.2.239.146:3306', 'gemin', '18842660927');

	if($conn){
	    echo 'success';
	    mysql_select_db('test');
	    $res = mysql_query('select * from student');
	    $suibian = mysql_fetch_assoc($res);
	    print_r($suibian);
	}
	else{
	    echo 'fail';
	}
}

echo "This string is added at 4/8/2015 by local zhangmenghao";

echo "System Environment:\n\n";
foreach ($_ENV as $key => $value) {
  echo "{$key}: {$value}\n";
}
