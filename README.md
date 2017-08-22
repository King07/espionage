# Log Analysis Tool (LAT)

Software cost estimation is crucial in software project lifecycles. Inaccurate estimations can lead to devastating results. Previous studies have used software metrics as proxies for maintenance effort (i.e. time spent on a unit of work). Previous studies have the assumptions that large classes and more complex classes require more maintenance effort. However, the relationship between these proxies and actual maintenance effort has not yet been empirically examined in detail. The main goal of this thesis is to assess whether it is possible to accurately predict maintenance effort using total Source Lines of Code (SLOC) and Cyclomatic Complexity (CC) of classes. 

To accomplish this goal, we reused a dataset acquired from a different study on the impact of code smell over maintenance effort. In the aforementioned study, six professional developers were hired to perform three maintenance tasks on four functionally equivalent Java Systems. Each developer performed three maintenance tasks. While working on the tasks, IDE activity logs were collected. In this study, we created a log analysis tool (LAT) to extract effort from developers' activity logs and investigate the effects of SLOC and CC on maintenance effort. We created and empirically validated LAT for accuracy using the dataset from the previous study. The results show that neither total SLOC nor CC are sufficient to predict maintenance effort. Furthermore, using SLOC by itself or together with WMC result in the same adjusted \(R^2\) of 0.21.

One major contribution of this thesis is that it demonstrates that activity logs are reliable data sources for measuring software maintenance effort. That could lead to more focus on collecting and analyzing activity logs to support better estimations on maintenance effort.

[Read the full master thesis](https://www.overleaf.com/read/mgspqmgggxyw)

## Replication Package

### Dataset for Research Question 1
```
How accurate can file-level effort measurements be when based on IDE activity logs analysis?
```
The dataset used to answer this question can be found [here](http://bit.ly/2vE9F77)

### Dataset for Research Question 2
```
Can we effectively use previously measured SLOC and CC of a class to estimate/predict future
maintenance effort on source code at class level?
```
To perform the study in this research, we use the same dataset from two previous studies from
Soh et al. [55] and Sjøberg et al. [52] in which the impact of code smell on maintenance effort was
studied. Both of these studies used the same dataset. However, the difference is that Sjøberg et al.
[52] measured sheer effort and Soh et al. [55] measured effort by type of activity. This dataset was
acquired from Soh et al. [55]. Sjøberg et al. [52] generated this dataset by the following means: Six
professional developers were hired to perform three maintenance tasks on four functionally equivalent
Java Systems. Each developer performed two rounds of maintenance tasks as seen in figure 3.10.
During maintenance task, Mimec [36] was used to record the IDE activity logs.
#### Step 1
* We measured the effort of both rounds of each developer.
* We measured the SLOC for each class on the four Java Systems.
* We measured the CCs for each class on the four Java Systems.
#### Step 2
* We merged step 1 together to create a dataset for each developer.
* And removed all classes with zero effort.
#### Step 3
* We merged step 2 together to create one full dataset of the developers.
* [The full dataset](https://github.com/King07/espionage/tree/master/dataset)


