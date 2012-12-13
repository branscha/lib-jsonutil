# Project Tasks
## To Do

-

## Done
### Version 1.1

* 2012-12-12: Fix parsing of atomic values: true, false, null. Don't forget to append these to the buffer so that the error message is correclty formatted. Parsing eg. [ true, false, bad ] will give error message [,, X <- because the values were not appended to the buffer.
* 2012-12-01: Fix parsing of scientific notation 2233.4343E+7. It was already fixed in the lib-jsontools.
* Check the parseJsonObject() if the key is not a string. The code applies toString() to the key. This means that you could write objects as keys as well (but they would be rendered to a string). So it will not produce errors, but the results of this construct is not usable. Check the JSON spec and put a test + warning in place.
