<?php
	include_once '../../credentials/credentials.php';
	include_once 'phplib.php';

	$fileName = basename(__FILE__);

	if(!isset($_GET['usr'])){
		returnError($fileName, "Can not read user info", 1);
	}
	else{
		$userID = openssl_decrypt($_GET["usr"], CIPHER, CIPHER_KEY);
		$link = mysqli_connect(DB_HOST, DB_USER, DB_PASSWORD, DB_NAME);

		if(!$link){
			returnError($fileName, "Can not connect to database", 1);
		}
		else{
			$stmt = "UPDATE Users SET verified=1 WHERE id=?";
			$sentence = $link->stmt_init();

			if(!$sentence->prepare($stmt)){
				mysqli_close($link);
				returnError($fileName, "Can not set user as verified", 1);
			}
			else{
				$sentence->bind_param('i',$userID);
				$sentence->execute();
				$sentence->close();
				mysqli_close($link);
				header("Location: ../verify.html");
				exit();
			}
		}
	}
?>