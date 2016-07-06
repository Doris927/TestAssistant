<?php
/**
 * Created by PhpStorm.
 * User: Administrator
 * Date: 2015/4/9
 * Time: 15:37
 */
require_once 'config.php';
require_once 'functions.php';
$result=array();//返回的结果
$action=$_GET['action'];//接口
$userID = @$_POST['userID'] ? $_POST['userID'] : -1;

if (empty($action)) {
    $result = array('data'=>NULL, 'info'=>'出错啦！', 'code'=>-201);
    exit(json_encode($result));
}

$conn=connectDB();//连接数据库
if($conn){
    mysql_select_db(SELECT_DB);
}else{
    $result = array('data'=>NULL, 'info'=>'数据库连接失败！', 'code'=>-202);
    exit(json_encode($result));
}


if($action=='register'){//////用户注册
     
    $userName=$_POST['uname'];
    $userPassword=$_POST['upassword'];
    $queryFind="SELECT COUNT(*) FROM users WHERE name='$userName'";
    $countResult=mysql_query($queryFind);
    $countResultArr=mysql_fetch_assoc($countResult);
    $count=$countResultArr['COUNT(*)'];
    if($count>0){
        $result = array('data'=>NULL, 'info'=>'注册失败，用户名已经被使用', 'code'=>-203);
        exit(json_encode($result));
    }else{
        $queryInsert="INSERT INTO users(name,password) VALUES('$userName','$userPassword')";
        mysql_query($queryInsert);
        $newId=mysql_insert_id();
        mysql_query("insert into friends(id1,id2) values ($newId,$newId)");
        if(mysql_errno()) {
            $result = array('data'=>NULL, 'info'=>'注册失败，插入数据库时发生错误', 'code'=>-204);
            exit(json_encode($result));
        }else{
            session_start();
             $_SESSION['userID'] = $newId;
            //获取当前session id
            $sessionID=session_id();
            $_SESSION['$sessionID'] = $sessionID;
            $result = array('data'=>NULL, 'info'=>'注册成功', 'code'=>200,'sessionID'=>$sessionID,"userID"=>$newId);
            
            exit(json_encode($result));
        }
    }


}elseif($action=='login'){
    session_start();

    $userName=$_POST['uname'];
    $userPassword=$_POST['upassword'];
    $check_query = mysql_query("select id from users where name='$userName' and
    password='$userPassword' limit 1");
    if($findResult = mysql_fetch_assoc($check_query)){

        $_SESSION['userID'] = $findResult['id'];
        //获取当前session id
        $sessionID=session_id();
        $_SESSION['$sessionID'] = $sessionID;

        $result = array('data'=>NULL, 'info'=>'登录成功', 'code'=>200,'sessionID'=>$sessionID,"userID"=>$findResult['id']);
        exit(json_encode($result));
    }else{
        $result = array('data'=>NULL, 'info'=>'用户名或密码错误', 'code'=>-206);
        exit(json_encode($result));
    }
}elseif($action=="checkLogin"){

    if(getUserInfo($userID)){
        $result = array('data'=>NULL, 'info'=>'用户已登录', 'code'=>200);
        exit(json_encode($result));
    }else{
        $result = array('data'=>NULL, 'info'=>'用户名登录信息过期，请重新登录', 'code'=>-207);
        exit(json_encode($result));
        return false;
    }

}else if($action=="addFriend"){
    if(getUserInfo($userID)){
        $friendName=$_POST['friendName'];
        $check_query = mysql_query("select id from users where name='$friendName' limit 1");

        if($findResult=mysql_fetch_assoc($check_query)){
            $friendID=$findResult['id'];
            $check_query2=mysql_query("select id1 from friends where id2=$friendID and id1=$userID limit 1");
            if(mysql_fetch_assoc($check_query2)){
                $result = array('data'=>NULL, 'info'=>'该用户已经是您的好友了', 'code'=>-209);
                exit(json_encode($result));

            }else{
                mysql_query("insert into friends(id1,id2) VALUES($userID,$friendID)");
                $result = array('data'=>NULL, 'info'=>'添加好友成功', 'code'=>200);
                exit(json_encode($result));
            }

        }else{
            $result = array('data'=>NULL, 'info'=>'该用户不存在', 'code'=>-208);
            exit(json_encode($result));
        }


    }else{
        $result = array('data'=>NULL, 'info'=>'用户名登录信息过期，请重新登录', 'code'=>-207);
        exit(json_encode($result));

    }
}else if($action=='addContent'){
    if(getUserInfo($userID)){
        $content=$_POST['content'];
        //echo $content;
        $time=intval(time());
        //echo $time;
        mysql_query("insert into content(content,userID,time) VALUES('$content',$userID,$time)");
        if(mysql_errno()) {
            $result = array('data'=>NULL, 'info'=>'发送状态失败，插入数据库时发生错误', 'code'=>-210);
            exit(json_encode($result));
        }else{
            $result = array('data'=>NULL, 'info'=>'发送状态成功', 'code'=>200);
            exit(json_encode($result));
        }

    }
}else if($action=='getContent'){

    if(getUserInfo($userID)){

        $findQuery=mysql_query("select b.content, c.name as friendName,b.id as contentID,c.id as friendID,b.time,avatar
        from friends as a left join content as b on a.id2 = b.userID left join users as c on a.id2 = c.id
        where a.id1=$userID and b.content is not null
        order by b.time desc limit 20");

        $arr =  array();
        while($row = mysql_fetch_assoc($findQuery)){
            $arr[] =  $row;
        }

        
        if(mysql_errno()) {
            $result = array('data'=>null, 'info'=>'获得状态失败', 'code'=>-211);
            exit(json_encode($result));
        }else{
            $result = array('data'=>json_encode($arr), 'info'=>'获得状态成功', 'code'=>200);
            exit(json_encode($result));
        }
    }
}else if($action=='addComment'){
    if(getUserInfo($userID)){
        $contentID=$_POST['contentID'];
        $comment=$_POST['comment'];
        $time=intval(time());
        mysql_query("insert into comments(contentID,comment,userID,time) VALUES($contentID,'$comment',$userID,$time)");
        if(mysql_errno()) {
            $result = array('data'=>NULL, 'info'=>'发送评论失败，插入数据库时发生错误', 'code'=>-212);
            exit(json_encode($result));
        }else{
            $result = array('data'=>NULL, 'info'=>'发送评论成功', 'code'=>200);
            exit(json_encode($result));
        }
    }
}else if($action=='getComment'){
    if(getUserInfo($userID)) {
        $contentID=$_POST['contentID'];
        $findQuery=mysql_query("select comment,name,time,avatar from comments LEFT JOIN users on users.id=userID where contentID=$contentID and comment is not null");
        $arr =  array();
        while($row = mysql_fetch_assoc($findQuery)){
            $arr[] =  $row;
        }

        if(mysql_errno()) {
            $result = array('data'=>null, 'info'=>'获得评论失败', 'code'=>-213);
            exit(json_encode($result));
        }else{
            $result = array('data'=>json_encode($arr), 'info'=>'获得评论成功', 'code'=>200);
            exit(json_encode($result));
        }
    }
}else if($action=='uploadAvatar'){
    
    if(getUserInfo($userID)){
        if ($_FILES["file"]["error"] > 0)
        {
            echo "Error: " . $_FILES["file"]["error"] . "<br />";
        }
        else
        {
            $type = $_FILES["file"]["type"];
            $size = $_FILES['file']['size'];
            $tmp=$_FILES["file"]["tmp_name"];
            //$fp = fopen($tmp,'rb');
            //$data = bin2hex(fread($fp,$size));
        
            
            
        
        
            
                
                if(!is_dir('upload')){
                    mkdir('upload');
                }
                move_uploaded_file($_FILES["file"]["tmp_name"],
                'upload/' . $_FILES["file"]["name"]);
                $path='upload/' . $_FILES["file"]["name"];
                mysql_query("update users set avatar='$path' WHERE id=$userID");
                if(mysql_errno()) {
                    $result = array('data'=>null, 'info'=>'头像插入数据库失败', 'code'=>-215);
                    exit(json_encode($result));
                }else{
                    $result = array('data'=>$path, 'info'=>'设置头像成功', 'code'=>200);
                    exit(json_encode($result));
                }
                 
           
            
    
    
           
        }
    }
   
    
}else if($action=="getTestInfo"){
    if(getUserInfo($userID)) {
        $findQuery=mysql_query("select title,link from testinfo WHERE title is not NULL order by time desc limit 5");
        $arr =  array();
        while($row = mysql_fetch_assoc($findQuery)){
            $arr[] =  $row;
        }

        if(mysql_errno()) {
            $result = array('data'=>null, 'info'=>'获得考试资讯失败', 'code'=>-214);
            exit(json_encode($result));
        }else{
            $result = array('data'=>json_encode($arr), 'info'=>'获得考试资讯成功', 'code'=>200);
            exit(json_encode($result));
        }
    }
}else if($action=='changePassword'){
    if(getUserInfo($userID)) {
        $originalPassword=$_POST['originalPassword'];
        $userPassword=$_POST['newPassword'];
        $queryFind="SELECT COUNT(*) FROM users WHERE id=$userID AND password='$originalPassword'";
        $countResult=mysql_query($queryFind);
        $countResultArr=mysql_fetch_assoc($countResult);
        $count=$countResultArr['COUNT(*)'];
        if($count>0){
            mysql_query("update users set password='$userPassword' WHERE id=$userID");
            if(mysql_errno()) {
                $result = array('data'=>NULL, 'info'=>'修改密码失败，插入数据库时发生错误', 'code'=>-215);
                exit(json_encode($result));
            }else{
                $result = array('data'=>NULL, 'info'=>'修改密码成功', 'code'=>200,);
                exit(json_encode($result));
            }
        }else{
            $result = array('data'=>NULL, 'info'=>'密码错误', 'code'=>-216,);
            exit(json_encode($result));

        }
        
    }
    
}
else{
    $result = array('data'=>NULL, 'info'=>'出错啦！', 'code'=>-205);
    exit(json_encode($result));
}


function getUserInfo($userID){

//获取客户端传递的session标识
    $sessionID=$_POST["sessionID"];
    session_id($sessionID);
//将会根据session id获得原来的session
    session_start();
//获取服务器端原来session记录的username,并且根据客户端传过来的username比较进行验证操作
    $sessionUserID=$_SESSION['userID'];
    //echo $sessionUserID;
    if($userID==$sessionUserID){
        return true;
    } else {
        $result = array('data'=>NULL, 'info'=>'用户名登录信息过期，请重新登录', 'code'=>-207);
        exit(json_encode($result));
        return false;
    }
}