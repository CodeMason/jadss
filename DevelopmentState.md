From now, we might work over **PulseAudio** and/or protocols like **SIP** (VoIP protocol) in order to go distributed.
It's important to know if there exists some signalisation mechanism in these systems/protocols, to 'return' the position and other data to our server.

**Maybe Ubuntu Studio might be a helpful OS to work with.**

## Goals Achieved ##

  * Math Sounds (Sin waves and so):
    * Play fixed sound
    * Loop fixed sound
  * File Sounds (wav, au and maybe others):
    * Play sound
    * Loop sound
  * Use volume control to simulate distance.
  * Use time shifting to simulate distance.
  * Play only concrete sounds chunks, specified by time.
  * Solve the 'chunk' issue.
  * Ameliorate quality/precision relation (make it less file-dependant).
  * Going distributed

## Next Goals ##

  * Estimate maximum/minimum delay due to every layer on this system (to see which ones we're able to get rid of).
  * Write client app (easy one, just sends a sound to server).

## Other Main Goals ##

  * ~~Virtual app~~