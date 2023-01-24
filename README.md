# intstar

This project defines a computational framework (named measurement calculus) for modelling complex adaptive hierarchical
systems. Some examples of such systems which exhibit (int)egrative (int)elligence include our brains, organizations,
social and economic systems.

Aim is to explore:

1) how complexity can arise from simple solvers interacting with each other under the guidance of a hierarchy,
2) how metrics on the structure of knowledge can be used to create solvers which generalize across domains, with little
   training,
3) how the combination of above two, could result into a flexible real time learning system, capable of taking decisions
   and adapting to environment in an unsupervised manner.

Read more on the related ideas
here: [Culture: Where Physical Meets Psychological](https://culturewherephysicalmeetspsychological.quora.com/)

**M calculus concepts:**

1) _Measurement_: We need a common way of describing information, which can work across different abstraction levels and
   domains. It should also allow us to analyze the structure of information. To achieve that we model information as a
   sequence of measurements. A measurement is modelled as a comparison between two measurable values. Further,
   uncertainty and confidence, can be associated with each measurement. This allows us to capture both noise and inexact
   or approximate knowledge. Conceptual relationships are encoded as measurements. Concepts get their meaning based on
   the relationships they have with other concepts. Related works would include probabilistic rules of prolog and
   numeric vectors. Logical rules can model discrete knowledge but poor at modelling analog information such as sensory
   data. Numeric vectors are good for doing mathematical transformation on analog data, but lack the domain context. By
   modelling information as measurements, we can retain both the domain context, and its analog nature. Measurable
   values are composed of a concept and a measure. System allows addition of new concepts, but measures are limited to a
   small set. Complex relationships need to be broken down and defined using that small set. See definition of
   Measurement [here](intstar-mcalculus/src/main/kotlin/intstar/mcalculus/Measurement.kt).

2) _Switch_: In hierarchical systems, higher parts of the system maintain an abstract view of the state of the system,
   while lower parts deal with actions and low level sensory data. These parts need to interact with each other via a
   mechanism. We call that mechanism "switch" in our framework. Switch allows two parts, which deal with world at
   different abstraction levels, to interact with each other. We use it create manifestation hierarchies: where one end
   of the switch declares concepts & their relationships, and the end other provides manifestations for the those
   concepts & associated actions. Any communication between two switch ends is done in terms of a sequence of
   measurements. It allows you to switch between a declarative and an imperative world. See definition of Switch
   [here](intstar-mcalculus/src/main/kotlin/intstar/mcalculus/Switch.kt).

3) _Attention Action Cycles_: Agent sits at the top of the hierarchy. It needs a mechanism to choose between multiple
   competing priorities. We use cycles of attention and action to achieve that. We instantiate an Agent, with
   appropriate implementations of Attention and Action, and a set of measurements to bootstrap with. See definition of
   Agent [here](intstar-mcalculus/src/main/kotlin/intstar/mcalculus/Agent.kt).

4) _Structural Metrics On Knowledge_: Adaptive systems need motivation on how to self organize and adapt. If we can
   quantify properties like consistency, genericity, statefulness, etc. as measures on information structures, they can
   be used to define optimization problems, for system to solve and adapt around. Read some examples of such metrics
   [here](intstar-ai/src/main/kotlin/intstar/ai/quantifier/README.md).

**Project architecture:**

1) _mcalculus_ module defines different parts of the M calculus computation model.

2) _mcalculus-lang_ module defines parsers and renderer for a human readable language representation of measurements.
This includes a syntax to specify measurement sets compactly by writing common parts only once.

3) _mcalculus-helper_ module defines: a dsl to easily define measurements directly in kotlin, a dsl/system to pattern
match over measurements, and some basic implementations of Attention and Action.

4) _mcalculus-examples_ module defines small systems illustrating usage of M calculus model and the associated
   machinery.

5) _ai_ module defines an AI system, built using M calculus computation model. (WIP)

6) _knowledge-creator_ module defines a system for creating knowledge in terms of measurement sequences. This includes
   decision making recipes and concept dictionaries. (WIP)

**AI module packages:**

1) _io_ defines switch sides for AI to interact with its environment and other useful knowledge systems.

2) _quantifier_ defines switch sides to quantify structural properties of measurement sets. They help us define
   optimization problems that the solvers should try to solve.

3) _solver_ defines switch sides to solve various optimization problems.

4) _agent_ defines switch sides which provide various Attention and Action mechanisms.

5) _prelude_ defines functionality to be used by above 4 packages.

**Usage:**

1) Define an Agent, by choosing an implementation of Attention & Action. Look at _example_ module.

2) Bootstrap with measurements to manifest various types of switches (io/quantifier/solver) based on functionality
   required.

3) Extend further with custom switch implementations beside the ones available from _ai_ module.

4) Create new decision making recipes or concept dictionaries using tools provided by _knowledge-creator_, and make them
   available to the Agent via a switch side.
