<?php

	include_once 'phplib.php';

	$pass = "123";

	$hash = password_hash($pass, PASSWORD_DEFAULT);
	if(password_verify("123", $hash)==false){
		echo "Mal";
	}
	else{
		echo "Bien";
	}
?>
