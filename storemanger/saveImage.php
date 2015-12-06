<?php
	$name = $_POST['name']; //image name
	$image = $_POST['image']; //image in string format

	//decode the image
	$decodedImage = base64_decode($image);

	//upload the image
	file_put_contents("pic/".$name.".jpg", $decodedImage);
?>