<?php

/**
 * Plugin capabilities.
 *
 * @package    repository_nuxeo
 * @copyright  2014 Rectorat Rennes
 * @author     
 * @license    
 */

defined('MOODLE_INTERNAL') || die();

$capabilities = array(

    'repository/nuxeoworkspaces:view' => array(
        'captype' => 'read',
        'contextlevel' => CONTEXT_MODULE,
        'archetypes' => array(
            'user' => CAP_ALLOW
        )
    )
);
