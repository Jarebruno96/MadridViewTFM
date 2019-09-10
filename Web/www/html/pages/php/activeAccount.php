<?php
	/*
	ERROR CODES:
		1: Internal error

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
			returnError($fileName, "Can not connect to database", 1);
		}
		else{
			$stmt = "SELECT * FROM Users WHERE userName=?";
			$sentence = $link->stmt_init();

			if(!$sentence->prepare($stmt)){
				mysqli_close($link);
				returnError($fileName, "Can not prepare sentence to search userName", 1);
			}else{
				$sentence->bind_param("s", $userName);
				$sentence->execute();
				$result = $sentence->get_result();
				$sentence->close();

				if($result->num_rows == 0){
					mysqli_close($link);
					returnError($fileName, "User not found", 2);
				}
				else if($result->num_rows !=1){
					mysqli_close($link);
					returnError($fileName, "Something went wrong", 1);
				}
				else{
					$row = $result->fetch_array(MYSQLI_ASSOC);

					if(password_verify($password, $row["password"])==false){
						mysqli_close($link);
						returnError($fileName, "Password incorrect", 3);
					}
					else{
						$stmt = "UPDATE Users SET activated=1 WHERE userName=?";
						$sentence = $link->stmt_init();

						if(!$sentence->prepare($stmt)){
							mysqli_close($link);
							returnError($fileName, "Can not prepare sentence to cancel account", 1);
						}
						else{
							$sentence->bind_param("s", $userName);
							$sentence->execute();
							$sentence->close();
							mysqli_close($link);
							$array = array("success" => 1, "description"=>"Account has been actived");
							echo json_encode($array);
						}
					}
				}
			}
		}
	}

?>