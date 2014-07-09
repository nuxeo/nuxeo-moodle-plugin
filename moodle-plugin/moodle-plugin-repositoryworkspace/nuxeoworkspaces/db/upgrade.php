<?php

/**  
 * Upgrade.
 *
 * @package    repository_nuxeo
 * @copyright  2014 Rectorat Rennes
 * @license    http://www.gnu.org/copyleft/gpl.html GNU GPL v3 or later
 */

defined('MOODLE_INTERNAL') || die();

/**
 * Upgrade function.
 *
 * @param int $oldversion the version we are upgrading from.
 * @return bool result
 */
function xmldb_repository_nuxeoworkspaces_upgrade($oldversion) {
    global $CFG, $DB;

    return true;
}
