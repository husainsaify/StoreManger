<?php
require_once "./core/init.php";
$result = array();
if(isset($_GET['userId']) && isset($_GET['name'])){
	$userId = e($_GET['userId']);
	$name = e($_GET['name']);
	$size = e($_GET['size']);
	$categoryId = e($_GET['cId']);
	$categoryName = e($_GET['cName']);
	//check $userId and product Name is not empty
	if (empty($userId) || empty($name)) {
		$result["message"] = "UserId and ProductName cannot be empty";
		$result["return"] = false;
		json($result); //echo json
	}

	//check userId is valid
    if(!check_user($userId)){
        $result["return"] = false;
        $result["message"] = "Invalid user.";
        json($result);
    }

    //check user is active or not
    if(!check_user_active($userId)){
        $result["return"] = false;
        $result["message"] = "Dear user! Please pay your bills to reactivate your account.";
        json($result);
    }

    //make search term
    $searchTerm = $name;
    if(!empty($size)){
    	$searchTerm .= " ".$size;
    }
    if (!empty($categoryName)) {
    	$searchTerm .= " ".$categoryName;
    }

    $searchs = preg_split("/[\s,]+/", $searchTerm);

    $term_count = 0;
    $q = "SELECT * FROM `product` WHERE ";
    $i = 0;

    //generate query
    foreach ($searchs as $search) {
    	$term_count++;
    	if ($term_count == 1) {
    		$q .= "`keywords` LIKE '%$search%' ";
    	}else{
    		$q .= "AND `keywords` LIKE '%$search%' ";
    	}
    }
    echo $q;
}else{
	$result["message"] = "Access denied";
	$result["return"] = false;
	json($result); //echo json
}