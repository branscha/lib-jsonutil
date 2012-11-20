# Project Notes
## Design Decisions

* No dependencies on external jars, other than the standard Java library.
* Convert JSON from/to plain maps and arrays, do not use an explicit JSON model to reduce the knowledge you need to have to use the library.
* Parser errors should contain context information, the exact location where the error occurs. Otherwise you are stuck with an exception without knowing exactly where the parser stopped. So extra contextual information (which reduces efficiency) can be passed around to keep track of the location.
* Use the existing, unchecked, IllegalArgumentException when something goes wrong. We will not introduce our own model.

## Possible Enhancements

* Loop detection. If you construct a recursive map structure (maps having elements that point to the parent maps|) then the rendering algorithm will loop forever. A possible cure might be to keep track of a list of the maps that were already rendered and throw an exception if a loop is detected. The parsing does not have this problem since it is impossible to represent a graph in plain JSON text.
* Derive our own List and Map classes to store parsing information, the location where the data structure was encountered in the input. In this way the data structure we get form the util can be handled as plain maps and lists, but if we really need to have information about the origin of the data we can get that too. It is only interesting for parsing, it does not apply to rendering. StreamTokenizer contains a method 'lineno()' and this information could be transferred into our specialized lists and maps.
* Doubles are used for all numbers. Maybe it could be useful to distinguish between integers and floats.