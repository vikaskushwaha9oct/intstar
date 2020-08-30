# intstar

This project defines a computation model/language for writing (int)egrative (int)elligence systems (named measurement
calculus).

It approaches AI from a distributed systems perspective and relies on structural metrics to guide intelligent behaviour.
Aim is to explore: 1) how complexity can arise from simple solvers working together in a distributed setting, 2) how
metrics on the structure of knowledge can be used to create solvers which generalize across domains, with little
training, and 3) how the combination of above two, could result into a flexible real time learning system, based on
conversation.

M calculus is based on following concepts:

1) Switch: An abstraction to allow two systems to talk with each other. We use it create manifestation hierarchies:
where one end of the switch declares concepts & their relationships, and the end other provides manifestations for the
those concepts & associated actions. Any communication between two switch ends is done in terms of a sequence of
measurements.

2) Measurement: A common representation system for knowledge/actions. It consist of two measurable values being compared
with each other. Further, uncertainty and confidence, can be associated with each measurement. The representation aims
to be minimal, with the ability to encode different types of noisy knowledge representations easily. Such a
representation should allow us to easily define and experiment with metrics based on structure of knowledge.

3) Agent: Provides a template for computation. It is defined in terms of attention and action cycles. Agent, Attention,
Action -- all 3 are defined as switch sides. To define an intelligent agent, we instantiate an Agent, with appropriate
implementations of Attention and Action, and a set of measurements to bootstrap with.

4) Language: Provides abstractions to define serializable languages on top of Measurement representation. The default
human readable language to serialize Measurement objects is called M Lang.

Next steps:

1) Add examples -- hello world, distributed application, logic system, neural networks, and their integrations.

2) Work towards improving the state of the art for AI systems.
