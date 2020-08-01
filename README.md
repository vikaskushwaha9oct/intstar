# intstar

This project defines a computation model/language for writing (int)egrative (int)elligence systems (called measurement
calculus).

M calculus is based on following concepts:

1) Switch: An abstraction to allow two systems to talk with each other. We use it create manifestation hierarchies:
where one end of the switch declares concepts & their relationships, and the end other provides manifestations for the
those concepts & associated actions.

2) Measurement: A common representation system for knowledge/actions. Any communication between two switch ends is done
in terms of a sequence of measurements. They consist of two measurable values being compared with each other. Further,
uncertainty and confidence, can be associated with each measurement. The representation aims to be minimal, with the
ability to encode different types of noisy knowledge representations easily.

3) Agent: Provides a template for computation. It is defined in terms of attention and action cycles. Agent, Attention,
Action -- all 3 are defined as switch sides. To define an intelligent agent, we instantiate an Agent, with appropriate
implementations of Attention and Action.

4) Language: Provides abstractions to define serializable languages on top of Measurement representation. The default
language to serialize Measurement objects is called M Lang.

Next steps:

1) Add examples -- hello world, distributed application, logic system, neural networks, and their integrations.

2) Work towards improving the state of the art for AI systems.
