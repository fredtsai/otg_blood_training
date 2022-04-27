<?php

require "init.php";
$con->close();

$host = "localhost";
$db_user = "root"; // is important
$db_password = ""; //is important
$table_name = "test_result";


$pid = $_POST["pid"];


$con = mysqli_connect($host,$db_user,$db_password,$db_name);

$sql = "SELECT * FROM blood_table WHERE `pid` = '$pid'";

$result = mysqli_query($con,$sql);
$response = array();

if ($result->num_rows > 0) {
    // output data of each row
    while($row = $result->fetch_assoc()) {
        array_push($response,array("pid"=>$row["pid"],"date"=>$row["date"],"sp"=>$row["sp"],"dp"=>$row["dp"],"hr"=>$row["hr"]));
    }
    echo json_encode($response);
	
} else {
    echo "0 results";
}

$con->close();

?>