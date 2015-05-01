# Network Architecture #

If possible, there's an interesting approach which consists in work over PulseAudio in order to avoid developing a real-time network protocol (PulseAudio uses [RTP](http://es.wikipedia.org/wiki/Real-time_Transport_Protocol)).

This is how it's supposed to work.
![http://jadss.googlecode.com/svn/trunk/jadss/imgs/network_connections.png](http://jadss.googlecode.com/svn/trunk/jadss/imgs/network_connections.png)


# PC Internal Architecture #

Here I'm taking some tips to see the project layers as a typical sound application.

  * There are some layers we need to 'cross' to play sound in our computer. Each one will take a delay between it's coming from superior level and going into the next one. If we're able to reduce these delays, it may achieve our goals.
<br>
<img src='http://jadss.googlecode.com/svn/trunk/jadss/imgs/layers.gif' />
<br>
<ul><li>Java layer:<br>
<ul><li>First of all, we have our Application layer. This layer is <b>over</b> our project, since we're trying to offer a service. We'll implement, of course, an example app to show the power of the project.<br><br>
</li><li>Next, there's our Sound system layer, which connects the application to Java Sound library. This is where we need to focus our attention in order to optimize the project, since the rest of them will not depend on us.<br><br>
</li><li>Then, the Java Sound as part of JMF (<i>or Java Media Framework</i>) is what connects our program with the OS. It interacts with the JVM directly, so it give us a very interesting abstraction layer.<br><br>
</li></ul></li><li>OS layer: First of all, we find a layer which contains the sound mixer, which depending on the OS selected, will be slightly different:<br><br>
<ul><li>On the one hand, if we choose a modern Windows platform, we'll find the UAA (Universal Audio Architecture) which is another abstraction layer which allows to manage the sound card without reaching to a driver-specific level. We may also find another kind of layer, the WDM (<i>Windows Driver Model</i>), but UAA was supposed to replace it in most of new sound card drivers. Anyway, independently of the election, if we opted for the windows solution, we'll find that below UAA/WDM is the windows kernel, which will directly use the sound card drivers to connect it to the hardware.<br><br>
</li><li>On the other hand, if we choose a Linux/UNIX distro as the OS for our system, we'll have a similar architecture, with some differences:<br><br>
<ul><li>OSS (Open Sound System) which was the first and simplest driver manager for every Unix Platform. It's the equivalent to X's video manager for audio. It's currently in desuse, but since it keeps being the simplest, and OSS4.x is very well improved and it's usable in Unix-based OS (not just Linux), it may be very useful for some architectures.<br><br>
</li><li>ALSA (Advanced Linux Sound Architecture) system, which is a powerful generic driver manager, which was created for automatic configuration of multiple sound cards. It was independent from Linux kernel until the 2.6 kernel version, where ALSA became the official linux sound Architecture.<br><br>
</li><li>PulseAudio, which is slightly different since it works over ALSA or OSS, might be also included in this list, since it's considered a link between our applications and ALSA/OSS, which will manage directly the sound card. <b>It can be used to stream audio to another PC in a network</b><br><br>
</li></ul></li></ul></li><li>Hardware: I won't go further into hardware layer, since it seems out of scope for this project. Let's just say the OS implements several ways to reduce the CPU time to it's minimum, and it won't imply important delays.<br>
<br>
<br></li></ul>

In the image below we can understand how the project plays the audio from a file (writing it into a sound card).<br>
<br>
<br>
<img src='http://jadss.googlecode.com/svn/trunk/jadss/imgs/buffers.png' />
<br>
<br>
<br>
<i>Extracted from <a href='http://www.linuxjournal.com/article/6735'>LinuxJournal</a>:</i>

<i>A sound card has a hardware buffer that stores recorded samples. When the buffer is sufficiently full, it generates an interrupt. The kernel sound driver then uses direct memory access (DMA) to transfer samples to an application buffer in memory. Similarly, for playback, another application buffer is transferred from memory to the sound card's hardware buffer using DMA.</i>

<br>

So, in order to play a file, we need to divide it into different chunks, so it can be passed from our method <code>write</code> to the sound card (passing across the different layers commented above). I'd like to remark that the <code>write</code> method is <i>blocking</i>, so if buffer is full it will wait so it can be emptied. This is actually a good thing, since as we create our buffer with the same size of our line, we're pretty sure the application won't be finished ahead of time. In addition, we won't need to use the <i>drain()</i> method, which would cause delays and indetermination while emptying the buffer.<br>
<br>
<br>
<br>
<br>

<h1>Simple Flowchart of playing process</h1>

Here is a simplified version of how audio is played (written in the line) within our system.<br>
<br>
<br>
<img src='http://jadss.googlecode.com/svn/trunk/jadss/imgs/flow.png' />
<br>
<br>
<br>
<br>


<h1>Testing the architecture parameters</h1>

<h2>General info for test</h2>
Audio files properties<br>
<br>
<table><thead><th>File</th><th>Type</th><th>rate</th><th>bits per sample</th><th>mono/stereo</th><th>B per frame</th><th>endianness</th><th>B/s</th></thead><tbody>
<tr><td>alert.wav</td><td>PCM_Unsigned</td><td>11025</td><td>8 </td><td>mono</td><td>1 </td><td>little-endian</td><td>11025</td></tr>
<tr><td>sin.wav</td><td>PCM_Unsigned</td><td>44100</td><td>8 </td><td>mono</td><td>1 </td><td>little-endian</td><td>44100</td></tr>
<tr><td>gong.wav</td><td>PCM_Signed</td><td>11025</td><td>16</td><td>mono</td><td>2 </td><td>little-endian</td><td>22050</td></tr>
<tr><td>stillalive.wav</td><td>PCM_Signed</td><td>22050</td><td>16</td><td>mono</td><td>2 </td><td>little-endian</td><td>44100</td></tr></tbody></table>


<h2>First test</h2>
Playing 10000 μs slices of different audio files (with different properties) 400 times (so each sound would be played for 4 seconds).<br>
<br>
Background indicates audible quality and the number in the cell the percentage of time required in the application. (Red => Unaudible, Yellow=>Random artifacts, Green=>Ok).<br>
<br>
<br>
Line buffer (playSampleCount) fixed to 4KB.<br>
<br>
<img src='http://jadss.googlecode.com/svn/trunk/jadss/imgs/test1.png' />
<br>
<br>
Line buffer (playSampleCount) fixed to 1KB.<br>
<br>
<img src='http://jadss.googlecode.com/svn/trunk/jadss/imgs/test2.png' />
<br>
<br>