The LangevinNoVis01 documentation in Markdown format (*.md) is edited in eclipse using the WikiText plugin

# LangevinNoVis01 - is the solver for SpringSaLaD simulation program

LangevinNoVis01 is a particle-based, spatial, stochastic solver.


## Eclipse Setup for Windows

These instructions are intended for Java developers
Requirements:  Git, Eclipse IDE for Java Developers and Java JDK 1.8 or later

  * Open CommandPrompt, navigate to the Eclipse workspace folder.
  * Clone the LangevinNoVis01 client using git:
  
   ```bash
   git clone https://github.com/jmasison/LangevinNoVis01.git
   ```
  * Open Eclipse, Import the cloned project using Maven. Depending on the Eclipse version there'll be small differences with the importing steps.
  * Wait for the import and the build to finish, there should be no errors.
  * Create a Debug configuration as a Java Application.
     * the Main Class is langevinnovis01.Global
     * there are two Program Arguments: <full path to the simulation input file> and: <number of simulations> (use 1).
     * example of arguments: C:\Users\MyName\Documents\springsalad_data\MyModel\MyModel.txt 1
