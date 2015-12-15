<?php
	require_once "./core/init.php";
	$result = array();

	if(isset($_POST["user_id"]) && isset($_POST["product_id"]) && isset($_POST["size"]) && isset($_POST['quantity']) && isset($_POST['price'])) {
		$user_id = e($_POST["user_id"]);
		$product_id = e($_POST["product_id"]);
		$size = e($_POST['size']);
		$quantity = e($_POST["quantity"]);
		$price = e($_POST["price"]);

		//check any stuff is not empty
		if (empty($user_id) || empty($product_id) || empty($size) || empty($quantity) || empty($price)) {
			$result["message"] = "Fill in all the fields";
			$result["return"] = true;
			json($result);
		}

		//check user id is valid
		if (!check_user($user_id)) {
			$result["message"] = "Invalid user";
			$result["return"] = false;
			json($result);
		}

		//check product id is valid
		if (!check_productId_is_valid($product_id, $user_id)) {
			$result["message"] = "Invalid product";
			$result["return"] = false;
			json($result);
		}

		//convert stack into array
		$sizeArray = remove_last_empty_item(explode(",", $size));
		$quantityArray = remove_last_empty_item(explode(",", $quantity));
		$priceArray = remove_last_empty_item(explode(",", $price));


		//add to sales
		$date = date("d:m:Y");
		$date_id = date("dmY");

		foreach ($sizeArray as $key => $s) {
			$q = $quantityArray[$key];
			$p = $priceArray[$key];

			//check size is valid
			$sizeCount = Db::rowCount("sq", array(
					"user_id" => $user_id,
					"product_id" => $product_id,
					"size" => $s
			), array("=", "=", "="));

			if ($sizeCount <= 0) {
				$result["message"] = "Invalid size `{$s}` of product";
				$result["return"] = false;
				json($result);
			}

			//check quantity is not zero
			$quantityQuery = Db::query("SELECT quantity FROM `sq` WHERE user_id=? AND product_id=? AND size=?",array(
				$user_id,
				$product_id,
				$s
			));

			//fetch quantity from the database
			$quantityFetch = $quantityQuery->fetchAll(PDO::FETCH_ASSOC);
			$fetchQuantity = $quantityFetch[0]["quantity"];

			//check quantity is not zero
			if($fetchQuantity <= 0){
				$result["message"] = "Quantity of size `{$s}` is zero";
				$result["return"] = false;
 				json($result);
			}

			//check quantity from app is not more then quantity from database
			if($q > $fetchQuantity){
				$result["message"] = "You have only {$fetchQuantity} piece of size `{$s}`, Can't reduce {$q} piece";
				$result["return"] = false;
				json($result);
			}

			//generate new quantity
			$newQuantity = $fetchQuantity - $q;

			//update quantity in `sq`
			$update = Db::query("UPDATE sq SET quantity=? WHERE user_id=? AND product_id=? AND size=?",array(
				$newQuantity,
				$user_id,
				$product_id,
				$s
			));

			if(!Db::getError()){
				//success

				//add into the database
				Db::insert("sell",array(
                    "user_id" => $user_id,
                    "product_id" => $product_id,
                    "size" => $s,
                    "quantity" => $q,
                    "price_per_q" => $p,
                    "date" => $date,
                    "date_id" => $date_id
                ));

				//if their is an error and we are unable to inter into sell database
				if(Db::getError()){
					$result["message"] = "Failed to insert into sales";
					$result["return"] = false;
					json($result);
				}

			}else{
				//error
				$result["message"] = "Failed to update quantity";
				$result["return"] = false;
				json($result);
			}
		} // foreach

		$result["message"] = "success";
		$result["return"] = true;
		json($result);
	}else{
		$result["message"] = "Access denied";
		$result["return"] = false;
		json($result);
	}
?>