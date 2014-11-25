ArithmeticParser by Trent Holliday
================

Arithmetic Parser for Theoretical Foundations taught by Professor Medidi, Fall 2014. This parser is able to 
determine if an expression containing letters, numbers, decimals, and the basic mathematical signs is valid.

#####Accepted Symbols:
+ **(**
+ **)**
+ **+** 
+ **-** 
+ **\*** 
+ **/** 
+ **a-zA-Z**
+ **0-9**
+ **.**

The file Parser.java is the updated parser code that actually works and looks a lot cleaner. The other file ParserOld.java
is my first attempt. It trues to implement the greibach form of the language and is unsucessful and very confusing.

The language this program accepts is:

| Variable | Rule |
| --------- | :-----------:|
|E | TE'|
|E' | +TE' \| -TE' \| lambda|
|T  | FT'|
|T' | *FT' \| /FT' \| lambda|
|F  |  n \| (E) \| v |