<?php
require "init.php";

$pid = $_POST["pid"];
$date = $_POST["date"];
$sp = $_POST["sp"];
$dp = $_POST["dp"];
$hr = $_POST["hr"];

$sql = "INSERT INTO `blood_table`(`pid`, `date`, `sp`, `dp`, `hr`) VALUES ('$pid','$date','$sp','$dp','$hr')";

echo $sql;
$result = mysqli_query($con,$sql);
$response = array();

mysqli_close($con);

?>