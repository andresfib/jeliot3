<?php //$Id

/**
 * This file defines de main newmodule configuration form
 * It uses the standard core Moodle (>1.8) formslib. For
 * more info about them, please visit:
 * 
 * http://docs.moodle.org/en/Development:lib/formslib.php
 *
 * The form must provide support for, at least these fields:
 *   - name: text element of 64cc max
 *
 * Also, it's usual to use these fields:
 *   - intro: one htmlarea element to describe the activity
 *            (will be showed in the list of activities of
 *             newmodule type (index.php) and in the header 
 *             of the newmodule main page (view.php).
 *   - introformat: The format used to write the contents
 *             of the intro field. It automatically defaults 
 *             to HTML when the htmleditor is used and can be
 *             manually selected if the htmleditor is not used
 *             (standard formats are: MOODLE, HTML, PLAIN, MARKDOWN)
 *             See lib/weblib.php Constants and the format_text()
 *             function for more info
 */

require_once ('moodleform_mod.php');

class mod_jeliot_mod_form extends moodleform_mod {

	function definition() {

		global $COURSE;
		$mform    =& $this->_form;

//-------------------------------------------------------------------------------
    /// Adding the "general" fieldset, where all the common settings are showed
        $mform->addElement('header', 'general', get_string('general', 'form'));
    /// Adding the standard "name" field
        $mform->addElement('text', 'name', get_string('jeliotname', 'jeliot'), array('size'=>'64'));
		$mform->setType('name', PARAM_TEXT);
		$mform->addRule('name', null, 'required', null, 'client');
    /// Adding the optional "intro" and "introformat" pair of fields
    	$mform->addElement('htmleditor', 'intro', get_string('description', 'jeliot'));
		$mform->setType('intro', PARAM_RAW);
		$mform->addRule('intro', get_string('required'), 'required', null, 'client');
        $mform->setHelpButton('intro', array('writing', 'richtext'), false, 'editorhelpbutton');

        $mform->addElement('format', 'introformat', get_string('format'));

//-------------------------------------------------------------------------------
    /// Adding the rest of newmodule settings, spreeading all them into this fieldset
    /// or adding more fieldsets ('header' elements) if needed for better logic
     $mform->addElement('choosecoursefile', 'sourcefile', get_string('file', 'jeliot'), array('courseid'=>$COURSE->id));
    $mform->addRule('sourcefile', null, 'required', null, 'client');
    $mform->addElement('selectyesno', 'displaysource', get_string('displaySource', 'jeliot'));
    $mform->addElement('selectyesno', 'questions', get_string('askquestions', 'jeliot'));
    $mform->addElement('modgrade', 'grade', get_string('grade','jeliot'), false);

    $mform->addElement('text', 'metadata', get_string('metadata', 'jeliot'), array('size'=>'64'));
    $mform->setType('metadata', PARAM_TEXT);

//-------------------------------------------------------------------------------
        // add standard elements, common to all modules
		$this->standard_coursemodule_elements();
//-------------------------------------------------------------------------------
        // add standard buttons, common to all modules
        $this->add_action_buttons();

	}
}

?>
