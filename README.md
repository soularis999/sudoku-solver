# sudoku-solver

The purpose of the project is to explore potential of the Clojure language while implementing the solution to solve Sudoku.
The implementation is following a brute force (backtracking) algorithm. The simplicity of this algorithm and the ability to test recursive calls were the causes of selecting the algorithm.  

## Usage

The project uses leiningen project manager. The code can either be ran from leiningen REPL or byu building the uber jar.
The uber jar can be executed with following call. 

    $ lein uberjar

The code can then be ran by going to target directory and executing

    $ java -jar sudoku-solver-0.1.0-standalone.jar

...

## License

Copyright © 2020 FIXME

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
