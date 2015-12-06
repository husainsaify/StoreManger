<?php
require_once "./core/init.php";
$result = array();
/*
 * Error code
 * 1 = fill in all the fields
 * 2 = invalid user id
 * 3 = inactive user
 * 4 = no category found for the user
 * 5 = error loading category
 * 6 = success
 * 7 = access denied
 * */
if(isset($_POST['userId'])){
    $userId = e($_POST['userId']);

    if(empty($userId)){
        $result["code"] = 1;
        $result["return"] = false;
        $result["message"] = "Fill in all the fields.";
        echo json_encode($result);
        exit;
    }

    //check user account exits
    if(!check_user($userId)){
        $result["code"] = 2;
        $result["return"] = false;
        $result["message"] = "Invalid user.";
        echo json_encode($result);
        exit;
    }

    //check user is active or not
    if(!check_user_active($userId)){
        $result["code"] = 3;
        $result["return"] = false;
        $result["message"] = "Dear user! Please pay your bills to reactivate your account.";
        echo json_encode($result);
        exit;
    }

    //count category
    $categoryCount = Db::rowCount("category",array(
            "user_id" => $userId),array("="));

    if($categoryCount <= 0){
        $result["code"] = 4;
        $result["return"] = false;
        $result["message"] = "No category found";
        echo json_encode($result);
        exit;
    }

    //fetch category list
    $list = Db::fetch("category",array(
        "user_id" => $userId),array("="));

    if(Db::getError()){
        $result["code"] = 5;
        $result["return"] = false;
        $result["message"] = "Failed to load category";
    }else{
        $result["code"] = 6;
        $result["return"] = true;
        $result["message"] = "Success";
        $result["category"] = $list;
    }
    echo json_encode($result);
    exit;
}else{
    $result["code"] = 7;
    $result["return"] = false;
    $result["message"] = "Access denied.";
    echo json_encode($result);
    exit;
}