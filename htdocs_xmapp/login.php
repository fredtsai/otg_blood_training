
<?php
require "init.php";

$account = $_POST["account"];
$password = $_POST["password"];


$sql = "select pid, name,email,sex from user_info where account like '".$account."' and password like '".$password."';";

$result = mysqli_query($con,$sql);
$response = array();

if(mysqli_num_rows($result)>0)
{
	$row = mysqli_fetch_row($result);
	$pid = $row[0];
	$name = $row[1];
	$email = $row[2];
	$sex = $row[3];
	$code = "login_success";
	array_push($response,array("code"=>$code,"pid"=>$pid,"name"=>$name,"email"=>$email,"sex"=>$sex));
	echo json_encode($response);
}
else
{
	$code = "login_failed";
	$message = "帳號錯誤，請重新登入";
	array_push($response,array("code"=>$code,"message"=>$message));
	echo json_encode($response);
}
mysqli_close($con);

?>