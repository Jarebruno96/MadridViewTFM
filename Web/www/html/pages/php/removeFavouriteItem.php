<?php
	
	require __DIR__.'./../../vendor/autoload.php';
	include "../../credentials/credentials.php";
	include_once 'phplib.php';

	$fileName = basename(__FILE__);


	if(!isset($_POST["userName"]) || !isset($_POST["name"]) || !isset($_POST["type"]) || !isset($_POST["image"])){
		returnError($fileName, "Can not read info", 1);
	}
	else{
		$userName = $_POST["userName"];
		$itemName = $_POST["name"];
		$itemType = $_POST["type"];
		$itemImage = $_POST["image"];

		$uri = "mongodb://".MONGO_USER.":".MONGO_PASSWORD."@".MONGO_HOST.":27017/".MONGO_DB;
		$client = new MongoDB\Client($uri);

		try{

			$dbs = $client->listDatabases();
			$collection = $client->madridViewDB->Favourites;

			$query  = array('favourites' => array('name'=>$itemName, 'type'=>$itemType, 'image'=>$itemImage));
			$result = $collection->updateOne(array('userName' => $userName), array('$pull' => $query));
			
			$response = array('success' => 1, "description" => "Removed");
			echo json_encode($response);

		}catch(Exception $e){
			echo $e->getMessage();
			returnError($fileName, "Can not prepare sentence to cancel account", 1);
		}
	}

?>