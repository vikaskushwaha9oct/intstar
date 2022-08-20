Are there a meta set of quantifiers of knowledge (measurement sets), which when used together, allow agents to
autonomously explore and create knowledge, and possibly exhibit "personality" and "value system"?

Below is a rough attempt at creating such a set of quantifiers.

* **Associativity** :
Measure of how closely connected is a given set of measurement to another fixed sensory measurement. It tries to emulate
things like how thinking about a food item may make us feel hungry, for example. We may be able to calculate it as
distance between measurements in a measurement graph.

* **Randomness** :
It is a measure of lack of patterns. One approach to calculate it is to cluster measurements based on patterns, and look
for number of measurements which span across clusters.

* **Consistency** :
How non contradictory are measurements in a given set? To calculate it, we can count the number of contradictory
measurement pairs found in an approximate scan of the set.

* **Stateness** :
A measure of information density, which can be calculated by the number of measurements which need focus in each epoch
of attention.

* **Genericity** :
How many measurements use a given measurement as their template?

* **Contextness** :
How much global information one needs to recall, when processing a set of measurements. We can calculate it as the
number of memory queries, required in its processing.

* **Procedurality** :
It is a measure of ease of converting a set of measurements to a list of actions. The more sequenced a set of
  measurements, the better.

* **Doubtness** :
  How much is unknown, with no associated plan of revealing it?

These quantifier would conflict with each other. The role of the attention is to schedule execution of solvers, based on
which quantifier presents most pressing needs, and a bias built into the system as a proxy for "pre-disposition to
certain behaviours". Roughly speaking, quality of these quantifiers and solvers determines intelligence. Associativity
values determines the values system.

**Stability of the system** :
Long term stability of a system (anti-fragility) needs 3 classes of quantifiers.

1) Defence: Detection of internal/external threats, which can throw the system off
2) Sustenance: These quantifiers setup the baseline that the system should meet most of the time
3) Exploration: Allow system to experiment with new states in a stable manner
