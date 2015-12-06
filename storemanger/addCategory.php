<?php
    require_once "./core/init.php";
    $result = array();

    if(isset($_POST['categoryName']) && isset($_POST['userId'])){
        $categoryName = e($_POST['categoryName']);
        $userId = e($_POST['userId']);

        //check fields are not empty
        if(empty($categoryName) || empty($userId)){
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

        //check length of the category name
        if(strlen($categoryName) <= 3){
            $result["return"] = false;
            $result["message"] = "Category name should be more then 3 characters.";
            echo json_encode($result);
            exit;   
        }

        //category name should not be more then 10 char
        if(strlen($categoryName) > 20){
            $result["return"] = false;
            $result["message"] = "Category name should not be more then 20 characters.";
            echo json_encode($result);
            exit;
        }

        //check category already extists or not
        if(!check_category_name_exits($categoryName,$userId)){
            $result["return"] = false;
            $result["message"] = "Category name already exists.";
            echo json_encode($result);
            exit;
        }

        $time =  time();
        //insert category
        $insert = Db::insert("category",array(
            "user_id" => $userId,
            "name" => $categoryName,
            "time" => $time
        ));

        //succes
        if(!Db::getError()){
            $result["return"] = true;
            $result["message"] = "Category added successfully.";
        }else{ //erro
            $result["return"] = false;
            $result["message"] = "Failed to add category.";
        }
        echo json_encode($result);
        exit;

    }else{
        $result["return"] = false;
        $result["message"] = "Access denied.";
        echo json_encode($result);
        exit;
    }
?>