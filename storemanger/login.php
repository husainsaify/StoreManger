<?php
    /*
     * Error code
     * 1 = access deneid
     * 2 = fill in all the fields
     * 3 =
     * */
    require_once "./core/init.php";
    $result = array();
    if(isset($_POST['email']) && isset($_POST['password'])){
        //escape value
        $email = e($_POST["email"]);
        $password = e($_POST['password']);

        if(empty($email) || empty($password)){
            $result["message"] = "Fill in all the fields";
            $result["return"] = false;
            echo json_encode($result);
            exit;
        }

        $encPassword = encryption("e",$password);

        //check email is valid
        $count = Db::rowCount("user",array(
            "email" => $email,
            "password" => $encPassword
        ),array("=","="));

        //if one user exits
        if($count == 1){
            //fetch results and display
            $detail = Db::fetch("user",array(
                "email" => $email,
                "password" => $encPassword
            ),array("=","="));

            if(db::getError() == true){
                $result["message"] = "Query failed";
                $result["return"] = false;
            }else{
                $result["message"] = "success";
                $result["return"] = true;
                $result["user"] = $detail;
            }
            //display result
            echo json_encode($result);
            exit;
        }else{
            $result["message"] = "Invalid email or password";
            $result["return"] = false;
            echo json_encode($result);
            exit;
        }

    }else{
        $result["message"] = "Access Denied";
        $result["return"] = false;
        echo json_encode($result);
        exit;
    }