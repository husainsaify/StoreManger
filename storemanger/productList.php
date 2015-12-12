<?php
require_once "./core/init.php";
$result = array();
if(isset($_POST["userId"]) && isset($_POST["categoryId"])){

    //escape
    $userId = e($_POST["userId"]);
    $categoryId = e($_POST["categoryId"]);

    //check no empty
    if(empty($userId) || empty($categoryId)){
        $result["message"] = "Invalid input";
        $result["return"] = false;
        echo json_encode($result);
        exit;
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

    //check categoryId is valid
    if(!check_category_is_valid($categoryId,$userId)){
        $result["return"] = false;
        $result["message"] = "Invalid category Id";
        echo json_encode($result);
        exit;
    }

    //count product
    $count = Db::rowCount("product",array(
        "user_id" => $userId,
        "category_id" => $categoryId,
        "active" => "y"
    ),array("=","=","="));

    if($count <= 0){
        $result["return"] = true;
        $result["message"] = "No product Added yet!";
        $result["product"] = null;
        echo json_encode($result);
        exit;
    }

    //fetch all the product
    $product = Db::fetch("product",array(
        "user_id" => $userId,
        "category_id" => $categoryId,
        "active" => "y"
    ),array("=","=","="));

    if(!Db::getError()){
        //create a new product array with only that fields which are required
        $newProductArray = array();
        foreach($product as $key => $value){
            $newProductArray[$key]["productId"] = $value["id"];
            $newProductArray[$key]["name"] = $value["name"];
            $newProductArray[$key]["image"] = $value["image"];
            $newProductArray[$key]["code"] = $value["code"];
        }

        //output the result json
        $result["return"] = true;
        $result["message"] = "Success";
        $result["product"] = $newProductArray;
    }else{
        $result["return"] = false;
        $result["message"] = "Failed to fetch products";
    }

    echo json_encode($result);
    exit;

}else{
    $result["message"] = "Access denied";
    $result["return"] = false;
    echo json_encode($result);
    exit;
}