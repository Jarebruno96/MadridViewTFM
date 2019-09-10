<?php

	include_once '../../credentials/credentials.php';
	include_once 'phplib.php';

	$fileName = basename(__FILE__);

	if(!isset($_POST["mail"])){
		returnError($fileName, "Can not read user info", 1);
	}
	else{
		$mail = $_POST["mail"];
		//$mail = "juanarellano1996@gmail.com";
		$link = mysqli_connect(DB_HOST, DB_USER, DB_PASSWORD, DB_NAME);

		if(!$link){
			returnError($fileName, "Can not connect to database", 1);
		}
		else{
			$stmt = "SELECT * FROM Users WHERE mail=?";
			$sentence = $link->stmt_init();

			if(!$sentence->prepare($stmt)){
				mysqli_close($link);
				returnError($fileName, "Can not change password", 1);
			}
			else{
				$sentence->bind_param('s', $mail);
				$sentence->execute();
				$result = $sentence->get_result();
				$sentence->close();

				if($result->num_rows == 0){
					mysqli_close($link);
					returnError($fileName, "Can not find user by this mail", 7);
				}	
				else if($result->num_rows != 1){
					mysqli_close($link);
					returnError($fileName, "Something went wrong with mail: ".$mail, 1);
				}
				else{
					$row = $result->fetch_array(MYSQLI_ASSOC);
					$password = generateRandomString(10);
					$hash = password_hash($password, PASSWORD_DEFAULT);
					$stmt = "UPDATE Users SET password=? WHERE mail=?";
					$sentence = $link->stmt_init();

					if(!$sentence->prepare($stmt)){
						mysqli_close($link);
						returnError($fileName, "Can not set new password to ".$mail, 1);
					}
					else{
						$sentence->bind_param('ss', $hash, $mail);
						$sentence->execute();
						$sentence->close();
						mysqli_close($link);

						$body = "<h1>Hola ".$row["userName"].".</h1>
								<p>En este correo se le comunica la nueva contraseña de su cuenta. Esta contraseña se ha generado de forma automática, por tanto se le recomienda cambiar la contraseña y poner una que solo usted conozca</p>
								<h3 style=\"vertical-align:absbottom; display:inline;\">Contraseña: </h3> 
								<h3 style=\"vertical-align:absbottom; display:inline;\">".$password."</h3>
								<p>Un saludo, Madrid View.</p>";

						if(!sendMail($mail, $row["userName"], "Madrid View", $body)){
							returnError($fileName, "Can not send mail with new password to ".$mail, 1);
						}
						else{
							$array = array("success" => 1, "description"=>"Password changed");
							echo json_encode($array);
						}
					}
				}
			}
		}
	}
?>