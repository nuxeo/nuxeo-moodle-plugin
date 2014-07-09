<?php
/*
 * (C) Copyright 2011 Nuxeo SA (http://nuxeo.com/) and contributors. All rights reserved. This program and the accompanying materials are made available under the terms of the GNU Lesser General Public License (LGPL) version 2.1 which accompanies this distribution, and is available at http://www.gnu.org/licenses/lgpl.html This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. Contributors: Gallouin Arthur
 */
require_once ('NuxeoAutomationUtilities.php');

/**
 * phpAutomationClient class
 *
 * Class which initializes the php client with an URL
 *
 * @author Arthur GALLOUIN for NUXEO agallouin@nuxeo.com
 */
class NuxeoPhpAutomationClient {
	private $url;
	private $session;
	private $interceptor;
	public function NuxeoPhpAutomationClient($url = 'http://localhost:8080/nuxeo/site/automation', $interceptor) {
		$this->url = $url;
		$this->interceptor = $interceptor;
		$this->interceptor->setUrl ( $this->url );
	}
	public function getSession() {
		return $this->interceptor->getSession ();
	}
}

/**
 *
 * @author mamoutou
 *        
 */
interface RequestInterceptor {
	public function getSession();
	
	/**
	 *
	 * @param string $url        	
	 */
	public function setUrl($url);
}

/**
 *
 * @author mamoutou
 *        
 */
class NuxeoRequestInterceptor implements RequestInterceptor {
	public $url;
	public $username;
	public $password;
	public $session;
	public function NuxeoRequestInterceptor($username, $password) {
		$this->username = $username;
		$this->password = $password;
	}
	/**
	 * getSession function
	 *
	 * Open a session from a phpAutomationClient
	 *
	 * @var $username : username for your session
	 *      $password : password matching the usename
	 * @author Arthur GALLOUIN for NUXEO agallouin@nuxeo.com
	 */
	public function getSession() {
		$this->session = $this->username . ":" . $this->password;
		$session = new NuxeoSession ( $this->url, $this->session );
		return $session;
	}
	public function setUrl($url) {
		$this->url = $url;
	}
}

/**
 *
 * @author mamoutou
 *        
 */
class PortalSSORequestInterceptor implements RequestInterceptor {
	private $url;
	private $username;
	private $secretekey;
	public function PortalSSORequestInterceptor($username, $secretekey) {
		$this->username = $username;
		$this->secretekey = $secretekey;
	}
	public function getSession() {
		$session = new NuxeoPortalSSOSession ( $this->url, $this );
		return $session;
	}
	public function setUrl($url) {
		$this->url = $url;
	}
	function getToken() {
		$ts = time () * 1000;
		$random = $random = rand ( 0, $ts );
		
		$token_clair = $ts . ":" . $random . ":" . $this->secretekey . ":" . $this->username;
		
		// On encode le jeton
		$token = hash ( 'MD5', $token_clair, true );
		
		$base64HashedToken = base64_encode ( $token );
		
		$token = new PortalSSOToken ( $this->username, $ts, $random, $base64HashedToken );
		return $token;
	}
}

/**
 *
 * @author mamoutou
 *        
 */
class PortalSSOToken {
	public $user_name;
	public $ts;
	public $random;
	public $base64HashedToken;
	public function PortalSSOToken($user_name, $ts, $random, $base64HashedToken) {
		$this->user_name = $user_name;
		$this->ts = $ts;
		$this->random = $random;
		$this->base64HashedToken = $base64HashedToken;
	}
}
interface Session {
	function newRequest($requestType);
	function isValide();
}

/**
 *
 * @author mamoutou
 *        
 */
class NuxeoPortalSSOSession implements Session {
	private $urlLoggedIn;
	private $headers;
	private $requestContent;
	private $interceptor;
	public function NuxeoPortalSSOSession($url, $interceptor) {
		$this->interceptor = $interceptor;
		$this->urlLoggedIn = str_replace ( "http://", "", $url );
		if (strpos ( $url, 'https' ) !== false) {
			$this->urlLoggedIn = "https://" . $this->urlLoggedIn;
		} elseif (strpos ( $url, 'http' ) !== false) {
			$this->urlLoggedIn = "http://" . $this->urlLoggedIn;
		} else {
			// throw new Exception("URL not valide");
		}
	}
	
