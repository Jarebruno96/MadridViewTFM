<?php
	/*
	ERROR CODES:
		1: Internal error
		4: Username already exists
		5: Mail already exists
		6: Passwords are not the same
	*/

	include_once '../../credentials/credentials.php';
	include_once 'phplib.php';
	
	$fileName = basename(__FILE__);

	if (!isset($_POST["userName"]) || !isset($_POST["mail"]) || !isset($_POST["password1"]) || !isset($_POST["password2"])){
		returnError($fileName, "Can not detect read user info", 1);
	}
	else{

		$userName = $_POST["userName"];
		$password1 = $_POST["password1"];
		$password2 = $_POST["password2"];
		$mail = $_POST["mail"];

		#$userName = "Jarebruno";
		#$password1 = "123";
		#$password2 = "123";
		#$mail = "juanbare@ucm.es";

		$ip = SERVER_IP;

		$link = mysqli_connect(DB_HOST, DB_USER, DB_PASSWORD, DB_NAME);

		if(mysqli_connect_errno($link)){
			returnError($fileName, "Sorry, can not connect to database. ".mysqli_connect_errno(), 1);
		}
		else{
			$stmt = "SELECT * FROM Users WHERE userName=?";
			$sentence = $link->stmt_init();

			if(!$sentence->prepare($stmt)){
				mysqli_close($link);
				returnError($fileName, "Sorry, can not prepare sentence to check if user name already exists", 1);
			}
			else{
				$sentence->bind_param('s', $userName);
				$sentence->execute();
				$result = $sentence->get_result();
				$sentence->close();

				if($result->num_rows != 0){
					mysqli_close($link);
					returnError($fileName, "Sorry, User name is taken", 4);
				}
				else{
					$stmt = "SELECT * FROM Users WHERE mail=?";
					$sentence = $link->stmt_init();

					if(!$sentence->prepare($stmt)){
						mysqli_close($link);
						returnError($fileName, "Sorry, can not prepare sentence to check if mail already exists", 5);
					}
					else{
						$sentence->bind_param('s', $mail);
						$sentence->execute();
						$result = $sentence->get_result();
						$sentence->close();

						if($result->num_rows != 0){
							mysqli_close($link);
							returnError($fileName, "Sorry, mail is taken", 1);
						}
						else{
							if($password1 != $password2){
								mysqli_close($link);
								returnError($fileName, "Sorry, Passwords do not match", 6);
							}
							else{
								$hash = password_hash($password1, PASSWORD_DEFAULT);

								$stmt = "INSERT INTO Users (userName, mail, password) VALUES (?, ?, ?)";
								$sentence = $link->stmt_init();

								if(!$sentence->prepare($stmt)){
									mysqli_close($link);
									returnError($fileName, "Sorry, can not prepare sentence to register the new user", 1);
								}
								else{
									$sentence->bind_param("sss", $userName, $mail, $hash);
									$sentence->execute();
									$sentence->close();

									$stmt = "SELECT * FROM Users WHERE userName=?";
									$sentence = $link->stmt_init();

									if(!$sentence->prepare($stmt)){
										mysqli_close($link);
										returnError($fileName, "Can not get user info from database", 1);
									}else{
										$sentence->bind_param('s',$userName);
										$sentence->execute();
										$result = $sentence->get_result();
										$sentence->close();

										mysqli_close($link);

										if($result->num_rows != 1){
											returnError($fileName, "Can not find data from user already registered", 2);
										}
										else{

											$userID = -1;

											while($row = $result->fetch_array(MYSQLI_ASSOC)){
												$userID = $row['id'];
												break;
											}

											$userLink = "https://".$ip."/pages/php/verifyAccount.php?usr=".openssl_encrypt($userID, CIPHER, CIPHER_KEY);

											$body = "<h1>Bienvenido ".$userName.".</h1>
														<p>Madrid View le da las gracias por utilizar esta aplicación para conocer la ciudad de Madrid.</p>
														<p>Madrid View le comunica que está a su disposición ante cualquier duda que pueda tener.</p>
														<p>Para verificar su cuenta, pulse en el enlace que aparece a continuación.</p>
														<a href=\"".$userLink."\">Enlace</a>
														<p>Un saludo, Juan Arellano.</p>";

											if(!sendMail($mail, $userName, "Bienvenido a Madrid View", $body)){
												returnError($fileName, "Can not send verification mail", 1);
											}
											else{
												$data = array("userName" => $row["userName"], "mail"=>$row["mail"], "verified"=>$row["verified"], "activated "=>$row["activated"], "dateIn"=>$row["dateIn"]);
												$array = array("success" => 1, "data" => $data);
												echo json_encode($array);
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}
?>
