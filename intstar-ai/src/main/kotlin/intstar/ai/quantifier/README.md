Are there a meta set of quantifiers of knowledge (measurement sets), which when used together, allow agents to
autonomously explore and create knowledge, and possibly exhibit "personality" and "value system"?

Below is a rough attempt at creating such a set of quantifiers.

* **Associativity** :
  Measure of how closely connected is a given set of measurement to another fixed sensory measurement. It tries to
  emulate things like how thinking about a food item may make us feel hungry, for example. We may be able to calculate
  it as distance between measurements in a measurement graph.

* **Randomness** :
  It is a measure of lack of patterns. One approach to calculate it is to cluster measurements based on patterns, and
  look for number of measurements which span across clusters.

* **Consistency** :
  How non contradictory are measurements in a given set? To calculate it, we can count the number of contradictory
  measurement pairs found in an approximate scan of the set.

* **Genericity** :
  How many measurements use a given measurement as their template?

* **Doubtness** :
  How much is unknown, with no associated plan of revealing it?

* **Stateness** :
  A measure of information density, which can be calculated by the number of measurements which need focus in each epoch
  of attention.

* **Contextness** :
  How much global information one needs to recall, when processing a set of measurements. We can calculate it as the
  number of memory queries, required in its processing.

* **Procedurality** :
  It is a measure of ease of converting a set of measurements to a list of actions. The more sequenced a set of
  measurements, the better.

These quantifiers would conflict with each other. The role of the attention is to schedule execution of solvers, based
on which quantifier are most attention grabbing, and a bias built into the system as a proxy for "pre-disposition to
certain behaviours".

**Intelligence exhibited by the system** :

1) _Autocomplete intelligence_: The ability to look for and remember patterns and do actions based on them
2) _Storytelling intelligence_: The ability to tell a story requires logic, symbolism, questioning etc
3) _Control intelligence_: The ability to control/limit thinking/impluses

**Stability of the system** :
Long term stability of a system (anti-fragility) needs a balance of 3 categories of behaviours.

1) Sustenance: Baseline/ritualistic behaviours that system should perform regularly
2) Exploration: Allow system to experiment with new states in a stable manner. Connectivity determines the frequency at
   which we see new changes.
3) Defence: Control over internal/external stimuli, which can throw the system off