	/**
	 * newRequest function
	 *
	 * Create a request with a authentification header from a session
	 *
	 * @var $requestType : type of request you want to execute (such as Document.Create
	 *      for exemple)
	 * @author mamoutou
	 */
	public function newRequest($requestType) {
		$token = $this->interceptor->getToken ();
		$this->headers = "NX_TS: " . $token->ts . "\r\n" . "NX_RD: " . $token->random . "\r\n" . "NX_TOKEN: " . $token->base64HashedToken . "\r\n" . "NX_USER: " . $token->user_name . "\r\n";
		$newRequest = new NuxeoRequest ( $this->urlLoggedIn, $this->headers, $requestType );
		return $newRequest;
	}
	function isValide() {
		try {
			$answer = $this->newRequest ( "Document.Query" )->set ( 'params', 'query', "SELECT * FROM Workspace " )->sendRequest ();
		} catch ( Exception $e ) {
			return false;
		}
		return true;
	}
}

/**
 * Session class
 *
 * Class which stocks username,password, and open requests
 *
 * @author Arthur GALLOUIN for NUXEO agallouin@nuxeo.com
 */
class NuxeoSession implements Session {
	private $urlLoggedIn;
	private $headers;
	private $requestContent;
	public function NuxeoSession($url, $session) {
		$this->urlLoggedIn = str_replace ( "http://", "", $url );
		if (strpos ( $url, 'https' ) !== false) {
			$this->urlLoggedIn = "https://" . $session . "@" . $this->urlLoggedIn;
		} elseif (strpos ( $url, 'http' ) !== false) {
			$this->urlLoggedIn = "http://" . $session . "@" . $this->urlLoggedIn;
		} else {
			throw Exception;
		}
	}
	
	/**
	 * newRequest function
	 *
	 * Create a request from a session
	 *
	 * @var $requestType : type of request you want to execute (such as Document.Create
	 *      for exemple)
	 * @author Arthur GALLOUIN for NUXEO agallouin@nuxeo.com
	 */
	public function newRequest($requestType) {
		$this->headers = '';
		$newRequest = new NuxeoRequest ( $this->urlLoggedIn, $this->headers, $requestType );
		return $newRequest;
	}
	function isValide() {
		try {
			$answer = $this->newRequest ( "Document.Query" )->set ( 'params', 'query', "SELECT * FROM Workspace " )->sendRequest ();
		} catch ( Exception $e ) {
			return false;
		}
		return true;
	}
}

/**
 * Document class
 *
 * hold a return document
 *
 * @author Arthur GALLOUIN for NUXEO agallouin@nuxeo.com
 */
class NuxeoDocument {
	private $object;
	private $properties;
	Public function NuxeoDocument($newDocument) {
		$this->object = $newDocument;
		if (array_key_exists ( 'properties', $this->object ))
			$this->properties = $this->object ['properties'];
		else
			$this->properties = null;
	}
	public function getUid() {
		return $this->object ['uid'];
	}
	public function getPath() {
		return $this->object ['path'];
	}
	public function getRepository() {
		return $this->object ['repository'];
	}
	public function getType() {
		return $this->object ['type'];
	}
	public function getState() {
		return $this->object ['state'];
	}
	public function getTitle() {
		return $this->object ['title'];
	}
	Public function output() {
		$value = sizeof ( $this->object );
		
		for($test = 0; $test < $value - 1; $test ++) {
			echo '<td> ' . current ( $this->object ) . '</td>';
			next ( $this->object );
		}
		
		if ($this->properties !== NULL) {
			$value = sizeof ( $this->properties );
			for($test = 0; $test < $value; $test ++) {
				echo '<td>' . key ( $this->properties ) . ' : ' . current ( $this->properties ) . '</td>';
				next ( $this->properties );
			}
		}
	}
	public function getObject() {
		return $this->object;
	}
	public function getProperty($schemaNamePropertyName) {
		if (array_key_exists ( $schemaNamePropertyName, $this->properties )) {
			return $this->properties [$schemaNamePropertyName];
		} else
			return null;
	}
	public function getPictureFilename() {
			if (array_key_exists ( 'picture:views', $this->properties )) {
				$pv = $this->properties ['picture:views'];
				if (is_array($pv) && sizeof($pv) > 0){
					$current = current($pv) ; 					
					return $current['filename'];
				}
			} 
			return null;
	}
}

