<?php

/**
 * @param int $oldversion the version we are upgrading from
 * @return bool result
 */
function xmldb_portfolio_googledocs_upgrade($oldversion) {
    global $CFG, $DB;
    require_once(__DIR__.'/upgradelib.php');

    return true;
}
