<?php

$name = "test111";
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

//================================================================

// Create table

$test = new mysqli("127.0.0.1","root","",$name);
// Check connection
if ($test->connect_error) {
    die("Connection failed: " . $test->connect_error);
} 

$sql="CREATE TABLE Persons(FirstName CHAR(30),LastName CHAR(30),Age INT)";

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