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

			$userSearch = array('userName' => $userName);
			$result = $collection->count($userSearch);

			if($result==0){

				$item = array('name' => $itemName, 'type'=>$itemType, 'image'=>$itemImage);
				$query  = array('userName'=> $userName, 'favourites' => [$item]);
				$result = $collection->insertOne($query);
				
				$response = array('sucess' => 1, 'description'=>"Added to favourites");
				echo json_encode($response);
			}
			else{

				#Check if it is already inserted

				$query  = array('userName' => $userName, 'favourites.name'=>$itemName, 'favourites.type'=>$itemType, 'favourites.image'=>$itemImage);
				$result = $collection->count($query);

				if($result==0){

					$itemQuery  = array('favourites' => array('name' => $itemName, 'type'=>$itemType, 'image'=>$itemImage));
					$result = $collection->updateOne($userSearch, array('$push'=>$itemQuery));

					$response = array('sucess' => 1, 'description'=>"Added to favourites");
					echo json_encode($response);
					
				}
				else{
					$response = array('sucess' => 1, 'description'=>"Already added");
					echo json_encode($response);
				}
			}

		}catch(Exception $e){
			echo $e->getMessage();
			returnError($fileName, "Can not prepare sentence to cancel account", 1);
		}
	}

?>