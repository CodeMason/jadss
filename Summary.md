# Introduction #

The project will consist in three major applications:

  1. A **preliminary version totally virtual**, which will generate tone pulses. Then, this version will also be useful as a virtual implementation, since it will include (hopefully) a 3D world where the four PC's (or in this case, sound sources) will be placed to simulate the distance from the listener with NativeFMOD (or an equivalent library).
  1. A second version, **distributed**, a **real-life implementation** of the first version, solving the problem of managing the delays on the communications between different PCs. Positioning will not be implemented yet.
  1. The **final version** with **real positioning**. We will have to discuss the positioning method, depending on different factors. This version may not be finished at the end of the project, but it will be anyway scheduled.

---

<br>
<br>
<br>
<br></li></ul>

<h1>Choices</h1>

For API Choices see <a href='choices.md'>choices page</a>
<hr />
<br>
<br>
<br>
<br>

<h1>Ideas</h1>

<ul><li>Keep a global clock, and synchronize using it as timestamps.<br>
</li><li>Try to do the same treatment to sampled and files sounds.<br>
</li><li>Establish the limitations to each layer.<br>
<hr />
<br>
<br>
<br>
<br></li></ul>

<h1>Architecture</h1>

See ProjectArchitecture wiki page.<br>
<hr />
<br>
<br>
<br>
<br>

<h1>Interesting Formulas</h1>

<h3>Distance delay</h3>
We assume that sound moves at 343m/s. So sound delay should be a function with distance as parameter:<br>
<br>
<code>delay(x) = x/343 s</code>

For the different nodes, we'll make as we can see in bibliography:<br>
<code>TD</code><sub>i</sub><code> = [max(d</code><sub>i=1,..,4</sub><code>) - d</code><sub>i</sub><code>] / 343</code>

<h3>Attenuation</h3>

<code>Att = 20*log(r2/r1);</code>

where <code>r1</code> is usually 1meter (in speakers). But maybe we can use some reorder, if we know the actual sound amplitude:<br>
<br>
<code>Att = 20*log(r2) - 20*log(r1);</code>

It might be considered that <code>r1</code> doesn't really matters, as we should know the amplitude since it is synthesized <b>(we should eventually confirm that statement, this is just theory)</b>, so the formula should be:<br>
<br>
<code>Actual_sound(r) = Initial_Amplitude - 20*log(r);</code>

So if we want to listen with the initial amplitude the sound in a distance r we should make the sound at source a <i>20*log(r)</i> greater in amplitude.<br>
<br>
In documentation we can find this formula, which seems to adjust to our implementation:<br>
<br>
<code>VOL</code><sub>i</sub><code> = VOL</code><sub>Ref</sub><code> - 19.93 log(d</code><sub>i</sub><code> / max(d</code><sub>i = 1,..., 4</sub><code>))</code>
<hr />
<br>
<br>
<br>
<br>

<h1>Links</h1>
See <a href='Links.md'>links page</a>.