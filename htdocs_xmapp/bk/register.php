<?php
require "init.php";
$name = $_POST["name"];
$email = $_POST["email"];
$account = $_POST["account"];
$password = $_POST["password"];

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
	$message = "User name already exit";
	array_push($response,array("code"=>$code,"message"=>$message));
	echo json_encode($response);
}
else if(mysqli_num_rows($result1)>0)
{
	$code = "reg_failed";
	$message = "Email already exit";
	array_push($response,array("code"=>$code,"message"=>$message));
	echo json_encode($response);
}
else if(mysqli_num_rows($result2)>0)
{
	$code = "reg_failed";
	$message = "Account already exit";
	array_push($response,array("code"=>$code,"message"=>$message));
	echo json_encode($response);
}
else
{
	$sql = "insert into user_info values('".$name."','".$email."','".$account."','".$password."')";
	$result = mysqli_query($con,$sql);
	$code = "reg_success";
	$message = "Thank you for register with us. Now you can login";
	array_push($response,array("code"=>$code,"message"=>$message));
	echo json_encode($response);
}
mysqli_close($con);

// Create database

// Create connection
$conn = new mysqli("127.0.0.1","root","","");
// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
} 

$sql = "CREATE DATABASE {$name}";
if ($conn->query($sql) === TRUE) {
    echo "Database created successfully";
	echo '<br>';  
} else {
    echo "Error creating database: " . $conn->error;
	echo '<br>';  
}
$conn->close();

// Create table

$test = mysqli_connect("127.0.0.1","root","",$name);

$sql="CREATE TABLE BreathTraining (id varchar(75), date varchar(75),time varchar(75),pressure varchar(75),count varchar(75))";

if (mysqli_query($test,$sql))
{
echo "Table persons created successfully";
echo '<br>';  
}
else
{
echo "Error creating table: " . mysqli_error($test);
echo '<br>';  
}
$test->close();

?>