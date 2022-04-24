<?php
$host = "localhost";
$db_user = "root"; // is important
$db_password = ""; //is important
//$db_name = "fredtsai";

$name = $_POST["db_name"];
$date = $_POST["date"];
$temp = $_POST["temp"];

$con = mysqli_connect($host,$db_user,$db_password,$name);

//$sql = "select * from temperature where date like '".$date."';";

//$result = mysqli_query($con,$sql);
$response = array();
/*
if(mysqli_num_rows($result)>0)
{
	$code = "reg_failed";
	$message = "User already exit .....";
	array_push($response,array("code"=>$code,"message"=>$message));
	echo jason_encode($response);
}
*/
//else
//{
	$sql = "insert into temperature values('".$date."','".$temp."')";
	$result = mysqli_query($con,$sql);
	$code = "reg_success";
	$message = "Thank you for register with us. Now you can login";
	array_push($response,array("code"=>$code,"message"=>$message));
	echo json_encode($response);
//}

mysqli_close($con);



?>