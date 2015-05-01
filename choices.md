# Sound API #

| **_API_** | **_PRO_** | **_CONS_** |
|:----------|:----------|:-----------|
| [Java Sound / JMF](http://java.sun.com/products/java-media/sound/) |  <ul><li>Official</li><li>Good API Documentation</li><li>Network tasks works well</li><li>Play control good enough</li><li>Powerful Synthesizer</li></ul> | <ul><li>Does not support MP3 (maybe possible in the new SPI?).</li><li>A little confusing for volume and other controls.</li></ul>|
| [JavaZoom](http://www.javazoom.net) | <ul><li> Seems easy to work with</li><li>MP3 Support</li><li>Works over Java Sound</li></ul> | <ul> <li>Not officially supported</li><li>Lacks a little in documentation </li></ul>|
| [Tritonus](http://www.tritonus.org/) | <ul><li> Plugin for OGG and MP3 decoder/encoder</li><li>Cross-plataform?</li></ul> | <ul><li>It's just a plugin, the rest remains as Java Sound</li><li>Seems to have issues with some hardware drivers in different OS</li></ul> |
| [NativeFmod](http://jerome.jouvie.free.fr/Fmod/NativeFmod/index.php) | <ul><li>GREAT for 3D sound simulation</li><li>Documentation for Fmod is quite extense</li><li>Used in different architectures (even game consoles)</li><li>Broadcasting and streaming seems easy</li></ul> | <ul><li>It's a C++ binding</li><li>Oriented to interactive apps</li><li>Maybe a little too professional</li><li> Not GPL (but free for non-profitable tasks).</li></ul> |