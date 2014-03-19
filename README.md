Intellij IDEA JCR content editor plugin
=======================================

The idea behind this plugin is to allow people developing for CQ5 to use intellij for all aspects of development, so
that they don't need to utilize CRXDE to edit jcr content (nodes/properties).
This plugin provides a way of managing JCR content represented in XML (from the
vault tool), and import/export functions to hopefully eliminate the need for
the vault tool in the future. As the import/export functions are newer and have
less testing, they may vaporize your code and/or CQ5 installation (but you have
everything stored in version control, so it doesn't matter, right?).
For safety, choose vault. For convenience, use the builtin plugin functions.

Installation
------------

You can now find this plugin by going to Browse Repositories in Intellij's
plugin section. It is named "JCR Content Editor".

Setup
-----

Add the JCR facet to a module.
You can change the connection settings if you wish, all though they should work
as-is on a vanilla CQ5 installation.
You must specify at least one mount point in the "Mount Points" tab.

Mount Points
------------

The idea of a mount point is that a folder in your project should be aliased to
a node in the JCR tree of your CQ5 installation. Most plugin operations will
only work when activated at or within your file system mount point.

Intended development process using this plugin
----------------------------------------------

1.	Setup the JCR facet
2.	Export a section of the JCR tree to a local folder
3.	Right-click nodes on the file system and select "Edit Node" to edit.
4.	Add new nodes underneath the file system mount point with the "New" menu
5.	Import the content back into the JCR

Note that vault can be used in the import/export steps in place of the plugin,
if desired.


Functions
---------
Two new actions are added to the new menu when you right click in the project
pane: "New Component" and "New Node". New Component is just New Node where the
nodetype is already set to cq:Component. There are also new child node
suggestions that are based on the parent node type.

Right clicking on a .content.xml file or its parent folder, you can select
"Edit Node" to bring up a dialog for editing node properties. 

If you wish to edit content exported by vlt, you can right-click on an xml file
(anything created by vlt like dialog.xml, except .content.xml which essentially
is already unpacked), and select unpack xml. This creates folders and
.content.xml files for the inner nodes of the xml file. After doing so, you 
will probably want to delete the original xml file.

Vault export will fail with an error if any of the files that would be exported
already exist. On the other hand, vault import will delete all the content 
under the node you're importing to, and then copy over the new content.


Known issues
------------

When importing content to CQ5, the properties for the node you're importing 
won't be wiped in CQ5. Thus, new properties will be added, but old properties
will not be removed
