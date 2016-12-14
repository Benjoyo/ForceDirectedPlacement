# ForceDirectedPlacement
Java implementation of **Fruchterman and Reingold's** graph layout algorithm using **force-directed placement**.
This is the *straight forward* variant of the algorithm that has a time complexity of **O(|V|²)**.
## Installation
Simply download and run the latest `.jar`-File found under `release`. 
## Usage
* Pick a stop criterion
    * **Iterations**: Simulation will definitly stop after **n** iterations, no matter what the result is
    * **Mechanical Equilibrium**: Simulate until the net forces on all vertices are below a certain **threshold**, but at most **1,000** iterations
* Pick formulars for the attractive and repulsive forces
    * You can use simple Java-style math expressions including e.g. **log(x)** and **sqrt(x)**
    * You can insert multiple expressions seperated by a semicolon to compare their performance
* Pick a cooling rate
    * The **cooling rate c** reduces the **temperature t** in every step: **t = t * (1 - c)**  
    * The temperature determines how much the vertices are allowed to move in every step
    * You can find a near optimal cooling rate for your current graph by clicking `Find Optimum...`
        * Select `Show Chart`to get a chart of how a certain cooling rate performs for that graph
* Generate a graph
    * Various types of graphs can be selected, **size** is the number of vertices
    * Note that **size** relates to the *dimension* for the graph type **Hyper Cube** and the *side length* for the graph type **Grid**
* Pick a frame delay
    * Specifies the animation speed, zero delay means as fast as possible

## Screenshots
![Main Window](https://raw.githubusercontent.com/Benjoyo/ForceDirectedPlacement/master/screenshots/main_window.PNG)

![Chart Window](https://raw.githubusercontent.com/Benjoyo/ForceDirectedPlacement/master/screenshots/chart_window.PNG)