/**
 * Documents class
 *
 * hold an Array of Document
 *
 * @author Arthur GALLOUIN for NUXEO agallouin@nuxeo.com
 */
class NuxeoDocuments {
	private $documentsList;
	public function NuxeoDocuments($newDocList) {
		$this->documentsList = null;
		$test = true;
		if (! empty ( $newDocList ['entries'] )) {
			while ( false !== $test ) {
				if (is_array ( current ( $newDocList ['entries'] ) )) {
					$this->documentsList [] = new NuxeoDocument ( current ( $newDocList ['entries'] ) );
				}
				$test = each ( $newDocList ['entries'] );
			}
			$test = sizeof ( $this->documentsList );
			unset ( $this->documentsList [$test] );
		} elseif (! empty ( $newDocList ['uid'] )) {
			$this->documentsList [] = new NuxeoDocument ( $newDocList );
		} elseif (is_array ( $newDocList )) {
			return null;
		} else {
			return $newDocList;
		}
	}
	public function output() {
		$value = sizeof ( $this->documentsList );
		echo '<table>';
		echo '<tr><TH>Entity-type</TH><TH>Repository</TH><TH>uid</TH><TH>Path</TH>
			<TH>Type</TH><TH>State</TH><TH>Title</TH><TH>Download as PDF</TH>';
		for($test = 0; $test < $value; $test ++) {
			echo '<tr>';
			current ( $this->documentsList )->output ();
			echo '<td><form id="test" action="../tests/B5bis.php" method="post" >';
			echo '<input type="hidden" name="a_recup" value="' . current ( $this->documentsList )->getPath () . '"/>';
			echo '<input type="submit" value="download"/>';
			echo '</form></td></tr>';
			next ( $this->documentsList );
		}
		echo '</table>';
	}
	public function getDocument($number) {
		$value = sizeof ( $this->documentsList );
		if ($number < $value and $number >= 0)
			return $this->documentsList [$number];
		else
			return null;
	}
	public function getDocumentList() {
		return $this->documentsList;
	}
}

/**
 * Contains Utilities such as date wrappers
 */
class NuxeoUtilities {
	private $ini;
	public function dateConverterPhpToNuxeo($date) {
		return date_format ( $date, 'Y-m-d' );
	}
	public function dateConverterNuxeoToPhp($date) {
		$newDate = explode ( 'T', $date );
		$phpDate = new DateTime ( $newDate [0] );
		return $phpDate;
	}
	public function dateConverterInputToPhp($date) {
		$edate = explode ( '/', $date );
		$day = $edate [2];
		$month = $edate [1];
		$year = $edate [0];
		
		if ($month > 0 and $month < 12)
			if ($month % 2 == 0)
				if ($day < 1 or $day > 31) {
					throw new Exception ( 'date not correct' );
					exit ();
				} elseif ($month == 2)
					if (year % 4 == 0)
						if ($day > 29 or $day < 0) {
							throw new Exception ( 'date not correct' );
							exit ();
						} else if ($day > 28 or $day < 0) {
							throw new Exception ( 'date not correct' );
							exit ();
						} else if ($day > 30 or $day < 0) {
							throw new Exception ( 'date not correct' );
							exit ();
						}
		
		$phpDate = new DateTime ( $year . '-' . $month . '-' . $day );
		
		return $phpDate;
	}
	
	/**
	 * Function Used to get Data from Nuxeo, such as a blob.
	 * MUST BE PERSONALISED. (Or just move the
	 * headers)
	 *
	 * @param $path path
	 *        	of the file
	 */
	function getFileContent($path) {
		$eurl = explode ( "/", $path );
		
		$client = new NuxeoPhpAutomationClient ( 'http://localhost:8080/nuxeo/site/automation' );
		
		$session = $client->getSession ( 'Administrator', 'Administrator' );
		
		$answer = $session->newRequest ( "Chain.getDocContent" )->set ( 'context', 'path' . $path )->sendRequest ();
		
		if (! isset ( $answer ) or $answer == false)
			throw new Exception ( '$answer is not set' );
		else {
			header ( 'Content-Description: File Transfer' );
			header ( 'Content-Type: application/octet-stream' );
			header ( 'Content-Disposition: attachment; filename=' . end ( $eurl ) . '.pdf' );
			readfile ( 'tempstream' );
		}
	}
}

?>
