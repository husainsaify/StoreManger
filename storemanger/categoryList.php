<?php
require_once "./core/init.php";
$result = array();
if(isset($_POST['userId'])){
    $userId = e($_POST['userId']);

    if(empty($userId)){
        $result["return"] = false;
        $result["message"] = "Fill in all the fields.";
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

    //count category
    $categoryCount = Db::rowCount("category",array(
            "user_id" => $userId),array("="));

    if($categoryCount <= 0){
        $result["return"] = false;
        $result["message"] = "No category found";
        echo json_encode($result);
        exit;
    }

    //fetch category list
    $list = Db::fetch("category",array(
        "user_id" => $userId),array("="));

    //create a new list
    $newList = array();
    foreach ($list as $key => $value) {
        $newList[$key]["id"] = $value["id"];
        $newList[$key]["name"] = $value["name"];
    }

    if(Db::getError()){
        $result["return"] = false;
        $result["message"] = "Failed to load category";
    }else{
        $result["return"] = true;
        $result["message"] = "Success";
        $result["category"] = $newList;
    }
    echo json_encode($result);
    exit;
}else{
    $result["return"] = false;
    $result["message"] = "Access denied.";
    echo json_encode($result);
    exit;
}