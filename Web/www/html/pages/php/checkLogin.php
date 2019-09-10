<?php
	/*
	ERROR CODES:
		1: Internal error
		2: User not found
		3: Incorrect password
	*/
	include_once '../../credentials/credentials.php';
	include_once 'phplib.php';

	$fileName = basename(__FILE__);

	if(!isset($_POST["userName"]) || !isset($_POST["password"])){
		returnError($fileName, "Can not read user info", 1);
	}
	else{
		$userName = $_POST["userName"];
		$password = $_POST["password"];
		$link = mysqli_connect(DB_HOST, DB_USER, DB_PASSWORD, DB_NAME);

		if(!$link){
			returnError($fileName, "Can not connect to database to log in user", 1);
		}
		else{
			$stmt = "SELECT * FROM Users WHERE userName=?";
			$sentence = $link->stmt_init();

			if(!$sentence->prepare($stmt)){
				mysqli_close($link);
				returnError($fileName, "Can not prepare sentence to get user info", 1);
			}else{
				$sentence->bind_param('s', $userName);
				$sentence->execute();
				$result = $sentence->get_result();
				$sentence->close();

				if($result->num_rows != 1){
					mysqli_close($link);
					returnError($fileName, "User not found", 2);
				}else{
					while($row = $result->fetch_array(MYSQLI_ASSOC)){
						break;
					}

					if(password_verify($password, $row["password"])==false){
						mysqli_close($link);
						returnError($fileName, $userName." set incorrect password. Can not login", 3);
					}
					else{
						mysqli_close($link);
						$data = array("verified" => $row["verified"], "activated"=> $row["activated"], "dateIn"=>$row["dateIn"], "userName"=>$row["userName"], "mail"=>$row["mail"]);
						$array = array("success" => 1, "data"=>$data);
						echo json_encode($array);
					}
				}
			}
		}
	}
?>