<?php
$servername = "localhost";
$username = "root";
$password = "";
//$dbname = "fredtsai";

$name = $_POST["db_name"];
// Create connection
$conn = new mysqli($servername, $username, $password, $name);

// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
} 

$sql = "SELECT date, temp FROM temperature";
$result = $conn->query($sql);
$response = array();

if ($result->num_rows > 0) {
    // output data of each row
    while($row = $result->fetch_assoc()) {
        array_push($response,array("date"=>$row["date"],"temp"=>$row["temp"]));
    }
    echo json_encode($response);
	
} else {
    echo "0 results";
}

$conn->close();

?>