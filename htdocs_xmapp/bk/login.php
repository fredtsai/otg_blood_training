
<?php
require "init.php";

$account = $_POST["account"];
$password = $_POST["password"];

$sql = "select name,email from user_info where account like '".$account."' and password like '".$password."';";

$result = mysqli_query($con,$sql);
$response = array();

if(mysqli_num_rows($result)>0)
{
	$row = mysqli_fetch_row($result);
	$name = $row[0];
	$email = $row[1];
	$code = "login_success";
	array_push($response,array("code"=>$code,"name"=>$name,"email"=>$email));
	echo json_encode($response);
}
else
{
	$code = "login_failed";
	$message = "User not found....Please try again";
	array_push($response,array("code"=>$code,"message"=>$message));
	echo json_encode($response);
}
mysqli_close($con);

?>