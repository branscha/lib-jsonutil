# Project Tasks

* 2012-12-01: Fix parsing of scientific notation 2233.4343E+7. It was already fixed in the lib-jsontools.
* Check the parseJsonObject() if the key is not a string. The code applies toString() to the key. This means that you could write objects as keys as well (but they would be rendered to a string). So it will not produce errors, but the results of this construct is not usable. Check the JSON spec and put a test + warning in place.
