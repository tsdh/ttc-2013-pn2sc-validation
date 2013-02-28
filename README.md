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

Leiningen is a configuration and build management tool based on
[Maven](http://maven.apache.org/).

## Usage

First, clone this project and change into its directory:

````
$ cd ttc-stuff/
$ git clone git://github.com/tsdh/ttc-2013-pn2sc-validation.git
$ cd ttc-2013-pn2sc-validation/
````

Afterwards, you can use `lein run` to run the validator.  The first invocation
will fetch all required dependencies for you.  This may seem as if it was
downloading the whole internet (it'll also download Maven and everything
required by it), so be patient.

`lein run` without arguments will print the synopsis of the command:

````
$ lein run
You are using me wrongly!

Usage: lein run <testcase> <statechart-xmi>
  - <testcase> may be 1, 2, or 3
  - <statechart-xmi> is an EMF XMI file containing the target statechart
````

So you should call it with a number and a statechart model XMI file.  The
number tells the validator to what testcase the provided model corresponds.  An
invocation for testing a model `sc1.xmi` if it conforms to the expected outcome
of the first testcase is given below:

````
$ lein run 1 ../path/to/sc1.xmi
````

## License

Copyright © 2013 Tassilo Horn <horn@uni-koblenz.de>

Distributed under the GNU General Public License, version 3 (or later).
