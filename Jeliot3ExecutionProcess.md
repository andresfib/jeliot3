# Jeliot 3 Execution Process #
## Main threads ##

Launcher
JeliotWindow
Interpreter (DynamicJava)
Director
MCodeTheaterInterpreter
Other MCodeinterpreters


## What happens when animation starts ##

  1. Compile button is pushed
  1. JeliotWindow object looks for the class with the main method.
  1. Control transfers from Jeliot object that creates
    1. the DynamicJava interpreter through a Launcher object (own thread)
    1. visualizations in the visualization pane: Theather, Call Tree, and History
  1. Play button is pushed
  1. JeliotWindow object transfers the control to Jeliot object and MCode-interpreters for each visualization are created: TheaterMCodeInterpreter, etc. (each in own thread)
  1. Jeliot object transfers the control to main MCodeInterpreter (TheaterMCodeInterpreter) through Director Object
  1. For each animation step a line (or a few lines) of MCode is read in  MCodeInterpreter as it arrives through the pipe from the Interpreter (DynamicJava) and for each instruction there is a command method in the corresponding MCodeInterpreter that determines what happens after each instruction. For example, TheatherMCodeInterpreter calls Director in order to produce a visualization in the Theater and saves the created value for further use.

<IMAGE: [process diagram ](http://www.websequencediagrams.com/cgi-bin/cdraw?lz=VXNlci0-SmVsaW90IFdpbmRvdzogQ2xpY2tzIGNvbXBpbGUKABENACUIOiBFeGVjdXRlAB8JKCkAIwctPkxhdW5jaGVyOiBTdGFydHMgbAAKByB0aHJlYWQKbm90ZSByaWdodCBvZiAAJQoKc2V0cyBpbnRlcnByZXRlciBtZXRob2QKY2FsbCBhbmQgcmVhZC93cml0ZSBwaXBlcwplbmQgbm90ZQoAbQgtPkkANgo6AEMKIEphdmEgY29kZQoAFgsAIA4AVAVzIE1Db2RlIHRvAF4FAIEeDwBRDApub2JvZHkgaXMAgRMFaW5nIApmcm9tIHRoAIEZBiB5ZXQAgRgKAIIXCCAAgmEPdHJ5IHRvIGVudGVyIGFuaW1hACILAIJnCHJld2luZACCWgtEaXJlY3RvcjogY3JlYXRlcyBkAAsHAIJ_CQCBQwVUaGVhdGVyAIIIDW5ldwCCdRYAGRkKYWxzbwCDOAdzIGZvciBvdGhlciAKAIMiC3MgaWYgbmVlZACDCwoAhEsVYwCEWgZQbGF5AIRNEACBSwpzaG93IG1lAIJGBWdvb2RzCgCBaQgAgT0bc3RhcnQAgwMJAIJ-DQoAgX0QAIN4CACCPgpkcmF3IGJveGVz&s=default)>
<---  To be used in  http://www.websequencediagrams.com/ ------>
User->Jeliot Window: Clicks compile
Jeliot Window->Jeliot: Executes compile()
Jeliot->Launcher: Starts launcher thread
note right of Launcher:
sets interpreter method
call and read/write pipes
end note
Launcher->Interpreter: interpret Java code
Interpreter->Interpreter:writes MCode to pipe
note right of Interpreter:
nobody is reading
from the pipe yet
end note
Jeliot-> Jeliot Window: try to enter animate
Jeliot->Jeliot: rewind()
Jeliot->Director: creates director
Jeliot->MCodeTheaterInterpreter: new thread
note right of MCodeTheaterInterpreter:
also threads for other
interpreters if need
end note
User->Jeliot Window: clicks Play
Jeliot Window->Director: show me the goods
Director->MCodeTheaterInterpreter: start reading from the pipe
MCodeTheaterIntepreter->Director: draw boxes
---->
## Actors ##

Decentants of the Actor class are the basic components of the animation. Each Actor has its own part (e.g., ValueActor plays a value in the animation and VariableActor plays a Variable). For instnace, in order to create a new PointerActor one would refactor the ReferenceActor so that it can have different kinds of actors as its targets. Currently, ReferenceActor can only have InstanceActors as its targets where it points to.

Actors can be combined to other Actors and components that implement the ActorContainer interface.

//Just wondering if you have this image?
<IMAGE: Actor and ActorContainer hierarchy>