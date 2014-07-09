<?php
function getToken(){
	// username
	$user_id = "Administrator";
	
	// SSO secret key
	$secret_key = "nuxeo5secretkey";
	
	$validity_time = 3600;
	
	$ts=time()*1000;
	$random = rand (0, $ts);
	
	// construct token
	$token_clair = $ts . ":" . $random . ":" . $secret_key . ":" . $user_id;
	

	$token = hash ( 'MD5', $token_clair, true );
	$base64HashedToken = base64_encode ( $token );
	
	return array( 
			"NX_RD" => $random ,
			"NX_TOKEN" =>  $base64HashedToken ,
			"NX_USER" => $user_id ) ;
}

?>