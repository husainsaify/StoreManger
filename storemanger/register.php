<?php
	require_once "core/init.php";
	$result = array();

	if(isset($_POST["register"])){
		$name = e($_POST['name']);
		$phone = e($_POST['phone']);
		$email = e($_POST['email']);
		$desc = e($_POST['description']);
		$time = time();

		if(!empty($name) && !empty($phone) && !empty($email) && !empty($desc)){
			//insert
			Db::insert("register",array(
				"name" => $name,
				"phone" => $phone,
				"email" => $email,
				"description" => $desc,
				"time" => $time
			));
		}else{
			$result["message"] = "Fill in all the field";
			$result["return"] = false;
			echo json_encode($result);
			exit;
		}


		if(Db::getError() == false){
			$result["message"] = "Thanks for showing interest. Our team will contact you shortly";
			$result["return"] = true;
		}else{
			$result["message"] = "Try again";
			$result["return"] = false;
		}
		echo json_encode($result);
		exit;
	}else{
		$result["message"] = "Access denied";
		$result["return"] = false;
		echo json_encode($result);
		exit;
	}