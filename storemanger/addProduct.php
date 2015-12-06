<?php
require_once "./core/init.php";
$result = array();
	if (isset($_POST)) {
		//Escape variables
		$cName = e($_POST["categoryName"]);
		$cId = e($_POST["categoryId"]);
		$userId = e($_POST["userId"]);
		$image = e($_POST["pImage"]);
		$name = e($_POST["pName"]);
		$code = e($_POST["pCode"]);
		$cp = e($_POST["pCP"]);
		$sp = e($_POST['pSP']);
		$sizeStack = e($_POST["pSize"]);
		$quantityStack = e($_POST["pQuantity"]);

		//empty
		if (empty($cName) || empty($cId) || empty($userId) || empty($name) || empty($code) || empty($cp) || empty($sp) || empty($sizeStack) || empty($quantityStack)) {
			$result["message"] = "Fill in all the fields";
			$result["return"] = false;
		}

		//check user account exits
		if(!check_user($userId)){
			$result["return"] = false;
			$result["message"] = "Invalid user.";
			echo json_encode($result);
			exit;
		}

		//check user is active or not
		if(!check_user_active($userId)){
			$result["return"] = false;
			$result["message"] = "Dear user! Please pay your bills to reactivate your account.";
			echo json_encode($result);
			exit;
		}

		//check category name exits
		if(!check_category_is_valid($cId,$userId)){
			$result["message"] = "Invalid Category: This category does not belongs to you";
			$result["return"] = false;
			echo json_encode($result);
			exit;
		}

		//check name length
		if(strlen($name) < 2){
			$result["message"] = "Product name should be more then 2 characters";
			$result["return"] = false;
			echo json_encode($result);
			exit;
		}

		//code length
		if(strlen($code) < 2){
			$result["message"] = "Product code should be more then 2 characters";
			$result["return"] = false;
			echo json_encode($result);
			exit;
		}

		//check code is unique
		if(!check_product_code_is_unique($code,$userId)){
			$result["message"] = "Product code already exits. Product code must be Unique";
			$result["return"] = false;
			echo json_encode($result);
			exit;
		}

		//check image
		if(!empty($image)){
			//decode the image and upload it
			$decodemage = base64_decode($image);
			$filename = "IMG_".time().".jpg";

			//make a dir if not exits
			if(!is_dir("pic/{$userId}/{$cName}")){
				mkdir("pic/{$userId}/");
				mkdir("pic/{$userId}/{$cName}");
			}
			//upload image
			file_put_contents("pic/{$userId}/{$cName}/{$filename}",$decodemage);
			$imagePath = "pic/{$userId}/{$cName}/{$filename}";
		}else{
			$imagePath = "";
		}


		//insert into database
		Db::insert("product",array(
			"name" => $name,
			"image" => $imagePath,
			"code" => $code,
			"size" => $sizeStack,
			"quantity" => $quantityStack,
			"user_id" => $userId,
			"category_id" => $cId,
			"category_name" => $cName,
			"time" => time()
		));


		if(Db::getError() == false){
			$result["message"] = "Success! Product added";
			$result["return"] = true;
		}else{
			$result["message"] = "Failed to insert into Database";
			$result["return"] = false;
		}
		echo json_encode($result);
		exit;


	}else{
		$result["message"] = "Access Denied";
		$result["return"] = false;
		echo json_encode($result);
		exit;
	}
?>