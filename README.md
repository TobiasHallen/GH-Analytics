# GitHub Analytics

A repository containing my CS3012 Project, which is a Github user data visualization. It consists of a java file which gathers the data from the github api and writes it to a json file, and an html file which uses d3js to create a force-directed graph visualization out of user follows data.

# Known Bugs:

Occasionally when swapping between datasets quickly, the graph can become stuck in the top-left corner. Jiggling an exposed node slightly corrects the issue. Also, the graph can be quite slow to expand whn it first spawns with a large dataset. Spreading it apart manually fixes that, too.

# Running Code:

https://tobiashallen.github.io/GH-Analytics/
