<?php
require "init.php";
$name = $_POST["name"];
mysqli_close($con);

$id = $_POST["id"];
$date = $_POST["date"];
$time = $_POST["time"];
$pressure = $_POST["pressure"];
$count = $_POST["count"];

// Create table

$test = mysqli_connect("127.0.0.1","root","",$name);

$sql = "insert into BreathTraining values('".$id."','".$date."','".$time."','".$pressure."','".$count."')";

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