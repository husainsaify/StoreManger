<?php

function e($string){
    return htmlentities($string,ENT_QUOTES,"UTF-8");
}

function redirect($location){
    header("Location: {$location}");
    exit();
}

// function to calculate the time
function time_ago($session_time){
    // check if time is not provided
    if (empty($session_time)) {
        return false;
        exit();
    }
    $time_difference = time() - $session_time;
    $seconds = $time_difference;
    $minutes = round($time_difference / 60);
    $hours = round($time_difference / 3600);
    $day = round($time_difference / 86400);
    $weeks = round($time_difference / 604800);
    $months = round($time_difference / 2419200);
    $year = round($time_difference / 29030400);

    if($seconds <= 60){
        return $seconds." Seconds ago";
    }
    else if ($minutes <= 60){
        if($minutes == 1){
            return "One Minute ago";
        }else{
            return $minutes." Minutes ago";
        }
    }
    else if($hours <= 24){
        if($hours == 1){
            return "One Hour ago";
        }else{
            return $hours." Hours ago";
        }
    }
    else if($day <= 7){
        if($day == 1){
            return "One day ago";
        }else{
            return $day." days ago";
        }
    }
    else if($weeks <= 4){
        if($weeks == 1){
            return "One week ago";
        }else{
            return $weeks." weeks ago";
        }
    }
    else if($months <= 12){
        if($months == 1){
            return "One month ago";
        }else{
            return $months." months ago";
        }
    }
    else{
        if($year == 1){
            return "One year ago";
        }else{
            return $year." years ago";
        }
    }
}

function random_password() {
    $alphabet = "abcdefghijklmnopqrstuwxyzABCDEFGHIJKLMNOPQRSTUWXYZ0123456789";
    $pass = array(); //remember to declare $pass as an array
    $alphaLength = strlen($alphabet) - 1; //put the length -1 in cache
    for ($i = 0; $i < 8; $i++) {
        $n = rand(0, $alphaLength);
        $pass[] = $alphabet[$n];
    }
    return implode($pass); //turn the array into a string
}

//  function to get the extension
function get_extension($file){
    $file = strtolower($file);
    $explode = explode(".", $file);
    $key = count($explode) - 1;
    return $explode[$key];
}

// custom encryption function
function encryption($action, $string) {
    $output = false;

    $encrypt_method = "AES-256-CBC";
    $secret_key = '@@#$%^&()_+Si';
    $secret_iv = 'AaBBqwuGIEIHOJWWjshswhuehwehWREYEIhduwownceh';

    // hash
    $key = hash('sha256', $secret_key);

    // iv - encrypt method AES-256-CBC expects 16 bytes - else you will get a warning
    $iv = substr(hash('sha256', $secret_iv), 0, 16);

    if( $action == 'e') {
        $output = openssl_encrypt($string, $encrypt_method, $key, 0, $iv);
        $output = base64_encode($output);
    }
    else if( $action == 'd'){
        $output = openssl_decrypt(base64_decode($string), $encrypt_method, $key, 0, $iv);
    }

    return $output;
}

function not_login($id, $url){
    if (!isset($id)) {
        redirect($url);
    }
}

function is_login($id, $url){
    if (isset($id)) {
        redirect($url);
    }
}

//get full url
function get_address($link){
    $explode = explode("/", $link);
    $key = count($explode) - 1;
    unset($explode[$key]);
    $link = implode("/", $explode);
    return $link;
}

//email validation function
function check_email($email){
    if (filter_var($email, FILTER_VALIDATE_EMAIL)) {
        return true;
    } else {
        return false;
    }
}

//mobile number
function check_mobile($mobile){
    if(preg_match("/^(\+91-|\+91|0)?\d{10}$/", $mobile)){
        return true;
    }else{
        return false;
    }
}

//check user is valid
function check_user($userId){
    $userCount = Db::rowCount("user",array("id" => $userId),array("="));
    return $userCount <= 0 ? false : true;
}

//check user is active or not
function check_user_active($userId){
    $userCount = Db::rowCount(
    "user",
    array(
        "id" => $userId,
        "active" => "y"),
    array("=","="));
    return $userCount <= 0 ? false : true;   
}

//check categoryName already exits with $userId
function check_category_name_exits($categoryName, $userId){
    $count = Db::rowCount("category",array(
        "name" => $categoryName,
        "user_id" => $userId), array("=","="));
    return $count <= 0 ? true : false;
}

function check_category_is_valid($categoryId,$userId){
    $cout = Db::rowCount("category",array(
        "id" => $categoryId,
        "user_id" => $userId
    ),array("=","="));

    return $cout == 1 ? true : false;
}

function check_product_code_is_unique($code,$userId){
    $c = Db::rowCount("product",array(
        "code" => $code,
        "user_id" =>$userId
    ),array("=","="));
    return $c <= 0 ? true : false;
}