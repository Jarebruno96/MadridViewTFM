<?php
	include_once 'phplib.php';
	include_once '../../credentials/credentials.php';

	$fileName = basename(__FILE__);

	if(!isset($_POST["userName"]) || !isset($_POST["oldPassword"]) || !isset($_POST["newPassword1"]) || !isset($_POST["newPassword2"])){
		returnError($fileName, "Can not read user info", 1);
	}
	else{
		$userName = $_POST["userName"];
		$oldPassword = $_POST["oldPassword"];
		$newPassword1 = $_POST["newPassword1"];
		$newPassword2 = $_POST["newPassword2"];

		if($newPassword1 != $newPassword2){
			returnError($fileName, $userName." tried to change password but they are not the same", 6);
		}
		else{
			$link = mysqli_connect(DB_HOST, DB_USER, DB_PASSWORD, DB_NAME);

			if(!$link){
				returnError($fileName, "Can not connect to database", 1);
			}
			else{
				$stmt = "SELECT * FROM Users WHERE userName=?";
				$sentence = $link->stmt_init();

				if(!$sentence->prepare($stmt)){
					mysqli_close($link);
					returnError($fileName, "Can not prepare sentence to get user info", 1);
				}
				else{
					$sentence->bind_param('s', $userName);
					$sentence->execute();
					$result = $sentence->get_result();
					$sentence->close();

					if($result->num_rows == 0){
						mysqli_close($link);
						returnError($fileName, "User not found", 2);
					}
					else if($result->num_rows != 1){
						mysqli_close($link);
						returnError($fileName, "Something went wrong", 1);
					}
					else{

						while($row = $result->fetch_array(MYSQLI_ASSOC)){
							break;
						}

						if(password_verify($oldPassword, $row["password"])==false){
							mysqli_info($link);
							returnError($fileName, "Old password is not correct", 3);
						}
						else{
							$hash = password_hash($newPassword1, PASSWORD_DEFAULT);
							$stmt = "UPDATE Users SET password=? WHERE userName=?";
							$sentence = $link->stmt_init();

							if(!$sentence->prepare($stmt)){
								mysqli_close($link);
								returnError($fileName, "Can not save the new password", 1);
							}
							else{
								$sentence->bind_param('ss', $hash, $userName);
								$sentence->execute();
								$sentence->close();
								mysqli_close($link);

								$array = array("success" => 1,  "description"=>"Password correctly changed");
								echo json_encode($array);
							}
						}
					}
				}
			}
		}
	}
?>