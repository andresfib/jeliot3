**Python command supported in PEliot:**

Print command (int, string, etc.)<br>
Variables (int type only)<br>
Assignments<br>
Logical operations (and, or, not)<br>
Arithmetic operations (add, sub, mult, mod, div)<br>
Conditions (if, elif, else)<br>
Loops (do while)<br>

<h1>Introduction</h1>
The Jeliot family is a group of program and algorithm visualization tools to help<br>
novices understand concepts of programming and algorithms. It has been shown that animation systems can provide help in understanding how program execution occurs “behind the scenes”; it visualizes both the data and the control flow of the programs, including object-oriented concepts such as constructors and objects.<br>
<br>
Jeliot 3 provides a learning tool for the Java language. The modular design connects the interpreter and the visualization engine to each other through a specially defined intermediate language called MCode. In order to add support for Python, Jeliot 3 was converted to a “loosely coupled language” by generalizing and parameterizing the environment, and by replacing the DynamicJava interpreter with a Python interpreter, for which I chose Jython. Changes ware made to Jython so that a call to the interpret method would yield the appropriate MCode instead of compiled binary Java classes. The Jeliot 3 animation layer would later process the MCode that describes the code to create the animation.<br>
<br>
Please see the PEliot technical document for more detailed technical description. In brief, Section I discusses PEliot version – how to retrieve the build this document is based on and which Python commands this build supports. Section II expends GUI issues such as how to enable Python on PEliot and how to set the editor language to Hebrew. Section III discusses the code modifications that were made.<br>
Lastly, how to steps of adding additional language support to Jeliot3 on the extended framework can be found in the appendixes section. Those steps are described from my experience with adding Python support and include the following:<br>
Appendix I steps to add a new language interpreter to Jeliot 3<br>
Appendix II: Recommended steps in producing MCode inside new interpreters.<br>
<br>
<br>
<br>
<h1>Details</h1>
•	Download PEliot beta version (binaries) from <a href='http://code.google.com/p/jeliot3/downloads/list'>http://code.google.com/p/jeliot3/downloads/list</a><br>
•	Download PEliot beta version (source code) from <a href='https://jeliot3.googlecode.com/svn/'>https://jeliot3.googlecode.com/svn/</a> PEliot branch.<br><br>
<blockquote>The ‎‪src‬\‎‪examples‬\‎‪Python directory includes .py exapmle files that Jeliot3 can visualize. <br>Those examples include code for math operations (average.py), compound conditions (compundCondition.py), printing (helloWorld.py), Loops and variables (iterativeFibonacci.py and while.py), conditions (nestedelsif.py, simpleconditions.py).</blockquote>

Add your content here.  Format your content with:<br>
<ul><li>Text in <b>bold</b> or <i>italic</i>
</li><li>Headings, paragraphs, and lists<br>
</li><li>Automatic links to other wiki pages