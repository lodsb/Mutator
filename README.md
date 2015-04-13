# Mutator - Genetic Algorithm Approach to Collaborative Music Creation

This is the original source code for the prototypical beat / groove sequencing application in the 
publication:

Klügel, Niklas and Lindström, Andreas and Groh, Georg. "A Genetic Algorithm Approach to Collaborative Music Creation on a Multi-Touch Table

Abstract:
Multi-touch interfaces provide new opportunities for collaborative
music composing. In this report, an approach
using genetic algorithms to evolve musical beats in a collaborative
setting is presented. A prototype using a multitouch
interface is developed and evaluated

which can be found here: http://speech.di.uoa.gr/ICMC-SMC-2014/images/VOL_1/0286.pdf , published in the proceedings of the ICMC2014

Mutator is originally a tabletop application but the framework also allows use with mouse input apart from TUIO and native win7 touch.

Dependencies:
- reakt: https://github.com/lodsb/reakt
- UltraCom: https://github.com/lodsb/UltraCom
- mutant5000: https://github.com/lodsb/mutant5000

- checkout and build + sbt publish for each of these

- The application only produces midi-data for controlling external instruments, the sequencing is performed in PureData (see additional .pd patch) 




