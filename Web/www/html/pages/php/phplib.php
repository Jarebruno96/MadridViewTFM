<?php

	require_once './../../composer/vendor/autoload.php';
	
	function mylog($fileName, $info){
		$error = "[".$fileName."] [".date("Y-m-d h:i:sa")." - ".$info;
	}

	function returnError($fileName, $info, $code){
		mylog($fileName, $info);
		$array = array("success" => 0, "description" => $info, "code"=>$code);
		echo json_encode($array);
	}

	function sendMail($recipient, $userName, $subject, $body){

		$mail = new PHPMailer\PHPMailer\PHPMailer();
		$mail->CharSet = "UTF-8";
		$mail->isSMTP();
		$mail->SMTPAuth = true;
		$mail->Host = "smtp.gmail.com";
		$mail->Username = "madridviewadm@gmail.com";
		$mail->Password = "Madrid1234";
		$mail->SMTPSecure = "tls";
		$mail->Port = 587;
		$mail->SMTPDebug = 0;

		$mail->From = "madridviewadm@gmail.com";
		$mail->FromName = "Madrid View"; 
		$mail->addReplyTo("madridviewadm@gmail.com","Reply address");
		$mail->addAddress($recipient, $userName);
		$mail->isHTML(true);
		$mail->Subject = $subject;
		$mail->Body = $body;
		$mail->AltBody = "Something went wrong with mail´s body";

		if(!$mail->send()){
			//echo $mail->ErrorInfo;
			return 0;
		}

		return 1;
	}

	function generateRandomString($length){

		$chars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXZ";
		$charsLength = strlen($chars);
		$string = "";
		
		for($i=0; $i<$length;$i++){
			$string.=$chars[rand(0, $charsLength -1)];
		}

		return $string;
	}

?>