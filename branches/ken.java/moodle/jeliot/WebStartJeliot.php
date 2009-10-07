<?php header("Content-Type: application/x-java-jnlp-file"); ?>
<?php 
       echo "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n";
       $codebase=$_GET['codebase'];
	$sourcefile = $_GET['sourcefile'];	
	$userID = $_GET['userID'];
	$sessionID = $_GET['sessionID'];
	$questions = $_GET['questions'];
	$parameters = "?codebase=".$codebase."&sourcefile=".$sourcefile."&userID=".$userID."&sessionID=".$sessionID."&questions=".$questions;
	$href = "WebStartJeliot.php".$parameters;
?>

<jnlp spec="1.0+" codebase="<?php echo "$codebase";?>" href="<?php echo "$href"; ?>">
  <information>
    <title>Jeliot 3</title>
    <vendor>University of Joensuu</vendor>
    <description>Jeliot 3, the program animation tool</description>
    <offline-allowed/>
  </information>
  <security>
   <all-permissions/>
  </security>
  <resources>
   <j2se version="1.4+"/>
   <jar href="jeliot.jar"/>
  </resources>
   <application-desc main-class="jeliot.MoodleJeliot">
<?php 

	 echo  "<argument>".$sourcefile."</argument>\n";
	 echo  "<argument>".$sessionID."</argument>\n";
	 echo  "<argument>".$userID."</argument>\n";
     echo  "<argument>".$questions."</argument>\n";
 ?>
   </application-desc> 
<!-- <application-desc main-class="jeliot.Jeliot"/> -->
</jnlp>
