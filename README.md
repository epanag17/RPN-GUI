# RPN-GUI
Graphical User Interface Editor/Simulator for Reversible Petri Nets


## Features:
- RPN Graphical Editor: Add/Delete/Move/Edit places, transitions, tokens and arcs.
- Save/Load RPNs: In XML format.
- Simulator Mode: Execute (enabled) transitions with Forward, Backtracking, Causal and Out-Of-Causal execution modes.
- Reachability: Test if a marking is reachable from the initial marking.


## Instructions (Windows)

1. Install the Java Development Kit version 13 (jdk-13+) 
2. Download and Install Eclipse (https://www.eclipse.org/)
3. Download and extract the Github project
4. In Eclipse: Select "File>Import" and pick "General>Existing Workspace Into Project". Browse and select the extracted Github project.
5. Download JavaFX Windows SDK from https://gluonhq.com/products/javafx/
6. Extract JavaFX SDK and place the folder somewhere you can find it easily.
7. In Eclipse: Right-Click the project name from the Package Explorer and select "Bulid Path>Add External Archives". Select all \*.jar files from the lib folder of the extracted javafx sdk and press "Open".
8. Open Run Configurations for RPNApp, and set "Arguments>VM Arguments" to: --module-path (path to javafx sdk lib folder) --add-modules=javafx.controls,javafx.fxml
9. Apply and Run

## (Optional) SceneBuilder

...
