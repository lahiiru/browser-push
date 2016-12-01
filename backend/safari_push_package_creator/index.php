<?php
$certificate_path = "pkey.p12";     // Change this to the path where your certificate is located
$certificate_password = "123456";   // Change this to the certificate's import password

//Build and output push package to current dir.
include ("createPushPackage.php");

$package_path = create_push_package();
if (empty($package_path)) {
	http_response_code(500);
	die;
}
