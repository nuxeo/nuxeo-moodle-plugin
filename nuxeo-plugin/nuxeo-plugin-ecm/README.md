**Plugin d'extension du service FileManager : corps du plugin** 
===============================================================


Description 
===========

Cet projet est un point d'extention du FileManager Nuxeo pour l'import d'archive
Moodle à travert  [**le plugin portfolio moodle**] [1].

Il permet d'importer vers nuxeo les ressources moodle de differentes formats:

*	**Format HTML** : la ressource (forum par exemple) est importée comme note nuxeo à la quelle on attache les pièces jointes

*	**Format leap2a** : idem que le format HTML (fonctionnalité à ameliorée)

*	**Format File** : les fichiers sont importés directement comme File nuxeo 


Une classe principale **MoodleZIPImporter** s'assure de la présence de la signature du [**le plugin portfolio moodle**] [1].

Pour chaque format d'export, correspond une classe implementant l'interface **MoodleExportParser**.



Installation 
============


Configuration 
=============



## Point à amélioré

L'import depuis le fomat [**leap2a**][2] moodle, assuré par la classe **MoodleXMLExportParser** n'est pas opérationnel.

[1]: https://github.com/nuxeo/nuxeo-moodle-plugin/tree/master/moodle-plugin/moodle-plugin-portfolio
[2]: http://www.eportfolios.ac.uk/leap2a




