<?php // $Id: index.php,v 1.7 2007/09/03 12:23:36 jamiesensei Exp $
/**
 * This page lists all the instances of jeliot in a particular course
 *
 * @author
 * @version $Id: index.php,v 1.7 2007/09/03 12:23:36 jamiesensei Exp $
 * @package jeliot
 **/


    require_once("../../config.php");
    require_once("lib.php");

    $id = required_param('id', PARAM_INT);   // course

    if (! $course = get_record("course", "id", $id)) {
        error("Course ID is incorrect");
    }

    require_login($course->id);

    add_to_log($course->id, "jeliot", "view all", "index.php?id=$course->id", "");


/// Get all required stringsjeliot

    $strjeliots = get_string("modulenameplural", "jeliot");
    $strjeliot  = get_string("modulename", "jeliot");


/// Print the header

    $navlinks = array();
    $navlinks[] = array('name' => $strjeliots, 'link' => '', 'type' => 'activity');
    $navigation = build_navigation($navlinks);

    print_header_simple("$strjeliots", "", $navigation, "", "", true, "", navmenu($course));

/// Get all the appropriate data

    if (! $jeliots = get_all_instances_in_course("jeliot", $course)) {
        notice("There are no jeliots", "../../course/view.php?id=$course->id");
        die;
    }

/// Print the list of instances (your module will probably extend this)

    $timenow = time();
    $strname  = get_string("name");
    $strweek  = get_string("week");
    $strtopic  = get_string("topic");

    if ($course->format == "weeks") {
        $table->head  = array ($strweek, $strname);
        $table->align = array ("center", "left");
    } else if ($course->format == "topics") {
        $table->head  = array ($strtopic, $strname);
        $table->align = array ("center", "left", "left", "left");
    } else {
        $table->head  = array ($strname);
        $table->align = array ("left", "left", "left");
    }

    foreach ($jeliots as $jeliot) {
        if (!$jeliot->visible) {
            //Show dimmed if the mod is hidden
            $link = "<a class=\"dimmed\" href=\"view.php?id=$jeliot->coursemodule\">$jeliot->name</a>";
        } else {
            //Show normal if the mod is visible
            $link = "<a href=\"view.php?id=$jeliot->coursemodule\">$jeliot->name</a>";
        }

        if ($course->format == "weeks" or $course->format == "topics") {
            $table->data[] = array ($jeliot->section, $link);
        } else {
            $table->data[] = array ($link);
        }
    }

    echo "<br />";

    print_table($table);

/// Finish the page

    print_footer($course);

?>
