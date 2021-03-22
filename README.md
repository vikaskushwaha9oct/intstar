# intstar

This project defines a computation model/language/framework for writing (int)egrative (int)elligence systems (named
measurement calculus).

It approaches AI from a distributed systems perspective and relies on structural metrics to guide intelligent behaviour.
Aim is to explore: 1) how complexity can arise from simple solvers working together in a distributed setting, 2) how
metrics on the structure of knowledge can be used to create solvers which generalize across domains, with little
training, and 3) how the combination of above two, could result into a flexible real time learning system, based on
conversation.

**M calculus concepts:**

1) _Switch_: An abstraction to allow two systems to talk with each other. We use it create manifestation hierarchies:
where one end of the switch declares concepts & their relationships, and the end other provides manifestations for the
those concepts & associated actions. Any communication between two switch ends is done in terms of a sequence of
measurements.

2) _Measurement_: A common representation system for knowledge/actions. It consist of two measurable values being
compared with each other. Further, uncertainty and confidence, can be associated with each measurement. The
representation aims to be minimal, with the ability to encode different types of noisy knowledge representations easily.
Such a representation should allow us to easily define and experiment with metrics based on structure of knowledge.

3) _Agent_: Provides a template for computation. It is defined in terms of attention and action cycles. Agent,
Attention, Action -- all 3 are defined as switch sides. To define an intelligent agent, we instantiate an Agent, with
appropriate implementations of Attention and Action, and a set of measurements to bootstrap with.

4) _Language_: Provides abstractions to define serializable languages on top of Measurement representation. The default
human readable language to serialize Measurement objects is called M Lang.

**Project architecture:**

1) _mcalculus_ module defines different parts of the M calculus computation model.

2) _mlang_ module defines parsers and renderer for a human readable language representation of measurements. This
includes a syntax to specify measurement sets compactly by writing common parts only once.

3) _base_ module defines: a dsl to easily define measurements directly in kotlin, a dsl/system to pattern match over
measurements, and some basic implementations of Attention and Action.

4) _examples_ module defines small systems illustrating usage of M calculus model and the associated machinery.

5) _ai_ module defines a comprehensive AI system, which can be be used to write intelligent conversational agents and
decision makers. (WIP)

**AI module packages:**

1) _io_ defines switches for AI to interact with its environment.

2) _quantifier_ defines switches to quantify structural properties of measurement sets. They help us define optimization
problems that the solvers should try to solve. Read more [here](intstar-ai/src/main/kotlin/intstar/ai/quantifier/README.md).

3) _solver_ defines switches to solve various optimization problems.

4) _agent_ defines switches which provide various Attention and Action mechanisms.

5) _prelude_ defines functionality to be used by above 4 packages.

**Usage:**

1) Define an Agent, by choosing an implementation of Attention & Action. Look at _example_ module.

2) Bootstrap with measurements to manifest various types of switches (io/quantifier/solver) based on functionality
required.

3) Extend further with custom switch implementations beside the ones available from _ai_ module.
