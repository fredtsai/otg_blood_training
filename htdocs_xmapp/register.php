<?php
require "init.php";
$name = $_POST["name"];
$email = $_POST["email"];
$account = $_POST["account"];
$password = $_POST["password"];
$sex = $_POST["sex"];
$age = $_POST["age"];

$sql = "select * from user_info where name like '".$name."';";
$sql1 = "select * from user_info where email like '".$email."';";
$sql2 = "select * from user_info where account like '".$account."';";

$result = mysqli_query($con,$sql);
$result1 = mysqli_query($con,$sql1);
$result2 = mysqli_query($con,$sql2);

$response = array();

if(mysqli_num_rows($result)>0)
{
	$code = "reg_failed";
	$message = "使用者名稱已存在";
	array_push($response,array("code"=>$code,"message"=>$message));
	echo json_encode($response);
}
else if(mysqli_num_rows($result1)>0)
{
	$code = "reg_failed";
	$message = "電子郵件已存在";
	array_push($response,array("code"=>$code,"message"=>$message));
	echo json_encode($response);
}
else if(mysqli_num_rows($result2)>0)
{
	$code = "reg_failed";
	$message = "帳號已存在";
	array_push($response,array("code"=>$code,"message"=>$message));
	echo json_encode($response);
}
else
{
	$sql = "insert into `user_info`(`name`, `age`, `sex`, `email`, `account`, `password`) values('".$name."','".$age."','".$sex."','".$email."','".$account."','".$password."')";
	$result = mysqli_query($con,$sql);
	$code = "reg_success";
	$message = "註冊成功";
	array_push($response,array("code"=>$code,"message"=>$message));
	echo json_encode($response);
}
mysqli_close($con);


?>