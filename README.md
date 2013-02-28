# A Validator for the TTC 2013 PetriNet-to-Statechart Case

This is a simple validator that checks the result Statchart models of the TTC
2013 PetriNet-to-Statechart Case.  It works only with the test cases 1 to 3.

The following things are checked:

  - For the test cases 1 and 2, there has to be exactly one `Statechart`
    element containing exactly one `AND` state containing the top-most `OR`
    state created by the reduction rules.

  - For every element type, the expected number of instances is checked against
    the actual number of instances.

  - The expected containment hierarchy is checked against the actual
    containment hierarchy.
	
  - The expected contents of the `rnext` and `next` references of each
    `HyperEdge` are checked against their actual contents.  Hereby, the order
    is ignored.

## Prerequisites

You need to install the `lein` script from [Leiningen](http://leiningen.org)
and place it somewhere in your `PATH`.

  - Linux/Mac: [lein](https://raw.github.com/technomancy/leiningen/stable/bin/lein)
  - Windows: [lein.bat](https://raw.github.com/technomancy/leiningen/preview/bin/lein.bat)

Leiningen is a configuration and build management tool based on Maven.

## Usage

First, clone this project:

````
cd ttc-stuff/
git clone 
````

## License

Copyright © 2013 Tassilo Horn <horn@uni-koblenz.de>

Distributed under the GNU General Public License, version 3 (or later).
