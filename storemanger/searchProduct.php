<?php
	header('Content-Type: application/json');
	require_once "./core/init.php";
	$result = array();

	if(isset($_POST['s']) && isset($_POST['user_id'])){
		$s = e($_POST["s"]);
		$user_id = e($_POST['user_id']);

		$q = "SELECT id,name,code FROM `product` WHERE ";
		$term_count=0;
		$searchs = explode(" ", $s);

		foreach ($searchs as $search) {
			$term_count++;
			if($term_count == 1){
				$q .= "keywords LIKE '%$search%' ";
			}else{
				$q .= "AND keywords LIKE '%$search%' ";
			}
		}

		$q .= "AND user_id='$user_id'";
		//execute query
		$stmt = Db::query($q,array());
		//get the result count
		$count = $stmt->rowCount();

		//if count is > 0
		$fetch = array();
		if($count > 0){
			//fetch result from the database
			$fetch = $stmt->fetchAll(PDO::FETCH_ASSOC);
		}

		$result["message"] = "success";
		$result["return"] = true;
		$result["count"] = $count;
		$result["result"] = $fetch;

		json($result);
	}else{
		$result["message"] = "Access denied";
		$result["return"] = false;
		json($result);
	}
?>