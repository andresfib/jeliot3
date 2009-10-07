<?php  // $Id: view.php,v 1.6 2007/09/03 12:23:36 jamiesensei Exp $
/**
 * This page prints a particular instance of jeliot
 *
 * @author
 * @version $Id: view.php,v 1.6 2007/09/03 12:23:36 jamiesensei Exp $
 * @package jeliot
 **/

    require_once("../../config.php");
    require_once("lib.php");

    $id = optional_param('id', 0, PARAM_INT); // Course Module ID, or
    $a  = optional_param('a', 0, PARAM_INT);  // jeliot ID

    if ($id) {
        if (! $cm = get_record("course_modules", "id", $id)) {
            error("Course Module ID was incorrect");
        }

        if (! $course = get_record("course", "id", $cm->course)) {
            error("Course is misconfigured");
        }

        if (! $jeliot = get_record("jeliot", "id", $cm->instance)) {
            error("Course module is incorrect");
        }

    } else {
        if (! $jeliot = get_record("jeliot", "id", $a)) {
            error("Course module is incorrect");
        }
        if (! $course = get_record("course", "id", $jeliot->course)) {
            error("Course is misconfigured");
        }
        if (! $cm = get_coursemodule_from_instance("jeliot", $jeliot->id, $course->id)) {
            error("Course Module ID was incorrect");
        }
    }

    require_login($course->id);

    add_to_log($course->id, "jeliot", "view", "view.php?id=$cm->id", "$jeliot->id");

/// Print the page header
    $strjeliots = get_string("modulenameplural", "jeliot");
    $strjeliot  = get_string("modulename", "jeliot");

    if ($course->category) {
        $navigation = "<a href=\"../../course/view.php?id=$course->id\">$course->shortname</a> ->";
    } else {
        $navigation = '';
    }

    $linkCSS = "<link rel=\"stylesheet\" type=\"text/css\" href=\"jeliot.css\" />";
    print_header("$course->shortname: $jeliot->name", "$course->fullname",
                 "$navigation <a href=index.php?id=$course->id>$strjeliots</a> -> $jeliot->name",
                  "", $linkCSS, true, update_module_button($cm->id, $course->id, $strjeliot),
                  navmenu($course, $cm));

/// Print the main part of the page
?>  

    <div id="jeliot_description">
      <p>
        <?php echo $jeliot->intro; ?>
      </p>
    </div>
    <div id="jeliot_link">  
    <a title="Start Jeliot!" href="<?php echo jeliot_create_JNLP_link($course, $jeliot);?>">
         <img src="logo3d32.png" title="Start Jeliot 3" height="32" width="32" alt="Jeliot 3 logo"/><br/> Start Jeliot</a><br/>  
    <?php helpbutton("WebStartHelp",get_string('WSHelp_title','jeliot'), 'jeliot',false,true); //true,get_string('WSHelp_content','jeliotWS'));
?>

    </div>
    <?php if ($jeliot->displaysource){
              echo "<hr/><div id=\"jeliot_sourceCode\">";
    
              //$filepath = $CFG->dataroot.$relativepath."/".$course->id."/".$jeliot->file;
              $filepath = $CFG->dataroot."/".$course->id."/".$jeliot->sourcefile;
              $text = htmlentities(implode('', file($filepath)));
              echo '<pre>'. $text .'</pre>';
              echo "</div>";
          }
    ?>
<?php
/// Finish the page
    print_footer($course);
?>
